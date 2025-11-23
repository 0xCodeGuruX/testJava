package com.tron.gamestate;

import com.tron.main.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;

public class MenuState implements GameState {

    private GameStateManager gsm;
    private Image mainMenuImage;
    private Image playMenuImage;
    private Image instructionsPageImage;

    private List<Image> mainMenuItems;
    private List<Image> playMenuItems;

    private boolean showPlayMenu = false;
    private boolean showInstructions = false;

    private int selectedMainMenuOption = 0; // 0: Play, 1: Instructions, 2: Quit
    private int selectedPlayMenuOption = 0; // 0: Story, 1: Survival, 2: Two Player

    public MenuState(GameStateManager gsm) {
        this.gsm = gsm;
        loadImages();
    }

    private void loadImages() {
        try {
            mainMenuImage = new Image(getClass().getResourceAsStream("/main_menu.png")); // This should be tron0_0.jpg, changing later
            playMenuImage = new Image(getClass().getResourceAsStream("/play_menu.jpg"));
            instructionsPageImage = new Image(getClass().getResourceAsStream("/instructions_page.png"));

            mainMenuItems = new ArrayList<>();
            mainMenuItems.add(new Image(getClass().getResourceAsStream("/play_before.png")));
            mainMenuItems.add(new Image(getClass().getResourceAsStream("/instructions_before.png")));
            mainMenuItems.add(new Image(getClass().getResourceAsStream("/quit_before.png")));

            playMenuItems = new ArrayList<>();
            playMenuItems.add(new Image(getClass().getResourceAsStream("/story.png")));
            playMenuItems.add(new Image(getClass().getResourceAsStream("/survival.png")));
            playMenuItems.add(new Image(getClass().getResourceAsStream("/two_player.png")));

        } catch (Exception e) {
            System.err.println("Error loading menu images: " + e.getMessage());
        }
    }

    @Override
    public void init() {
        // Any specific initialization for the menu
    }

    @Override
    public void handleInput() {
        Main.getScene().setOnKeyPressed(event -> {
            if (showPlayMenu) {
                handlePlayMenuInput(event);
            } else if (showInstructions) {
                handleInstructionsInput(event);
            } else {
                handleMainMenuInput(event);
            }
        });
    }

    private void handleMainMenuInput(KeyEvent event) {
        if (event.getCode() == KeyCode.UP) {
            selectedMainMenuOption = (selectedMainMenuOption - 1 + mainMenuItems.size()) % mainMenuItems.size();
        } else if (event.getCode() == KeyCode.DOWN) {
            selectedMainMenuOption = (selectedMainMenuOption + 1) % mainMenuItems.size();
        } else if (event.getCode() == KeyCode.ENTER) {
            if (selectedMainMenuOption == 0) { // Play
                showPlayMenu = true;
                showInstructions = false;
            } else if (selectedMainMenuOption == 1) { // Instructions
                showInstructions = true;
                showPlayMenu = false;
            } else if (selectedMainMenuOption == 2) { // Quit
                System.exit(0);
            }
        }
    }

    private void handlePlayMenuInput(KeyEvent event) {
        if (event.getCode() == KeyCode.UP) {
            selectedPlayMenuOption = (selectedPlayMenuOption - 1 + playMenuItems.size()) % playMenuItems.size();
        } else if (event.getCode() == KeyCode.DOWN) {
            selectedPlayMenuOption = (selectedPlayMenuOption + 1) % playMenuItems.size();
        } else if (event.getCode() == KeyCode.ENTER) {
            if (selectedPlayMenuOption == 0) { // Story
                gsm.setState(new PlayingState(gsm, 3)); // 3 for Story
            } else if (selectedPlayMenuOption == 1) { // Survival
                gsm.setState(new PlayingState(gsm, 1)); // 1 for Survival
            } else if (selectedPlayMenuOption == 2) { // Two Player
                gsm.setState(new PlayingState(gsm, 2)); // 2 for Two Player
            }
        } else if (event.getCode() == KeyCode.ESCAPE) { // Back to main menu
            showPlayMenu = false;
        }
    }

    private void handleInstructionsInput(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) { // Back to main menu
            showInstructions = false;
        }
    }

    @Override
    public void update(double dt) {
        // Menu state typically doesn't need continuous updates
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.clearRect(0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT); // Clear canvas

        if (showInstructions) {
            if (instructionsPageImage != null) {
                gc.drawImage(instructionsPageImage, 0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
            }
        } else if (showPlayMenu) {
            if (playMenuImage != null) {
                gc.drawImage(playMenuImage, 0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
            }
            // Render Play menu options
            for (int i = 0; i < playMenuItems.size(); i++) {
                Image item = playMenuItems.get(i);
                double x = (Main.WINDOW_WIDTH - item.getWidth()) / 2;
                double y = Main.WINDOW_HEIGHT / 2.0 + (i * (item.getHeight() + 10)); // Adjust positioning
                if (i == selectedPlayMenuOption) {
                    gc.setGlobalAlpha(0.7); // Highlight selected
                } else {
                    gc.setGlobalAlpha(1.0);
                }
                gc.drawImage(item, x, y);
                gc.setGlobalAlpha(1.0);
            }
        } else {
            if (mainMenuImage != null) {
                gc.drawImage(mainMenuImage, 0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
            }
            // Render Main menu options
            for (int i = 0; i < mainMenuItems.size(); i++) {
                Image item = mainMenuItems.get(i);
                double x = (Main.WINDOW_WIDTH - item.getWidth()) / 2;
                double y = Main.WINDOW_HEIGHT / 2.0 + (i * (item.getHeight() + 10)); // Adjust positioning
                if (i == selectedMainMenuOption) {
                    gc.setGlobalAlpha(0.7); // Highlight selected
                } else {
                    gc.setGlobalAlpha(1.0);
                }
                gc.drawImage(item, x, y);
                gc.setGlobalAlpha(1.0);
            }
        }
    }
}
