package com.tron.model;

import com.tron.model.PlayerModel.TronColor; // Add this import

import java.util.ArrayList;
import java.util.List;

public class TronMapSurvivalModel extends TronMapModel {

    private ScoreModel scoreManager; // Manages high scores
    private String highScoresFilename = "HighScores.txt"; // Hardcoded for now, will make configurable later
    
    public TronMapSurvivalModel(int numPlayers) {
        super(numPlayers);
        // Initialize score manager
        scoreManager = new ScoreModel(highScoresFilename);
    }

    @Override
    public void reset() {
        // Clear existing players
        players.clear();

        // Create human player (only one in survival mode)
        int[] start = getRandomStart();
        humanPlayer = new PlayerHumanModel(start[0], start[1], start[2], start[3], 
                                           PlayerModel.WIDTH, PlayerModel.HEIGHT, PlayerModel.TronColor.CYAN);
        players.add(humanPlayer);
        
        // Reset scores
        scorePlayer1 = 0;
        
        gameRunning = true;
    }

    @Override
    public void tick(double dt) {
        if (!gameRunning) return;

        // Move all players
        for (PlayerModel p : players) {
            if (p != null && p.isAlive()) {
                // Ensure bounds are set for collision detection (Model's responsibility)
                p.setBounds(MAPWIDTH, MAPHEIGHT); 
                p.move();
            }
        }

        // Check for collisions
        boolean playerCrashed = false;
        for (int i = 0; i < players.size(); i++) {
            PlayerModel p1 = players.get(i);
            if (p1 == null || !p1.isAlive()) continue;

            // Check collision with other players (and their paths)
            playerCrashed = p1.checkCrash(players); // PlayerModel handles specific crash logic

            if (!p1.isAlive()) {
                // If the human player crashed, stop the game
                if (p1 == humanPlayer) {
                    gameRunning = false;
                    addScore(0, scorePlayer1); // Add current score to high scores
                }
            }
        }

        // Update score if game is running
        if (gameRunning) {
            scorePlayer1++; // Increment score for survival mode
        }
    }

    @Override
    public void updateScores() {
        // In survival mode, score is updated continuously in tick
        // This method could be used for other game modes if needed
    }

    @Override
    public void addScore(int playerIndex, int scoreToAdd) {
        // In survival mode, we add the final score of the human player
        if (playerIndex == 0) { // Assuming playerIndex 0 is the human player
            scoreManager.addHighScore(scoreToAdd);
        }
    }

    public List<Integer> getHighScores() {
        return scoreManager.getHighScores();
    }
}
