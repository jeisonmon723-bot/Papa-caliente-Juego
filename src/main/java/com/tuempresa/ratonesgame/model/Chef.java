package com.tuempresa.ratonesgame.model;

import java.util.List;
import java.util.function.IntSupplier;

public final class Chef extends Entity {
    private static final double FOOD_PREPARATION_SECONDS = 3;
    private static final List<Food.Type> HOT_FOOD_CYCLE = List.of(
            Food.Type.HOT_PIZZA,
            Food.Type.HOT_SOUP,
            Food.Type.HOT_ROAST,
            Food.Type.HOT_PASTA,
            Food.Type.HOT_FISH,
            Food.Type.HOT_PIE,
            Food.Type.HOT_VEGETABLES,
            Food.Type.HOT_BREAD);

    public enum State {
        PREPARING_FOOD,
        PATROLLING,
        INSPECTING_TRAP,
        CHASING,
        SEARCHING,
        ATTACKING,
        RETURNING_TO_WORK
    }

    private State state = State.PREPARING_FOOD;
    private final double spawnX;
    private final double spawnY;
    private double facingX = 1;
    private double facingY;
    private double foodPreparationElapsed;
    private int nextHotFoodIndex;
    private Food preparedFood;

    public Chef(double x, double y) {
        super(x, y, 110);
        spawnX = x;
        spawnY = y;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        if (state == null) {
            throw new IllegalArgumentException("Chef state cannot be null");
        }
        this.state = state;
    }

    public void faceToward(double directionX, double directionY) {
        double length = Math.hypot(directionX, directionY);
        if (length > 0) {
            facingX = directionX / length;
            facingY = directionY / length;
        }
    }

    public double getFacingX() {
        return facingX;
    }

    public double getFacingY() {
        return facingY;
    }

    public void updateFoodPreparation(double deltaSeconds, IntSupplier foodPointsSupplier) {
        if (state != State.PREPARING_FOOD || preparedFood != null) {
            return;
        }
        foodPreparationElapsed += deltaSeconds;
        if (foodPreparationElapsed >= FOOD_PREPARATION_SECONDS) {
            Food.Type type = HOT_FOOD_CYCLE.get(nextHotFoodIndex);
            nextHotFoodIndex = (nextHotFoodIndex + 1) % HOT_FOOD_CYCLE.size();
            preparedFood = new Food(type, 95, foodPointsSupplier.getAsInt(), true);
            foodPreparationElapsed = 0;
        }
    }

    public Food stealPreparedFood() {
        Food stolenFood = preparedFood;
        preparedFood = null;
        foodPreparationElapsed = 0;
        return stolenFood;
    }

    public boolean hasPreparedFood() {
        return preparedFood != null;
    }

    public Food getPreparedFood() {
        return preparedFood;
    }

    public void resetForNewGame() {
        setPosition(spawnX, spawnY);
        setVelocity(0, 0);
        state = State.PREPARING_FOOD;
        facingX = 1;
        facingY = 0;
        foodPreparationElapsed = 0;
        nextHotFoodIndex = 0;
        preparedFood = null;
    }
}