package com.tuempresa.ratonesgame.model;

public abstract class Entity {
    private double x;
    private double y;
    private double velocityX;
    private double velocityY;
    private final double speed;

    protected Entity(double x, double y, double speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public void update(double deltaSeconds) {
        x += velocityX * deltaSeconds;
        y += velocityY * deltaSeconds;
    }

    public void setVelocity(double directionX, double directionY) {
        setVelocity(directionX, directionY, speed);
    }

    public void setVelocity(double directionX, double directionY, double movementSpeed) {
        if (movementSpeed < 0) {
            throw new IllegalArgumentException("Movement speed cannot be negative");
        }
        double length = Math.hypot(directionX, directionY);
        if (length == 0 || movementSpeed == 0) {
            velocityX = 0;
            velocityY = 0;
            return;
        }
        velocityX = directionX / length * movementSpeed;
        velocityY = directionY / length * movementSpeed;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getSpeed() {
        return speed;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public boolean isMoving() {
        return velocityX != 0 || velocityY != 0;
    }
}