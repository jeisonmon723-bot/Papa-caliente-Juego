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
import com.tuempresa.ratonesgame.view.GameCanvas;
import com.tuempresa.ratonesgame.view.HUDView;
import com.tuempresa.ratonesgame.world.RestaurantMap;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public final class MainApp extends Application {
    private final ResourceManager resources = new ResourceManager();
    private GameEngine engine;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        showModeSelection();
        stage.setTitle("Ratones Game");
        stage.show();
    }

    private void showModeSelection() {
        Label title = new Label("RATONES GAME");
        title.getStyleClass().add("menu-title");
        Button onePlayer = new Button("1 jugador");
        Button twoPlayers = new Button("2 jugadores (vs Chef)");
        onePlayer.setOnAction(event -> startGame(GameManager.GameMode.ONE_PLAYER));
        twoPlayers.setOnAction(event -> startGame(GameManager.GameMode.TWO_PLAYERS));
        VBox menu = new VBox(14, title, onePlayer, twoPlayers);
        menu.setAlignment(javafx.geometry.Pos.CENTER);
        menu.getStyleClass().add("menu-screen");
        Scene scene = new Scene(menu, 960, 540);
        addStylesheet(scene);
        stage.setScene(scene);
    }

    private void startGame(GameManager.GameMode mode) {
        Player firstPlayer = new Player(256, 320);
        Player secondPlayer = new Player(640, 320);
        Chef chef = new Chef(1200, 350);
        Burrow burrow = new Burrow(160, 880, 38);
        RestaurantMap map = new RestaurantMap();
        GameManager gameManager = new GameManager(mode, firstPlayer, secondPlayer,
                chef, burrow, map);
        GameCanvas canvas = new GameCanvas(960, 540, resources);
        HUDView hud = new HUDView(firstPlayer, burrow, gameManager);
        InputController input = new InputController(firstPlayer, chef, gameManager);
        Camera camera = new Camera(960, 540, GameCanvas.WORLD_WIDTH,
                GameCanvas.WORLD_HEIGHT, 5.5);
        AudioManager audioManager = new AudioManager();
        ChefConfig chefConfig = ChefConfig.standard(firstPlayer.getSpeed());
        ChefAI chefAI = new ChefAI(chef, chefConfig, map, burrow);
        engine = new GameEngine(canvas, firstPlayer, secondPlayer, chef, burrow,
                hud, camera, audioManager, map, chefAI, gameManager);
        for (Trap trap : map.getTraps()) {
            trap.setActivationListener(engine::notifyTrapActivated);
        }

        StackPane root = new StackPane(canvas, hud);
        root.setFocusTraversable(true);
        Scene scene = new Scene(root, 960, 540);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                engine.togglePause();
                input.clearPressedKeys();
            } else if (event.getCode() == KeyCode.Q) {
                engine.finishGame();
            } else {
                input.handleKeyPressed(event);
            }
        });
        scene.setOnKeyReleased(input::handleKeyReleased);
        addStylesheet(scene);
        stage.setScene(scene);
        root.requestFocus();
        engine.start();
    }

    private void addStylesheet(Scene scene) {
        var stylesheet = MainApp.class.getResource("/com/tuempresa/ratonesgame/styles/hud.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }
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