class_name HealthComponent
extends Node

signal health_changed(current_health: int, maximum_health: int)
signal depleted

const MAX_HEALTH: int = 100

var current_health: int = MAX_HEALTH


func take_damage(amount: int) -> void:
	if amount <= 0 or current_health <= 0:
		return

	current_health = maxi(0, current_health - amount)
	health_changed.emit(current_health, MAX_HEALTH)
	if current_health == 0:
		depleted.emit()


func heal(amount: int) -> void:
	if amount <= 0 or current_health >= MAX_HEALTH:
		return

	current_health = mini(MAX_HEALTH, current_health + amount)
	health_changed.emit(current_health, MAX_HEALTH)


func restore_full_health() -> void:
	current_health = MAX_HEALTH
	health_changed.emit(current_health, MAX_HEALTH)
