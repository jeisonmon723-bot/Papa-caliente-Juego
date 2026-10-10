package com.tuempresa.ratonesgame.core;

import com.tuempresa.ratonesgame.model.Burrow;
import com.tuempresa.ratonesgame.model.Chef;
import com.tuempresa.ratonesgame.model.Player;
import com.tuempresa.ratonesgame.world.RestaurantMap;
import javafx.geometry.Point2D;

import java.util.List;
import java.util.function.Consumer;

public final class AttackSystem {
    public enum Phase {
        READY,
        WINDUP,
        STRIKING,
        RECOVERY
    }

    private static final double STRIKE_HALF_WIDTH = 22;
    private static final double STRIKE_MIN_REACH = 8;

    private final ChefConfig config;
    private final RestaurantMap map;
    private Phase phase = Phase.READY;
    private Point2D attackDirection = new Point2D(1, 0);
    private double phaseElapsed;
    private boolean hitRegistered;

    public AttackSystem(ChefConfig config, RestaurantMap map) {
        this.config = config;
        this.map = map;
    }

    public boolean begin(Chef chef, Player target) {
        if (phase != Phase.READY || !target.isActive()) {
            return false;
        }
        Point2D direction = new Point2D(target.getX() - chef.getX(), target.getY() - chef.getY());
        if (direction.magnitude() == 0) {
            return false;
        }
        attackDirection = direction.normalize();
        chef.faceToward(attackDirection.getX(), attackDirection.getY());
        phase = Phase.WINDUP;
        phaseElapsed = 0;
        hitRegistered = false;
        return true;
    }

    public void update(double deltaSeconds, Chef chef, List<Player> players,
                       Burrow burrow, Consumer<Player> onHit) {
        if (phase == Phase.READY) {
            return;
        }
        phaseElapsed += deltaSeconds;
        if (phase == Phase.WINDUP && phaseElapsed >= config.attackWindupSeconds()) {
            phase = Phase.STRIKING;
            phaseElapsed = 0;
        }
        if (phase == Phase.STRIKING) {
            if (!hitRegistered) {
                for (Player player : players) {
                    if (player.isActive() && !burrow.contains(player) && intersectsSweep(chef, player)) {
                        hitRegistered = true;
                        onHit.accept(player);
                        break;
                    }
                }
            }
            if (phaseElapsed >= config.attackActiveSeconds()) {
                phase = Phase.RECOVERY;
                phaseElapsed = 0;
            }
        } else if (phase == Phase.RECOVERY
                && phaseElapsed >= config.attackRecoverySeconds()) {
            phase = Phase.READY;
            phaseElapsed = 0;
        }
    }

    public boolean isBusy() {
        return phase != Phase.READY;
    }

    public boolean isStrikeActive() {
        return phase == Phase.STRIKING;
    }

    public Phase getPhase() {
        return phase;
    }

    public Point2D getAttackDirection() {
        return attackDirection;
    }

    private boolean intersectsSweep(Chef chef, Player player) {
        Point2D offset = new Point2D(player.getX() - chef.getX(), player.getY() - chef.getY());
        double forward = offset.dotProduct(attackDirection);
        double side = Math.abs(offset.getX() * attackDirection.getY()
                - offset.getY() * attackDirection.getX());
        return forward >= STRIKE_MIN_REACH
                && forward <= config.attackRange()
            && side <= STRIKE_HALF_WIDTH
            && map.hasLineOfSight(
                new Point2D(chef.getX(), chef.getY()),
                new Point2D(player.getX(), player.getY()));
    }
}