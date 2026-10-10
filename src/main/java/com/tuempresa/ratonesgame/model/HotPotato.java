package com.tuempresa.ratonesgame.model;

public final class HotPotato extends Food {
    public static final double FUSE_SECONDS = 30;

    private double secondsRemaining = FUSE_SECONDS;
    private boolean exploded;

    public HotPotato() {
        super(Type.HOT_POTATO, 100, 0, true);
    }

    public void updateFuse(double deltaSeconds) {
        if (exploded) {
            return;
        }
        secondsRemaining = Math.max(0, secondsRemaining - deltaSeconds);
        if (secondsRemaining == 0) {
            exploded = true;
        }
    }

    public double getSecondsRemaining() {
        return secondsRemaining;
    }

    public boolean isExploded() {
        return exploded;
    }
}