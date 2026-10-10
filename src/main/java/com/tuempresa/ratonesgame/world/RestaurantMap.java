package com.tuempresa.ratonesgame.world;

import com.tuempresa.ratonesgame.model.Entity;
import com.tuempresa.ratonesgame.model.Trap;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public final class RestaurantMap {
    public static final double WIDTH = 1920;
    public static final double HEIGHT = 1080;
    public static final double PATH_GRID_SIZE = 32;

    public enum RouteAction {
        WORKSTATION,
        PATROL,
        INSPECT_TRAP
    }

    public record RoutePoint(Point2D position, RouteAction action, Trap trap) {
        public RoutePoint {
            if (position == null || action == null
                    || (action == RouteAction.INSPECT_TRAP && trap == null)) {
                throw new IllegalArgumentException("Route point is missing required data");
            }
        }
    }

    private final List<Rectangle2D> solidObstacles;
    private final List<Trap> traps;
    private final List<Point2D> workstations;
    private final List<RoutePoint> patrolRoute;

    public RestaurantMap() {
        traps = List.of(
                new Trap("trap-kitchen", new Point2D(820, 650), 18, Trap.State.DISABLED),
                new Trap("trap-dining", new Point2D(1460, 700), 18, Trap.State.ACTIVE));
        solidObstacles = List.of(
                new Rectangle2D(0, 128, 360, 48),
                new Rectangle2D(680, 128, 880, 48),
                new Rectangle2D(1650, 128, WIDTH - 1650, 48),
                new Rectangle2D(320, 230, 360, 74),
                new Rectangle2D(940, 245, 92, 68),
                new Rectangle2D(1250, 245, 92, 68),
                new Rectangle2D(940, 445, 92, 68),
                new Rectangle2D(1250, 445, 92, 68),
                new Rectangle2D(1570, 135, 190, 76));
        workstations = List.of(new Point2D(500, 360), new Point2D(540, 360));
        patrolRoute = List.of(
                new RoutePoint(new Point2D(500, 360), RouteAction.WORKSTATION, null),
                new RoutePoint(new Point2D(820, 360), RouteAction.PATROL, null),
                new RoutePoint(traps.get(0).getPosition(), RouteAction.INSPECT_TRAP, traps.get(0)),
                new RoutePoint(new Point2D(1050, 650), RouteAction.PATROL, null),
                new RoutePoint(new Point2D(1400, 360), RouteAction.PATROL, null),
                new RoutePoint(traps.get(1).getPosition(), RouteAction.INSPECT_TRAP, traps.get(1)),
                new RoutePoint(new Point2D(1740, 400), RouteAction.PATROL, null),
                new RoutePoint(new Point2D(1740, 850), RouteAction.PATROL, null),
                new RoutePoint(new Point2D(1200, 850), RouteAction.PATROL, null),
                new RoutePoint(new Point2D(500, 850), RouteAction.PATROL, null));
    }

    public List<Rectangle2D> getSolidObstacles() {
        return solidObstacles;
    }

    public List<Trap> getTraps() {
        return traps;
    }

    public List<Point2D> getWorkstations() {
        return workstations;
    }

    public List<RoutePoint> getPatrolRoute() {
        return patrolRoute;
    }

    public boolean canOccupy(double x, double y, double radius) {
        if (x - radius < 32 || y - radius < 32
                || x + radius > WIDTH - 32 || y + radius > HEIGHT - 32) {
            return false;
        }
        for (Rectangle2D obstacle : solidObstacles) {
            double nearestX = clamp(x, obstacle.getMinX(), obstacle.getMaxX());
            double nearestY = clamp(y, obstacle.getMinY(), obstacle.getMaxY());
            if (Math.hypot(x - nearestX, y - nearestY) < radius) {
                return false;
            }
        }
        return true;
    }

    public void move(Entity entity, double deltaSeconds, double radius) {
        double previousX = entity.getX();
        double previousY = entity.getY();
        double nextX = previousX + entity.getVelocityX() * deltaSeconds;
        double nextY = previousY + entity.getVelocityY() * deltaSeconds;
        if (canOccupy(nextX, previousY, radius)) {
            previousX = nextX;
        }
        if (canOccupy(previousX, nextY, radius)) {
            previousY = nextY;
        }
        entity.setPosition(previousX, previousY);
    }

    public boolean hasLineOfSight(Point2D start, Point2D end) {
        for (Rectangle2D obstacle : solidObstacles) {
            if (segmentIntersectsRectangle(start, end, obstacle)) {
                return false;
            }
        }
        return true;
    }

    public List<Point2D> findPath(Point2D start, Point2D destination, double radius) {
        int columns = (int) (WIDTH / PATH_GRID_SIZE);
        int rows = (int) (HEIGHT / PATH_GRID_SIZE);
        int startIndex = nearestWalkableCell(start, radius, columns, rows);
        int targetIndex = nearestWalkableCell(destination, radius, columns, rows);
        if (startIndex < 0 || targetIndex < 0) {
            return List.of();
        }
        if (startIndex == targetIndex) {
            return List.of(cellCenter(targetIndex, columns));
        }

        int[] previous = new int[columns * rows];
        Arrays.fill(previous, -2);
        previous[startIndex] = -1;
        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(startIndex);
        int[] directionX = {1, -1, 0, 0};
        int[] directionY = {0, 0, 1, -1};

        while (!queue.isEmpty() && previous[targetIndex] == -2) {
            int current = queue.remove();
            int currentX = current % columns;
            int currentY = current / columns;
            for (int direction = 0; direction < directionX.length; direction++) {
                int nextX = currentX + directionX[direction];
                int nextY = currentY + directionY[direction];
                if (nextX < 0 || nextX >= columns || nextY < 0 || nextY >= rows) {
                    continue;
                }
                int next = nextY * columns + nextX;
                if (previous[next] != -2 || !canOccupy(
                        nextX * PATH_GRID_SIZE + PATH_GRID_SIZE / 2,
                        nextY * PATH_GRID_SIZE + PATH_GRID_SIZE / 2, radius)) {
                    continue;
                }
                previous[next] = current;
                queue.add(next);
            }
        }

        if (previous[targetIndex] == -2) {
            return List.of();
        }
        List<Point2D> path = new ArrayList<>();
        for (int cell = targetIndex; cell != startIndex; cell = previous[cell]) {
            path.add(cellCenter(cell, columns));
        }
        java.util.Collections.reverse(path);
        return List.copyOf(path);
    }

    private int nearestWalkableCell(Point2D point, double radius, int columns, int rows) {
        int baseX = Math.max(0, Math.min(columns - 1, (int) (point.getX() / PATH_GRID_SIZE)));
        int baseY = Math.max(0, Math.min(rows - 1, (int) (point.getY() / PATH_GRID_SIZE)));
        for (int distance = 0; distance < Math.max(columns, rows); distance++) {
            for (int y = Math.max(0, baseY - distance); y <= Math.min(rows - 1, baseY + distance); y++) {
                for (int x = Math.max(0, baseX - distance); x <= Math.min(columns - 1, baseX + distance); x++) {
                    if (Math.max(Math.abs(x - baseX), Math.abs(y - baseY)) != distance) {
                        continue;
                    }
                    double centerX = x * PATH_GRID_SIZE + PATH_GRID_SIZE / 2;
                    double centerY = y * PATH_GRID_SIZE + PATH_GRID_SIZE / 2;
                    if (canOccupy(centerX, centerY, radius)) {
                        return y * columns + x;
                    }
                }
            }
        }
        return -1;
    }

    private Point2D cellCenter(int index, int columns) {
        return new Point2D((index % columns) * PATH_GRID_SIZE + PATH_GRID_SIZE / 2,
                (index / columns) * PATH_GRID_SIZE + PATH_GRID_SIZE / 2);
    }

    private boolean segmentIntersectsRectangle(Point2D start, Point2D end, Rectangle2D rectangle) {
        double deltaX = end.getX() - start.getX();
        double deltaY = end.getY() - start.getY();
        double[] interval = {0, 1};
        return clip(-deltaX, start.getX() - rectangle.getMinX(), interval)
                && clip(deltaX, rectangle.getMaxX() - start.getX(), interval)
                && clip(-deltaY, start.getY() - rectangle.getMinY(), interval)
                && clip(deltaY, rectangle.getMaxY() - start.getY(), interval)
                && interval[0] < interval[1];
    }

    private boolean clip(double denominator, double numerator, double[] interval) {
        if (denominator == 0) {
            return numerator >= 0;
        }
        double amount = numerator / denominator;
        if (denominator < 0) {
            if (amount > interval[1]) {
                return false;
            }
            interval[0] = Math.max(interval[0], amount);
        } else {
            if (amount < interval[0]) {
                return false;
            }
            interval[1] = Math.min(interval[1], amount);
        }
        return true;
    }

    private double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(value, maximum));
    }
}