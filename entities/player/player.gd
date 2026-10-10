extends CharacterBody2D

signal lives_changed(remaining_lives: int, maximum_lives: int)
signal incapacitated_changed(value: bool)
signal cheese_changed(cheese_count: int)

@export var player_number: int = 1
@export var speed: float = 200.0
@export_range(1, 99, 1) var max_lives: int = 3
@export_range(0.0, 0.5, 0.01) var max_food_slowdown: float = 0.15
@export var revive_range: float = 100.0

var lives: int
var cheese_count: int = 0
var incapacitated: bool = false
var frozen: bool = false
var carried_food: FoodItem

var health_component: HealthComponent
var heat_component: HeatComponent
var _freeze_timer: Timer
var _pending_cheese_trap: Trap


func _ready() -> void:
	add_to_group("players")
	queue_redraw()
	lives = max_lives

	health_component = get_node_or_null("HealthComponent") as HealthComponent
	if health_component == null:
		health_component = HealthComponent.new()
		health_component.name = "HealthComponent"
		add_child(health_component)
	health_component.depleted.connect(_on_health_depleted)

	heat_component = get_node_or_null("HeatComponent") as HeatComponent
	if heat_component == null:
		heat_component = HeatComponent.new()
		heat_component.name = "HeatComponent"
		add_child(heat_component)

	_freeze_timer = Timer.new()
	_freeze_timer.one_shot = true
	_freeze_timer.timeout.connect(_on_freeze_timeout)
	add_child(_freeze_timer)
	lives_changed.emit(lives, max_lives)


func _draw() -> void:
	var fur_color := Color(0.76, 0.65, 0.53) if player_number == 1 else Color(0.55, 0.68, 0.84)
	draw_circle(Vector2(-8.0, -10.0), 6.0, fur_color.darkened(0.1))
	draw_circle(Vector2(8.0, -10.0), 6.0, fur_color.darkened(0.1))
	draw_circle(Vector2.ZERO, 14.0, fur_color)
	draw_circle(Vector2(5.0, -3.0), 2.0, Color.BLACK)
	draw_circle(Vector2(9.0, 2.0), 2.5, Color(0.9, 0.42, 0.52))


func _physics_process(_delta: float) -> void:
	if incapacitated or frozen:
		velocity = Vector2.ZERO
		move_and_slide()
		return

	var direction := Vector2.ZERO

	if player_number == 1:
		direction.x = Input.get_axis("player1_left", "player1_right")
		direction.y = Input.get_axis("player1_up", "player1_down")
	else:
		direction.x = Input.get_axis("player2_left", "player2_right")
		direction.y = Input.get_axis("player2_up", "player2_down")

	var carry_slowdown := 0.0
	if is_instance_valid(carried_food):
		carry_slowdown = minf(
			carried_food.get_weight_ratio() * 0.04,
			max_food_slowdown
		)

	velocity = direction.normalized() * speed * (1.0 - carry_slowdown)
	move_and_slide()


func _unhandled_input(event: InputEvent) -> void:
	if not event is InputEventKey or not event.pressed or event.echo:
		return

	if _pending_cheese_trap != null:
		if event.physical_keycode >= KEY_1 and event.physical_keycode <= KEY_3:
			choose_cheese_potato(event.physical_keycode - KEY_1)
			get_viewport().set_input_as_handled()
		return

	if event.physical_keycode == KEY_L:
		_try_revive_nearby_player()


func pickup_food(food: FoodItem) -> bool:
	if incapacitated or frozen or carried_food != null or not is_instance_valid(food):
		return false

	carried_food = food
	food.attach_to(self)
	return true


func drop_carried_food() -> void:
	if not is_instance_valid(carried_food):
		carried_food = null
		return

	var world := get_tree().current_scene
	if world == null:
		world = get_parent()
	carried_food.release_from(self, world)
	carried_food = null


func deposit_carried_food(burrow: Burrow) -> bool:
	if not is_instance_valid(carried_food) or not is_instance_valid(burrow):
		carried_food = null
		return false

	var food := carried_food
	if not burrow.add_food(food):
		return false

	carried_food = null
	food.carrier = null
	food.queue_free()
	return true


func eat_carried_food() -> bool:
	if incapacitated or frozen or not is_instance_valid(carried_food):
		carried_food = null
		return false

	var food := carried_food
	carried_food = null
	food.carrier = null
	if food.is_hot():
		heat_component.start_burning(self)
	food.queue_free()
	return true


func on_carried_food_expired(food: FoodItem) -> void:
	if carried_food == food:
		carried_food = null


func take_damage(amount: int = 1) -> void:
	if incapacitated:
		return
	health_component.take_damage(amount)


func apply_freeze(duration: float) -> void:
	if incapacitated or duration <= 0.0:
		return
	frozen = true
	_freeze_timer.start(duration)


func begin_cheese_choice(trap: Trap) -> void:
	if incapacitated or frozen or _pending_cheese_trap != null:
		return
	_pending_cheese_trap = trap


func choose_cheese_potato(index: int) -> void:
	if not is_instance_valid(_pending_cheese_trap):
		_pending_cheese_trap = null
		return

	var trap := _pending_cheese_trap
	_pending_cheese_trap = null
	trap.resolve_choice(self, index)


func _on_freeze_timeout() -> void:
	frozen = false


func _try_revive_nearby_player() -> void:
	if incapacitated or frozen or cheese_count <= 0:
		return

	var nearest_target: Node2D
	var nearest_distance := revive_range
	for candidate in get_tree().get_nodes_in_group("players"):
		if candidate == self or not candidate is Node2D:
			continue
		if candidate.get("incapacitated") != true:
			continue

		var candidate_node := candidate as Node2D
		var distance := global_position.distance_to(candidate_node.global_position)
		if distance <= nearest_distance:
			nearest_target = candidate_node
			nearest_distance = distance

	if nearest_target == null:
		return

	cheese_count -= 1
	cheese_changed.emit(cheese_count)
	nearest_target.call("revive")


func receive_cheese() -> void:
	cheese_count += 1
	cheese_changed.emit(cheese_count)


func heal(amount: int) -> void:
	if incapacitated or frozen:
		return
	health_component.heal(amount)


func _on_health_depleted() -> void:
	lives = maxi(0, lives - 1)
	lives_changed.emit(lives, max_lives)
	heat_component.stop_burning()
	_freeze_timer.stop()
	frozen = false

	if lives == 0:
		set_incapacitated(true)
	else:
		health_component.restore_full_health()


func set_incapacitated(value: bool) -> void:
	incapacitated = value
	if incapacitated:
		velocity = Vector2.ZERO
	incapacitated_changed.emit(incapacitated)


func revive() -> void:
	if not incapacitated:
		return

	lives = 1
	health_component.restore_full_health()
	lives_changed.emit(lives, max_lives)
	set_incapacitated(false)
