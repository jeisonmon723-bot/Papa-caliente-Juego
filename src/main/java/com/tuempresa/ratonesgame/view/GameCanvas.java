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

import java.util.EnumMap;
import java.util.Map;

public final class GameCanvas extends Canvas {
    private static final String SPRITE_ROOT = "/com/tuempresa/ratonesgame/sprites/";
    private static final String GAMEPLAY_ROOT = "/com/tuempresa/ratonesgame/assets/gameplay/";
    private static final int TILE_SIZE = 32;
    public static final double WORLD_WIDTH = 1920;
    public static final double WORLD_HEIGHT = 1080;

    private final Image mouseIdle;
    private final Image mouseEat;
    private final Image mouseDamage;
    private final Image mouseTrap;
    private final Image ratWalk;
    private final Image chefSheet;
    private final Image chefTools;
    private final Image furnitureSheet;
    private final Image fruitSheet;
    private final Image meatSheet;
    private final Image vegetableSheet;
    private final Image kitchenTiles;
    private final Image tilesSheet;
    private final Image cheeseIcon;
    private final Map<Food.Type, Image> foodSprites = new EnumMap<>(Food.Type.class);

    public GameCanvas(double width, double height, ResourceManager resources) {
        super(width, height);
        mouseIdle = resources.loadImage(SPRITE_ROOT + "MouseIdle.png");
        mouseEat = resources.loadImage(SPRITE_ROOT + "MouseEat.png");
        mouseDamage = resources.loadImage(SPRITE_ROOT + "MouseDamage.png");
        mouseTrap = resources.loadImage(SPRITE_ROOT + "MouseTrap.png");
        ratWalk = resources.loadImage(GAMEPLAY_ROOT + "RatWalk.png");
        chefSheet = resources.loadImage(GAMEPLAY_ROOT + "ChefSheet.png");
        chefTools = resources.loadImage(GAMEPLAY_ROOT + "ChefTools.png");
        furnitureSheet = resources.loadImage(GAMEPLAY_ROOT + "Furniture.png");
        fruitSheet = resources.loadImage(GAMEPLAY_ROOT + "Fruit.png");
        meatSheet = resources.loadImage(GAMEPLAY_ROOT + "Meat.png");
        vegetableSheet = resources.loadImage(GAMEPLAY_ROOT + "Vegetables.png");
        kitchenTiles = resources.loadImage(GAMEPLAY_ROOT + "KitchenTiles.png");
        tilesSheet = resources.loadImage(GAMEPLAY_ROOT + "Tiles.png");
        cheeseIcon = resources.loadImage(GAMEPLAY_ROOT + "food/Cheese.png");
        loadFoodSprites(resources);
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
        drawWoodBorder(graphics);
    }

    private void drawWoodBorder(GraphicsContext graphics) {
        for (int x = 0; x < WORLD_WIDTH; x += TILE_SIZE) {
            graphics.drawImage(tilesSheet, 15 * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE,
                    x, 0, TILE_SIZE, TILE_SIZE);
            graphics.drawImage(tilesSheet, 15 * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE,
                    x, WORLD_HEIGHT - TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
        for (int y = TILE_SIZE; y < WORLD_HEIGHT - TILE_SIZE; y += TILE_SIZE) {
            graphics.drawImage(tilesSheet, 15 * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE,
                    0, y, TILE_SIZE, TILE_SIZE);
            graphics.drawImage(tilesSheet, 15 * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE,
                    WORLD_WIDTH - TILE_SIZE, y, TILE_SIZE, TILE_SIZE);
        }
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
        int[] fruitFrames = {0, 2, 5, 7, 2, 0};
        for (int item = 0; item < fruitFrames.length; item++) {
            graphics.drawImage(fruitSheet, fruitFrames[item] * TILE_SIZE, 0,
                    TILE_SIZE, TILE_SIZE, 810 + item * 46, 91, 22, 22);
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
        graphics.strokeLine(345, 174, 655, 174);
        for (int frame = 0; frame < 10; frame++) {
            graphics.drawImage(chefTools, frame * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE,
                    350 + frame * 30, 150, 24, 24);
        }
    }

    private void drawKitchenWorktop(GraphicsContext graphics) {
        graphics.drawImage(kitchenTiles, 0, 176, 144, 48,
                320, 177, 360, 120);
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
        graphics.drawImage(furnitureSheet, TILE_SIZE, 0, TILE_SIZE, TILE_SIZE,
                x, y, 92, 68);
    }

    private void drawChair(GraphicsContext graphics, double x, double y) {
        graphics.drawImage(furnitureSheet, 0, 0, TILE_SIZE, TILE_SIZE,
                x, y, 20, 18);
    }

    private void drawFood(GraphicsContext graphics, java.util.List<FoodItem> foodItems) {
        for (FoodItem item : foodItems) {
            double x = item.getPosition().getX();
            double y = item.getPosition().getY();
            Food food = item.getFood();
            if (food.isHot()) {
                graphics.setFill(Color.rgb(238, 83, 38, 0.2));
                graphics.fillOval(x - 18, y - 18, 36, 36);
            }
            drawFoodSprite(graphics, food.getType(), x - 14, y - 14, 28, 28);
            if (food.isHot()) {
                graphics.setFill(Color.web("#fff4d0"));
                graphics.fillOval(x - 5, y - 20, 2, 4);
                graphics.fillOval(x + 4, y - 18, 2, 4);
            }
        }
    }

    private void drawHeldFood(GraphicsContext graphics, Player player) {
        if (player.getHeldFood() == null) {
            return;
        }
        drawFoodSprite(graphics, player.getHeldFood().getType(),
                player.getX() + 8, player.getY() - 20, 20, 20);
    }

    private void loadFoodSprites(ResourceManager resources) {
        foodSprites.put(Food.Type.CHEESE, loadFoodSprite(resources, "Cheese"));
        foodSprites.put(Food.Type.MEAT, loadFoodSprite(resources, "Steak"));
        foodSprites.put(Food.Type.BREAD, loadFoodSprite(resources, "Bread"));
        foodSprites.put(Food.Type.SAUSAGE, loadFoodSprite(resources, "Sausages"));
        foodSprites.put(Food.Type.APPLE, loadFoodSprite(resources, "Apple"));
        foodSprites.put(Food.Type.FISH, loadFoodSprite(resources, "Fish"));
        foodSprites.put(Food.Type.HOT_PIZZA, loadFoodSprite(resources, "PieApple"));
        foodSprites.put(Food.Type.HOT_PASTA, loadFoodSprite(resources, "Sushi"));
        foodSprites.put(Food.Type.HOT_FISH, loadFoodSprite(resources, "FishFillet"));
        foodSprites.put(Food.Type.HOT_PIE, loadFoodSprite(resources, "PiePumpkin"));
        foodSprites.put(Food.Type.HOT_VEGETABLES, loadFoodSprite(resources, "Tomato"));
        foodSprites.put(Food.Type.HOT_BREAD, loadFoodSprite(resources, "Bread"));
        foodSprites.put(Food.Type.HOT_POTATO, loadFoodSprite(resources, "Potato"));
    }

    private Image loadFoodSprite(ResourceManager resources, String name) {
        return resources.loadImage(GAMEPLAY_ROOT + "food/" + name + ".png");
    }

    private void drawFoodSprite(GraphicsContext graphics, Food.Type type,
                                double x, double y, double width, double height) {
        switch (type) {
            case MUSHROOM -> drawSheetFrame(graphics, vegetableSheet, 6, x, y, width, height);
            case CARROT -> drawSheetFrame(graphics, vegetableSheet, 3, x, y, width, height);
            case HOT_SOUP -> drawSheetFrame(graphics, chefTools, 6, x, y, width, height);
            case HOT_ROAST -> drawSheetFrame(graphics, meatSheet, 1, x, y, width, height);
            case HOT_VEGETABLES -> graphics.drawImage(
                    foodSprites.get(type), x, y, width, height);
            default -> graphics.drawImage(foodSprites.get(type), x, y, width, height);
        }
    }

    private void drawSheetFrame(GraphicsContext graphics, Image sheet, int frame,
                                double x, double y, double width, double height) {
        graphics.drawImage(sheet, frame * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE,
                x, y, width, height);
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
            graphics.save();
            graphics.setGlobalAlpha(trap.isActive() ? 1 : 0.55);
            int frame = (int) ((System.nanoTime() / 220_000_000L) % 4);
            graphics.drawImage(mouseTrap, 0, frame * TILE_SIZE, TILE_SIZE, TILE_SIZE,
                    x - 16, y - 16, TILE_SIZE, TILE_SIZE);
            graphics.restore();
            graphics.drawImage(cheeseIcon, x + 7, y - 16, 12, 12);
        }
    }

    private void drawPlayer(GraphicsContext graphics, Player player, Color marker) {
        long animationFrame = System.nanoTime() / 180_000_000L;
        Image sprite;
        int sourceX = 0;
        int sourceY;
        if (player.getHealth() < 40) {
            sprite = mouseDamage;
            sourceY = (int) (animationFrame % 4) * TILE_SIZE;
        } else if (player.getHeldFood() != null && !player.isMoving()) {
            sprite = mouseEat;
            sourceY = (int) (animationFrame % 5) * TILE_SIZE;
        } else if (player.isMoving()) {
            sprite = ratWalk;
            sourceX = (int) (animationFrame % 4) * TILE_SIZE;
            sourceY = 0;
        } else {
            sprite = mouseIdle;
            sourceY = (int) (animationFrame % 6) * TILE_SIZE;
        }
        graphics.save();
        if (sprite == ratWalk && player.getVelocityX() < 0) {
            graphics.translate(player.getX(), 0);
            graphics.scale(-1, 1);
            graphics.drawImage(sprite, sourceX, sourceY, TILE_SIZE, TILE_SIZE,
                    -TILE_SIZE / 2.0, player.getY() - TILE_SIZE / 2.0,
                    TILE_SIZE, TILE_SIZE);
        } else {
            graphics.drawImage(sprite, sourceX, sourceY, TILE_SIZE, TILE_SIZE,
                    player.getX() - TILE_SIZE / 2.0, player.getY() - TILE_SIZE / 2.0,
                    TILE_SIZE, TILE_SIZE);
        }
        graphics.restore();
        graphics.setFill(marker);
        graphics.fillRect(player.getX() - 3, player.getY() + 17, 6, 3);
    }

    private void drawChef(GraphicsContext graphics, Chef chef, AttackSystem attack) {
        long animationFrame = System.nanoTime() / 180_000_000L;
        int frame = chef.isMoving() ? 4 + (int) (animationFrame % 8)
                : (int) (animationFrame % 4);
        if (attack.getPhase() == AttackSystem.Phase.STRIKING) {
            frame = 11;
        }
        graphics.save();
        if (chef.getFacingX() < 0) {
            graphics.translate(chef.getX(), 0);
            graphics.scale(-1, 1);
            graphics.drawImage(chefSheet, frame * 48, 0, 48, 56,
                    -22, chef.getY() - 28, 44, 52);
        } else {
            graphics.drawImage(chefSheet, frame * 48, 0, 48, 56,
                    chef.getX() - 22, chef.getY() - 28, 44, 52);
        }
        graphics.restore();
        if (chef.hasPreparedFood()) {
            drawFoodSprite(graphics, chef.getPreparedFood().getType(),
                    chef.getX() + 14, chef.getY() - 22, 20, 20);
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
        graphics.drawImage(chefTools, 9 * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE,
                10, -12, 42, 24);
        graphics.restore();
    }
}