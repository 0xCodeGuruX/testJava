package com.tron.gamestate;

import com.tron.main.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class PlayingState implements GameState {

    private GameStateManager gsm;
    private int gameMode; // 1 for Survival, 2 for Two-Player, 3 for Story

    public PlayingState(GameStateManager gsm, int gameMode) {
        this.gsm = gsm;
        this.gameMode = gameMode;
    }

    @Override
    public void init() {
        System.out.println("Entering PlayingState for mode: " + gameMode);
        // TODO: Initialize game map, players based on gameMode
    }

    @Override
    public void handleInput() {
        Main.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                // TODO: Pause menu or return to PlayMenuState
                gsm.pop(); // For now, just pop back
            }
            // TODO: Handle player movement input
        });
    }

    @Override
    public void update(double dt) {
        // TODO: Update game logic (player movement, collisions, etc.)
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.clearRect(0, 0, Main.WIDTH, Main.HEIGHT); // Clear canvas
        // TODO: Render game elements (map, players, scores)
        gc.strokeText("Playing Game Mode: " + gameMode, Main.WIDTH / 2 - 50, Main.HEIGHT / 2);
    }
}
