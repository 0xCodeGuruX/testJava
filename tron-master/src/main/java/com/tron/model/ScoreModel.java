package com.tron.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ScoreModel {

    private List<Integer> highScores;
    private String filename;

    public ScoreModel(String filename) {
        this.filename = filename;
        this.highScores = new ArrayList<>();
        loadHighScores();
    }

    private void loadHighScores() {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            String line;
            int count = 0;
            while ((line = in.readLine()) != null && count < 10) {
                line = line.trim();
                highScores.add(Integer.parseInt(line));
                count++;
            }
        } catch (IOException e) {
            System.err.println("Error loading high scores from " + filename + ": " + e.getMessage());
            // Initialize with empty list if file not found or error occurs
            highScores = new ArrayList<>(); 
        } catch (NumberFormatException e) {
            System.err.println("Error parsing high score from " + filename + ": " + e.getMessage());
            highScores = new ArrayList<>();
        }
    }

    public void addHighScore(int newScore) {
        highScores.add(newScore);
        // Sort in descending order
        Collections.sort(highScores, Collections.reverseOrder());
        
        // Keep only top 10 scores
        while (highScores.size() > 10) {
            highScores.remove(highScores.size() - 1);
        }
        saveHighScores();
    }

    private void saveHighScores() {
        try (PrintStream out = new PrintStream(new File(filename))) {
            for (int i = 0; i < highScores.size(); i++) {
                out.print(highScores.get(i));
                if (i != highScores.size() - 1) {
                    out.print('\n');
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving high scores to " + filename + ": " + e.getMessage());
        }
    }

    public List<Integer> getHighScores() {
        // Return a copy to prevent external modification
        return new ArrayList<>(highScores); 
    }
}
