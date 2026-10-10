package com.tuempresa.ratonesgame.core;

import com.tuempresa.ratonesgame.model.Burrow;
import com.tuempresa.ratonesgame.model.Chef;
import com.tuempresa.ratonesgame.model.Player;
import com.tuempresa.ratonesgame.model.Trap;
import com.tuempresa.ratonesgame.world.RestaurantMap;
import javafx.geometry.Point2D;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class ChefIntegrationChecks {
    private ChefIntegrationChecks() {
    }

    public static void main(String[] args) {
        visionChecksDirectionAndOcclusion();
        pathfindingRoutesAroundFurniture();
        chefTargetsTheSinglePlayer();
        chefSearchesAndResumesWhenVisionReturns();
        resettingChefAIStopsThePreviousPursuit();
        broomAttackHasWindupAndSingleHit();
        broomAttackCanMiss();
        chasedBurrowEntryRuleRequiresNewEntry();
        trapEventsRespectTrapState();
        System.out.println("Chef integration checks passed (9)");
    }

    private static void visionChecksDirectionAndOcclusion() {
        RestaurantMap map = new RestaurantMap();
        Burrow burrow = new Burrow(160, 880, 38);
        VisionSystem vision = new VisionSystem(ChefConfig.standard(150), map, burrow);
        Chef chef = new Chef(500, 360);
        chef.faceToward(1, 0);
        Player inFront = new Player(600, 360);
        Player behind = new Player(400, 360);
        check(vision.canSee(chef, inFront), "chef should see a player in front");
        check(!vision.canSee(chef, behind), "chef should not see a player behind");

        chef.setPosition(860, 280);
        Player behindTable = new Player(1100, 280);
        check(!vision.canSee(chef, behindTable), "a table should block vision");
    }

    private static void pathfindingRoutesAroundFurniture() {
        RestaurantMap map = new RestaurantMap();
        List<Point2D> route = map.findPath(new Point2D(860, 280), new Point2D(1100, 280), 14);
        check(!route.isEmpty(), "path around dining table should exist");
        for (Point2D waypoint : route) {
            check(map.canOccupy(waypoint.getX(), waypoint.getY(), 14),
                    "path waypoint must not overlap a solid obstacle");
        }
    }

    private static void chefTargetsTheSinglePlayer() {
        RestaurantMap map = new RestaurantMap();
        Burrow burrow = new Burrow(160, 880, 38);
        Chef chef = new Chef(500, 360);
        Player player = new Player(640, 360);
        ChefAI ai = new ChefAI(chef, ChefConfig.standard(player.getSpeed()), map, burrow);

        ai.update(1.0 / 60, player);

        check(ai.getState() == Chef.State.CHASING, "chef should enter chase state");
        check(ai.getTarget() == player, "chef should target the only player");
        check(Math.hypot(chef.getVelocityX(), chef.getVelocityY()) > player.getSpeed(),
                "chase speed should exceed player speed");
    }

    private static void chefSearchesAndResumesWhenVisionReturns() {
        RestaurantMap map = new RestaurantMap();
        Burrow burrow = new Burrow(160, 880, 38);
        Chef chef = new Chef(500, 360);
        Player player = new Player(600, 360);
        ChefAI ai = new ChefAI(chef, ChefConfig.standard(150), map, burrow);

        ai.update(1.0 / 60, player);
        check(ai.getState() == Chef.State.CHASING, "visible player should be chased");
        player.setPosition(1200, 360);
        ai.update(1.0 / 60, player);
        check(ai.getState() == Chef.State.SEARCHING, "lost player should trigger bounded search");
        check(ai.getLastKnownPosition().equals(new Point2D(600, 360)),
                "search should use only last observed position");
        player.setPosition(620, 360);
        ai.update(1.0 / 60, player);
        check(ai.getState() == Chef.State.CHASING && ai.getTarget() == player,
                "regained line of sight should resume pursuit");
    }

    private static void resettingChefAIStopsThePreviousPursuit() {
        RestaurantMap map = new RestaurantMap();
        Burrow burrow = new Burrow(160, 880, 38);
        Chef chef = new Chef(500, 360);
        Player player = new Player(600, 360);
        ChefAI ai = new ChefAI(chef, ChefConfig.standard(player.getSpeed()), map, burrow);
        ai.update(1.0 / 60, player);
        check(ai.getTarget() == player, "Chef should have an active target before reset");

        ai.reset();

        check(ai.getTarget() == null && ai.getLastKnownPosition() == null,
                "reset should clear the Chef's previous target and search position");
        check(ai.getState() == Chef.State.RETURNING_TO_WORK
                        && !chef.isMoving()
                        && ai.getAttackSystem().getPhase() == AttackSystem.Phase.READY,
                "reset should stop movement and clear any pending attack");
    }

    private static void broomAttackHasWindupAndSingleHit() {
        RestaurantMap map = new RestaurantMap();
        ChefConfig config = ChefConfig.standard(150);
        AttackSystem attack = new AttackSystem(config, map);
        Chef chef = new Chef(500, 500);
        Player target = new Player(540, 500);
        Burrow burrow = new Burrow(160, 880, 38);
        AtomicInteger hits = new AtomicInteger();

        check(attack.begin(chef, target), "attack should start for a valid target");
        check(attack.getPhase() == AttackSystem.Phase.WINDUP, "attack must begin with windup");
        attack.update(config.attackWindupSeconds(), chef, target, burrow,
                player -> {
                    if (player.capture()) {
                        hits.incrementAndGet();
                    }
                });
        check(attack.isStrikeActive(), "attack should expose an active strike window");
        attack.update(0.05, chef, target, burrow,
                player -> {
                    if (player.capture()) {
                        hits.incrementAndGet();
                    }
                });
        attack.update(0.05, chef, target, burrow,
                player -> {
                    if (player.capture()) {
                        hits.incrementAndGet();
                    }
                });
        check(hits.get() == 1, "one swing must produce at most one hit");
        check(!target.isActive(), "valid strike should capture only the target");
    }

    private static void broomAttackCanMiss() {
        RestaurantMap map = new RestaurantMap();
        ChefConfig config = ChefConfig.standard(150);
        AttackSystem attack = new AttackSystem(config, map);
        Chef chef = new Chef(500, 500);
        Player target = new Player(530, 500);
        Burrow burrow = new Burrow(160, 880, 38);
        AtomicInteger hits = new AtomicInteger();

        check(attack.begin(chef, target), "a target in range should allow windup");
        target.setPosition(530, 535);
        attack.update(config.attackWindupSeconds(), chef, target, burrow,
                ignored -> hits.incrementAndGet());

        check(hits.get() == 0 && target.isActive(),
                "a player outside the broom's narrow sweep should evade the attack");
    }

    private static void chasedBurrowEntryRuleRequiresNewEntry() {
        check(GameEngine.shouldEmptyBurrowOnChasedEntry(false, true, true),
                "new burrow entry during chase should empty it");
        check(!GameEngine.shouldEmptyBurrowOnChasedEntry(false, true, false),
                "entry without pursuit must preserve fill");
        check(!GameEngine.shouldEmptyBurrowOnChasedEntry(true, true, true),
                "remaining inside must not repeatedly trigger emptying");
        check(!GameEngine.shouldEmptyBurrowOnChasedEntry(false, false, true),
                "not entering must not empty the burrow");

        Burrow burrow = new Burrow(160, 880, 38);
        burrow.addFood(new com.tuempresa.ratonesgame.model.Food(
                com.tuempresa.ratonesgame.model.Food.Type.CHEESE, 20, 100));
        burrow.empty();
        check(burrow.getFillPercent() == 0, "emptying must reset the model value to 0%");
    }

    private static void trapEventsRespectTrapState() {
        Trap trap = new Trap("disabled-test", new Point2D(800, 600), 18,
                Trap.State.DISABLED);
        Chef chef = new Chef(760, 600);
        Player player = new Player(500, 600);
        Burrow burrow = new Burrow(160, 880, 38);
        ChefAI ai = new ChefAI(chef, ChefConfig.standard(150),
            new RestaurantMap(), burrow);
        trap.setActivationListener(ai::notifyTrapActivated);
        check(!trap.trigger(), "a disabled trap must not emit an activation event");
        ai.update(0.1, player);
        check(!trap.isActive(), "inspection must not reactivate a disabled trap");
        check(ai.getState() != Chef.State.INSPECTING_TRAP,
            "disabled trap inspection must not be triggered by an activation event");

        Trap activeTrap = new Trap("active-test", new Point2D(900, 600), 18,
            Trap.State.ACTIVE);
        activeTrap.setActivationListener(ai::notifyTrapActivated);
        check(activeTrap.trigger(), "an active trap should emit an activation event");
        check(ai.getState() == Chef.State.INSPECTING_TRAP,
            "chef should investigate an active trap event");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}