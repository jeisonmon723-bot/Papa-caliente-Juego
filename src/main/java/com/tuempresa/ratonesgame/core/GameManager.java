package com.tuempresa.ratonesgame.core;

import com.tuempresa.ratonesgame.model.Burrow;
import com.tuempresa.ratonesgame.model.Chef;
import com.tuempresa.ratonesgame.model.Food;
import com.tuempresa.ratonesgame.model.FoodItem;
import com.tuempresa.ratonesgame.model.HotPotato;
import com.tuempresa.ratonesgame.model.Player;
import com.tuempresa.ratonesgame.world.RestaurantMap;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.prefs.Preferences;

public final class GameManager {
    public static final double BURROW_DURATION_SECONDS = 120;
    public static final double HOT_POTATO_SPAWN_CHANCE = 0.20;
    public static final double FOOD_RESPAWN_SECONDS = 4;
    public static final double INTERACTION_RADIUS = 34;
    public static final double CHEF_THEFT_RADIUS = 48;
    public static final double BURROW_DELIVERY_MARGIN = 20;
    private static final double BURROW_QUOTA_POINTS = 100;
    private static final double FOOD_VALUE_MAX = 15;
    private static final String HIGH_SCORE_KEY = "ratonesgame.highScore";
    private static final List<Food.Type> FLOOR_FOOD_TYPES = List.of(
            Food.Type.CHEESE,
            Food.Type.MEAT,
            Food.Type.BREAD,
            Food.Type.SAUSAGE,
            Food.Type.APPLE,
            Food.Type.FISH,
            Food.Type.MUSHROOM,
            Food.Type.CARROT);

    public enum State {
        RUNNING,
        PAUSED,
        GAME_OVER
    }

    private final Player player;
    private final Chef chef;
    private final Burrow burrow;
    private final RestaurantMap map;
    private final Random random;
    private final List<FoodItem> floorFood = new ArrayList<>();
    private final Preferences preferences = Preferences.userNodeForPackage(GameManager.class);

    private State state = State.RUNNING;
    private int score;
    private int highScore;
    private int completedBurrows;
    private double burrowSecondsRemaining = BURROW_DURATION_SECONDS;
    private HotPotato hotPotato;
    private FoodItem potatoPickup;
    private Player potatoHolder;
    private String alertMessage = "";
    private double alertSecondsRemaining;

    public GameManager(Player player, Chef chef, Burrow burrow, RestaurantMap map) {
        this(player, chef, burrow, map, new Random());
    }

    public GameManager(Player player, Chef chef, Burrow burrow, RestaurantMap map,
                       Random random) {
        if (player == null || chef == null || burrow == null || map == null || random == null) {
            throw new IllegalArgumentException("GameManager requires a complete game setup");
        }
        this.player = player;
        this.chef = chef;
        this.burrow = burrow;
        this.map = map;
        this.random = random;
        highScore = preferences.getInt(HIGH_SCORE_KEY, 0);
        resetGame();
    }

    public void resetGame() {
        state = State.RUNNING;
        score = 0;
        completedBurrows = 0;
        burrowSecondsRemaining = BURROW_DURATION_SECONDS;
        alertMessage = "";
        alertSecondsRemaining = 0;
        potatoHolder = null;
        hotPotato = null;
        potatoPickup = null;
        floorFood.clear();
        player.resetForNewGame();
        chef.resetForNewGame();
        map.reset();
        configureNextBurrow();
        spawnFloorFood();
        if (random.nextDouble() < HOT_POTATO_SPAWN_CHANCE) {
            hotPotato = new HotPotato();
            Point2D position = randomSafePosition(90);
            potatoPickup = new FoodItem(hotPotato, position);
        }
    }

    public void update(double deltaSeconds) {
        if (state != State.RUNNING) {
            return;
        }
        if (!player.isActive()) {
            finishGame();
            return;
        }
        updateAlert(deltaSeconds);
        updateFloorFood(deltaSeconds);
        updateHotPotato(deltaSeconds);
        if (state != State.RUNNING) {
            return;
        }

        if (burrow.isComplete()) {
            completeBurrow();
            return;
        }

        burrowSecondsRemaining = Math.max(0, burrowSecondsRemaining - deltaSeconds);
        if (burrowSecondsRemaining == 0) {
            failBurrow();
            return;
        }
        if (!player.isActive()) {
            finishGame();
        }
    }

    public void interact(Player player) {
        if (state != State.RUNNING || player != this.player || !player.isActive()) {
            return;
        }
        if (player.getHeldFood() != null) {
            deliverFood(player);
            return;
        }

        if (distance(player, chef) <= CHEF_THEFT_RADIUS && chef.hasPreparedFood()) {
            Food stolenFood = chef.stealPreparedFood();
            player.holdFood(stolenFood);
            showAlert("El ratón robó comida caliente al Chef", 2.2);
            return;
        }

        FoodItem nearest = findNearestAvailableFood(player);
        if (nearest != null && nearest.getPosition().distance(positionOf(player)) <= INTERACTION_RADIUS) {
            Food pickedFood = nearest.take(FOOD_RESPAWN_SECONDS);
            if (pickedFood != null) {
                player.holdFood(pickedFood);
                if (pickedFood instanceof HotPotato) {
                    potatoHolder = player;
                    showAlert("¡PAPA CALIENTE! Llévala a la madriguera antes de 30 s", 3.5);
                }
                return;
            }
        }
        showAlert("Acércate a comida o al Chef para robar un plato", 1.8);
    }

    public void setPaused(boolean paused) {
        if (state == State.GAME_OVER) {
            return;
        }
        state = paused ? State.PAUSED : State.RUNNING;
    }

    public void finishGame() {
        if (state == State.GAME_OVER) {
            return;
        }
        state = State.GAME_OVER;
        if (score > highScore) {
            highScore = score;
            preferences.putInt(HIGH_SCORE_KEY, highScore);
        }
        showAlert("GAME OVER", 0);
    }

    public Burrow getBurrow() {
        return burrow;
    }

    public Player getPlayer() {
        return player;
    }

    public State getState() {
        return state;
    }

    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }

    public int getCompletedBurrows() {
        return completedBurrows;
    }

    public double getBurrowSecondsRemaining() {
        return burrowSecondsRemaining;
    }

    public List<FoodItem> getAvailableFoodItems() {
        List<FoodItem> available = new ArrayList<>();
        for (FoodItem item : floorFood) {
            if (item.isAvailable()) {
                available.add(item);
            }
        }
        if (potatoPickup != null && potatoPickup.isAvailable()) {
            available.add(potatoPickup);
        }
        return List.copyOf(available);
    }

    public HotPotato getHotPotato() {
        return potatoHolder == null ? null : hotPotato;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public boolean isAlertVisible() {
        return !alertMessage.isBlank() && (alertSecondsRemaining > 0 || state == State.GAME_OVER);
    }

    public static int clampFoodPoints(int requestedPoints) {
        return Math.max(1, Math.min((int) FOOD_VALUE_MAX, requestedPoints));
    }

    public int nextFoodPoints() {
        return clampFoodPoints(random.nextInt(1, 16));
    }

    private void deliverFood(Player player) {
        if (distance(player, burrow) > burrow.getRadius() + BURROW_DELIVERY_MARGIN) {
            showAlert("Acércate a la madriguera para entregar", 1.7);
            return;
        }
        Food food = player.releaseFood();
        if (food instanceof HotPotato potato && !potato.isExploded()) {
            burrow.fillCompletely();
            potatoHolder = null;
            potatoPickup = null;
            hotPotato = null;
            showAlert("¡Papa entregada! Madriguera completada", 2.5);
            return;
        }
        burrow.addFood(food);
        showAlert("+" + food.getFoodPoints() + " puntos de comida", 1.4);
    }

    private void updateHotPotato(double deltaSeconds) {
        if (hotPotato == null || potatoHolder == null) {
            return;
        }
        if (!potatoHolder.isActive() || potatoHolder.getHeldFood() != hotPotato) {
            potatoHolder = null;
            hotPotato = null;
            potatoPickup = null;
            return;
        }
        hotPotato.updateFuse(deltaSeconds);
        if (hotPotato.isExploded()) {
            potatoHolder.releaseFood();
            potatoHolder = null;
            potatoPickup = null;
            hotPotato = null;
            showAlert("¡La Papa Caliente explotó! Se perdió", 2.5);
            return;
        }
        if (burrow.contains(potatoHolder)) {
            burrow.fillCompletely();
            potatoHolder.releaseFood();
            potatoHolder = null;
            potatoPickup = null;
            hotPotato = null;
            showAlert("¡Papa entregada! Madriguera completada", 2.5);
        }
    }

    private void updateFloorFood(double deltaSeconds) {
        for (FoodItem item : floorFood) {
            if (item.updateRespawn(deltaSeconds)) {
                item.setPosition(randomSafePosition(80));
            }
        }
    }

    private void completeBurrow() {
        score++;
        completedBurrows++;
        burrowSecondsRemaining = BURROW_DURATION_SECONDS;
        showAlert("¡Madriguera completada! Puntaje " + score, 2.2);
        configureNextBurrow();
    }

    private void failBurrow() {
        if (player.isActive()) {
            player.loseLife();
            showAlert("Perdiste una vida: cuota vencida", 2.5);
        }
        if (!player.isActive()) {
            finishGame();
            return;
        }
        burrowSecondsRemaining = BURROW_DURATION_SECONDS;
        configureNextBurrow();
    }

    private void configureNextBurrow() {
        burrow.empty();
        Point2D location = randomSafePosition(120);
        burrow.resetAt(location.getX(), location.getY(), BURROW_QUOTA_POINTS);
    }

    private void spawnFloorFood() {
        for (Food.Type type : FLOOR_FOOD_TYPES) {
            int points = clampFoodPoints(random.nextInt(1, 16));
            Food food = new Food(type, 20, points, false);
            floorFood.add(new FoodItem(food, randomSafePosition(80)));
        }
    }

    private FoodItem findNearestAvailableFood(Player player) {
        Point2D playerPosition = positionOf(player);
        List<FoodItem> items = getAvailableFoodItems();
        return items.stream().min((first, second) -> Double.compare(
                first.getPosition().distance(playerPosition),
                second.getPosition().distance(playerPosition))).orElse(null);
    }

    private Point2D randomSafePosition(double minimumBurrowDistance) {
        for (int attempt = 0; attempt < 1000; attempt++) {
            double x = 64 + random.nextDouble() * (RestaurantMap.WIDTH - 128);
            double y = 200 + random.nextDouble() * (RestaurantMap.HEIGHT - 264);
            Point2D candidate = new Point2D(x, y);
            if (!map.canOccupy(x, y, 12)
                    || candidate.distance(chef.getX(), chef.getY()) < 80
                    || candidate.distance(burrow.getX(), burrow.getY()) < minimumBurrowDistance) {
                continue;
            }
            boolean overlapsFood = floorFood.stream().anyMatch(item -> item.isAvailable()
                    && item.getPosition().distance(candidate) < 36);
            if (!overlapsFood) {
                return candidate;
            }
        }
        return new Point2D(500, 500);
    }

    private void updateAlert(double deltaSeconds) {
        if (alertSecondsRemaining > 0) {
            alertSecondsRemaining = Math.max(0, alertSecondsRemaining - deltaSeconds);
            if (alertSecondsRemaining == 0 && state != State.GAME_OVER) {
                alertMessage = "";
            }
        }
    }

    private void showAlert(String message, double seconds) {
        alertMessage = message;
        alertSecondsRemaining = seconds;
    }

    private Point2D positionOf(Player player) {
        return new Point2D(player.getX(), player.getY());
    }

    private double distance(Player player, Burrow target) {
        return Math.hypot(player.getX() - target.getX(), player.getY() - target.getY());
    }

    private double distance(Player player, Chef target) {
        return Math.hypot(player.getX() - target.getX(), player.getY() - target.getY());
    }

}