class_name WorldSpawner
class_name WorldSpawner
extends Node2D

@export var spawn_bounds: Rect2 = Rect2(-400.0, -250.0, 800.0, 500.0)
@export_range(1, 64, 1) var normal_food_count: int = 8
@export_range(1, 64, 1) var prepared_food_count: int = 8
@export_range(0, 32, 1) var cockroach_count: int = 6
@export_range(1.0, 300.0, 1.0) var food_respawn_seconds: float = 30.0
@export_range(1, 4, 1) var max_poisoned_food: int = 4
@export_range(0.0, 1.0, 0.01) var poison_chance: float = 1.0 / 7.0
@export_range(60.0, 3600.0, 60.0) var first_glue_spawn_seconds: float = 600.0
@export_range(60.0, 3600.0, 60.0) var glue_respawn_seconds: float = 600.0
@export_range(1, 4, 1) var max_glue_traps: int = 4

var _cheese_traps: Array[Trap] = []
var _glue_traps: Array[Trap] = []


func _ready() -> void:
	_spawn_initial_food()
	for index in range(cockroach_count):
		_spawn_cockroach()
	for index in range(2):
		_spawn_cheese_trap()

	call_deferred("_connect_players")
	_wait_and_spawn_glue(first_glue_spawn_seconds)
	_relocate_cheese_traps()


func _spawn_initial_food() -> void:
	var total_food := normal_food_count + prepared_food_count
	var poisoned_count := randi_range(1, mini(max_poisoned_food, total_food))
	var poisoned_indices: Array[int] = []
	for index in range(total_food):
		poisoned_indices.append(index)
	poisoned_indices.shuffle()

	for index in range(normal_food_count):
		var food_type := FoodItem.FoodType.FRUIT if randf() < 0.5 else FoodItem.FoodType.VEGETABLE
		_spawn_food(food_type, poisoned_indices.slice(0, poisoned_count).has(index))
	for index in range(prepared_food_count):
		var combined_index := normal_food_count + index
		_spawn_food(
			FoodItem.FoodType.PREPARED,
			poisoned_indices.slice(0, poisoned_count).has(combined_index)
		)


func _spawn_food(food_type: int, is_poisoned: bool) -> void:
	var food := FoodItem.new()
	food.food_type = food_type
	food.value_points = 20 if food_type == FoodItem.FoodType.PREPARED else 5
	food.roll_poison_on_ready = false
	food.poisoned = is_poisoned
	food.visual_size = Vector2(40.0, 36.0) if food_type == FoodItem.FoodType.PREPARED else Vector2(32.0, 32.0)
	add_child(food)
	food.global_position = _random_spawn_position()
	food.tree_exiting.connect(_on_food_exiting.bind(food_type), CONNECT_ONE_SHOT)


func _on_food_exiting(food_type: int) -> void:
	_respawn_food_after_delay(food_type)


func _respawn_food_after_delay(food_type: int) -> void:
	await get_tree().create_timer(food_respawn_seconds, true, false, true).timeout
	if not is_inside_tree():
		return
	_spawn_food(food_type, _roll_replacement_poison())


func _roll_replacement_poison() -> bool:
	var poisoned_count := 0
	for node in get_tree().get_nodes_in_group("all_food"):
		if node is FoodItem and node.poisoned:
			poisoned_count += 1
	if poisoned_count == 0:
		return true
	if poisoned_count >= max_poisoned_food:
		return false
	return randf() < poison_chance


func _spawn_cockroach() -> void:
	var cockroach := Cockroach.new()
	add_child(cockroach)
	cockroach.global_position = _random_spawn_position()


func _spawn_cheese_trap() -> void:
	var trap := Trap.new()
	trap.trap_type = Trap.TrapType.CHEESE
	trap.set_enabled(false)
	add_child(trap)
	trap.global_position = _random_spawn_position()
	_cheese_traps.append(trap)


func _spawn_glue_trap() -> void:
	if _glue_traps.size() >= max_glue_traps:
		return
	var trap := Trap.new()
	trap.trap_type = Trap.TrapType.GLUE
	add_child(trap)
	trap.global_position = _random_spawn_position()
	trap.glue_triggered.connect(_on_glue_trap_triggered)
	_glue_traps.append(trap)


func _on_glue_trap_triggered(trap: Trap) -> void:
	_glue_traps.erase(trap)
	_remove_glue_after_release(trap)


func _remove_glue_after_release(trap: Trap) -> void:
	await get_tree().create_timer(Trap.FREEZE_SECONDS, true, false, true).timeout
	if is_instance_valid(trap):
		trap.queue_free()
	_wait_and_spawn_glue(glue_respawn_seconds)


func _connect_players() -> void:
	for player in get_tree().get_nodes_in_group("players"):
		if not player.incapacitated_changed.is_connected(_on_player_incapacitated_changed):
			player.incapacitated_changed.connect(_on_player_incapacitated_changed)
	_on_player_incapacitated_changed(false)


func _on_player_incapacitated_changed(_value: bool) -> void:
	var someone_incapacitated := false
	for player in get_tree().get_nodes_in_group("players"):
		if player.get("incapacitated") == true:
			someone_incapacitated = true
			break
	for trap in _cheese_traps:
		if is_instance_valid(trap):
			trap.set_enabled(someone_incapacitated)


func _wait_and_spawn_glue(delay: float) -> void:
	await get_tree().create_timer(delay, true, false, true).timeout
	if not is_inside_tree():
		return

	var available_slots := max_glue_traps - _glue_traps.size()
	if available_slots <= 0:
		return
	var spawn_count := mini(randi_range(1, 3), available_slots)
	for index in range(spawn_count):
		_spawn_glue_trap()


func _relocate_cheese_traps() -> void:
	while is_inside_tree():
		await get_tree().create_timer(randf_range(60.0, 120.0), true, false, true).timeout
		if not is_inside_tree():
			return
		for trap in _cheese_traps:
			if is_instance_valid(trap):
				trap.move_to(_random_spawn_position())


func _random_spawn_position() -> Vector2:
	var margin := 32.0
	var min_x := spawn_bounds.position.x + margin
	var min_y := spawn_bounds.position.y + margin
	var max_x := spawn_bounds.end.x - margin
	var max_y := spawn_bounds.end.y - margin
	if max_x < min_x or max_y < min_y:
		return to_global(spawn_bounds.get_center())

	for attempt in range(20):
		var candidate := Vector2(randf_range(min_x, max_x), randf_range(min_y, max_y))
		var world_candidate := to_global(candidate)
		var occupied := false
		for group_name in ["food", "cockroaches", "traps", "players"]:
			for node in get_tree().get_nodes_in_group(group_name):
				if node is Node2D and world_candidate.distance_to(node.global_position) < 40.0:
					occupied = true
					break
			if occupied:
				break
		if not occupied:
			return world_candidate
	return to_global(spawn_bounds.get_center())
