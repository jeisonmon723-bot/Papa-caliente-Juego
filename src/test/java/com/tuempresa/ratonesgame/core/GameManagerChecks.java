package com.tuempresa.ratonesgame.core;

import com.tuempresa.ratonesgame.controller.InputController;
import com.tuempresa.ratonesgame.model.Burrow;
import com.tuempresa.ratonesgame.model.Chef;
import com.tuempresa.ratonesgame.model.Food;
import com.tuempresa.ratonesgame.model.FoodItem;
import com.tuempresa.ratonesgame.model.HotPotato;
import com.tuempresa.ratonesgame.model.Player;
import com.tuempresa.ratonesgame.model.Trap;
import com.tuempresa.ratonesgame.world.RestaurantMap;
import javafx.geometry.Point2D;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public final class GameManagerChecks {
    private GameManagerChecks() {
    }

    public static void main(String[] args) {
        gameUsesOnlyMouseControls();
        newGameRestoresPlayerAndChefState();
        spawnsEightFoodTypesAndCapsTheirValue();
        chefCyclesThroughEightHotDishes();
        foodRespawnCreatesANewInstance();
        pickedFoodCanBeDeliveredIntoQuota();
        pressingEAgainDropsFoodAndRestoresSpeed();
        failedBurrowCostsTheAssignedPlayerALife();
        threeFailedBurrowsEndOnePlayerGame();
        pauseFreezesBurrowTimer();
        hotFoodDamageStartsAfterFiveSeconds();
        hotPotatoDamageStartsAfterThreeSeconds();
        hotPotatoCanOnlyBeDroppedAtBurrow();
        hotPotatoFuseExplodesAfterThirtySeconds();
        hotPotatoDeliveryCompletesTheBurrow();
        carriedFoodSlowsPlayerByPortionSize();
        cameraUsesZoomedViewport();
        System.out.println("Game manager checks passed (17)");
    }

    private static void gameUsesOnlyMouseControls() {
        Player mouse = new Player(256, 320);
        Chef chef = new Chef(1200, 350);
        GameManager manager = new GameManager(mouse, chef, new Burrow(160, 880, 38),
                new RestaurantMap(), new Random(3));
        InputController input = new InputController(mouse, manager);

        input.handleKeyPressed(keyEvent(KeyEvent.KEY_PRESSED, KeyCode.W));
        input.handleKeyPressed(keyEvent(KeyEvent.KEY_PRESSED, KeyCode.UP));
        input.handleKeyPressed(keyEvent(KeyEvent.KEY_PRESSED, KeyCode.RIGHT));

        check(mouse.getVelocityY() < 0 && mouse.getVelocityX() == 0,
                "WASD should move the mouse");
        check(chef.getVelocityY() == 0 && chef.getVelocityX() == 0,
                "keyboard input should never control the Chef");

        input.handleKeyReleased(keyEvent(KeyEvent.KEY_RELEASED, KeyCode.W));
        check(mouse.getVelocityY() == 0,
                "releasing the movement key should stop the mouse");

        input.clearPressedKeys();
        input.handleKeyPressed(keyEvent(KeyEvent.KEY_PRESSED, KeyCode.RIGHT));
        check(chef.getVelocityX() == 0 && chef.getVelocityY() == 0,
                "arrow keys should not take manual control of the Chef");
    }

    private static KeyEvent keyEvent(javafx.event.EventType<KeyEvent> type, KeyCode code) {
        return new KeyEvent(type, "", "", code, false, false, false, false);
    }

    private static void spawnsEightFoodTypesAndCapsTheirValue() {
        GameManager manager = newManager(7);
        Set<Food.Type> types = new HashSet<>();
        for (FoodItem item : manager.getAvailableFoodItems()) {
            if (!(item.getFood() instanceof HotPotato)) {
                types.add(item.getFood().getType());
                check(item.getFood().getFoodPoints() >= 1
                                && item.getFood().getFoodPoints() <= 15,
                        "floor food value must be within 1..15");
            }
        }
        check(types.size() == 8, "the floor should contain all eight normal food types");
        check(GameManager.clampFoodPoints(99) == 15, "food points must cap at 15");
    }

    private static void chefCyclesThroughEightHotDishes() {
        Chef chef = new Chef(1200, 350);
        Set<Food.Type> dishes = new HashSet<>();
        for (int index = 0; index < 8; index++) {
            int foodPoints = 10 + index;
            chef.updateFoodPreparation(3, () -> foodPoints);
            Food dish = chef.stealPreparedFood();
            check(dish != null && dish.isHot(), "Chef should prepare a hot dish");
            check(dish.getFoodPoints() <= 15, "Chef food value must not exceed 15 points");
            dishes.add(dish.getType());
        }
        check(dishes.size() == 8, "Chef should cycle through eight distinct hot dishes");
    }

    private static void newGameRestoresPlayerAndChefState() {
        Player first = new Player(256, 320);
        Chef chef = new Chef(1200, 350);
        RestaurantMap map = new RestaurantMap();
        GameManager manager = new GameManager(first, chef, new Burrow(160, 880, 38),
                map, new Random(4));
        first.loseLife();
        first.setPosition(900, 900);
        first.holdFood(new Food(Food.Type.HOT_SOUP, 95, 10, true));
        chef.setPosition(1000, 900);
        chef.setState(Chef.State.CHASING);
        chef.updateFoodPreparation(3, () -> 10);
        manager.getBurrow().fillCompletely();
        manager.update(0);
        map.getTraps().get(0).setState(Trap.State.ACTIVE);
        map.getTraps().get(1).setState(Trap.State.DISABLED);

        manager.resetGame();

        check(first.getLives() == 3 && first.getHealth() == 100 && first.getHeldFood() == null,
                "new game should restore player lives, health, and inventory");
        check(first.getX() == 256 && first.getY() == 320,
                "restarting should restore the mouse spawn position");
        check(chef.getX() == 1200 && chef.getY() == 350
                        && chef.getState() == Chef.State.PREPARING_FOOD
                        && !chef.hasPreparedFood(),
                "restarting should restore the Chef's initial state");
        check(manager.getScore() == 0
                        && manager.getCompletedBurrows() == 0
                        && manager.getBurrowSecondsRemaining() == GameManager.BURROW_DURATION_SECONDS,
                "restarting should clear scores and restore the quota timer");
        check(map.getTraps().get(0).getState() == Trap.State.DISABLED
                        && map.getTraps().get(1).getState() == Trap.State.ACTIVE,
                "restarting should restore each trap's initial state");
        check(manager.getAvailableFoodItems().size() >= 8,
                "restarting should respawn the floor food");
    }

    private static void pickedFoodCanBeDeliveredIntoQuota() {
        GameManager manager = newManager(13);
        Player player = manager.getPlayer();
        FoodItem pickup = normalFood(manager);
        player.setPosition(pickup.getPosition().getX(), pickup.getPosition().getY());
        Food pickedFood = pickup.getFood();
        manager.interact(player);
        check(player.getHeldFood() == pickedFood, "interaction should collect nearby food");

        int points = player.getHeldFood().getFoodPoints();
        player.setPosition(manager.getBurrow().getX(), manager.getBurrow().getY());
        manager.interact(player);
        check(player.getHeldFood() == null, "delivered food should leave the player's hands");
        check(manager.getBurrow().getFoodPoints() == points,
                "burrow progress should increase by the food's actual point value");
        check(Math.abs(manager.getBurrow().getFillPercent() - points) < 0.0001,
                "a 100-point quota should map one point to one percent");
    }

    private static void pressingEAgainDropsFoodAndRestoresSpeed() {
        GameManager manager = newManager(15);
        Player player = manager.getPlayer();
        FoodItem pickup = normalFood(manager);
        Food.Type foodType = pickup.getFood().getType();
        Point2D position = pickup.getPosition();
        player.setPosition(position.getX(), position.getY());
        player.setVelocity(1, 0);

        manager.interact(player);
        check(player.getHeldFood() != null && player.getVelocityX() < player.getSpeed(),
                "picking food with E should apply its carrying speed penalty");

        player.setPosition(100, 100);
        manager.interact(player);
        check(player.getHeldFood() == null,
                "pressing E again outside the burrow should drop ordinary food");
        check(Math.abs(player.getVelocityX() - player.getSpeed()) < 0.01,
                "dropping food with E should immediately restore normal speed");
        check(manager.getAvailableFoodItems().stream().anyMatch(item ->
                        item.getPosition().equals(new Point2D(100, 100))
                                && item.getFood().getType() == foodType),
                "dropped food should be available at the player's location");
    }

    private static void foodRespawnCreatesANewInstance() {
        FoodItem item = new FoodItem(new Food(Food.Type.CHEESE, 20, 12, false),
            new Point2D(300, 300));
        Food pickedFood = item.take(4);
        check(item.getFood() == null, "taken food should no longer remain in the pickup");
        check(item.updateRespawn(4), "pickup should respawn after its cooldown");
        check(item.getFood() != pickedFood,
                "respawn should create a new food instance instead of duplicating the carried object");
    }

    private static void failedBurrowCostsTheAssignedPlayerALife() {
        GameManager manager = newManager(19);
        Player player = manager.getPlayer();
        manager.update(GameManager.BURROW_DURATION_SECONDS);
        check(player.getLives() == 2, "expired quota should cost the player one life");
        check(manager.getBurrowSecondsRemaining() == GameManager.BURROW_DURATION_SECONDS,
                "a replacement burrow should receive a fresh two-minute timer");
    }

    private static void threeFailedBurrowsEndOnePlayerGame() {
        GameManager manager = newManager(29);
        for (int attempt = 0; attempt < 3; attempt++) {
            manager.update(GameManager.BURROW_DURATION_SECONDS);
        }
        check(manager.getState() == GameManager.State.GAME_OVER,
                "losing all three lives to expired quotas should end the game");
        check(manager.getHighScore() >= manager.getScore(),
                "final game-over state should retain a valid high score");
    }

    private static void pauseFreezesBurrowTimer() {
        GameManager manager = newManager(31);
        manager.setPaused(true);
        manager.update(30);
        check(manager.getBurrowSecondsRemaining() == GameManager.BURROW_DURATION_SECONDS,
                "burrow timer should not advance during pause");
        manager.setPaused(false);
        manager.update(1);
        check(Math.abs(manager.getBurrowSecondsRemaining()
                - (GameManager.BURROW_DURATION_SECONDS - 1)) < 0.0001,
                "burrow timer should resume after unpausing");
    }

    private static void hotFoodDamageStartsAfterFiveSeconds() {
        Player player = new Player(100, 100);
        player.holdFood(new Food(Food.Type.HOT_SOUP, 95, 12, true));
        player.updateHeat(5);
        check(player.getHealth() == 100, "hot food should have a five-second grace period");
        player.updateHeat(1);
        check(player.getHealth() == 95, "hot food should deal 5 health damage per second");
    }

    private static void hotPotatoDamageStartsAfterThreeSeconds() {
        Player player = new Player(100, 100);
        player.holdFood(new HotPotato());
        player.updateHeat(3);
        check(player.getHealth() == 100,
                "Papa Caliente should not damage the player during its first three seconds");
        player.updateHeat(1);
        check(player.getHealth() == 96,
                "Papa Caliente should remove four health points per second after three seconds");
        player.updateHeat(0.5);
        check(player.getHealth() == 94,
                "Papa Caliente damage should scale by elapsed time");
    }

    private static void hotPotatoCanOnlyBeDroppedAtBurrow() {
        GameManager manager = managerWithHotPotato(101);
        Player player = manager.getPlayer();
        collectHotPotato(manager, player);
        player.setPosition(100, 100);
        manager.interact(player);
        check(player.getHeldFood() instanceof HotPotato,
                "Papa Caliente should stay in the player's hands away from the burrow");
        player.updateHeat(4);
        check(player.getHealth() == 96,
                "Papa Caliente should damage the player when E cannot release it");

        player.setPosition(manager.getBurrow().getX(), manager.getBurrow().getY());
        manager.interact(player);
        check(player.getHeldFood() == null && manager.getHotPotato() == null,
                "the interaction key should release Papa Caliente into the burrow");
        check(manager.getBurrow().isComplete(),
                "releasing Papa Caliente in the burrow should complete its quota");
        double healthAfterDelivery = player.getHealth();
        player.updateHeat(1);
        check(player.getHealth() == healthAfterDelivery,
                "delivering Papa Caliente should stop its damage immediately");
    }

    private static void hotPotatoDeliveryCompletesTheBurrow() {
        GameManager manager = managerWithHotPotato(101);
        Player player = manager.getPlayer();
        collectHotPotato(manager, player);
        player.setPosition(manager.getBurrow().getX(), manager.getBurrow().getY());
        manager.interact(player);
        manager.update(0);
        check(manager.getCompletedBurrows() == 1 && manager.getScore() == 1,
                "delivering a live potato should instantly complete the burrow");
        check(player.getHeldFood() == null, "delivered potato should be removed from the player");
    }

    private static void carriedFoodSlowsPlayerByPortionSize() {
        Player player = new Player(100, 100);
        player.setVelocity(1, 0);
        check(Math.abs(player.getVelocityX() - player.getSpeed()) < 0.01,
                "unladen player should move at full speed");

        player.holdFood(new Food(Food.Type.CHEESE, 20, 15, false));
        check(Math.abs(player.getVelocityX() - player.getSpeed() * 0.55) < 0.01,
                "large portion should apply the maximum proportional speed penalty");

        player.holdFood(new Food(Food.Type.BREAD, 20, 3, false));
        check(player.getVelocityX() > player.getSpeed() * 0.55
                        && player.getVelocityX() < player.getSpeed(),
                "small portion should apply a smaller speed penalty");

        player.releaseFood();
        check(Math.abs(player.getVelocityX() - player.getSpeed()) < 0.01,
                "releasing food should restore full movement speed");
    }

    private static void cameraUsesZoomedViewport() {
        Camera camera = new Camera(960, 540, 1920, 1080, 5.5);
        check(camera.getZoom() == 2, "camera should start with a 2x zoom");
        check(camera.getViewportWidth() == 480 && camera.getViewportHeight() == 270,
                "zoom should halve the visible world dimensions");
    }

    private static GameManager managerWithHotPotato(long startingSeed) {
        for (long seed = startingSeed; seed < startingSeed + 200; seed++) {
            GameManager manager = newManager(seed);
            boolean hasPotato = manager.getAvailableFoodItems().stream()
                    .anyMatch(item -> item.getFood() instanceof HotPotato);
            if (hasPotato) {
                return manager;
            }
        }
        throw new AssertionError("20% potato spawn should occur within the deterministic sample");
    }

    private static void collectHotPotato(GameManager manager, Player player) {
        FoodItem potato = manager.getAvailableFoodItems().stream()
                .filter(item -> item.getFood() instanceof HotPotato)
                .findFirst()
                .orElseThrow(() -> new AssertionError("expected a spawned hot potato"));
        Point2D position = potato.getPosition();
        player.setPosition(position.getX(), position.getY());
        manager.interact(player);
        check(player.getHeldFood() instanceof HotPotato, "nearby potato should be picked up");
    }

    private static void hotPotatoFuseExplodesAfterThirtySeconds() {
        GameManager manager = managerWithHotPotato(101);
        Player player = manager.getPlayer();
        collectHotPotato(manager, player);
        manager.update(29.9);
        check(manager.getHotPotato() != null
                        && manager.getHotPotato().getSecondsRemaining() > 0,
                "potato should remain safe before the 30-second fuse expires");
        manager.update(0.2);
        check(manager.getHotPotato() == null, "exploded potato should be removed from the HUD");
        check(player.getHeldFood() == null, "exploded potato should no longer be carried");
    }

    private static FoodItem normalFood(GameManager manager) {
        return manager.getAvailableFoodItems().stream()
                .filter(item -> !(item.getFood() instanceof HotPotato))
                .findFirst()
                .orElseThrow(() -> new AssertionError("expected normal floor food"));
    }

    private static GameManager newManager(long seed) {
        Player player = new Player(256, 320);
        Chef chef = new Chef(1200, 350);
        Burrow burrow = new Burrow(160, 880, 38);
        return new GameManager(player, chef, burrow,
                new RestaurantMap(), new Random(seed));
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}