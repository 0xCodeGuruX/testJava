package com.tron.model;

import com.tron.model.PlayerModel.TronColor; // Add this import
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TronMapStoryModel extends TronMapModel {

    private boolean levelWon = false;
    private boolean gameOver = false;
    private int currentLevel = 1;
    private long levelTransitionTimer = 0; // In nanoseconds
    private final long LEVEL_TRANSITION_DURATION_NANO = 1_000_000_000L; // 1 second

    private final int MAX_AI_PLAYERS = 7; // Max players: 1 human + 7 AI

    public TronMapStoryModel(int numInitialPlayers) {
        super(numInitialPlayers);
        // Initially, numInitialPlayers will be 1 (for human player), AI will be added dynamically
        this.players = new ArrayList<>();
    }

    @Override
    public void reset() {
        levelWon = false;
        gameOver = false;
        currentLevel = 1;
        scorePlayer1 = 0; // Total score for story mode
        levelTransitionTimer = 0;
        gameRunning = true;
        addPlayersForLevel();
    }

    private void addPlayersForLevel() {
        players.clear(); // Clear previous players

        // Human player
        int[] startHuman = getRandomStart();
        humanPlayer = new PlayerHumanModel(startHuman[0], startHuman[1], startHuman[2], startHuman[3],
                                           PlayerModel.WIDTH, PlayerModel.HEIGHT, PlayerModel.TronColor.BLUE);
        players.add(humanPlayer);

        // AI players (currentLevel - 1, starting from 1 for level 1)
        for (int i = 0; i < currentLevel && i < MAX_AI_PLAYERS; i++) {
            int[] startAI = getRandomStart();
            PlayerAIModel aiPlayer = new PlayerAIModel(startAI[0], startAI[1], startAI[2], startAI[3],
                                                     PlayerModel.WIDTH, PlayerModel.HEIGHT, availableColors.get((i + 1) % availableColors.size()));
            players.add(aiPlayer);
        }

        // Set all players for AI's
        for (PlayerModel p : players) {
            if (p instanceof PlayerAIModel) {
                ((PlayerAIModel) p).setAllPlayers(players);
            }
        }
    }

    @Override
    public void tick(double dt) {
        if (gameOver) return;

        if (levelWon) {
            if (levelTransitionTimer == 0) {
                levelTransitionTimer = System.nanoTime();
            }
            if (System.nanoTime() - levelTransitionTimer >= LEVEL_TRANSITION_DURATION_NANO) {
                nextLevel();
                levelTransitionTimer = 0;
            }
            return;
        }

        // Move all players
        for (PlayerModel p : players) {
            if (p != null && p.isAlive()) {
                p.setBounds(MAPWIDTH, MAPHEIGHT);
                p.move();
            }
        }

        // Check for collisions
        // The human player is always at index 0 for convenience
        boolean humanPlayerCrashed = !humanPlayer.isAlive();
        
        if (humanPlayer.isAlive()) { // Only check if not already crashed
            humanPlayerCrashed = humanPlayer.checkCrash(players);
        }
        
        if (humanPlayerCrashed) {
            gameOver = true;
            gameRunning = false;
            addScore(0, scorePlayer1); // Human player lost, pass current score
            return;
        }

        // Check if all AI players are defeated
        List<PlayerModel> aliveAIPlayers = players.stream()
                                                 .filter(p -> p instanceof PlayerAIModel && p.isAlive())
                                                 .collect(Collectors.toList());

        if (aliveAIPlayers.isEmpty() && currentLevel <= MAX_AI_PLAYERS) { // All AI defeated, and not max level
            levelWon = true;
            gameRunning = false; // Pause game for transition
            addScore(0, scorePlayer1); // Human player won the level, pass current score
        }
        
        // Update score (continuous for story mode?)
        // Original game increased score on level completion, not continuously
    }

    @Override
    public void updateScores() {
        // Scores are updated at the end of each level in addScore()
    }

    @Override // Override the abstract method from TronMapModel
    public void addScore(int playerIndex, int scoreToAdd) {
        if (levelWon) {
            scorePlayer1 += 50 * currentLevel; // Score based on level completed
            // Additional logic for high score, if any specific to story mode
        }
        // If gameOver, score is just final scorePlayer1
    }

    private void nextLevel() {
        if (currentLevel < MAX_AI_PLAYERS) {
            currentLevel++;
            addPlayersForLevel(); // Reinitialize players for the new level
            levelWon = false;
            gameRunning = true;
        } else {
            // All levels completed, overall game won
            gameOver = true;
            gameRunning = false;
            // Potentially add final score to high scores or show "You Won!" screen
        }
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public boolean isLevelWon() {
        return levelWon;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
