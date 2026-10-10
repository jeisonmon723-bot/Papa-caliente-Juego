package com.tuempresa.ratonesgame.core;

import com.tuempresa.ratonesgame.model.Burrow;
import com.tuempresa.ratonesgame.model.Chef;
import com.tuempresa.ratonesgame.model.Food;
import com.tuempresa.ratonesgame.model.FoodItem;
import com.tuempresa.ratonesgame.model.HotPotato;
import com.tuempresa.ratonesgame.model.Player;
import com.tuempresa.ratonesgame.world.RestaurantMap;
import javafx.geometry.Point2D;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public final class GameManagerChecks {
    private GameManagerChecks() {
    }

    public static void main(String[] args) {
        supportsOneAndTwoPlayerModes();
        newGameRestoresPlayerAndChefState();
        spawnsEightFoodTypesAndCapsTheirValue();
        chefCyclesThroughEightHotDishes();
        foodRespawnCreatesANewInstance();
        pickedFoodCanBeDeliveredIntoQuota();
        failedBurrowCostsTheAssignedPlayerALife();
        threeFailedBurrowsEndOnePlayerGame();
        pauseFreezesBurrowTimer();
        hotFoodDamageStartsAfterFiveSeconds();
        hotPotatoFuseExplodesAfterThirtySeconds();
        hotPotatoDeliveryCompletesTheBurrow();
        carriedFoodSlowsPlayerByPortionSize();
        cameraUsesZoomedViewport();
        System.out.println("Game manager checks passed (14)");
    }

    private static void supportsOneAndTwoPlayerModes() {
        GameManager onePlayer = newManager(GameManager.GameMode.ONE_PLAYER, 1);
        check(onePlayer.getActivePlayers().size() == 1, "1P mode should activate one player");
        check(!onePlayer.getPlayers().get(1).isParticipating(),
                "second player should be disabled in 1P mode");

        GameManager twoPlayers = newManager(GameManager.GameMode.TWO_PLAYERS, 2);
        check(twoPlayers.getActivePlayers().size() == 2, "2P mode should activate both players");
    }

    private static void spawnsEightFoodTypesAndCapsTheirValue() {
        GameManager manager = newManager(GameManager.GameMode.ONE_PLAYER, 7);
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
        GameManager manager = newManager(GameManager.GameMode.TWO_PLAYERS, 4);
        Player first = manager.getPlayers().get(0);
        first.loseLife();
        first.setPosition(900, 900);
        first.holdFood(new Food(Food.Type.HOT_SOUP, 95, 10, true));

        manager.startNewGame(GameManager.GameMode.ONE_PLAYER);

        check(first.getLives() == 3 && first.getHealth() == 100 && first.getHeldFood() == null,
                "new game should restore player lives, health, and inventory");
        check(!manager.getPlayers().get(1).isParticipating(),
                "new game mode should be applied to the second player");
    }

    private static void pickedFoodCanBeDeliveredIntoQuota() {
        GameManager manager = newManager(GameManager.GameMode.ONE_PLAYER, 13);
        Player player = manager.getPlayers().get(0);
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
        GameManager manager = newManager(GameManager.GameMode.TWO_PLAYERS, 19);
        Player firstOwner = manager.getBurrowOwner();
        manager.update(GameManager.BURROW_DURATION_SECONDS);
        check(firstOwner.getLives() == 2, "expired quota should cost its owner one life");
        check(manager.getBurrowOwner() != firstOwner,
                "next two-player burrow should be assigned to the other player");
        check(manager.getBurrowSecondsRemaining() == GameManager.BURROW_DURATION_SECONDS,
                "a replacement burrow should receive a fresh two-minute timer");
    }

    private static void threeFailedBurrowsEndOnePlayerGame() {
        GameManager manager = newManager(GameManager.GameMode.ONE_PLAYER, 29);
        for (int attempt = 0; attempt < 3; attempt++) {
            manager.update(GameManager.BURROW_DURATION_SECONDS);
        }
        check(manager.getState() == GameManager.State.GAME_OVER,
                "losing all three lives to expired quotas should end the game");
        check(manager.getHighScore() >= manager.getScore(),
                "final game-over state should retain a valid high score");
    }

    private static void pauseFreezesBurrowTimer() {
        GameManager manager = newManager(GameManager.GameMode.ONE_PLAYER, 31);
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

    private static void hotPotatoFuseExplodesAfterThirtySeconds() {
        GameManager manager = managerWithHotPotato(101);
        Player player = manager.getPlayers().get(0);
        collectHotPotato(manager, player);
        manager.update(29.9);
        check(manager.getHotPotato() != null
                        && manager.getHotPotato().getSecondsRemaining() > 0,
                "potato should remain safe before the 30-second fuse expires");
        manager.update(0.2);
        check(manager.getHotPotato() == null, "exploded potato should be removed from the HUD");
        check(player.getHeldFood() == null, "exploded potato should no longer be carried");
    }

    private static void hotPotatoDeliveryCompletesTheBurrow() {
        GameManager manager = managerWithHotPotato(101);
        Player player = manager.getPlayers().get(0);
        collectHotPotato(manager, player);
        player.setPosition(manager.getBurrow().getX(), manager.getBurrow().getY());
        manager.update(0.01);
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
            GameManager manager = newManager(GameManager.GameMode.ONE_PLAYER, seed);
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

    private static FoodItem normalFood(GameManager manager) {
        return manager.getAvailableFoodItems().stream()
                .filter(item -> !(item.getFood() instanceof HotPotato))
                .findFirst()
                .orElseThrow(() -> new AssertionError("expected normal floor food"));
    }

    private static GameManager newManager(GameManager.GameMode mode, long seed) {
        Player first = new Player(256, 320);
        Player second = new Player(640, 320);
        Chef chef = new Chef(1200, 350);
        Burrow burrow = new Burrow(160, 880, 38);
        return new GameManager(mode, first, second, chef, burrow,
                new RestaurantMap(), new Random(seed));
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}