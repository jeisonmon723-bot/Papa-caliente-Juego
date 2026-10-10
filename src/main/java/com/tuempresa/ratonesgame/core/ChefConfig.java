package com.tuempresa.ratonesgame.core;

public record ChefConfig(
        double fieldOfViewDegrees,
        double detectionDistance,
        double patrolSpeed,
        double chaseSpeed,
        double searchSpeed,
        double attackRange,
        double attackWindupSeconds,
        double attackActiveSeconds,
        double attackRecoverySeconds,
        double maximumSearchSeconds,
        double inspectionSeconds,
        double foodPreparationSeconds,
        double arrivalDistance,
        double unreachablePointSeconds) {

    public ChefConfig {
        if (fieldOfViewDegrees < 90 || fieldOfViewDegrees > 180
                || detectionDistance <= 0 || patrolSpeed <= 0 || chaseSpeed <= patrolSpeed
                || searchSpeed <= 0 || attackRange <= 0 || attackWindupSeconds <= 0
                || attackActiveSeconds <= 0 || attackRecoverySeconds <= 0
                || maximumSearchSeconds <= 0 || inspectionSeconds <= 0
                || foodPreparationSeconds <= 0 || arrivalDistance <= 0
                || unreachablePointSeconds <= 0) {
            throw new IllegalArgumentException("Chef configuration values are outside valid ranges");
        }
    }

    public static ChefConfig standard(double playerSpeed) {
        double safePlayerSpeed = Math.max(1, playerSpeed);
        return new ChefConfig(
                120,
                420,
                safePlayerSpeed * 0.72,
                safePlayerSpeed * 1.12,
                safePlayerSpeed * 0.82,
                58,
                0.58,
                0.22,
                0.82,
                4.5,
                1.25,
                3.0,
                12,
                2.0);
    }
}