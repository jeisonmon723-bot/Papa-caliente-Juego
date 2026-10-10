package com.tuempresa.ratonesgame.model;

public final class Player extends Entity {
    private static final double MAX_HEAT = 100;
    private static final double HOT_FOOD_GRACE_SECONDS = 5;
    private static final double HOT_FOOD_DAMAGE_PER_SECOND = 5;
    private static final double HOT_POTATO_GRACE_SECONDS = 3;
    private static final double HOT_POTATO_DAMAGE_PER_SECOND = 4;
    private static final double MAX_CARRY_SPEED_PENALTY = 0.45;

    private final double spawnX;
    private final double spawnY;
    private int lives = 3;
    private double health = MAX_HEAT;
    private Food heldFood;
    private boolean active = true;
    private double hotFoodHoldSeconds;
    private double requestedMovementSpeed;

    public Player(double x, double y) {
        super(x, y, 150);
        spawnX = x;
        spawnY = y;
        requestedMovementSpeed = getSpeed();
    }

    public void holdFood(Food food) {
        heldFood = food;
        hotFoodHoldSeconds = 0;
        updateCarryingSpeed();
    }

    public Food releaseFood() {
        Food food = heldFood;
        heldFood = null;
        hotFoodHoldSeconds = 0;
        updateCarryingSpeed();
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

        boolean holdingHotPotato = heldFood instanceof HotPotato;
        double graceSeconds = holdingHotPotato
                ? HOT_POTATO_GRACE_SECONDS : HOT_FOOD_GRACE_SECONDS;
        double damagePerSecond = holdingHotPotato
                ? HOT_POTATO_DAMAGE_PER_SECOND : HOT_FOOD_DAMAGE_PER_SECOND;
        double previousHoldSeconds = hotFoodHoldSeconds;
        hotFoodHoldSeconds += deltaSeconds;
        double damagingSeconds = Math.max(0,
                hotFoodHoldSeconds - graceSeconds)
                - Math.max(0, previousHoldSeconds - graceSeconds);
        if (damagingSeconds > 0) {
            health = Math.max(0, health - damagingSeconds * damagePerSecond);
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
        return active;
    }

    public boolean loseLife() {
        if (!isActive()) {
            return false;
        }
        lives = Math.max(0, lives - 1);
        health = MAX_HEAT;
        heldFood = null;
        hotFoodHoldSeconds = 0;
        updateCarryingSpeed();
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
        heldFood = null;
        hotFoodHoldSeconds = 0;
        requestedMovementSpeed = getSpeed();
        setPosition(spawnX, spawnY);
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
        setVelocity(directionX, directionY, getSpeed());
    }

    @Override
    public void setVelocity(double directionX, double directionY, double movementSpeed) {
        if (!isActive()) {
            super.setVelocity(0, 0, 0);
            return;
        }
        super.setVelocity(directionX, directionY,
                movementSpeed * carryingSpeedMultiplier());
        requestedMovementSpeed = movementSpeed;
    }

    private void updateCarryingSpeed() {
        double velocityX = getVelocityX();
        double velocityY = getVelocityY();
        if (velocityX != 0 || velocityY != 0) {
            super.setVelocity(velocityX, velocityY,
                    requestedMovementSpeed * carryingSpeedMultiplier());
        }
    }

    // Food points represent the portion size; larger portions slow the mouse proportionally.
    private double carryingSpeedMultiplier() {
        if (heldFood == null) {
            return 1;
        }
        double portionSize = heldFood.getFoodPoints() / 15.0;
        return 1 - Math.min(1, portionSize) * MAX_CARRY_SPEED_PENALTY;
    }

}