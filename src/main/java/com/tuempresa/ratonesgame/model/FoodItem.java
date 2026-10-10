package com.tuempresa.ratonesgame.model;

import javafx.geometry.Point2D;

public final class FoodItem {
    private Food food;
    private final Food.Type foodType;
    private final double initialTemperature;
    private final int foodPoints;
    private final boolean hot;
    private Point2D position;
    private boolean available = true;
    private double respawnRemaining;

    public FoodItem(Food food, Point2D position) {
        if (food == null || position == null) {
            throw new IllegalArgumentException("Food item needs food and a position");
        }
        this.food = food;
        foodType = food.getType();
        initialTemperature = food.getTemperatureCelsius();
        foodPoints = food.getFoodPoints();
        hot = food.isHot();
        this.position = position;
    }

    public Food getFood() {
        return food;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        if (position == null) {
            throw new IllegalArgumentException("Food item position cannot be null");
        }
        this.position = position;
    }

    public boolean isAvailable() {
        return available;
    }

    public Food take(double respawnSeconds) {
        if (!available) {
            return null;
        }
        available = false;
        respawnRemaining = Math.max(0, respawnSeconds);
        Food pickedFood = food;
        food = null;
        return pickedFood;
    }

    public boolean updateRespawn(double deltaSeconds) {
        if (available || respawnRemaining <= 0) {
            return false;
        }
        respawnRemaining = Math.max(0, respawnRemaining - deltaSeconds);
        if (respawnRemaining == 0) {
            food = new Food(foodType, initialTemperature, foodPoints, hot);
            available = true;
            return true;
        }
        return false;
    }
}