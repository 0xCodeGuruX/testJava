package com.tron.model;

import java.util.ArrayList;
import java.util.List;

public class TronMapTwoPlayerModel extends TronMapModel {

    private PlayerHumanModel player2; // Second human player
    private MatchOutcome lastMatchOutcome;

    public TronMapTwoPlayerModel(int numPlayers) {
        super(numPlayers);
        // Ensure the players list is cleared or properly sized
        this.players = new ArrayList<>(numPlayers);
    }

    @Override
    public void reset() {
        lastMatchOutcome = MatchOutcome.NONE;

        // Clear existing players
        players.clear();

        // Player 1
        int[] start1 = getRandomStart();
        humanPlayer = new PlayerHumanModel(start1[0], start1[1], start1[2], start1[3],
                                           PlayerModel.WIDTH, PlayerModel.HEIGHT, TronColor.CYAN);
        players.add(humanPlayer);

        // Player 2
        int[] start2 = getRandomStart();
        player2 = new PlayerHumanModel(start2[0], start2[1], start2[2], start2[3],
                                           PlayerModel.WIDTH, PlayerModel.HEIGHT, TronColor.PINK);
        players.add(player2);

        // Make sure all players know about each other for collision
        // (This might be better managed by a central GameWorld or CollisionManager)
        humanPlayer.setAllPlayers(players);
        player2.setAllPlayers(players);
        
        gameRunning = true;
    }

    @Override
    public void tick(double dt) {
        if (!gameRunning) return;

        // Move all players
        for (PlayerModel p : players) {
            if (p != null && p.isAlive()) {
                p.setBounds(MAPWIDTH, MAPHEIGHT);
                p.move();
            }
        }

        // Check for collisions
        boolean p1Crashed = !humanPlayer.isAlive();
        boolean p2Crashed = !player2.isAlive();

        // If not already crashed, check for new crashes
        if (humanPlayer.isAlive()) {
            p1Crashed = humanPlayer.checkCrash(players);
        }
        if (player2.isAlive()) {
            p2Crashed = player2.checkCrash(players);
        }

        if (p1Crashed || p2Crashed) {
            gameRunning = false; // Round ends
            addScore(p1Crashed, p2Crashed); // Update scores based on who crashed
        }
    }

    @Override
    public void updateScores() {
        // Scores are updated at the end of each round in addScore()
    }

    // This method is called when a round ends
    private void addScore(boolean p1Crashed, boolean p2Crashed) {
        if (!p1Crashed && p2Crashed) { // Player 1 wins
            scorePlayer1++;
            lastMatchOutcome = MatchOutcome.P1_WINS;
        } else if (p1Crashed && !p2Crashed) { // Player 2 wins
            scorePlayer2++;
            lastMatchOutcome = MatchOutcome.P2_WINS;
        } else { // Both crashed or other tie condition
            lastMatchOutcome = MatchOutcome.TIE;
        }
    }
    
    // Public method to reset cumulative scores for a new game
    public void restartGame() {
        scorePlayer1 = 0;
        scorePlayer2 = 0;
        lastMatchOutcome = MatchOutcome.NONE;
    }

    public PlayerHumanModel getPlayer1() {
        return humanPlayer;
    }

    public PlayerHumanModel getPlayer2() {
        return player2;
    }

    public MatchOutcome getLastMatchOutcome() {
        return lastMatchOutcome;
    }

    public enum MatchOutcome {
        NONE, P1_WINS, P2_WINS, TIE
    }
}
