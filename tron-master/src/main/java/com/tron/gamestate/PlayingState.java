package com.tron.gamestate;

import com.tron.main.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import com.tron.view.GameView; // Import GameView

public class PlayingState implements GameState {

    private GameStateManager gsm;
    private int gameMode; // 1 for Survival, 2 for Two-Player, 3 for Story
    private TronMapModel currentMap; // The active game map model
    private GameView gameView; // The view for rendering the game

    public PlayingState(GameStateManager gsm, int gameMode) {
        this.gsm = gsm;
        this.gameMode = gameMode;
        switch (gameMode) {
            case 1: // Survival
                currentMap = new TronMapSurvivalModel(1); // 1 player for survival
                break;
            case 2: // Two-Player
                // currentMap = new TronMapTwoPlayerModel(2); // will implement later
                break;
            case 3: // Story
                // currentMap = new TronMapStoryModel(1); // will implement later
                break;
            default:
                // Handle invalid game mode
                System.err.println("Invalid game mode: " + gameMode);
                gsm.pop(); // Go back to previous state
                break;
        }
        gameView = new GameView(currentMap); // Initialize the GameView with the current map model
    }

    @Override
    public void init() {
        gc.strokeText("Entering PlayingState for mode: " + gameMode + " Score: " + currentMap.getScorePlayer1(), Main.WINDOW_WIDTH / 2 - 50, Main.WINDOW_HEIGHT / 2);
        if (currentMap != null) {
            currentMap.reset(); // Initialize game map, players based on gameMode
        }
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
        if (currentMap != null) {
            currentMap.tick(dt);
            // Check if game is over
            if (!currentMap.isGameRunning()) {
                gsm.pop(); // Go back to previous state (e.g., PlayMenuState) when game ends
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.clearRect(0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT); // Clear canvas
        if (currentMap != null) {
            gameView.render(gc); // Delegate rendering to GameView
            // For now, drawing text for score over the game view
            gc.strokeText("Playing Game Mode: " + gameMode + " Score: " + currentMap.getScorePlayer1(), 10, Main.MAP_DIMENSION + 20); // Position below map
        } else {
            gc.strokeText("No Map Loaded for mode: " + gameMode, Main.WINDOW_WIDTH / 2 - 50, Main.WINDOW_HEIGHT / 2);
        }
    }
}
