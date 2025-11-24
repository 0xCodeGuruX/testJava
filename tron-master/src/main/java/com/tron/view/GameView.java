package com.tron.view;

import com.tron.model.PlayerModel;
import com.tron.model.PathSegment;
import com.tron.model.TronMapModel;
import com.tron.main.Main; // Add this import
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class GameView {

    private TronMapModel mapModel;

    public GameView(TronMapModel mapModel) {
        this.mapModel = mapModel;
    }

    public void render(GraphicsContext gc) {
        // Render map boundaries
        gc.setStroke(Color.WHITE);
        gc.strokeRect(0, 0, Main.MAP_DIMENSION, Main.MAP_DIMENSION);

        // Render players and their paths
        for (PlayerModel player : mapModel.getPlayers()) {
            if (player != null) {
                // Set player color
                Color playerColor = getFXColor(player.getColor());
                gc.setFill(playerColor);
                gc.setStroke(playerColor);

                // Render player "head"
                gc.fillRect(player.getX() - PlayerModel.WIDTH / 2, 
                            player.getY() - PlayerModel.HEIGHT / 2, 
                            PlayerModel.WIDTH, PlayerModel.HEIGHT);

                // Render player path
                for (PathSegment segment : player.getPath()) {
                    gc.setLineWidth(PlayerModel.WIDTH); // Path has same width as player
                    gc.strokeLine(segment.getStartX(), segment.getStartY(), 
                                  segment.getEndX(), segment.getEndY());
                }
            }
        }
    }

    // Helper to convert TronColor enum to JavaFX Color
    private Color getFXColor(PlayerModel.TronColor tronColor) {
        switch (tronColor) {
            case BLUE: return Color.BLUE;
            case ORANGE: return Color.ORANGE;
            case GREEN: return Color.LIME; // Using LIME for brighter green
            case RED: return Color.RED;
            case YELLOW: return Color.YELLOW;
            case PINK: return Color.PINK;
            default: return Color.WHITE;
        }
    }
}
