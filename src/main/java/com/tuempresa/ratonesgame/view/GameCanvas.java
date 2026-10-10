package com.tuempresa.ratonesgame.view;

import com.tuempresa.ratonesgame.core.ResourceManager;
import com.tuempresa.ratonesgame.core.Camera;
import com.tuempresa.ratonesgame.core.AttackSystem;
import com.tuempresa.ratonesgame.core.GameManager;
import com.tuempresa.ratonesgame.model.Chef;
import com.tuempresa.ratonesgame.model.Burrow;
import com.tuempresa.ratonesgame.model.Food;
import com.tuempresa.ratonesgame.model.FoodItem;
import com.tuempresa.ratonesgame.model.Player;
import com.tuempresa.ratonesgame.model.Trap;
import com.tuempresa.ratonesgame.world.RestaurantMap;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public final class GameCanvas extends Canvas {
    private static final String SPRITE_ROOT = "/com/tuempresa/ratonesgame/sprites/";
    private static final int TILE_SIZE = 32;
    public static final double WORLD_WIDTH = 1920;
    public static final double WORLD_HEIGHT = 1080;

    private final Image mouseIdle;
    private final Image mouseRun;
    private final LightingSystem lightingSystem = new LightingSystem();

    public GameCanvas(double width, double height, ResourceManager resources) {
        super(width, height);
        mouseIdle = resources.loadImage(SPRITE_ROOT + "MouseIdle.png");
        mouseRun = resources.loadImage(SPRITE_ROOT + "MouseRun.png");
        getGraphicsContext2D().setImageSmoothing(false);
    }

    public void render(Player firstPlayer, Player secondPlayer, Chef chef,
                       Burrow burrow, Camera camera, RestaurantMap map,
                       AttackSystem attack, GameManager gameManager, boolean captured) {
        GraphicsContext graphics = getGraphicsContext2D();
        graphics.setImageSmoothing(false);
        graphics.clearRect(0, 0, getWidth(), getHeight());
        double offsetX = camera.getOffsetX();
        double offsetY = camera.getOffsetY();

        graphics.save();
        graphics.translate(-offsetX, -offsetY);
        drawKitchen(graphics, offsetX, offsetY, camera.getViewportWidth(), camera.getViewportHeight());
        drawRestaurantZones(graphics);
        drawBurrow(graphics, burrow);
        drawTraps(graphics, map.getTraps());
        drawFood(graphics, gameManager.getAvailableFoodItems());
        if (firstPlayer.isActive() && !burrow.contains(firstPlayer)) {
            drawPlayer(graphics, firstPlayer, Color.web("#42a69a"));
            drawHeldFood(graphics, firstPlayer);
        }
        if (secondPlayer.isActive() && !burrow.contains(secondPlayer)) {
            drawPlayer(graphics, secondPlayer, Color.web("#e2b84f"));
            drawHeldFood(graphics, secondPlayer);
        }
        drawChef(graphics, chef, attack);
        graphics.restore();

        boolean hidden = burrow.contains(firstPlayer) || burrow.contains(secondPlayer);
        lightingSystem.render(graphics, getWidth(), getHeight(), camera, hidden);
        if (captured) {
            graphics.setFill(Color.rgb(135, 18, 25, 0.3));
            graphics.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void drawKitchen(GraphicsContext graphics, double offsetX, double offsetY,
                             double viewportWidth, double viewportHeight) {
        graphics.setFill(Color.web("#80664a"));
        graphics.fillRect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        int firstColumn = Math.max(0, (int) (offsetX / TILE_SIZE));
        int lastColumn = Math.min((int) (WORLD_WIDTH / TILE_SIZE),
                (int) ((offsetX + viewportWidth) / TILE_SIZE) + 1);
        int firstRow = Math.max(0, (int) (offsetY / TILE_SIZE));
        int lastRow = Math.min((int) (WORLD_HEIGHT / TILE_SIZE),
                (int) ((offsetY + viewportHeight) / TILE_SIZE) + 1);
        for (int row = firstRow; row < lastRow; row++) {
            for (int column = firstColumn; column < lastColumn; column++) {
                graphics.setFill((row + column) % 2 == 0
                        ? Color.web("#9b7958")
                        : Color.web("#8d6e50"));
                graphics.fillRect(column * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
        graphics.setFill(Color.web("#594333"));
        graphics.fillRect(0, 0, WORLD_WIDTH, 32);
        graphics.fillRect(0, 32, 32, WORLD_HEIGHT - 32);
        graphics.fillRect(WORLD_WIDTH - 32, 32, 32, WORLD_HEIGHT - 32);
        graphics.fillRect(0, WORLD_HEIGHT - 32, WORLD_WIDTH, 32);
        graphics.fillRect(0, 128, WORLD_WIDTH, 48);
        graphics.setFill(Color.web("#c0955e"));
        graphics.fillRect(0, 128, WORLD_WIDTH, 8);
    }

    private void drawRestaurantZones(GraphicsContext graphics) {
        drawZoneLabel(graphics, "COCINA", 470, 88, Color.web("#f0dfbd"));
        graphics.setFill(Color.web("#594333"));
        graphics.fillRect(320, 230, 360, 74);
        graphics.setFill(Color.web("#c0955e"));
        graphics.fillRect(320, 230, 360, 12);
        graphics.setFill(Color.web("#715038"));
        graphics.fillRect(382, 236, 236, 56);

        drawZoneLabel(graphics, "MESAS FAMILIARES", 1010, 85, Color.web("#f0dfbd"));
        drawTable(graphics, 940, 245);
        drawTable(graphics, 1250, 245);
        drawTable(graphics, 940, 445);
        drawTable(graphics, 1250, 445);

        drawZoneLabel(graphics, "CHIMENEA", 1555, 85, Color.web("#f1d7ad"));
        graphics.setFill(Color.web("#5e4030"));
        graphics.fillRect(1570, 135, 190, 76);
        graphics.setFill(Color.web("#bc623c"));
        graphics.fillRect(1590, 151, 150, 44);
        graphics.setFill(Color.web("#f0a249"));
        graphics.fillOval(1640, 158, 34, 34);

        drawZoneLabel(graphics, "MADRIGUERAS", 470, 745, Color.web("#e2c9a6"));
    }

    private void drawZoneLabel(GraphicsContext graphics, String label,
                               double x, double y, Color color) {
        graphics.setFill(color);
        graphics.fillText(label, x, y);
    }

    private void drawTable(GraphicsContext graphics, double x, double y) {
        graphics.setFill(Color.web("#543a2b"));
        graphics.fillRect(x + 10, y + 12, 72, 10);
        graphics.fillRect(x + 15, y + 57, 62, 9);
        graphics.setFill(Color.web("#b68554"));
        graphics.fillRoundRect(x, y, 92, 68, 8, 8);
        graphics.setFill(Color.web("#d1a36c"));
        graphics.fillRoundRect(x + 5, y + 5, 82, 58, 6, 6);
        graphics.setFill(Color.web("#f0dfbd"));
        graphics.fillOval(x + 38, y + 25, 16, 16);
    }

    private void drawFood(GraphicsContext graphics, java.util.List<FoodItem> foodItems) {
        for (FoodItem item : foodItems) {
            double x = item.getPosition().getX();
            double y = item.getPosition().getY();
            Food food = item.getFood();
            if (food instanceof com.tuempresa.ratonesgame.model.HotPotato) {
                graphics.setFill(Color.rgb(238, 83, 38, 0.35));
                graphics.fillOval(x - 18, y - 18, 36, 36);
                graphics.setFill(Color.web("#e64e2f"));
                graphics.fillOval(x - 9, y - 9, 18, 18);
                graphics.setFill(Color.web("#ffd66b"));
                graphics.fillRect(x - 2, y - 12, 4, 5);
                continue;
            }
            Color color = switch (food.getType()) {
                case CHEESE -> Color.web("#f1c64d");
                case MEAT, SAUSAGE, HOT_ROAST -> Color.web("#b85c43");
                case BREAD, HOT_BREAD, HOT_PIE -> Color.web("#d9a35a");
                case APPLE -> Color.web("#cf5143");
                case FISH, HOT_FISH -> Color.web("#91b4aa");
                case MUSHROOM -> Color.web("#d9c4a0");
                case CARROT -> Color.web("#e8893a");
                default -> Color.web("#d99043");
            };
            graphics.setFill(Color.web("#4d392a"));
            graphics.fillRect(x - 9, y - 7, 18, 15);
            graphics.setFill(color);
            graphics.fillRect(x - 7, y - 7, 14, 12);
            graphics.setFill(Color.web("#fff0c8"));
            graphics.fillRect(x - 4, y - 5, 3, 3);
            graphics.fillRect(x + 2, y + 1, 3, 3);
            if (food.isHot()) {
                graphics.setFill(Color.web("#fff4d0"));
                graphics.fillRect(x - 5, y - 14, 2, 5);
                graphics.fillRect(x + 4, y - 16, 2, 6);
            }
        }
    }

    private void drawHeldFood(GraphicsContext graphics, Player player) {
        if (player.getHeldFood() == null) {
            return;
        }
        graphics.setFill(player.getHeldFood() instanceof com.tuempresa.ratonesgame.model.HotPotato
                ? Color.web("#ef5635") : Color.web("#f1c64d"));
        graphics.fillRect(player.getX() + 8, player.getY() - 14, 8, 8);
    }

    private void drawBurrow(GraphicsContext graphics, Burrow burrow) {
        double x = burrow.getX();
        double y = burrow.getY();
        double radius = burrow.getRadius();
        graphics.setFill(Color.web("#493628"));
        graphics.fillOval(x - radius, y - radius * 0.55, radius * 2, radius * 1.1);
        graphics.setStroke(Color.web("#c79b65"));
        graphics.setLineWidth(6);
        graphics.strokeArc(x - radius - 3, y - radius * 0.65,
                radius * 2 + 6, radius * 1.3, 0, 180, ArcType.OPEN);
    }

    private void drawTraps(GraphicsContext graphics, java.util.List<Trap> traps) {
        for (Trap trap : traps) {
            double x = trap.getPosition().getX();
            double y = trap.getPosition().getY();
            graphics.setFill(trap.isActive() ? Color.web("#983c32") : Color.web("#66605a"));
            graphics.fillRoundRect(x - 13, y - 8, 26, 16, 4, 4);
            graphics.setStroke(Color.web("#d9c7a8"));
            graphics.setLineWidth(2);
            graphics.strokeLine(x - 8, y, x + 8, y);
            graphics.strokeLine(x - 5, y - 4, x - 5, y + 4);
            graphics.strokeLine(x + 5, y - 4, x + 5, y + 4);
        }
    }

    private void drawPlayer(GraphicsContext graphics, Player player, Color marker) {
        Image sprite = player.isMoving() ? mouseRun : mouseIdle;
        int frame = (int) ((System.nanoTime() / 180_000_000L) % 6);
        double frameY = frame * TILE_SIZE;
        graphics.drawImage(sprite, 0, frameY, TILE_SIZE, TILE_SIZE,
                player.getX() - TILE_SIZE / 2.0, player.getY() - TILE_SIZE / 2.0,
                TILE_SIZE, TILE_SIZE);
        graphics.setFill(marker);
        graphics.fillRect(player.getX() - 3, player.getY() + 17, 6, 3);
    }

    private void drawChef(GraphicsContext graphics, Chef chef, AttackSystem attack) {
        graphics.setFill(chef.getState() == Chef.State.CHASING
                ? Color.web("#9b4337") : Color.web("#44332b"));
        graphics.fillOval(chef.getX() - 12, chef.getY() - 12, 24, 24);
        graphics.setFill(Color.web("#eee6d2"));
        graphics.fillOval(chef.getX() - 10, chef.getY() - 15, 20, 16);
        graphics.setFill(Color.web("#b84e3e"));
        graphics.fillRect(chef.getX() - 7, chef.getY() + 2, 14, 8);
        if (chef.hasPreparedFood()) {
            graphics.setFill(Color.web("#efe3ce"));
            graphics.fillOval(chef.getX() + 12, chef.getY() - 14, 18, 18);
            graphics.setFill(Color.web("#d68a42"));
            graphics.fillOval(chef.getX() + 17, chef.getY() - 9, 8, 8);
            graphics.setFill(Color.web("#fff5d8"));
            graphics.fillRect(chef.getX() + 18, chef.getY() - 20, 2, 5);
        }
        drawBroom(graphics, chef, attack);
    }

    private void drawBroom(GraphicsContext graphics, Chef chef, AttackSystem attack) {
        if (attack.getPhase() != AttackSystem.Phase.WINDUP
                && attack.getPhase() != AttackSystem.Phase.STRIKING) {
            return;
        }
        double angle = Math.toDegrees(Math.atan2(
                attack.getAttackDirection().getY(), attack.getAttackDirection().getX()));
        graphics.save();
        graphics.translate(chef.getX(), chef.getY());
        graphics.rotate(angle);
        graphics.setGlobalAlpha(attack.isStrikeActive() ? 1 : 0.55);
        graphics.setFill(Color.web("#765033"));
        graphics.fillRect(12, -3, 32, 6);
        graphics.setFill(attack.isStrikeActive() ? Color.web("#d6b86f") : Color.web("#f3d887"));
        graphics.fillRect(39, -7, 9, 14);
        graphics.restore();
    }
}