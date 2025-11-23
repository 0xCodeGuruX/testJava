package com.tron.model;

import com.tron.model.PlayerModel.Direction;
import com.tron.model.PlayerModel.TronColor;

import java.util.List;
import java.util.Random;

public class PlayerAIModel extends PlayerModel {

    private int time = 40; // The number of steps before a random turn
    private List<PlayerModel> allPlayers; // List of all players including itself
    private Random rand = new Random();

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

        // Boosts randomly
        int r = rand.nextInt(100);
        if (r == 1) {
            startBoost();
        }

        // Logic to avoid walls and other players' paths
        // This is a complex part and will require careful adaptation from original
        // It involves checking distances to PathSegments of all players.

        // Placeholder for AI movement decision
        if (time <= 0) {
            int rando = rand.nextInt(4);
            Direction newDirection = null;
            if (rando == 0) newDirection = Direction.LEFT;
            else if (rando == 1) newDirection = Direction.RIGHT;
            else if (rando == 2) newDirection = Direction.UP;
            else if (rando == 3) newDirection = Direction.DOWN;

            if (newDirection != null) {
                setDirection(newDirection);
            }
            time = 40;
        }
        time--;
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
