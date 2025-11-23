package com.tron.model;

import com.tron.model.PlayerModel.Direction;
import com.tron.model.PlayerModel.TronColor;

public class PlayerHumanModel extends PlayerModel {

    public PlayerHumanModel(int x, int y, int velocityX, int velocityY, int width, int height, TronColor color) {
        super(x, y, velocityX, velocityY, width, height, color);
    }

    @Override
    public void move() {
        int oldX = x;
        int oldY = y;
        updateBoost(System.nanoTime()); // Update boost status
        
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
        // Only set if not opposing current motion
        switch (direction) {
            case UP:
                if (this.velocityY == 0) {
                    this.velocityX = 0;
                    this.velocityY = -startVel;
                }
                break;
            case DOWN:
                if (this.velocityY == 0) {
                    this.velocityX = 0;
                    this.velocityY = startVel;
                }
                break;
            case LEFT:
                if (this.velocityX == 0) {
                    this.velocityX = -startVel;
                    this.velocityY = 0;
                }
                break;
            case RIGHT:
                if (this.velocityX == 0) {
                    this.velocityX = startVel;
                    this.velocityY = 0;
                }
                break;
        }
    }
}
