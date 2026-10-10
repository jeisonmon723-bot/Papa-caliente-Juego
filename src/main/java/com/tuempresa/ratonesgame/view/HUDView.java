package com.tuempresa.ratonesgame.view;

import com.tuempresa.ratonesgame.model.Burrow;
import com.tuempresa.ratonesgame.model.Player;
import com.tuempresa.ratonesgame.core.GameManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public final class HUDView extends StackPane {
    private final Player firstPlayer;
    private final Player secondPlayer;
    private final Burrow burrow;
    private final GameManager gameManager;
    private final Label firstPlayerStatus = new Label();
    private final Label secondPlayerStatus = new Label();
    private final Label roundTimer = new Label();
    private final Label scoreStatus = new Label();
    private final Label healthStatus = new Label();
    private final Label burrowStatus = new Label();
    private final Label potatoStatus = new Label();
    private final Label alertStatus = new Label();
    private final Label firstControls = new Label();
    private final Label secondControls = new Label();
    private final ProgressBar burrowProgress = new ProgressBar();
    private final ProgressBar firstHealth = new ProgressBar();
    private final ProgressBar secondHealth = new ProgressBar();
    private final StackPane captureOverlay = new StackPane();
    private final Label captureLabel = new Label();

    public HUDView(Player firstPlayer, Player secondPlayer, Burrow burrow,
                   GameManager gameManager) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.burrow = burrow;
        this.gameManager = gameManager;
        VBox statusPanel = new VBox(8, firstPlayerStatus, secondPlayerStatus,
                roundTimer, scoreStatus, healthStatus, firstHealth, secondHealth,
                burrowStatus, burrowProgress, potatoStatus, alertStatus,
                firstControls, secondControls);
        statusPanel.setPadding(new Insets(18));
        statusPanel.getStyleClass().add("hud");
        StackPane.setAlignment(statusPanel, Pos.TOP_LEFT);
        burrowProgress.setPrefWidth(280);
        firstHealth.setPrefWidth(280);
        secondHealth.setPrefWidth(280);
        firstHealth.getStyleClass().add("health-bar");
        secondHealth.getStyleClass().add("health-bar");
        firstControls.getStyleClass().add("controls-hint");
        secondControls.getStyleClass().add("controls-hint");
        captureLabel.getStyleClass().add("capture-label");
        captureOverlay.getStyleClass().add("capture-overlay");
        captureOverlay.getChildren().add(captureLabel);
        captureOverlay.setVisible(false);
        getChildren().addAll(statusPanel, captureOverlay);
        setPickOnBounds(false);
        refresh();
    }

    public void setCaptured(boolean captured) {
        captureOverlay.setVisible(captured);
        captureOverlay.setManaged(captured);
        if (captured) {
            captureLabel.setText("GAME OVER\nPuntaje: " + gameManager.getScore()
                + "\nRécord: " + gameManager.getHighScore());
        }
    }

    public void refresh() {
        firstPlayerStatus.setText(playerStatus("Ratón 1", firstPlayer));
        secondPlayerStatus.setText(playerStatus("Ratón 2", secondPlayer));
        int seconds = (int) Math.ceil(gameManager.getBurrowSecondsRemaining());
        roundTimer.setText(String.format("Cuota en: %02d:%02d", seconds / 60, seconds % 60));
        scoreStatus.setText("Madrigueras: " + gameManager.getScore()
            + "  |  Récord: " + gameManager.getHighScore());
        healthStatus.setText("Salud: " + (int) firstPlayer.getHealth() + "% / "
            + (secondPlayer.isParticipating() ? (int) secondPlayer.getHealth() + "%" : "1P"));
        firstHealth.setProgress(firstPlayer.getHealth() / 100.0);
        secondHealth.setProgress(secondPlayer.isParticipating()
            ? secondPlayer.getHealth() / 100.0 : 0);
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
        firstControls.setText("E: " + interactionHint(firstPlayer));
        secondControls.setText("ENTER: " + interactionHint(secondPlayer));
        secondControls.setVisible(secondPlayer.isParticipating());
        secondControls.setManaged(secondPlayer.isParticipating());
    }

    // E and Enter pick up food, then release it into the burrow when close enough.
    private String interactionHint(Player player) {
        return player.getHeldFood() == null
                ? "Agarrar comida"
                : "Soltar comida en la madriguera";
    }

    private String playerStatus(String name, Player player) {
        if (!player.isParticipating()) {
            return name + "  |  No participa";
        }
        return player.isActive()
                ? name + "  |  Vidas: " + player.getLives()
                : name + "  |  CAPTURADO";
    }
}