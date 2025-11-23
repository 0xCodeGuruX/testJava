package com.tron.model;

import java.util.ArrayList;
import java.util.List;

public abstract class PlayerModel extends GameObjectModel {

    protected TronColor color; // Using a custom enum or simple string for color in Model
    protected boolean alive = true;
    protected boolean jump = false;
    protected boolean booster = false;

    protected int startVel = 0;
    protected int boostLeft = 3;
    protected long boostStartTime = 0; // For tracking boost duration
    protected final long BOOST_DURATION_NANO = 300_000_000L; // 300ms in nanoseconds

    public static final int WIDTH = 5;
    public static final int HEIGHT = 5;
    public static final int VELBOOST = 5;
    public static final int JUMPHEIGHT = 16;

    protected List<PathSegment> path = new ArrayList<>();

    public PlayerModel(int x, int y, int velocityX, int velocityY, int width, int height, TronColor color) {
        super(x, y, velocityX, velocityY, width, height);
        startVel = Math.max(Math.abs(velocityX), Math.abs(velocityY));
        this.color = color;
    }

    public int getBoostsLeft() {
        return boostLeft;
    }

    @Override
    public void accelerate() {
        if (x < 0 || x > rightBound) {
            velocityX = 0;
            alive = false;
        }
        if (y < 0 || y > bottomBound) {
            velocityY = 0;
            alive = false;
        }
    }

    public void jump() {
        jump = true; // Logic for jumping will be handled in concrete player update
    }

    public void startBoost() {
        if (boostLeft > 0 && !booster) {
            booster = true;
            boostStartTime = System.nanoTime();
            boostLeft--;
        }
    }

    public void updateBoost(long currentTime) {
        if (booster) {
            if (currentTime - boostStartTime >= BOOST_DURATION_NANO) {
                booster = false;
            }
            if (velocityX > 0) {
                velocityX = VELBOOST;
            } else if (velocityX < 0) {
                velocityX = -VELBOOST;
            } else if (velocityY > 0) {
                velocityY = VELBOOST;
            } else if (velocityY < 0) {
                velocityY = -VELBOOST;
            }
        } else {
            // Restore original velocity if not boosting
            if (velocityX > 0) {
                velocityX = startVel;
            } else if (velocityX < 0) {
                velocityX = -startVel;
            } else if (velocityY > 0) {
                velocityY = startVel;
            } else if (velocityY < 0) {
                velocityY = -startVel;
            }
        }
    }

    @Override
    public void move() {
        int oldX = x;
        int oldY = y;
        super.move(); // Calls GameObjectModel's move, which updates x, y

        // Add a path segment if moved
        if (oldX != x || oldY != y) {
            // Only add segment if player is still alive and moving
            if (alive && (velocityX != 0 || velocityY != 0)) {
                path.add(new PathSegment(oldX, oldY, x, y));
            }
        }
    }

    public boolean checkCrash(List<PlayerModel> allPlayers) {
        // Check collision with game bounds (already handled by accelerate)
        if (!alive) return true;

        // Check collision with own path (excluding the very last segment being drawn)
        if (path.size() > 5) { // Avoid immediate self-collision with just created segment
            for (int i = 0; i < path.size() - 2; i++) { // Don't check against last two segments (current and previous)
                if (intersects(path.get(i))) {
                    alive = false;
                    return true;
                }
            }
        }
        
        // Check collision with other players' paths and bodies
        for (PlayerModel otherPlayer : allPlayers) {
            if (otherPlayer == this) continue; // Don't check against self

            // Check collision with other player's body
            if (intersects(otherPlayer.x, otherPlayer.y, otherPlayer.width, otherPlayer.height)) {
                alive = false;
                return true;
            }

            // Check collision with other player's path
            for (PathSegment segment : otherPlayer.getPath()) {
                if (intersects(segment)) {
                    alive = false;
                    return true;
                }
            }
        }
        return false;
    }

    // Helper for collision detection with a path segment (more accurate)
    private boolean intersects(PathSegment segment) {
        // Player's current position is treated as a point (x, y) with a bounding box (x-WIDTH/2, y-HEIGHT/2, WIDTH, HEIGHT)
        // Check if player's bounding box intersects the given line segment

        int playerMinX = x - WIDTH / 2;
        int playerMaxX = x + WIDTH / 2;
        int playerMinY = y - HEIGHT / 2;
        int playerMaxY = y + HEIGHT / 2;

        int segStartX = segment.getStartX();
        int segStartY = segment.getStartY();
        int segEndX = segment.getEndX();
        int segEndY = segment.getEndY();

        // Check if player's bounding box intersects with the line segment
        // This is a basic AABB-line segment intersection test.

        // Line segment horizontal
        if (segStartY == segEndY) {
            int lineMinX = Math.min(segStartX, segEndX);
            int lineMaxX = Math.max(segStartX, segEndX);
            int lineY = segStartY;

            return (playerMaxY >= lineY && playerMinY <= lineY && playerMaxX >= lineMinX && playerMinX <= lineMaxX);
        }
        // Line segment vertical
        else if (segStartX == segEndX) {
            int lineMinY = Math.min(segStartY, segEndY);
            int lineMaxY = Math.max(segStartY, segEndY);
            int lineX = segStartX;

            return (playerMaxX >= lineX && playerMinX <= lineX && playerMaxY >= lineMinY && playerMinY <= lineMaxY);
        }
        return false; // Should not happen for axis-aligned Tron paths
    }


    // Helper for collision detection with another player's bounding box
    private boolean intersects(int otherX, int otherY, int otherWidth, int otherHeight) {
        // AABB intersection check
        return (x < otherX + otherWidth &&
                x + width > otherX &&
                y < otherY + otherHeight &&
                y + height > otherY);
    }


    public TronColor getColor() {
        return color;
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    public List<PathSegment> getPath() {
        return path;
    }

    // Methods for changing direction will be added to concrete subclasses
    public abstract void setDirection(Direction direction);

    // Enum for colors used in Tron Model
    public enum TronColor {
        BLUE, ORANGE, GREEN, RED, YELLOW, PINK
    }

    // Enum for direction
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}
