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

    public GameCanvas(double width, double height, ResourceManager resources) {
        super(width, height);
        mouseIdle = resources.loadImage(SPRITE_ROOT + "MouseIdle.svg");
        mouseRun = resources.loadImage(SPRITE_ROOT + "MouseRun.svg");
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
        // Scale only the world layer so the HUD remains full-size and readable.
        graphics.scale(camera.getZoom(), camera.getZoom());
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

        // The scene stays fully lit; no vignette or darkness overlay is applied.
        if (captured) {
            graphics.setFill(Color.rgb(135, 18, 25, 0.3));
            graphics.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void drawKitchen(GraphicsContext graphics, double offsetX, double offsetY,
                             double viewportWidth, double viewportHeight) {
        graphics.setFill(Color.web("#d7b987"));
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
                        ? Color.web("#e4cda5")
                        : Color.web("#ddc397"));
                graphics.fillRect(column * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                graphics.setStroke(Color.rgb(157, 116, 69, 0.18));
                graphics.setLineWidth(1);
                graphics.strokeLine(column * TILE_SIZE + 4, row * TILE_SIZE + 28,
                        column * TILE_SIZE + 27, row * TILE_SIZE + 28);
            }
        }
        graphics.setFill(Color.web("#f1dfbd"));
        graphics.fillRect(32, 32, WORLD_WIDTH - 64, 96);
        graphics.setStroke(Color.web("#c99c62"));
        graphics.setLineWidth(2);
        for (int x = 40; x < WORLD_WIDTH - 32; x += 40) {
            graphics.strokeLine(x, 36, x, 124);
        }
        graphics.setFill(Color.web("#8f573b"));
        for (int x = 48; x < WORLD_WIDTH - 64; x += 80) {
            graphics.fillRect(x, 52, 32, 4);
            graphics.fillRect(x + 40, 78, 32, 4);
            graphics.fillRect(x, 104, 32, 4);
        }
        drawWallDecor(graphics);
        graphics.setFill(Color.web("#76523a"));
        graphics.fillRect(0, 0, WORLD_WIDTH, 32);
        graphics.fillRect(0, 32, 32, WORLD_HEIGHT - 32);
        graphics.fillRect(WORLD_WIDTH - 32, 32, 32, WORLD_HEIGHT - 32);
        graphics.fillRect(0, WORLD_HEIGHT - 32, WORLD_WIDTH, 32);
        graphics.setFill(Color.web("#a86f43"));
        graphics.fillRect(0, 128, WORLD_WIDTH, 48);
        graphics.setFill(Color.web("#f0d49e"));
        graphics.fillRect(0, 128, WORLD_WIDTH, 8);
        graphics.setFill(Color.web("#c18a50"));
        graphics.fillRect(0, 169, WORLD_WIDTH, 7);
    }

    private void drawRestaurantZones(GraphicsContext graphics) {
        drawZoneLabel(graphics, "COCINA", 470, 88, Color.web("#70452f"));
        graphics.setFill(Color.web("#e8d4ad"));
        graphics.fillRect(320, 177, 360, 53);
        graphics.setStroke(Color.web("#c99b61"));
        graphics.setLineWidth(2);
        for (int x = 324; x < 680; x += 24) {
            graphics.strokeLine(x, 178, x, 229);
        }
        for (int y = 181; y < 230; y += 17) {
            graphics.strokeLine(320, y, 680, y);
        }
        drawKitchenUtensils(graphics);
        graphics.setFill(Color.web("#795039"));
        graphics.fillRect(320, 230, 360, 74);
        graphics.setFill(Color.web("#f1d49d"));
        graphics.fillRect(320, 230, 360, 13);
        graphics.setFill(Color.web("#986a43"));
        graphics.fillRect(326, 243, 348, 53);
        drawKitchenWorktop(graphics);

        drawZoneLabel(graphics, "MESAS FAMILIARES", 1010, 85, Color.web("#70452f"));
        drawWallShelf(graphics, 1080, 188, 190);
        drawDiner(graphics, 955, 229, Color.web("#668b78"), Color.web("#d89a63"));
        drawDiner(graphics, 1009, 229, Color.web("#7a7898"), Color.web("#d7a078"));
        drawDiner(graphics, 1265, 229, Color.web("#9a6657"), Color.web("#e1b17d"));
        drawDiner(graphics, 1319, 229, Color.web("#6c8790"), Color.web("#c98562"));
        drawDiner(graphics, 955, 429, Color.web("#8b7651"), Color.web("#dca477"));
        drawDiner(graphics, 1009, 429, Color.web("#638779"), Color.web("#e2b17e"));
        drawDiner(graphics, 1265, 429, Color.web("#7d6d91"), Color.web("#d49b71"));
        drawDiner(graphics, 1319, 429, Color.web("#a66d52"), Color.web("#e5b585"));
        drawTable(graphics, 940, 245);
        drawTable(graphics, 1250, 245);
        drawTable(graphics, 940, 445);
        drawTable(graphics, 1250, 445);

        drawZoneLabel(graphics, "CHIMENEA", 1555, 85, Color.web("#70452f"));
        graphics.setFill(Color.web("#5e4030"));
        graphics.fillRect(1570, 135, 190, 76);
        graphics.setFill(Color.web("#c58a52"));
        graphics.fillRect(1581, 142, 168, 59);
        graphics.setFill(Color.web("#713f2e"));
        graphics.fillRoundRect(1592, 151, 146, 47, 8, 8);
        graphics.setFill(Color.web("#f2a84f"));
        graphics.fillOval(1627, 160, 35, 32);
        graphics.setFill(Color.web("#ffd77b"));
        graphics.fillOval(1641, 154, 27, 38);
        graphics.setFill(Color.web("#fff0b4"));
        graphics.fillOval(1652, 162, 12, 25);
        drawWallShelf(graphics, 1390, 188, 126);

        drawZoneLabel(graphics, "MADRIGUERAS", 470, 745, Color.web("#70452f"));
    }

    private void drawWallDecor(GraphicsContext graphics) {
        drawWallFrame(graphics, 95, 52, Color.web("#8f6548"), Color.web("#71917d"));
        drawWallFrame(graphics, 750, 52, Color.web("#95613f"), Color.web("#d29a58"));
        drawWallFrame(graphics, 1420, 52, Color.web("#805b43"), Color.web("#8095a1"));
        drawWallFrame(graphics, 1780, 52, Color.web("#95613f"), Color.web("#a06c50"));
        graphics.setFill(Color.web("#a66c41"));
        graphics.fillRect(790, 113, 310, 9);
        graphics.setFill(Color.web("#e0b86f"));
        for (int x = 810; x < 1088; x += 46) {
            graphics.fillOval(x, 96, 22, 17);
            graphics.setFill(Color.web("#709071"));
            graphics.fillOval(x + 6, 91, 8, 8);
            graphics.setFill(Color.web("#e0b86f"));
        }
    }

    private void drawWallFrame(GraphicsContext graphics, double x, double y,
                               Color frame, Color artwork) {
        graphics.setFill(Color.web("#805337"));
        graphics.fillRoundRect(x - 3, y - 3, 54, 57, 4, 4);
        graphics.setFill(frame);
        graphics.fillRect(x, y, 48, 51);
        graphics.setFill(Color.web("#fff0d3"));
        graphics.fillRect(x + 4, y + 4, 40, 43);
        graphics.setFill(artwork);
        graphics.fillOval(x + 10, y + 12, 28, 26);
        graphics.setFill(Color.web("#f4d58b"));
        graphics.fillOval(x + 17, y + 17, 14, 15);
    }

    private void drawKitchenUtensils(GraphicsContext graphics) {
        graphics.setStroke(Color.web("#80573c"));
        graphics.setLineWidth(3);
        graphics.strokeLine(345, 190, 655, 190);
        for (int x : new int[]{365, 405, 445, 565, 605, 645}) {
            graphics.setStroke(Color.web("#986747"));
            graphics.setLineWidth(2);
            graphics.strokeLine(x, 190, x, 207);
            graphics.setFill(Color.web("#d9e0d7"));
            graphics.fillOval(x - 5, 203, 10, 13);
            graphics.setStroke(Color.web("#986747"));
            graphics.strokeLine(x - 4, 211, x + 4, 211);
        }
        graphics.setFill(Color.web("#d7ded6"));
        graphics.fillOval(476, 188, 29, 29);
        graphics.setFill(Color.web("#aa7048"));
        graphics.fillOval(482, 194, 17, 17);
        graphics.setFill(Color.web("#e6e0cf"));
        graphics.fillRect(521, 190, 8, 26);
        graphics.setFill(Color.web("#bd8050"));
        graphics.fillOval(517, 185, 16, 9);
    }

    private void drawKitchenWorktop(GraphicsContext graphics) {
        graphics.setFill(Color.web("#e9ddc4"));
        graphics.fillRoundRect(345, 246, 92, 43, 5, 5);
        graphics.setFill(Color.web("#d8c8a7"));
        graphics.fillRect(353, 253, 76, 5);
        graphics.setFill(Color.web("#f5efe0"));
        graphics.fillOval(369, 262, 39, 19);
        graphics.setFill(Color.web("#d49a55"));
        graphics.fillOval(380, 268, 17, 8);
        graphics.setFill(Color.web("#e7dfcd"));
        graphics.fillRoundRect(456, 250, 74, 36, 6, 6);
        graphics.setFill(Color.web("#55514a"));
        for (int x : new int[]{468, 495, 521}) {
            graphics.fillOval(x, 254, 12, 12);
            graphics.setFill(Color.web("#d9cbb0"));
            graphics.fillOval(x + 3, 257, 6, 6);
            graphics.setFill(Color.web("#55514a"));
        }
        graphics.setFill(Color.web("#f4eee0"));
        graphics.fillOval(552, 254, 28, 18);
        graphics.fillOval(585, 254, 28, 18);
        graphics.setFill(Color.web("#d8a55e"));
        graphics.fillOval(559, 259, 13, 8);
        graphics.setFill(Color.web("#92a78a"));
        graphics.fillOval(592, 259, 13, 8);
        graphics.setFill(Color.web("#c6a77d"));
        graphics.fillRect(635, 250, 23, 35);
        graphics.setFill(Color.web("#f2e6ce"));
        graphics.fillRect(638, 246, 17, 7);
    }

    private void drawWallShelf(GraphicsContext graphics, double x, double y, double width) {
        graphics.setFill(Color.web("#97623d"));
        graphics.fillRect(x, y + 25, width, 7);
        graphics.setFill(Color.web("#c89456"));
        graphics.fillRect(x, y + 32, width, 4);
        for (int item = 0; item < 4; item++) {
            double itemX = x + 12 + item * (width - 36) / 3;
            graphics.setFill(item % 2 == 0 ? Color.web("#efe2c5") : Color.web("#9cae9c"));
            graphics.fillRoundRect(itemX, y + 7, 21, 18, 5, 5);
            graphics.setFill(Color.web("#d2bb93"));
            graphics.fillOval(itemX + 4, y + 5, 13, 5);
        }
    }

    private void drawDiner(GraphicsContext graphics, double x, double y,
                           Color clothing, Color skin) {
        graphics.setFill(Color.rgb(79, 55, 38, 0.2));
        graphics.fillOval(x - 12, y + 5, 25, 8);
        graphics.setFill(clothing);
        graphics.fillRoundRect(x - 10, y - 1, 20, 19, 7, 7);
        graphics.setFill(skin);
        graphics.fillOval(x - 8, y - 14, 16, 16);
        graphics.setFill(Color.web("#584337"));
        graphics.fillOval(x - 9, y - 16, 18, 8);
        graphics.setFill(Color.web("#fff3d9"));
        graphics.fillOval(x - 4, y - 8, 2, 2);
        graphics.fillOval(x + 3, y - 8, 2, 2);
    }

    private void drawZoneLabel(GraphicsContext graphics, String label,
                               double x, double y, Color color) {
        graphics.setFill(color);
        graphics.fillText(label, x, y);
    }

    private void drawTable(GraphicsContext graphics, double x, double y) {
        drawChair(graphics, x + 4, y + 20);
        drawChair(graphics, x + 70, y + 20);
        drawChair(graphics, x + 4, y + 48);
        drawChair(graphics, x + 70, y + 48);
        graphics.setFill(Color.web("#543a2b"));
        graphics.fillRect(x + 10, y + 12, 72, 10);
        graphics.fillRect(x + 15, y + 57, 62, 9);
        graphics.setFill(Color.web("#b68554"));
        graphics.fillRoundRect(x, y, 92, 68, 8, 8);
        graphics.setFill(Color.web("#d1a36c"));
        graphics.fillRoundRect(x + 5, y + 5, 82, 58, 6, 6);
        graphics.setFill(Color.web("#f1e5cd"));
        graphics.fillRoundRect(x + 10, y + 9, 72, 48, 5, 5);
        graphics.setStroke(Color.web("#d6c6a9"));
        graphics.setLineWidth(1);
        graphics.strokeLine(x + 46, y + 11, x + 46, y + 55);
        graphics.strokeLine(x + 12, y + 33, x + 80, y + 33);
        graphics.setFill(Color.web("#f0dfbd"));
        graphics.fillOval(x + 34, y + 20, 23, 23);
        graphics.setFill(Color.web("#f7f4e9"));
        graphics.fillOval(x + 17, y + 20, 12, 12);
        graphics.fillOval(x + 64, y + 37, 12, 12);
        graphics.setFill(Color.web("#d89a53"));
        graphics.fillOval(x + 41, y + 27, 10, 10);
        graphics.setFill(Color.web("#8caa79"));
        graphics.fillOval(x + 46, y + 25, 5, 4);
    }

    private void drawChair(GraphicsContext graphics, double x, double y) {
        graphics.setFill(Color.web("#795035"));
        graphics.fillRoundRect(x, y, 17, 14, 4, 4);
        graphics.setFill(Color.web("#bd8951"));
        graphics.fillRoundRect(x + 2, y + 2, 13, 10, 3, 3);
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
        graphics.setFill(Color.rgb(248, 218, 166, 0.32));
        graphics.fillOval(x - radius - 18, y - radius * 0.8 - 12,
                radius * 2 + 36, radius * 1.6 + 24);
        graphics.setFill(Color.web("#a9794d"));
        graphics.fillOval(x - radius - 6, y - radius * 0.62 - 3,
                radius * 2 + 12, radius * 1.24 + 6);
        graphics.setFill(Color.web("#60402f"));
        graphics.fillOval(x - radius, y - radius * 0.52,
                radius * 2, radius * 1.04);
        graphics.setFill(Color.web("#b78045"));
        graphics.fillOval(x - radius * 0.63, y - radius * 0.35,
                radius * 1.26, radius * 0.5);
        graphics.setFill(Color.web("#f2d38f"));
        graphics.fillOval(x - radius * 0.43, y - radius * 0.22,
                radius * 0.86, radius * 0.23);
        graphics.setStroke(Color.web("#d6a968"));
        graphics.setLineWidth(6);
        graphics.strokeArc(x - radius - 5, y - radius * 0.7,
                radius * 2 + 10, radius * 1.4, 0, 180, ArcType.OPEN);
        graphics.setStroke(Color.web("#9a6a42"));
        graphics.setLineWidth(3);
        graphics.strokeArc(x - radius * 0.75, y - radius * 0.53,
                radius * 1.5, radius, 0, 180, ArcType.OPEN);

        double progress = burrow.getFillPercent();
        if (progress > 0) {
            // Delivered food contributes at least 1%, matching the minimum food-point value.
            double barWidth = 90;
            double barX = x - barWidth / 2;
            double barY = y + radius * 0.72;
            graphics.setFill(Color.web("#fff8e9"));
            graphics.fillRoundRect(barX - 3, barY - 3, barWidth + 6, 20, 8, 8);
            graphics.setFill(Color.web("#76523a"));
            graphics.fillRoundRect(barX, barY, barWidth, 14, 6, 6);
            graphics.setFill(Color.web("#73a66b"));
            graphics.fillRoundRect(barX, barY, barWidth * progress / 100, 14, 6, 6);
            graphics.setFill(Color.web("#fff8e9"));
            graphics.fillText(String.format("%.0f%%", progress), x - 12, barY - 6);
        }
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