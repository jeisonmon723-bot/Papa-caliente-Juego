package com.tuempresa.ratonesgame.model;

public final class Burrow {
    private double x;
    private double y;
    private final double radius;
    private double foodPoints;
    private double quotaPoints = 100;

    public Burrow(double x, double y, double radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Burrow radius must be positive");
        }
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public boolean contains(Entity entity) {
        return Math.hypot(entity.getX() - x, entity.getY() - y) <= radius;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }

    public void addFood(Food food) {
        foodPoints = Math.min(quotaPoints, foodPoints + food.getFoodPoints());
    }

    public double getFillPercent() {
        return quotaPoints == 0 ? 0 : Math.min(100, foodPoints / quotaPoints * 100);
    }

    public double getFoodPoints() {
        return foodPoints;
    }

    public double getQuotaPoints() {
        return quotaPoints;
    }

    public boolean isComplete() {
        return foodPoints >= quotaPoints;
    }

    public void fillCompletely() {
        foodPoints = quotaPoints;
    }

    public void resetAt(double x, double y, double quotaPoints) {
        if (quotaPoints <= 0) {
            throw new IllegalArgumentException("Burrow quota must be positive");
        }
        this.x = x;
        this.y = y;
        this.quotaPoints = quotaPoints;
        foodPoints = 0;
    }

    public void empty() {
        foodPoints = 0;
    }
}