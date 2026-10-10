class_name Trap
extends Area2D

signal glue_triggered(trap: Trap)
signal cheese_used(trap: Trap)

enum TrapType { GLUE, CHEESE }

const FREEZE_SECONDS: float = 10.0

@export var trap_type: TrapType = TrapType.GLUE

var enabled: bool = true
var _selection_pending: bool = false
var _chooser: Node
var _safe_potato: int = 0


func _ready() -> void:
	add_to_group("traps")
	body_entered.connect(_on_body_entered)
	_ensure_collision_shape()
	monitoring = enabled
	queue_redraw()


func _draw() -> void:
	if trap_type == TrapType.GLUE:
		draw_circle(Vector2.ZERO, 20.0, Color(0.45, 0.25, 0.12, 0.9))
		draw_circle(Vector2.ZERO, 13.0, Color(0.77, 0.57, 0.25, 0.9))
		return

	draw_circle(Vector2.ZERO, 26.0, Color(0.4, 0.24, 0.12))
	for index in range(3):
		var x := float(index - 1) * 18.0
		var potato_color := Color(0.72, 0.51, 0.27) if enabled else Color(0.37, 0.34, 0.3)
		draw_set_transform(Vector2(x, -2.0), -0.2 + float(index) * 0.2, Vector2(1.0, 1.5))
		draw_circle(Vector2.ZERO, 6.0, potato_color)
		draw_circle(Vector2(-1.0, -2.0), 1.0, Color(0.35, 0.22, 0.12))
		draw_circle(Vector2(2.0, 2.0), 1.0, Color(0.35, 0.22, 0.12))
		if ThemeDB.fallback_font != null:
			draw_string(
				ThemeDB.fallback_font,
				Vector2(-3.0, 20.0),
				str(index + 1),
				HORIZONTAL_ALIGNMENT_CENTER,
				8.0,
				10,
				Color.WHITE
			)
		draw_set_transform(Vector2.ZERO, 0.0, Vector2.ONE)


func set_enabled(value: bool) -> void:
	enabled = value
	monitoring = enabled and not _selection_pending
	queue_redraw()


func move_to(world_position: Vector2) -> void:
	if _selection_pending:
		return
	global_position = world_position


func resolve_choice(player: Node, index: int) -> void:
	if not _selection_pending or player != _chooser or index < 0 or index > 2:
		return

	_selection_pending = false
	_chooser = null
	player.call("receive_cheese")
	if index != _safe_potato:
		player.call("apply_freeze", FREEZE_SECONDS)
	set_enabled(false)
	cheese_used.emit(self)


func _on_body_entered(body: Node2D) -> void:
	if not enabled or _selection_pending or not body.is_in_group("players"):
		return

	if trap_type == TrapType.GLUE:
		body.call("apply_freeze", FREEZE_SECONDS)
		set_enabled(false)
		glue_triggered.emit(self)
		return

	if not body.has_method("begin_cheese_choice"):
		return
	_safe_potato = randi_range(0, 2)
	_chooser = body
	_selection_pending = true
	monitoring = false
	body.call("begin_cheese_choice", self)


func _ensure_collision_shape() -> void:
	var collision := get_node_or_null("CollisionShape2D") as CollisionShape2D
	if collision == null:
		collision = CollisionShape2D.new()
		collision.name = "CollisionShape2D"
		add_child(collision)
	if collision.shape == null:
		var circle := CircleShape2D.new()
		circle.radius = 26.0
		collision.shape = circle
