package com.tron.gamestate;

import com.tron.model.TronMapModel;
import com.tron.model.TronMapSurvivalModel;
import com.tron.model.TronMapTwoPlayerModel;
import com.tron.model.TronMapStoryModel;
import com.tron.model.PlayerModel;
import com.tron.model.PlayerAIModel; // Import PlayerAIModel
import com.tron.model.PlayerHumanModel; // Import PlayerHumanModel
import com.tron.main.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import com.tron.view.GameView;
import com.tron.view.GameResultView;
import com.tron.view.ScoreView; // Import ScoreView

public class PlayingState implements GameState {

    private GameStateManager gsm;
    private int gameMode; // 1 for Survival, 2 for Two-Player, 3 for Story
    private TronMapModel currentMap; // The active game map model
    private GameView gameView; // The view for rendering the game
    private GameResultView gameResultView; // The view for rendering game outcomes
    private ScoreView scoreView; // The view for rendering scores and info

    public PlayingState(GameStateManager gsm, int gameMode) {
        this.gsm = gsm;
        this.gameMode = gameMode;
        switch (gameMode) {
            case 1: // Survival
                currentMap = new TronMapSurvivalModel(1); // 1 player for survival
                break;
            case 2: // Two-Player
                currentMap = new TronMapTwoPlayerModel(2); // 2 players for two-player
                break;
            case 3: // Story
                currentMap = new TronMapStoryModel(1); // 1 player for story mode (human)
                break;
            default:
                // Handle invalid game mode
                System.err.println("Invalid game mode: " + gameMode);
                gsm.pop(); // Go back to previous state
                break;
        }
        gameView = new GameView(currentMap); // Initialize the GameView with the current map model
        gameResultView = new GameResultView(); // Initialize GameResultView
        scoreView = new ScoreView(); // Initialize ScoreView
    }

    @Override
    public void init() {
        System.out.println("Entering PlayingState for mode: " + gameMode);
        if (currentMap != null) {
            currentMap.reset(); // Initialize game map, players based on gameMode
            // Set all players for AI models, if any
            for (PlayerModel p : currentMap.getPlayers()) {
                if (p instanceof PlayerAIModel) {
                    ((PlayerAIModel) p).setAllPlayers(currentMap.getPlayers());
                }
            }
        }
    }

    @Override
    public void handleInput() {
        Main.getScene().setOnKeyPressed(event -> {
            if (currentMap != null && currentMap.isGameRunning() && currentMap.humanPlayer != null) {
                switch (event.getCode()) {
                    case LEFT:
                        currentMap.humanPlayer.setDirection(PlayerModel.Direction.LEFT);
                        break;
                    case RIGHT:
                        currentMap.humanPlayer.setDirection(PlayerModel.Direction.RIGHT);
                        break;
                    case UP:
                        currentMap.humanPlayer.setDirection(PlayerModel.Direction.UP);
                        break;
                    case DOWN:
                        currentMap.humanPlayer.setDirection(PlayerModel.Direction.DOWN);
                        break;
                    case SPACE:
                        currentMap.humanPlayer.jump();
                        break;
                    case B:
                        currentMap.humanPlayer.startBoost();
                        break;
                    // Player 2 controls
                    case A:
                        if (currentMap instanceof TronMapTwoPlayerModel) {
                            ((TronMapTwoPlayerModel) currentMap).getPlayer2().setDirection(PlayerModel.Direction.LEFT);
                        }
                        break;
                    case D:
                        if (currentMap instanceof TronMapTwoPlayerModel) {
                            ((TronMapTwoPlayerModel) currentMap).getPlayer2().setDirection(PlayerModel.Direction.RIGHT);
                        }
                        break;
                    case W:
                        if (currentMap instanceof TronMapTwoPlayerModel) {
                            ((TronMapTwoPlayerModel) currentMap).getPlayer2().setDirection(PlayerModel.Direction.UP);
                        }
                        break;
                    case S:
                        if (currentMap instanceof TronMapTwoPlayerModel) {
                            ((TronMapTwoPlayerModel) currentMap).getPlayer2().setDirection(PlayerModel.Direction.DOWN);
                        }
                        break;
                    case Q:
                        if (currentMap instanceof TronMapTwoPlayerModel) {
                            ((TronMapTwoPlayerModel) currentMap).getPlayer2().jump();
                        }
                        break;
                    case DIGIT1: // Key '1' for boost
                        if (currentMap instanceof TronMapTwoPlayerModel) {
                            ((TronMapTwoPlayerModel) currentMap).getPlayer2().startBoost();
                        }
                        break;
                    case ESCAPE:
                        // TODO: Implement pause menu or return to PlayMenuState
                        gsm.pop(); // For now, just pop back
                        break;
                    default:
                        break;
                }
            } else if (event.getCode() == KeyCode.ESCAPE) { // Allow escape to exit even if game not running
                gsm.pop();
            }
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

            // Render game results if game is not running
            if (!currentMap.isGameRunning()) {
                gameResultView.render(gc, currentMap);
            }
            
            // For now, drawing text for score over the game view
            scoreView.render(gc, currentMap); // Delegate score rendering to ScoreView
        } else {
            gc.strokeText("No Map Loaded for mode: " + gameMode, Main.WINDOW_WIDTH / 2 - 50, Main.WINDOW_HEIGHT / 2);
        }
    }
}
