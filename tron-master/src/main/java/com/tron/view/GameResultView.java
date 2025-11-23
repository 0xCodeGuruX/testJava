package com.tron.view;

import com.tron.model.TronMapModel;
import com.tron.model.TronMapTwoPlayerModel;
import com.tron.model.TronMapStoryModel;
import com.tron.main.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.IOException;

public class GameResultView {

    private Image gameOverImage;
    private Image winImage;
    private Image p1WinsImage;
    private Image p2WinsImage;
    private Image tieImage;

    public GameResultView() {
        try {
            gameOverImage = new Image(getClass().getResourceAsStream("/over.png"));
            winImage = new Image(getClass().getResourceAsStream("/win.png"));
            p1WinsImage = new Image(getClass().getResourceAsStream("/p1_wins.png"));
            p2WinsImage = new Image(getClass().getResourceAsStream("/p2_wins.png"));
            tieImage = new Image(getClass().getResourceAsStream("/tie.png"));
        } catch (Exception e) {
            System.err.println("Error loading game result images: " + e.getMessage());
        }
    }

    public void render(GraphicsContext gc, TronMapModel mapModel) {
        if (mapModel == null || mapModel.isGameRunning()) {
            return; // Only render results if game is not running
        }

        // Handle Story Mode results
        if (mapModel instanceof TronMapStoryModel) {
            TronMapStoryModel storyModel = (TronMapStoryModel) mapModel;
            if (storyModel.isGameOver()) {
                if (gameOverImage != null) {
                    gc.drawImage(gameOverImage, Main.WINDOW_WIDTH / 2 - gameOverImage.getWidth() / 2, Main.MAP_DIMENSION / 2 - gameOverImage.getHeight() / 2);
                }
            } else if (storyModel.isLevelWon()) {
                if (winImage != null) {
                    gc.drawImage(winImage, Main.WINDOW_WIDTH / 2 - winImage.getWidth() / 2, Main.MAP_DIMENSION / 2 - winImage.getHeight() / 2);
                }
            }
        }
        // Handle Two-Player Mode results
        else if (mapModel instanceof TronMapTwoPlayerModel) {
            TronMapTwoPlayerModel twoPlayerModel = (TronMapTwoPlayerModel) mapModel;
            TronMapTwoPlayerModel.MatchOutcome outcome = twoPlayerModel.getLastMatchOutcome();
            
            Image resultImage = null;
            if (outcome == TronMapTwoPlayerModel.MatchOutcome.P1_WINS && p1WinsImage != null) {
                resultImage = p1WinsImage;
            } else if (outcome == TronMapTwoPlayerModel.MatchOutcome.P2_WINS && p2WinsImage != null) {
                resultImage = p2WinsImage;
            } else if (outcome == TronMapTwoPlayerModel.MatchOutcome.TIE && tieImage != null) {
                resultImage = tieImage;
            }

            if (resultImage != null) {
                gc.drawImage(resultImage, Main.WINDOW_WIDTH / 2 - resultImage.getWidth() / 2, Main.MAP_DIMENSION / 2 - resultImage.getHeight() / 2);
            }
        }
        // Handle Survival Mode (Game Over only)
        else { // Assuming TronMapSurvivalModel if not other specific types
            if (gameOverImage != null && !mapModel.isGameRunning()) { // Check if game is indeed over
                 gc.drawImage(gameOverImage, Main.WINDOW_WIDTH / 2 - gameOverImage.getWidth() / 2, Main.MAP_DIMENSION / 2 - gameOverImage.getHeight() / 2);
            }
        }
    }
}
