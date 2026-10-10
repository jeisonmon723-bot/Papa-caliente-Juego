# Ratones Game

2D top-down game prototype built with Java 17 and JavaFX.

## Run

Install Java 17, then run from the repository root. The Maven Wrapper downloads
and runs the required Maven version automatically (an internet connection is
needed the first time):

```powershell
.\mvnw.cmd javafx:run
```

On macOS or Linux, use:

```sh
./mvnw javafx:run
```

Player 1 controls the mouse with WASD. In 1-player mode, the Chef is controlled by AI. In 2-player versus mode, player 2 controls the Chef with the arrow keys while player 1 continues controlling the mouse with WASD.

The start screen selects 1P or local mouse-versus-Chef mode. The mouse interacts with `E` to pick up food and deliver it to the burrow. In versus mode, the Chef captures the mouse on contact. `Escape` pauses/resumes, and `Q` ends the current game and records the final score.

Sprites and other original art resources are under `src/main/resources/com/tuempresa/ratonesgame/sprites/`. The generated prototype sheets are SVG; `ResourceManager` rasterizes SVG images in memory with Batik.

## Chef AI

`GameEngine` owns the game state and updates the Chef AI in 1-player mode. In 2-player versus mode, the arrow keys move the Chef and contact captures the mouse, which loses a life and respawns at its starting position. Escape pauses/resumes the simulation.

- `ChefConfig` centralizes view angle/range, movement speeds, attack timings, search limits, and arrival tolerances.
- `RestaurantMap` owns collision geometry, line-of-sight blockers, trap locations, workstations, and patrol points. Movement and BFS pathfinding use the same solid-obstacle list.
- `VisionSystem` checks each active player independently against the chef's facing, 120-degree field of view, detection range, burrow concealment, and map occlusion.
- `ChefAI` owns the finite-state routine: `PREPARING_FOOD`, `PATROLLING`, `INSPECTING_TRAP`, `CHASING`, `SEARCHING`, `ATTACKING`, and `RETURNING_TO_WORK`.
- `AttackSystem` handles windup, directional broom strike, one hit maximum, and recovery. It reports a hit through a callback; `GameEngine` decides whether to capture the player.
- `Trap.trigger()` notifies its activation listener only while the trap is active. `MainApp` connects map traps to `GameEngine.notifyTrapActivated`; inspecting a trap does not change its state.

When a player was actively chased and crosses from outside into the burrow, `GameEngine` calls `Burrow.empty()` immediately. The HUD reads that same `Burrow` instance for its percentage label and progress bar. Entering without active pursuit, or remaining inside, does not clear the fill.

The chef targets the nearest currently visible player, retains that target while it remains visible, and searches its last observed position for a bounded time after losing sight. A player entering a burrow breaks visibility; the chef may search the last observed location but cannot see or strike the hidden player.

## Burrows, Food, and Lives

`GameManager` creates eight randomized floor-food pickups (one per normal type), each worth 1-15 points, and respawns them after pickup. Each burrow has a 100-point quota and a 120-second timer. The mouse player owns the deadline in both modes; a timeout removes one life, completing the quota increments the score and moves the burrow. The high score is stored with Java Preferences.

Players start with 3 lives and 100 health. Holding a hot plate gives 5 seconds of grace, then drains 5 health per second. Losing all health costs one life. The Chef's eight hot-food types are only acquired by interacting near the Chef. The Papa Caliente has a 20% spawn chance once per game, a 30-second fuse after pickup, and fills the current burrow instantly if carried into it in time.

## Behavior Checks

The standalone check runners (no JUnit dependency) cover directional/occluded vision, pathfinding around furniture, active/nearest target selection, searching and reacquiring, valid and missed broom attacks, pursuit-only burrow emptying, trap events, solo/versus setup, simultaneous mouse/Chef controls, player reset, all eight Chef dishes, food quota delivery, timeout/life loss/Game Over, paused timers, hot-food damage timing, hot-potato fuse/delivery, non-duplicating food respawns, carrying-weight speed penalties, and camera zoom.

```powershell
.\mvnw.cmd test-compile dependency:build-classpath "-Dmdep.outputFile=target/test-classpath.txt"
$classpath = "target/classes;target/test-classes;$((Get-Content target/test-classpath.txt -Raw).Trim())"
java -cp $classpath com.tuempresa.ratonesgame.core.ChefIntegrationChecks
java -cp $classpath com.tuempresa.ratonesgame.core.GameManagerChecks
```

The prototype includes a 1920 x 1080 restaurant world shown through a smooth, zoomed camera viewport, bright uniform lighting, a safe burrow with a delivery progress bar, chef proximity/chase audio, and a capture overlay. Food portion size affects player movement speed, and the HUD shows the pickup/delivery controls. Sound clips are synthesized as temporary WAV files at startup because no sound assets are included yet.