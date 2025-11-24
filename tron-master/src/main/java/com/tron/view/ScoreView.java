package com.tron.view;

import com.tron.main.Main;
import com.tron.model.TronMapModel;
import com.tron.model.TronMapSurvivalModel;
import com.tron.model.TronMapTwoPlayerModel;
import com.tron.model.TronMapStoryModel;
import com.tron.model.PlayerModel; // Add this import
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ScoreView {

    public ScoreView() {
        // No complex initialization needed for now
    }

    public void render(GraphicsContext gc, TronMapModel mapModel) {
        if (mapModel == null) return;

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        gc.setFill(Color.WHITE);

        // Clear the score area first
        gc.clearRect(0, Main.MAP_DIMENSION, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT - Main.MAP_DIMENSION);

        if (mapModel instanceof TronMapSurvivalModel) {
            gc.fillText("Score: " + mapModel.getScorePlayer1() + 
                        "   Boost: " + mapModel.getHumanPlayer().getBoostsLeft(), 
                        10, Main.MAP_DIMENSION + 20);
        } else if (mapModel instanceof TronMapTwoPlayerModel) {
            TronMapTwoPlayerModel twoPlayerModel = (TronMapTwoPlayerModel) mapModel;
            gc.fillText("P1 Score: " + twoPlayerModel.getScorePlayer1() + 
                        "   Boost: " + twoPlayerModel.getHumanPlayer().getBoostsLeft(), 
                        10, Main.MAP_DIMENSION + 20);
            gc.fillText("P2 Score: " + twoPlayerModel.getScorePlayer2() + 
                        "   Boost: " + twoPlayerModel.getPlayer2().getBoostsLeft(), 
                        10, Main.MAP_DIMENSION + 40);
        } else if (mapModel instanceof TronMapStoryModel) {
            TronMapStoryModel storyModel = (TronMapStoryModel) mapModel;
            gc.fillText("Score: " + storyModel.getScorePlayer1() + 
                        "   Level: " + storyModel.getCurrentLevel() +
                        "   Boost: " + storyModel.getHumanPlayer().getBoostsLeft(), 
                        10, Main.MAP_DIMENSION + 20);
        }
    }
}
