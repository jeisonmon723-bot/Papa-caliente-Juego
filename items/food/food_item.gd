class_name FoodItem
extends Area2D

enum FoodType { FRUIT, VEGETABLE, PREPARED }

const LIFETIME_SECONDS: float = 60.0 * 60.0

@export var food_type: FoodType = FoodType.FRUIT
@export_range(0, 100, 1) var value_points: int = 5
@export var roll_poison_on_ready: bool = true
@export var poisoned: bool = false
@export var carry_offset: Vector2 = Vector2(12.0, -12.0)
@export var visual_size: Vector2 = Vector2(32.0, 32.0)
@export var reference_visual_size: Vector2 = Vector2(32.0, 32.0)

var carrier: Node2D
var cooling_progress: float = 0.0
var _lifetime_timer: Timer
var _previous_carrier: Node2D


func _ready() -> void:
	process_mode = Node.PROCESS_MODE_ALWAYS
	add_to_group("food")
	add_to_group("all_food")
	if roll_poison_on_ready:
		poisoned = randi_range(1, 7) == 1

	body_entered.connect(_on_body_entered)
	body_exited.connect(_on_body_exited)
	_ensure_collision_shape()

	_lifetime_timer = Timer.new()
	_lifetime_timer.one_shot = true
	_lifetime_timer.process_mode = Node.PROCESS_MODE_ALWAYS
	_lifetime_timer.ignore_time_scale = true
	_lifetime_timer.wait_time = LIFETIME_SECONDS
	_lifetime_timer.timeout.connect(_on_lifetime_timeout)
	add_child(_lifetime_timer)
	_lifetime_timer.start()
	queue_redraw()


func _process(_delta: float) -> void:
	if _lifetime_timer == null:
		return

	if food_type == FoodType.PREPARED:
		cooling_progress = clampf(
			1.0 - (_lifetime_timer.time_left / LIFETIME_SECONDS),
			0.0,
			1.0
		)
	queue_redraw()


func _draw() -> void:
	var body_color := Color(0.92, 0.24, 0.18)
	if food_type == FoodType.VEGETABLE:
		body_color = Color(0.32, 0.75, 0.25)
	elif food_type == FoodType.PREPARED:
		body_color = Color(1.0, 0.68, 0.24).lerp(
			Color(0.53, 0.52, 0.5),
			cooling_progress
		)

	if poisoned:
		body_color = Color(0.65, 0.24, 0.78)

	var radius := minf(visual_size.x, visual_size.y) * 0.45
	draw_circle(Vector2.ZERO, radius, body_color)
	if food_type == FoodType.FRUIT:
		draw_colored_polygon(
			PackedVector2Array([
				Vector2(-2.0, -radius + 3.0),
				Vector2(2.0, -radius - 5.0),
				Vector2(8.0, -radius - 2.0)
			]),
			Color(0.25, 0.68, 0.2)
		)
	elif food_type == FoodType.VEGETABLE:
		draw_line(
			Vector2(-radius * 0.7, 0.0),
			Vector2(radius * 0.7, 0.0),
			Color(0.85, 0.92, 0.35),
			2.0
		)
	if poisoned:
		draw_circle(Vector2(radius * 0.62, -radius * 0.62), 4.0, Color.BLACK)


func is_hot() -> bool:
	return food_type == FoodType.PREPARED and cooling_progress < 1.0


func get_weight_ratio() -> float:
	var reference_area := reference_visual_size.x * reference_visual_size.y
	if reference_area <= 0.0:
		return 0.0
	return maxf(0.0, visual_size.x * visual_size.y / reference_area)


func take_bite() -> bool:
	if carrier != null or value_points <= 0:
		return false

	value_points -= 1
	if value_points == 0:
		queue_free()
	else:
		queue_redraw()
	return true


func attach_to(new_carrier: Node2D) -> void:
	carrier = new_carrier
	_previous_carrier = null
	remove_from_group("food")
	monitoring = false
	monitorable = false
	reparent(new_carrier)
	position = carry_offset


func release_from(old_carrier: Node2D, world: Node) -> void:
	if carrier != old_carrier:
		return

	var world_position := global_position
	_previous_carrier = old_carrier
	carrier = null
	reparent(world)
	global_position = world_position
	add_to_group("food")
	monitorable = true
	monitoring = true


func _ensure_collision_shape() -> void:
	var collision := get_node_or_null("CollisionShape2D") as CollisionShape2D
	if collision == null:
		collision = CollisionShape2D.new()
		collision.name = "CollisionShape2D"
		add_child(collision)

	if collision.shape == null:
		var circle := CircleShape2D.new()
		circle.radius = minf(visual_size.x, visual_size.y) * 0.45
		collision.shape = circle


func _on_body_entered(body: Node2D) -> void:
	if body == _previous_carrier:
		return
	if body.has_method("pickup_food"):
		body.call("pickup_food", self)


func _on_body_exited(body: Node2D) -> void:
	if body == _previous_carrier:
		_previous_carrier = null


func _on_lifetime_timeout() -> void:
	if is_instance_valid(carrier) and carrier.has_method("on_carried_food_expired"):
		carrier.call("on_carried_food_expired", self)
	carrier = null
	queue_free()
