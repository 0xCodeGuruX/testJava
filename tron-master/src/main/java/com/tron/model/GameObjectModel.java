package com.tron.model;

public abstract class GameObjectModel {
    protected int x; // x and y coordinates upper left
    protected int y;

    protected int width; // width and height of the object
    protected int height;

    protected int velocityX; // Pixels to move each time move() is called.
    protected int velocityY;

    protected int rightBound; // Maximum permissible x, y values.
    protected int bottomBound;

    public GameObjectModel(int x, int y, int velocityX, int velocityY, int width, int height) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.width = width;
        this.height = height;
    }

    public void setBounds(int width, int height) {
        rightBound = width - this.width;
        bottomBound = height - this.height;
    }

    public void setXVelocity(int velocityX) {
        if (!(velocityX > 0 && this.velocityX < 0) && !(velocityX < 0 && this.velocityX > 0)) {
            this.velocityX = velocityX;
        }
    }

    public void setYVelocity(int velocityY) {
        if (!(velocityY > 0 && this.velocityY < 0) && !(velocityY < 0 && this.velocityY > 0)) {
            this.velocityY = velocityY;
        }
    }

    public void move() {
        x += velocityX;
        y += velocityY;

        accelerate(); // This will be implemented in concrete subclasses
        clip();
    }

    public void clip() {
        if (x < 0) x = 0;
        else if (x > rightBound) x = rightBound;

        if (y < 0) y = 0;
        else if (y > bottomBound) y = bottomBound;
    }
    
    // Abstract methods to be implemented by concrete game object models
    public abstract void accelerate();

    // These methods previously returned UI-specific types or related to UI,
    // they need to be redefined or removed based on the specific needs of the model.
    // For now, I'm keeping similar abstract methods as placeholders for model-specific logic.
    public abstract boolean isAlive(); // Renamed from getAlive for better clarity in model
    
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    // Path handling will be more complex, likely involving a list of points or segments, not UI Shapes.
    // For now, removing getPath() as Shape is a UI-related class.
    // Collision detection will also need to be purely based on coordinates/geometry.
}
