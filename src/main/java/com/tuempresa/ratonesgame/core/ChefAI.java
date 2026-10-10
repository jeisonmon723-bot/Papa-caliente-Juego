package com.tuempresa.ratonesgame.core;

import com.tuempresa.ratonesgame.model.Burrow;
import com.tuempresa.ratonesgame.model.Chef;
import com.tuempresa.ratonesgame.model.Player;
import com.tuempresa.ratonesgame.model.Trap;
import com.tuempresa.ratonesgame.world.RestaurantMap;
import javafx.geometry.Point2D;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class ChefAI {
    private static final double CHEF_COLLISION_RADIUS = 14;
    private static final double REPATH_INTERVAL_SECONDS = 0.35;

    private final Chef chef;
    private final ChefConfig config;
    private final RestaurantMap map;
    private final Burrow burrow;
    private final VisionSystem vision;
    private final AttackSystem attack;
    private Consumer<Player> onCapture = ignored -> { };

    private Chef.State state = Chef.State.RETURNING_TO_WORK;
    private Player target;
    private Point2D lastKnownPosition;
    private Point2D navigationTarget;
    private List<Point2D> currentPath = List.of();
    private int pathIndex;
    private double repathTimer;
    private double stateElapsed;
    private double searchElapsed;
    private boolean searchInspectionStarted;
    private int routeIndex;
    private int workstationIndex;
    private Trap trapEvent;
    private Trap inspectedTrap;

    public ChefAI(Chef chef, ChefConfig config, RestaurantMap map,
                  Burrow burrow) {
        this.chef = Objects.requireNonNull(chef);
        this.config = Objects.requireNonNull(config);
        this.map = Objects.requireNonNull(map);
        this.burrow = Objects.requireNonNull(burrow);
        vision = new VisionSystem(config, map, burrow);
        attack = new AttackSystem(config, map);
        chef.setState(state);
    }

    public void update(double deltaSeconds, List<Player> players) {
        if (deltaSeconds < 0) {
            throw new IllegalArgumentException("deltaSeconds cannot be negative");
        }
        List<Player> activePlayers = players.stream().filter(Player::isActive).toList();

        if (attack.isBusy()) {
            transitionTo(Chef.State.ATTACKING);
            attack.update(deltaSeconds, chef, activePlayers, burrow, this::capturePlayer);
            stateElapsed += deltaSeconds;
            if (attack.isBusy()) {
                return;
            }
        }

        Player visibleTarget = chooseVisibleTarget(activePlayers);
        if (visibleTarget != null) {
            target = visibleTarget;
            lastKnownPosition = positionOf(visibleTarget);
            searchElapsed = 0;
            searchInspectionStarted = false;
            trapEvent = null;
            chaseTarget(visibleTarget, deltaSeconds);
            return;
        }

        if (target != null && target.isActive()) {
            searchForLostTarget(deltaSeconds);
            return;
        }
        target = null;

        if (trapEvent != null) {
            investigateTrapEvent(deltaSeconds);
            return;
        }

        runRoutine(deltaSeconds);
    }

    public void notifyTrapActivated(Trap trap) {
        if (trap != null && trap.isActive()) {
            trapEvent = trap;
            if (state != Chef.State.ATTACKING && target == null) {
                transitionTo(Chef.State.INSPECTING_TRAP);
            }
        }
    }

    public void setCaptureHandler(Consumer<Player> onCapture) {
        this.onCapture = Objects.requireNonNull(onCapture);
    }

    public boolean isActivelyChasing(Player player) {
        return state == Chef.State.CHASING && target == player && player.isActive();
    }

    public Chef.State getState() {
        return state;
    }

    public Player getTarget() {
        return target;
    }

    public Point2D getLastKnownPosition() {
        return lastKnownPosition;
    }

    public AttackSystem getAttackSystem() {
        return attack;
    }

    public boolean isPursuitActive() {
        return state == Chef.State.CHASING
                || (state == Chef.State.ATTACKING && target != null && target.isActive());
    }

    public Trap getInspectedTrap() {
        return inspectedTrap;
    }

    public void stop() {
        chef.setVelocity(0, 0);
    }

    private Player chooseVisibleTarget(List<Player> players) {
        List<Player> visible = vision.visiblePlayers(chef, players);
        if (target != null && target.isActive() && visible.contains(target)) {
            return target;
        }
        return visible.stream()
                .min((first, second) -> Double.compare(
                        distanceTo(first), distanceTo(second)))
                .orElse(null);
    }

    private void chaseTarget(Player player, double deltaSeconds) {
        double distance = distanceTo(player);
        faceToward(player.getX(), player.getY());
        if (distance <= config.attackRange()) {
            chef.setVelocity(0, 0);
            transitionTo(Chef.State.ATTACKING);
            if (!attack.begin(chef, player)) {
                transitionTo(Chef.State.CHASING);
            }
            return;
        }
        transitionTo(Chef.State.CHASING);
        moveToward(positionOf(player), config.chaseSpeed(), deltaSeconds);
        stateElapsed += deltaSeconds;
    }

    private void searchForLostTarget(double deltaSeconds) {
        transitionTo(Chef.State.SEARCHING);
        searchElapsed += deltaSeconds;
        if (searchElapsed >= config.maximumSearchSeconds()) {
            clearTargetAndReturnToWork();
            return;
        }
        if (!searchInspectionStarted && moveToward(lastKnownPosition, config.searchSpeed(), deltaSeconds)) {
            searchInspectionStarted = true;
            stateElapsed = 0;
            chef.setVelocity(0, 0);
        } else if (searchInspectionStarted) {
            chef.setVelocity(0, 0);
            stateElapsed += deltaSeconds;
            double scanAngle = Math.sin(stateElapsed * 2.8) * Math.PI * 0.65;
            double baseAngle = Math.atan2(chef.getFacingY(), chef.getFacingX());
            chef.faceToward(Math.cos(baseAngle + scanAngle), Math.sin(baseAngle + scanAngle));
            if (stateElapsed >= config.inspectionSeconds()) {
                clearTargetAndReturnToWork();
            }
        }
    }

    private void investigateTrapEvent(double deltaSeconds) {
        transitionTo(Chef.State.INSPECTING_TRAP);
        if (!moveToward(trapEvent.getPosition(), config.patrolSpeed(), deltaSeconds)) {
            stateElapsed += deltaSeconds;
            return;
        }
        chef.setVelocity(0, 0);
        stateElapsed += deltaSeconds;
        inspectedTrap = trapEvent;
        if (stateElapsed >= config.inspectionSeconds()) {
            trapEvent = null;
            advanceRoute();
            transitionTo(Chef.State.PATROLLING);
        }
    }

    private void runRoutine(double deltaSeconds) {
        if (state == Chef.State.RETURNING_TO_WORK) {
            Point2D workstation = map.getWorkstations().get(workstationIndex);
            if (moveToward(workstation, config.patrolSpeed(), deltaSeconds)) {
                chef.setVelocity(0, 0);
                transitionTo(Chef.State.PREPARING_FOOD);
            } else {
                stateElapsed += deltaSeconds;
                skipUnreachableRoutinePointIfNeeded();
            }
            return;
        }

        if (state == Chef.State.PREPARING_FOOD) {
            chef.setVelocity(0, 0);
            stateElapsed += deltaSeconds;
            if (stateElapsed >= config.foodPreparationSeconds()) {
                routeIndex = 1 % map.getPatrolRoute().size();
                transitionTo(Chef.State.PATROLLING);
            }
            return;
        }

        if (state == Chef.State.INSPECTING_TRAP && trapEvent == null) {
            updatePatrolTrapInspection(deltaSeconds);
            return;
        }

        transitionTo(Chef.State.PATROLLING);
        List<RestaurantMap.RoutePoint> route = map.getPatrolRoute();
        RestaurantMap.RoutePoint point = route.get(routeIndex);
        if (!moveToward(point.position(), config.patrolSpeed(), deltaSeconds)) {
            stateElapsed += deltaSeconds;
            skipUnreachableRoutinePointIfNeeded();
            return;
        }

        chef.setVelocity(0, 0);
        if (point.action() == RestaurantMap.RouteAction.WORKSTATION) {
            transitionTo(Chef.State.PREPARING_FOOD);
        } else if (point.action() == RestaurantMap.RouteAction.INSPECT_TRAP) {
            inspectedTrap = point.trap();
            transitionTo(Chef.State.INSPECTING_TRAP);
        } else {
            stateElapsed += deltaSeconds;
            if (stateElapsed >= config.inspectionSeconds()) {
                advanceRoute();
                stateElapsed = 0;
            }
        }
    }

    private void updatePatrolTrapInspection(double deltaSeconds) {
        RestaurantMap.RoutePoint point = map.getPatrolRoute().get(routeIndex);
        if (!moveToward(point.position(), config.patrolSpeed(), deltaSeconds)) {
            stateElapsed += deltaSeconds;
            skipUnreachableRoutinePointIfNeeded();
            return;
        }
        chef.setVelocity(0, 0);
        inspectedTrap = point.trap();
        stateElapsed += deltaSeconds;
        if (stateElapsed >= config.inspectionSeconds()) {
            advanceRoute();
            transitionTo(Chef.State.PATROLLING);
        }
    }

    private void skipUnreachableRoutinePointIfNeeded() {
        if (stateElapsed < config.unreachablePointSeconds()) {
            return;
        }
        stateElapsed = 0;
        advanceRoute();
        transitionTo(Chef.State.PATROLLING);
    }

    private void clearTargetAndReturnToWork() {
        target = null;
        lastKnownPosition = null;
        searchElapsed = 0;
        searchInspectionStarted = false;
        workstationIndex = (workstationIndex + 1) % map.getWorkstations().size();
        transitionTo(Chef.State.RETURNING_TO_WORK);
    }

    private void capturePlayer(Player player) {
        onCapture.accept(player);
    }

    private boolean moveToward(Point2D destination, double speed, double deltaSeconds) {
        if (destination == null) {
            chef.setVelocity(0, 0);
            return false;
        }
        Point2D chefPosition = new Point2D(chef.getX(), chef.getY());
        double distance = chefPosition.distance(destination);
        if (distance <= config.arrivalDistance()) {
            chef.setVelocity(0, 0);
            return true;
        }

        repathTimer -= deltaSeconds;
        boolean targetCellChanged = navigationTarget == null
                || cell(navigationTarget.getX()) != cell(destination.getX())
                || cell(navigationTarget.getY()) != cell(destination.getY());
        if (targetCellChanged || repathTimer <= 0 || pathIndex >= currentPath.size()) {
            navigationTarget = destination;
            currentPath = map.findPath(chefPosition, destination, CHEF_COLLISION_RADIUS);
            pathIndex = 0;
            repathTimer = REPATH_INTERVAL_SECONDS;
        }

        while (pathIndex < currentPath.size()
                && chefPosition.distance(currentPath.get(pathIndex)) <= config.arrivalDistance()) {
            pathIndex++;
        }
        Point2D waypoint = pathIndex < currentPath.size()
                ? currentPath.get(pathIndex) : destination;
        Point2D direction = waypoint.subtract(chefPosition);
        if (direction.magnitude() == 0) {
            chef.setVelocity(0, 0);
            return false;
        }
        chef.faceToward(direction.getX(), direction.getY());
        chef.setVelocity(direction.getX(), direction.getY(), speed);
        return false;
    }

    private void transitionTo(Chef.State nextState) {
        if (state == nextState) {
            return;
        }
        state = nextState;
        chef.setState(nextState);
        stateElapsed = 0;
        navigationTarget = null;
        currentPath = List.of();
        pathIndex = 0;
        repathTimer = 0;
    }

    private void advanceRoute() {
        routeIndex = (routeIndex + 1) % map.getPatrolRoute().size();
    }

    private void faceToward(double x, double y) {
        chef.faceToward(x - chef.getX(), y - chef.getY());
    }

    private double distanceTo(Player player) {
        return Math.hypot(chef.getX() - player.getX(), chef.getY() - player.getY());
    }

    private Point2D positionOf(Player player) {
        return new Point2D(player.getX(), player.getY());
    }

    private int cell(double coordinate) {
        return (int) (coordinate / RestaurantMap.PATH_GRID_SIZE);
    }
}