package com.tuempresa.ratonesgame.core;

import com.tuempresa.ratonesgame.model.Burrow;
import com.tuempresa.ratonesgame.model.Chef;
import com.tuempresa.ratonesgame.model.Player;
import com.tuempresa.ratonesgame.model.Trap;
import com.tuempresa.ratonesgame.view.GameCanvas;
import com.tuempresa.ratonesgame.view.HUDView;
import com.tuempresa.ratonesgame.world.RestaurantMap;
import javafx.animation.AnimationTimer;

public final class GameEngine extends AnimationTimer {
    private static final double MAX_DELTA_SECONDS = 0.05;
    private static final double PLAYER_COLLISION_RADIUS = 12;
    private static final double CHEF_COLLISION_RADIUS = 14;

    public enum GameState {
        RUNNING,
        PAUSED,
        GAME_OVER
    }

    private final GameCanvas canvas;
    private final Player player;
    private final Chef chef;
    private final Burrow burrow;
    private final HUDView hud;
    private final Camera camera;
    private final AudioManager audioManager;
    private final RestaurantMap map;
    private final ChefAI chefAI;
    private final GameManager gameManager;
    private GameState state = GameState.RUNNING;
    private long lastUpdate;

    public GameEngine(GameCanvas canvas, Player player, Chef chef, Burrow burrow,
                      HUDView hud, Camera camera, AudioManager audioManager,
                      RestaurantMap map, ChefAI chefAI, GameManager gameManager) {
        this.canvas = canvas;
        this.player = player;
        this.chef = chef;
        this.burrow = burrow;
        this.hud = hud;
        this.camera = camera;
        this.audioManager = audioManager;
        this.map = map;
        this.chefAI = chefAI;
        this.gameManager = gameManager;
        chefAI.setCaptureHandler(this::handleChefCapture);
        hud.setRestartHandler(this::resetGame);
    }

    @Override
    public void handle(long now) {
        if (lastUpdate == 0) {
            lastUpdate = now;
            render();
            return;
        }

        double deltaTime = Math.min((now - lastUpdate) / 1_000_000_000.0, MAX_DELTA_SECONDS);
        lastUpdate = now;
        update(deltaTime);
        render();
    }

    public void setState(GameState state) {
        if (state == null) {
            throw new IllegalArgumentException("Game state cannot be null");
        }
        if (this.state == state) {
            return;
        }
        this.state = state;
        if (state != GameState.RUNNING) {
            player.setVelocity(0, 0);
            chefAI.stop();
            if (state == GameState.GAME_OVER) {
                gameManager.finishGame();
                audioManager.updateProximity(Double.POSITIVE_INFINITY, 0, false);
            } else {
                gameManager.setPaused(true);
                audioManager.reset();
            }
        } else {
            gameManager.setPaused(false);
            lastUpdate = 0;
        }
        hud.setGameOver(state == GameState.GAME_OVER);
    }

    public GameState getState() {
        return state;
    }

    public void togglePause() {
        if (state == GameState.RUNNING) {
            setState(GameState.PAUSED);
        } else if (state == GameState.PAUSED) {
            setState(GameState.RUNNING);
        }
    }

    public void finishGame() {
        if (state == GameState.RUNNING || state == GameState.PAUSED) {
            setState(GameState.GAME_OVER);
        }
    }

    public void resetGame() {
        gameManager.resetGame();
        chefAI.reset();
        camera.resetTo(player);
        audioManager.reset();
        state = GameState.RUNNING;
        lastUpdate = 0;
        hud.setGameOver(false);
        hud.refresh();
        render();
    }

    public void notifyTrapActivated(Trap trap) {
        chefAI.notifyTrapActivated(trap);
    }

    public void handleChefCapture(Player capturedPlayer) {
        if (state != GameState.RUNNING || capturedPlayer != player
                || !capturedPlayer.loseLife()) {
            return;
        }
        audioManager.playCaptureImpact();
        if (!player.isActive()) {
            finishGame();
        }
    }

    @Override
    public void stop() {
        lastUpdate = 0;
        audioManager.close();
        super.stop();
    }

    private void update(double deltaTime) {
        if (state != GameState.RUNNING) {
            return;
        }

        boolean wasInBurrow = burrow.contains(player);
        boolean wasBeingChased = chefAI.isActivelyChasing(player);
        if (player.isActive()) {
            map.move(player, deltaTime, PLAYER_COLLISION_RADIUS);
        }
        if (shouldEmptyBurrowOnChasedEntry(wasInBurrow, burrow.contains(player),
                wasBeingChased)) {
            burrow.empty();
        }

        player.updateHeat(deltaTime);
        chef.updateFoodPreparation(deltaTime, gameManager::nextFoodPoints);
        chefAI.update(deltaTime, player);
        if (state != GameState.RUNNING) {
            return;
        }
        map.move(chef, deltaTime, CHEF_COLLISION_RADIUS);
        gameManager.update(deltaTime);
        if (gameManager.getState() == GameManager.State.GAME_OVER) {
            setState(GameState.GAME_OVER);
            return;
        }
        updateAudio(deltaTime);
        if (player.isActive()) {
            camera.follow(player, deltaTime);
        } else {
            camera.follow(chef, deltaTime);
        }
    }

    private void render() {
        canvas.render(player, chef, burrow, camera, map,
                chefAI.getAttackSystem(), gameManager, state == GameState.GAME_OVER);
        hud.setGameOver(state == GameState.GAME_OVER);
        hud.refresh();
    }

    static boolean shouldEmptyBurrowOnChasedEntry(boolean wasInside,
                                                   boolean isInside,
                                                   boolean wasBeingChased) {
        return !wasInside && isInside && wasBeingChased;
    }

    private void updateAudio(double deltaTime) {
        double distance = !player.isActive() || burrow.contains(player)
                ? Double.POSITIVE_INFINITY : distance(chef, player);
        audioManager.updateProximity(distance, deltaTime, chefAI.isPursuitActive());
    }

    private double distance(Chef chef, Player player) {
        return Math.hypot(chef.getX() - player.getX(), chef.getY() - player.getY());
    }
}
