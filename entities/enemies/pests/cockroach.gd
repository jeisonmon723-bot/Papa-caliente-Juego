class_name Cockroach
extends CharacterBody2D

const TRAVEL_SECONDS: float = 30.0
const EAT_SECONDS: float = 15.0
const MOVE_SPEED: float = 48.0
const BITE_DISTANCE: float = 20.0

enum State { TRAVEL, EATING, WANDERING }

var _state: State = State.TRAVEL
var _state_time: float = TRAVEL_SECONDS
var _target: FoodItem
var _last_food_id: int = 0
var _wander_direction := Vector2.RIGHT


func _ready() -> void:
	add_to_group("cockroaches")
	_ensure_collision_shape()
	queue_redraw()


func _draw() -> void:
	for side in [-1.0, 1.0]:
		for offset in [-7.0, 0.0, 7.0]:
			draw_line(
				Vector2(offset, side * 2.0),
				Vector2(offset + side * 7.0, side * 11.0),
				Color(0.12, 0.07, 0.04),
				2.0
			)
	draw_set_transform(Vector2.ZERO, 0.0, Vector2(1.0, 1.45))
	draw_circle(Vector2.ZERO, 9.0, Color(0.36, 0.18, 0.08))
	draw_set_transform(Vector2(0.0, -9.0), 0.0, Vector2(1.2, 0.8))
	draw_circle(Vector2.ZERO, 6.0, Color(0.18, 0.1, 0.06))
	draw_set_transform(Vector2.ZERO, 0.0, Vector2.ONE)
	draw_line(Vector2(-3.0, -12.0), Vector2(-7.0, -19.0), Color(0.12, 0.07, 0.04), 1.5)
	draw_line(Vector2(3.0, -12.0), Vector2(7.0, -19.0), Color(0.12, 0.07, 0.04), 1.5)


func _physics_process(delta: float) -> void:
	match _state:
		State.TRAVEL:
			_travel_toward_food(delta)
		State.EATING:
			_eat_food(delta)
		State.WANDERING:
			_wander(delta)


func _travel_toward_food(delta: float) -> void:
	if not is_instance_valid(_target) or _target.carrier != null or _target.value_points <= 0:
		_choose_food()
		if not is_instance_valid(_target):
			_state = State.WANDERING
			_state_time = 2.0
			_wander_direction = Vector2.from_angle(randf_range(0.0, TAU))
			return

	if global_position.distance_to(_target.global_position) > BITE_DISTANCE:
		velocity = global_position.direction_to(_target.global_position) * MOVE_SPEED
		move_and_slide()
	else:
		velocity = Vector2.ZERO
	_state_time = maxf(0.0, _state_time - delta)

	if _state_time <= 0.0 and global_position.distance_to(_target.global_position) <= BITE_DISTANCE:
		_state = State.EATING
		_state_time = EAT_SECONDS


func _eat_food(delta: float) -> void:
	if not is_instance_valid(_target) or _target.carrier != null or _target.value_points <= 0:
		_start_travel()
		return

	if global_position.distance_to(_target.global_position) > BITE_DISTANCE:
		_start_travel()
		return

	velocity = Vector2.ZERO
	move_and_slide()
	_state_time -= delta
	if _state_time <= 0.0:
		_target.take_bite()
		_last_food_id = _target.get_instance_id()
		_target = null
		_start_travel()


func _wander(delta: float) -> void:
	velocity = _wander_direction * MOVE_SPEED * 0.55
	move_and_slide()
	_state_time -= delta
	if _state_time <= 0.0:
		_choose_food()
		if is_instance_valid(_target):
			_start_travel()
		else:
			_state_time = 2.0
			_wander_direction = Vector2.from_angle(randf_range(0.0, TAU))


func _choose_food() -> void:
	var nearest_other: FoodItem
	var nearest_any: FoodItem
	var other_distance := INF
	var any_distance := INF

	for candidate in get_tree().get_nodes_in_group("food"):
		if not candidate is FoodItem or candidate.carrier != null or candidate.value_points <= 0:
			continue
		var distance := global_position.distance_squared_to(candidate.global_position)
		if distance < any_distance:
			nearest_any = candidate
			any_distance = distance
		if candidate.get_instance_id() != _last_food_id and distance < other_distance:
			nearest_other = candidate
			other_distance = distance

	_target = nearest_other if is_instance_valid(nearest_other) else nearest_any
	_state_time = TRAVEL_SECONDS


func _start_travel() -> void:
	_state = State.TRAVEL
	_choose_food()


func _ensure_collision_shape() -> void:
	var collision := get_node_or_null("CollisionShape2D") as CollisionShape2D
	if collision == null:
		collision = CollisionShape2D.new()
		collision.name = "CollisionShape2D"
		add_child(collision)
	if collision.shape == null:
		var circle := CircleShape2D.new()
		circle.radius = 10.0
		collision.shape = circle
