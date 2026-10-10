package com.tuempresa.ratonesgame.controller;

import com.tuempresa.ratonesgame.core.GameManager;
import com.tuempresa.ratonesgame.model.Player;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.Set;

public final class InputController {
    private final Player player;
    private final GameManager gameManager;
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    public InputController(Player player, GameManager gameManager) {
        this.player = player;
        this.gameManager = gameManager;
    }

    public void handleKeyPressed(KeyEvent event) {
        if (!pressedKeys.add(event.getCode())) {
            return;
        }
        switch (event.getCode()) {
            case E -> gameManager.interact(player);
            default -> updateMovement();
        }
    }

    public void handleKeyReleased(KeyEvent event) {
        if (pressedKeys.remove(event.getCode())) {
            updateMovement();
        }
    }

    private void updateMovement() {
        player.setVelocity(
                axis(KeyCode.D, KeyCode.A),
                axis(KeyCode.S, KeyCode.W));
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