package com.tuempresa.ratonesgame.model;

public final class Player extends Entity {
    private static final double MAX_HEAT = 100;
    private static final double HOT_FOOD_GRACE_SECONDS = 5;
    private static final double HOT_FOOD_DAMAGE_PER_SECOND = 5;

    private int lives = 3;
    private double health = MAX_HEAT;
    private Food heldFood;
    private boolean active = true;
    private boolean participating = true;
    private double hotFoodHoldSeconds;

    public Player(double x, double y) {
        super(x, y, 150);
    }

    public void holdFood(Food food) {
        heldFood = food;
        hotFoodHoldSeconds = 0;
    }

    public Food releaseFood() {
        Food food = heldFood;
        heldFood = null;
        hotFoodHoldSeconds = 0;
        return food;
    }

    public void updateHeat(double deltaSeconds) {
        if (!isActive() || heldFood == null) {
            return;
        }
        heldFood.updateTemperature(deltaSeconds);
        if (!heldFood.isHot()) {
            hotFoodHoldSeconds = 0;
            return;
        }

        double previousHoldSeconds = hotFoodHoldSeconds;
        hotFoodHoldSeconds += deltaSeconds;
        double damagingSeconds = Math.max(0,
                hotFoodHoldSeconds - HOT_FOOD_GRACE_SECONDS)
                - Math.max(0, previousHoldSeconds - HOT_FOOD_GRACE_SECONDS);
        if (damagingSeconds > 0) {
            health = Math.max(0, health - damagingSeconds * HOT_FOOD_DAMAGE_PER_SECOND);
            if (health == 0) {
                loseLife();
            }
        }
    }

    public int getLives() {
        return lives;
    }

    public double getHeat() {
        return MAX_HEAT - health;
    }

    public double getHealth() {
        return health;
    }

    public double getHotFoodHoldSeconds() {
        return hotFoodHoldSeconds;
    }

    public Food getHeldFood() {
        return heldFood;
    }

    public boolean isActive() {
        return active && participating;
    }

    public boolean isParticipating() {
        return participating;
    }

    public void setParticipating(boolean participating) {
        this.participating = participating;
        if (!participating) {
            super.setVelocity(0, 0, 0);
            heldFood = null;
            hotFoodHoldSeconds = 0;
        }
    }

    public boolean loseLife() {
        if (!isActive()) {
            return false;
        }
        lives = Math.max(0, lives - 1);
        health = MAX_HEAT;
        heldFood = null;
        hotFoodHoldSeconds = 0;
        if (lives == 0) {
            active = false;
            super.setVelocity(0, 0, 0);
        }
        return true;
    }

    public void resetForNewGame() {
        lives = 3;
        health = MAX_HEAT;
        active = true;
        participating = true;
        heldFood = null;
        hotFoodHoldSeconds = 0;
        super.setVelocity(0, 0, 0);
    }

    public boolean capture() {
        if (!isActive()) {
            return false;
        }
        active = false;
        lives = 0;
        heldFood = null;
        super.setVelocity(0, 0, 0);
        return true;
    }

    @Override
    public void setVelocity(double directionX, double directionY) {
        if (!isActive()) {
            super.setVelocity(0, 0, 0);
            return;
        }
        super.setVelocity(directionX, directionY);
    }

    @Override
    public void setVelocity(double directionX, double directionY, double movementSpeed) {
        if (!active) {
            super.setVelocity(0, 0, 0);
            return;
        }
        super.setVelocity(directionX, directionY, movementSpeed);
    }
}