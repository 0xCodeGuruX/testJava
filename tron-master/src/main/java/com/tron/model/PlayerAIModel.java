package com.tron.model;

import com.tron.model.PlayerModel.Direction;
import com.tron.model.PlayerModel.TronColor;

import java.util.List;
import java.util.Random;

public class PlayerAIModel extends PlayerModel {

    private int time = 40; // The number of steps before a random turn
    private List<PlayerModel> allPlayers; // List of all players including itself
    private Random rand = new Random();

    // Helper to check for collision risk in a given direction
    private boolean detectCollisionRisk(int checkX, int checkY, int lookAheadDistance) {
        // Check for wall collision
        if (checkX < 0 || checkX > rightBound || checkY < 0 || checkY > bottomBound) {
            return true;
        }

        // Check for collision with other players' paths
        if (allPlayers != null) {
            for (PlayerModel otherPlayer : allPlayers) {
                if (otherPlayer == this) continue; // Don't check self

                for (PathSegment segment : otherPlayer.getPath()) {
                    // Simple point-in-segment check, could be more sophisticated
                    if (segment.isVertical()) {
                        if (Math.abs(checkX - segment.getStartX()) < WIDTH / 2 &&
                            checkY >= Math.min(segment.getStartY(), segment.getEndY()) &&
                            checkY <= Math.max(segment.getStartY(), segment.getEndY())) {
                            return true;
                        }
                    } else if (segment.isHorizontal()) {
                        if (Math.abs(checkY - segment.getStartY()) < HEIGHT / 2 &&
                            checkX >= Math.min(segment.getStartX(), segment.getEndX()) &&
                            checkX <= Math.max(segment.getStartX(), segment.getEndX())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public PlayerAIModel(int x, int y, int velocityX, int velocityY, int width, int height, TronColor color) {
        super(x, y, velocityX, velocityY, width, height, color);
    }

    // This method needs to be called by the game manager to provide the AI with other players
    public void setAllPlayers(List<PlayerModel> allPlayers) {
        this.allPlayers = allPlayers;
    }

    // Original reactProximity logic, adapted for Model
    private void reactProximity() {
        int velocity = Math.max(Math.abs(velocityX), Math.abs(velocityY));
        if (velocity == 0) velocity = startVel; // Ensure velocity is not zero

        // Boosts randomly
        int r = rand.nextInt(100);
        if (r == 1) {
            startBoost();
        }

        // Check for immediate collision risk
        int lookAheadDistance = 5; // How far to look ahead

        boolean forwardRisk = detectCollisionRisk(x + velocityX * lookAheadDistance, y + velocityY * lookAheadDistance, lookAheadDistance);

        if (forwardRisk || time <= 0) { // If there's a risk or it's time for a random turn
            // Try turning right, then left
            Direction currentDirection = getCurrentDirection();
            Direction turnRight = getTurnRightDirection(currentDirection);
            Direction turnLeft = getTurnLeftDirection(currentDirection);

            // Test if turning right is safe
            int testVelX = 0, testVelY = 0;
            if (turnRight == Direction.UP) { testVelY = -velocity; }
            else if (turnRight == Direction.DOWN) { testVelY = velocity; }
            else if (turnRight == Direction.LEFT) { testVelX = -velocity; }
            else if (turnRight == Direction.RIGHT) { testVelX = velocity; }

            boolean rightTurnRisk = detectCollisionRisk(x + testVelX * lookAheadDistance, y + testVelY * lookAheadDistance, lookAheadDistance);

            // Test if turning left is safe
            testVelX = 0; testVelY = 0;
            if (turnLeft == Direction.UP) { testVelY = -velocity; }
            else if (turnLeft == Direction.DOWN) { testVelY = velocity; }
            else if (turnLeft == Direction.LEFT) { testVelX = -velocity; }
            else if (turnLeft == Direction.RIGHT) { testVelX = velocity; }

            boolean leftTurnRisk = detectCollisionRisk(x + testVelX * lookAheadDistance, y + testVelY * lookAheadDistance, lookAheadDistance);

            if (!rightTurnRisk) { // If turning right is safe, take it
                setDirection(turnRight);
                time = 40; // Reset time for next decision
            } else if (!leftTurnRisk) { // If turning right is not safe, but turning left is
                setDirection(turnLeft);
                time = 40; // Reset time for next decision
            } else {
                // Both turns are risky, or no safe turn. Attempt a jump if possible, or just continue and crash
                // For now, continue in current direction or random if all else fails
                if (rand.nextBoolean()) {
                    setDirection(turnRight);
                } else {
                    setDirection(turnLeft);
                }
                time = 40;
            }
        }
        time--;
    }

    private Direction getCurrentDirection() {
        if (velocityX > 0) return Direction.RIGHT;
        if (velocityX < 0) return Direction.LEFT;
        if (velocityY > 0) return Direction.DOWN;
        if (velocityY < 0) return Direction.UP;
        return Direction.RIGHT; // Default
    }

    private Direction getTurnRightDirection(Direction current) {
        switch (current) {
            case UP: return Direction.RIGHT;
            case RIGHT: return Direction.DOWN;
            case DOWN: return Direction.LEFT;
            case LEFT: return Direction.UP;
        }
        return Direction.RIGHT; // Should not happen
    }

    private Direction getTurnLeftDirection(Direction current) {
        switch (current) {
            case UP: return Direction.LEFT;
            case LEFT: return Direction.DOWN;
            case DOWN: return Direction.RIGHT;
            case RIGHT: return Direction.UP;
        }
        return Direction.LEFT; // Should not happen
    }

    @Override
    public void move() {
        int oldX = x;
        int oldY = y;
        updateBoost(System.nanoTime()); // Update boost status

        reactProximity(); // AI decision making

        if (!jump) {
            super.move(); // Calls GameObjectModel's move, which updates x, y based on velocity

            // Add a path segment if moved
            if (oldX != x || oldY != y) {
                if (alive && (velocityX != 0 || velocityY != 0)) {
                    path.add(new PathSegment(oldX, oldY, x, y));
                }
            }
        } else {
            // Jumping logic
            if (velocityX > 0) {
                x += JUMPHEIGHT;
            } else if (velocityX < 0) {
                x -= JUMPHEIGHT;
            } else if (velocityY > 0) {
                y += JUMPHEIGHT;
            } else if (velocityY < 0) {
                y -= JUMPHEIGHT;
            }
            jump = false; // Reset jump state
        }
        accelerate(); // Check bounds and update alive status
        clip(); // Keep within bounds
    }

    @Override
    public void setDirection(Direction direction) {
        // AI's direction setting logic
        int currentVel = Math.max(Math.abs(velocityX), Math.abs(velocityY));
        if (currentVel == 0) currentVel = startVel; // If not moving, assume startVel

        switch (direction) {
            case UP:
                if (this.velocityY == 0) { // Only change if not moving vertically
                    this.velocityX = 0;
                    this.velocityY = -currentVel;
                }
                break;
            case DOWN:
                if (this.velocityY == 0) {
                    this.velocityX = 0;
                    this.velocityY = currentVel;
                }
                break;
            case LEFT:
                if (this.velocityX == 0) { // Only change if not moving horizontally
                    this.velocityX = -currentVel;
                    this.velocityY = 0;
                }
                break;
            case RIGHT:
                if (this.velocityX == 0) {
                    this.velocityX = currentVel;
                    this.velocityY = 0;
                }
                break;
        }
    }
}
