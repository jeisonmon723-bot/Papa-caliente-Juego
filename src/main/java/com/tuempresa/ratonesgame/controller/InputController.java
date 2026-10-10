package com.tuempresa.ratonesgame.controller;

import com.tuempresa.ratonesgame.model.Player;
import com.tuempresa.ratonesgame.core.GameManager;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.Set;

public final class InputController {
    private final Player firstPlayer;
    private final Player secondPlayer;
    private final GameManager gameManager;
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    public InputController(Player firstPlayer, Player secondPlayer, GameManager gameManager) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.gameManager = gameManager;
    }

    public void handleKeyPressed(KeyEvent event) {
        if (!pressedKeys.add(event.getCode())) {
            return;
        }
        switch (event.getCode()) {
            case E -> gameManager.interact(firstPlayer);
            case ENTER -> gameManager.interact(secondPlayer);
            default -> updateMovement();
        }
    }

    public void handleKeyReleased(KeyEvent event) {
        if (pressedKeys.remove(event.getCode())) {
            updateMovement();
        }
    }

    private void updateMovement() {
        firstPlayer.setVelocity(
                axis(KeyCode.D, KeyCode.A),
                axis(KeyCode.S, KeyCode.W));
        secondPlayer.setVelocity(
                axis(KeyCode.RIGHT, KeyCode.LEFT),
                axis(KeyCode.DOWN, KeyCode.UP));
    }

    private double axis(KeyCode positive, KeyCode negative) {
        return (pressedKeys.contains(positive) ? 1 : 0)
                - (pressedKeys.contains(negative) ? 1 : 0);
    }

    public void clearPressedKeys() {
        pressedKeys.clear();
        updateMovement();
    }
}