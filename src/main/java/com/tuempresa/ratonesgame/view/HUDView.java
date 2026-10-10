package com.tuempresa.ratonesgame.view;

import com.tuempresa.ratonesgame.core.GameManager;
import com.tuempresa.ratonesgame.model.Burrow;
import com.tuempresa.ratonesgame.model.HotPotato;
import com.tuempresa.ratonesgame.model.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Objects;

public final class HUDView extends StackPane {
    private final Player player;
    private final Burrow burrow;
    private final GameManager gameManager;
    private final Label playerStatus = new Label();
    private final Label roundTimer = new Label();
    private final Label scoreStatus = new Label();
    private final Label healthStatus = new Label();
    private final Label burrowStatus = new Label();
    private final Label potatoStatus = new Label();
    private final Label alertStatus = new Label();
    private final Label controls = new Label();
    private final ProgressBar burrowProgress = new ProgressBar();
    private final ProgressBar healthProgress = new ProgressBar();
    private final StackPane gameOverOverlay = new StackPane();
    private final Label gameOverLabel = new Label();
    private final Button restartButton = new Button("Jugar de nuevo");
    private Runnable restartHandler = () -> { };

    public HUDView(Player player, Burrow burrow, GameManager gameManager) {
        this.player = player;
        this.burrow = burrow;
        this.gameManager = gameManager;

        VBox statusPanel = new VBox(8, playerStatus, roundTimer, scoreStatus,
                healthStatus, healthProgress, burrowStatus, burrowProgress,
                potatoStatus, alertStatus, controls);
        statusPanel.setPadding(new Insets(18));
        statusPanel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        statusPanel.getStyleClass().add("hud");
        StackPane.setAlignment(statusPanel, Pos.TOP_LEFT);
        burrowProgress.setPrefWidth(280);
        healthProgress.setPrefWidth(280);
        healthProgress.getStyleClass().add("health-bar");
        controls.getStyleClass().add("controls-hint");

        VBox gameOverPanel = new VBox(18, gameOverLabel, restartButton);
        gameOverPanel.setAlignment(Pos.CENTER);
        gameOverPanel.getStyleClass().add("game-over-panel");
        gameOverLabel.getStyleClass().add("capture-label");
        restartButton.getStyleClass().add("restart-button");
        restartButton.setOnAction(event -> restartHandler.run());
        gameOverOverlay.getStyleClass().add("capture-overlay");
        gameOverOverlay.getChildren().add(gameOverPanel);
        gameOverOverlay.setVisible(false);
        gameOverOverlay.setManaged(false);
        gameOverOverlay.setPickOnBounds(true);

        getChildren().addAll(statusPanel, gameOverOverlay);
        setPickOnBounds(false);
        refresh();
    }

    public void setRestartHandler(Runnable restartHandler) {
        this.restartHandler = Objects.requireNonNull(restartHandler);
    }

    public void setGameOver(boolean gameOver) {
        gameOverOverlay.setVisible(gameOver);
        gameOverOverlay.setManaged(gameOver);
        if (gameOver) {
            gameOverLabel.setText("GAME OVER\nPuntaje: " + gameManager.getScore()
                    + "\nRécord: " + gameManager.getHighScore()
                    + "\nPresiona R o Enter para continuar");
        }
    }

    public void refresh() {
        playerStatus.setText("Ratón  |  Vidas: " + player.getLives());
        int seconds = (int) Math.ceil(gameManager.getBurrowSecondsRemaining());
        roundTimer.setText(String.format("Cuota en: %02d:%02d", seconds / 60, seconds % 60));
        scoreStatus.setText("Madrigueras: " + gameManager.getScore()
                + "  |  Récord: " + gameManager.getHighScore());
        healthStatus.setText("Salud: " + (int) player.getHealth() + "%");
        healthProgress.setProgress(player.getHealth() / 100.0);
        double fill = burrow.getFillPercent();
        burrowStatus.setText(String.format("Madriguera (%d/%d pts): %d%%",
                (int) burrow.getFoodPoints(), (int) burrow.getQuotaPoints(), (int) fill));
        burrowProgress.setProgress(fill / 100.0);
        if (gameManager.getHotPotato() != null) {
            potatoStatus.setText(String.format("PAPA CALIENTE: %.1f s",
                    gameManager.getHotPotato().getSecondsRemaining()));
            potatoStatus.setVisible(true);
        } else {
            potatoStatus.setText("");
            potatoStatus.setVisible(false);
        }
        alertStatus.setText(gameManager.isAlertVisible() ? gameManager.getAlertMessage() : "");
        alertStatus.setVisible(gameManager.isAlertVisible());
        controls.setText("WASD: mover  |  E: " + interactionHint());
    }

    private String interactionHint() {
        return player.getHeldFood() == null
                ? "agarrar comida"
                : player.getHeldFood() instanceof HotPotato
                        ? "E en la madriguera para soltarla"
                        : "E para soltar / entregar en madriguera";
    }
}
