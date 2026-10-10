class_name Burrow
extends Area2D

signal progress_changed(current_points: int, required_points: int)
signal completed

@export_range(1, 100000, 1) var required_points: int = 100

var collected_points: int = 0
var is_completed: bool = false


func _ready() -> void:
	body_entered.connect(_on_body_entered)


func add_food(food: FoodItem) -> bool:
	if is_completed or not is_instance_valid(food):
		return false

	var points := maxi(0, food.value_points)
	if food.poisoned:
		points = -points

	collected_points = clampi(collected_points + points, 0, required_points)
	progress_changed.emit(collected_points, required_points)

	if collected_points >= required_points:
		is_completed = true
		completed.emit()

	return true


func _on_body_entered(body: Node2D) -> void:
	if body.has_method("deposit_carried_food"):
		body.call("deposit_carried_food", self)
