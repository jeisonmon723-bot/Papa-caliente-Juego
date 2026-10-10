package com.tuempresa.ratonesgame.core;

import com.tuempresa.ratonesgame.model.Burrow;
import com.tuempresa.ratonesgame.model.Chef;
import com.tuempresa.ratonesgame.model.Player;
import com.tuempresa.ratonesgame.world.RestaurantMap;
import javafx.geometry.Point2D;

public final class VisionSystem {
    private final ChefConfig config;
    private final RestaurantMap map;
    private final Burrow burrow;

    public VisionSystem(ChefConfig config, RestaurantMap map, Burrow burrow) {
        this.config = config;
        this.map = map;
        this.burrow = burrow;
    }

    public boolean canSee(Chef chef, Player player) {
        if (!player.isActive() || burrow.contains(player)) {
            return false;
        }
        Point2D toPlayer = new Point2D(player.getX() - chef.getX(), player.getY() - chef.getY());
        double distance = toPlayer.magnitude();
        if (distance == 0 || distance > config.detectionDistance()) {
            return false;
        }
        Point2D facing = new Point2D(chef.getFacingX(), chef.getFacingY());
        double cosine = facing.dotProduct(toPlayer.normalize());
        double minimumCosine = Math.cos(Math.toRadians(config.fieldOfViewDegrees() / 2));
        if (cosine < minimumCosine) {
            return false;
        }
        return map.hasLineOfSight(
                new Point2D(chef.getX(), chef.getY()),
                new Point2D(player.getX(), player.getY()));
    }
}