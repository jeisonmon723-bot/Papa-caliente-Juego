package com.tuempresa.ratonesgame.controller;

import com.tuempresa.ratonesgame.core.GameManager;
import com.tuempresa.ratonesgame.model.Chef;
import com.tuempresa.ratonesgame.model.Player;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.Set;

public final class InputController {
    private final Player firstPlayer;
    private final Chef chef;
    private final GameManager gameManager;
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    public InputController(Player firstPlayer, Chef chef, GameManager gameManager) {
        this.firstPlayer = firstPlayer;
        this.chef = chef;
        this.gameManager = gameManager;
    }

    public void handleKeyPressed(KeyEvent event) {
        if (!pressedKeys.add(event.getCode())) {
            return;
        }
        switch (event.getCode()) {
            case E -> gameManager.interact(firstPlayer);
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
        if (gameManager.getGameMode() == GameManager.GameMode.TWO_PLAYERS) {
            double chefX = axis(KeyCode.RIGHT, KeyCode.LEFT);
            double chefY = axis(KeyCode.DOWN, KeyCode.UP);
            chef.setVelocity(chefX, chefY);
            if (chefX != 0 || chefY != 0) {
                chef.faceToward(chefX, chefY);
            }
        } else {
            chef.setVelocity(0, 0);
        }
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