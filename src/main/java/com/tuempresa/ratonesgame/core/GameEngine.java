package com.tuempresa.ratonesgame.core;

import com.tuempresa.ratonesgame.model.Burrow;
import com.tuempresa.ratonesgame.model.Chef;
import com.tuempresa.ratonesgame.model.Player;
import com.tuempresa.ratonesgame.model.Trap;
import com.tuempresa.ratonesgame.view.GameCanvas;
import com.tuempresa.ratonesgame.view.HUDView;
import com.tuempresa.ratonesgame.world.RestaurantMap;
import javafx.animation.AnimationTimer;

import java.util.List;

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
    private final Player firstPlayer;
    private final Player secondPlayer;
    private final Chef chef;
    private final Burrow burrow;
    private final HUDView hud;
    private final Camera camera;
    private final AudioManager audioManager;
    private final RestaurantMap map;
    private final ChefAI chefAI;
    private final GameManager gameManager;
    private final List<Player> players;
    private GameState state = GameState.RUNNING;
    private long lastUpdate;

    public GameEngine(GameCanvas canvas, Player firstPlayer, Player secondPlayer,
                      Chef chef, Burrow burrow, HUDView hud,
                      Camera camera, AudioManager audioManager,
                      RestaurantMap map, ChefAI chefAI, GameManager gameManager) {
        this.canvas = canvas;
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.chef = chef;
        this.burrow = burrow;
        this.hud = hud;
        this.camera = camera;
        this.audioManager = audioManager;
        this.map = map;
        this.chefAI = chefAI;
        this.gameManager = gameManager;
        this.players = List.of(firstPlayer, secondPlayer);
        chefAI.setCaptureHandler(this::handleChefCapture);
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
        this.state = state;
        if (state != GameState.RUNNING) {
            firstPlayer.setVelocity(0, 0);
            secondPlayer.setVelocity(0, 0);
            chefAI.stop();
            audioManager.updateProximity(Double.POSITIVE_INFINITY, 0, false);
            hud.setCaptured(state == GameState.GAME_OVER);
            if (state == GameState.GAME_OVER) {
                gameManager.finishGame();
            } else {
                gameManager.setPaused(true);
            }
        } else {
            gameManager.setPaused(false);
            lastUpdate = 0;
        }
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

    public void notifyTrapActivated(Trap trap) {
        chefAI.notifyTrapActivated(trap);
    }

    public void handleChefCapture(Player player) {
        if (state != GameState.RUNNING || !player.loseLife()) {
            return;
        }
        audioManager.playCaptureImpact();
        if (gameManager.getActivePlayers().isEmpty()) {
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

        boolean firstWasInBurrow = burrow.contains(firstPlayer);
        boolean secondWasInBurrow = burrow.contains(secondPlayer);
        boolean firstWasBeingChased = chefAI.isActivelyChasing(firstPlayer);
        boolean secondWasBeingChased = chefAI.isActivelyChasing(secondPlayer);

        if (firstPlayer.isActive()) {
            map.move(firstPlayer, deltaTime, PLAYER_COLLISION_RADIUS);
        }
        if (secondPlayer.isActive()) {
            map.move(secondPlayer, deltaTime, PLAYER_COLLISION_RADIUS);
        }
        emptyBurrowOnPursuitEntry(firstPlayer, firstWasInBurrow, firstWasBeingChased);
        emptyBurrowOnPursuitEntry(secondPlayer, secondWasInBurrow, secondWasBeingChased);

        firstPlayer.updateHeat(deltaTime);
        secondPlayer.updateHeat(deltaTime);

        chef.updateFoodPreparation(deltaTime, gameManager::nextFoodPoints);
        chefAI.update(deltaTime, players);
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
        Player cameraTarget = firstPlayer.isActive() ? firstPlayer : secondPlayer;
        if (cameraTarget.isActive()) {
            camera.follow(cameraTarget, deltaTime);
        } else {
            camera.follow(chef, deltaTime);
        }
    }

    private void render() {
        canvas.render(firstPlayer, secondPlayer, chef, burrow, camera,
            map, chefAI.getAttackSystem(), gameManager, state == GameState.GAME_OVER);
        hud.setCaptured(state == GameState.GAME_OVER);
        hud.refresh();
    }

    private void emptyBurrowOnPursuitEntry(Player player, boolean wasInBurrow,
                                           boolean wasBeingChased) {
        if (shouldEmptyBurrowOnChasedEntry(wasInBurrow, burrow.contains(player), wasBeingChased)) {
            burrow.empty();
        }
    }

    static boolean shouldEmptyBurrowOnChasedEntry(boolean wasInside,
                                                   boolean isInside,
                                                   boolean wasBeingChased) {
        return !wasInside && isInside && wasBeingChased;
    }

    private void updateAudio(double deltaTime) {
        double firstDistance = !firstPlayer.isActive() || burrow.contains(firstPlayer)
                ? Double.POSITIVE_INFINITY : distance(chef, firstPlayer);
        double secondDistance = !secondPlayer.isActive() || burrow.contains(secondPlayer)
                ? Double.POSITIVE_INFINITY : distance(chef, secondPlayer);
        double nearestDistance = Math.min(firstDistance, secondDistance);
        audioManager.updateProximity(nearestDistance, deltaTime,
            chefAI.isPursuitActive());
    }

    private double distance(Chef chef, Player player) {
        return Math.hypot(chef.getX() - player.getX(), chef.getY() - player.getY());
    }
}