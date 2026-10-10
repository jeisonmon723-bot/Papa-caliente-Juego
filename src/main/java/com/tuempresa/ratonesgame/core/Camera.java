package com.tuempresa.ratonesgame.core;

import com.tuempresa.ratonesgame.model.Entity;

public final class Camera {
    private final double viewportWidth;
    private final double viewportHeight;
    private final double worldWidth;
    private final double worldHeight;
    private final double followSpeed;
    private double centerX;
    private double centerY;

    public Camera(double viewportWidth, double viewportHeight,
                  double worldWidth, double worldHeight, double followSpeed) {
        if (viewportWidth <= 0 || viewportHeight <= 0
                || worldWidth < viewportWidth || worldHeight < viewportHeight
                || followSpeed <= 0) {
            throw new IllegalArgumentException("Camera and world dimensions must be positive and valid");
        }
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.followSpeed = followSpeed;
        centerX = viewportWidth / 2;
        centerY = viewportHeight / 2;
    }

    public void follow(Entity target, double deltaSeconds) {
        double desiredX = clamp(target.getX(), viewportWidth / 2, worldWidth - viewportWidth / 2);
        double desiredY = clamp(target.getY(), viewportHeight / 2, worldHeight - viewportHeight / 2);
        double blend = 1 - Math.exp(-followSpeed * Math.max(0, deltaSeconds));
        centerX += (desiredX - centerX) * blend;
        centerY += (desiredY - centerY) * blend;
    }

    public double getOffsetX() {
        return centerX - viewportWidth / 2;
    }

    public double getOffsetY() {
        return centerY - viewportHeight / 2;
    }

    public double getViewportWidth() {
        return viewportWidth;
    }

    public double getViewportHeight() {
        return viewportHeight;
    }

    private double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(value, maximum));
    }
}