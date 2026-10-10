package com.tuempresa.ratonesgame.model;

import javafx.geometry.Point2D;

import java.util.Objects;
import java.util.function.Consumer;

public final class Trap {
    public enum State {
        ACTIVE,
        DISABLED
    }

    private final String id;
    private final Point2D position;
    private final double radius;
    private final State initialState;
    private State state;
    private Consumer<Trap> activationListener = ignored -> { };

    public Trap(String id, Point2D position, double radius, State initialState) {
        if (id == null || id.isBlank() || position == null || radius <= 0 || initialState == null) {
            throw new IllegalArgumentException("Trap requires an id, position, radius, and state");
        }
        this.id = id;
        this.position = position;
        this.radius = radius;
        this.initialState = initialState;
        this.state = initialState;
    }

    public String getId() {
        return id;
    }

    public Point2D getPosition() {
        return position;
    }

    public double getRadius() {
        return radius;
    }

    public State getState() {
        return state;
    }

    public boolean isActive() {
        return state == State.ACTIVE;
    }

    public void setState(State state) {
        if (state == null) {
            throw new IllegalArgumentException("Trap state cannot be null");
        }
        this.state = state;
    }

    public void reset() {
        state = initialState;
    }

    public void setActivationListener(Consumer<Trap> activationListener) {
        this.activationListener = Objects.requireNonNull(activationListener);
    }

    public boolean trigger() {
        if (!isActive()) {
            return false;
        }
        activationListener.accept(this);
        return true;
    }
}