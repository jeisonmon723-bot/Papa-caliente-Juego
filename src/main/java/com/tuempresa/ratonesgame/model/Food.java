package com.tuempresa.ratonesgame.model;

public class Food {
    public enum Type {
        CHEESE,
        MEAT,
        BREAD,
        SAUSAGE,
        APPLE,
        FISH,
        MUSHROOM,
        CARROT,
        HOT_PIZZA,
        HOT_SOUP,
        HOT_ROAST,
        HOT_PASTA,
        HOT_FISH,
        HOT_PIE,
        HOT_VEGETABLES,
        HOT_BREAD,
        HOT_POTATO
    }

    private final Type type;
    private double temperatureCelsius;
    private final int foodPoints;
    private final boolean hot;

    public Food(Type type, double temperatureCelsius, double fillPercent) {
        this(type, temperatureCelsius, (int) Math.round(fillPercent),
                type.name().startsWith("HOT_") || temperatureCelsius >= 50);
    }

    public Food(Type type, double temperatureCelsius, int foodPoints, boolean hot) {
        if (type == null) {
            throw new IllegalArgumentException("Food type cannot be null");
        }
        this.type = type;
        this.temperatureCelsius = temperatureCelsius;
        this.foodPoints = Math.max(0, Math.min(15, foodPoints));
        this.hot = hot;
    }

    public boolean isHot() {
        return hot;
    }

    public Type getType() {
        return type;
    }

    public double getTemperatureCelsius() {
        return temperatureCelsius;
    }

    public void setTemperatureCelsius(double temperatureCelsius) {
        this.temperatureCelsius = Math.max(0, temperatureCelsius);
    }

    public void updateTemperature(double deltaSeconds) {
        if (hot) {
            temperatureCelsius = Math.max(20, temperatureCelsius - deltaSeconds * 3);
        }
    }

    public double getFillPercent() {
        return foodPoints;
    }

    public int getFoodPoints() {
        return foodPoints;
    }
}