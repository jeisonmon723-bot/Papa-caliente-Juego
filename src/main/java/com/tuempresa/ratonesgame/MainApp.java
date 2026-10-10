package com.tuempresa.ratonesgame;

import com.tuempresa.ratonesgame.controller.InputController;
import com.tuempresa.ratonesgame.core.AudioManager;
import com.tuempresa.ratonesgame.core.Camera;
import com.tuempresa.ratonesgame.core.ChefAI;
import com.tuempresa.ratonesgame.core.ChefConfig;
import com.tuempresa.ratonesgame.core.GameEngine;
import com.tuempresa.ratonesgame.core.GameManager;
import com.tuempresa.ratonesgame.core.ResourceManager;
import com.tuempresa.ratonesgame.model.Burrow;
import com.tuempresa.ratonesgame.model.Chef;
import com.tuempresa.ratonesgame.model.Player;
import com.tuempresa.ratonesgame.model.Trap;
import com.tuempresa.ratonesgame.view.AspectRatioPane;
import com.tuempresa.ratonesgame.view.GameCanvas;
import com.tuempresa.ratonesgame.view.HUDView;
import com.tuempresa.ratonesgame.world.RestaurantMap;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public final class MainApp extends Application {
    private final ResourceManager resources = new ResourceManager();
    private GameEngine engine;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Ratones Game");
        stage.setResizable(true);
        startGame(stage);
        stage.show();
    }

    private void startGame(Stage stage) {
        Player player = new Player(256, 320);
        Chef chef = new Chef(1200, 350);
        Burrow burrow = new Burrow(160, 880, 38);
        RestaurantMap map = new RestaurantMap();
        GameManager gameManager = new GameManager(player, chef, burrow, map);
        GameCanvas canvas = new GameCanvas(
                GameCanvas.VIEW_WIDTH, GameCanvas.VIEW_HEIGHT, resources);
        HUDView hud = new HUDView(player, burrow, gameManager);
        InputController input = new InputController(player, gameManager);
        Camera camera = new Camera(GameCanvas.VIEW_WIDTH, GameCanvas.VIEW_HEIGHT,
                GameCanvas.WORLD_WIDTH, GameCanvas.WORLD_HEIGHT, 5.5);
        AudioManager audioManager = new AudioManager();
        ChefConfig chefConfig = ChefConfig.standard(player.getSpeed());
        ChefAI chefAI = new ChefAI(chef, chefConfig, map, burrow);
        GameEngine gameEngine = new GameEngine(canvas, player, chef, burrow,
                hud, camera, audioManager, map, chefAI, gameManager);
        engine = gameEngine;
        hud.setRestartHandler(() -> {
            input.clearPressedKeys();
            gameEngine.resetGame();
        });
        for (Trap trap : map.getTraps()) {
            trap.setActivationListener(gameEngine::notifyTrapActivated);
        }

        StackPane root = new StackPane(canvas, hud);
        root.setFocusTraversable(true);
        Scene scene = new Scene(new AspectRatioPane(root,
                GameCanvas.VIEW_WIDTH, GameCanvas.VIEW_HEIGHT),
                GameCanvas.VIEW_WIDTH, GameCanvas.VIEW_HEIGHT);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                gameEngine.togglePause();
                input.clearPressedKeys();
            } else if (gameEngine.getState() == GameEngine.GameState.GAME_OVER
                    && (event.getCode() == KeyCode.R || event.getCode() == KeyCode.ENTER)) {
                input.clearPressedKeys();
                gameEngine.resetGame();
            } else if (event.getCode() == KeyCode.Q) {
                gameEngine.finishGame();
            } else {
                input.handleKeyPressed(event);
            }
        });
        scene.setOnKeyReleased(input::handleKeyReleased);
        var stylesheet = MainApp.class.getResource("/com/tuempresa/ratonesgame/styles/hud.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }
        stage.setScene(scene);
        root.requestFocus();
        gameEngine.start();
    }

    @Override
    public void stop() {
        if (engine != null) {
            engine.stop();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
