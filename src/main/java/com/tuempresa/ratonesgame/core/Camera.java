package com.tuempresa.ratonesgame.core;

import com.tuempresa.ratonesgame.model.Entity;

public final class Camera {
    private final double viewportWidth;
    private final double viewportHeight;
    private final double worldWidth;
    private final double worldHeight;
    private final double followSpeed;
    private final double zoom;
    private double centerX;
    private double centerY;

    public Camera(double viewportWidth, double viewportHeight,
                  double worldWidth, double worldHeight, double followSpeed) {
        this(viewportWidth, viewportHeight, worldWidth, worldHeight, followSpeed, 2.0);
    }

    public Camera(double viewportWidth, double viewportHeight,
                  double worldWidth, double worldHeight, double followSpeed, double zoom) {
        if (viewportWidth <= 0 || viewportHeight <= 0 || zoom < 1
                || worldWidth < viewportWidth / zoom || worldHeight < viewportHeight / zoom
                || followSpeed <= 0) {
            throw new IllegalArgumentException("Camera and world dimensions must be positive and valid");
        }
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.followSpeed = followSpeed;
        this.zoom = zoom;
        centerX = viewportWidth / 2;
        centerY = viewportHeight / 2;
    }

    public void follow(Entity target, double deltaSeconds) {
        double halfViewWidth = viewportWidth / (2 * zoom);
        double halfViewHeight = viewportHeight / (2 * zoom);
        double desiredX = clamp(target.getX(), halfViewWidth, worldWidth - halfViewWidth);
        double desiredY = clamp(target.getY(), halfViewHeight, worldHeight - halfViewHeight);
        double blend = 1 - Math.exp(-followSpeed * Math.max(0, deltaSeconds));
        centerX += (desiredX - centerX) * blend;
        centerY += (desiredY - centerY) * blend;
    }

    public double getOffsetX() {
        return centerX - getViewportWidth() / 2;
    }

    public double getOffsetY() {
        return centerY - getViewportHeight() / 2;
    }

    public double getViewportWidth() {
        return viewportWidth / zoom;
    }

    public double getViewportHeight() {
        return viewportHeight / zoom;
    }

    public double getZoom() {
        return zoom;
    }

    private double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(value, maximum));
    }
}