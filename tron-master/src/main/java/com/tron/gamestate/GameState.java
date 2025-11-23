package com.tron.gamestate;

import javafx.scene.canvas.GraphicsContext;

public interface GameState {
    void init();
    void handleInput();
    void update(double dt);
    void render(GraphicsContext gc);
}
