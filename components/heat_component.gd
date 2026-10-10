class_name HeatComponent
extends Node

@export var grace_seconds: float = 5.0
@export var damage_per_second: int = 5

var _target: Node
var _elapsed: float = 0.0
var _damage_elapsed: float = 0.0
var _burning: bool = false


func start_burning(target: Node) -> void:
	_target = target
	_elapsed = 0.0
	_damage_elapsed = 0.0
	_burning = true
	set_process(true)


func stop_burning() -> void:
	_burning = false
	_target = null
	_elapsed = 0.0
	_damage_elapsed = 0.0
	set_process(false)


func _process(delta: float) -> void:
	if not _burning or not is_instance_valid(_target):
		stop_burning()
		return

	_elapsed += delta
	if _elapsed <= grace_seconds:
		return

	_damage_elapsed += delta
	while _burning and _damage_elapsed >= 1.0:
		_damage_elapsed -= 1.0
		if not _target.has_method("take_damage"):
			stop_burning()
			return
		_target.call("take_damage", damage_per_second)
