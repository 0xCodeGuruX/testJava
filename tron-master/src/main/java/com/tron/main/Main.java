package com.tron.main;

import com.tron.gamestate.GameStateManager;
import com.tron.gamestate.MenuState;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static final int WINDOW_WIDTH = 500;
    public static final int WINDOW_HEIGHT = 560; // 500 for map + 60 for UI below map
    public static final int MAP_DIMENSION = 500; // Map is square 500x500

    private GameStateManager gsm;
    private long lastTime = 0;

    private static Scene currentScene;

    public static Scene getScene() {
        return currentScene;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tron");

        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        
        Scene scene = new Scene(root);
        currentScene = scene; // Store the scene
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        gsm = GameStateManager.getInstance();
        // Here, we could pass the GameStateManager to the MenuState constructor
        // or just let the MenuState get the instance itself.
        gsm.push(new MenuState(gsm));
        
        lastTime = System.nanoTime();
        
        new AnimationTimer() {
            @Override
            public void handle(long currentTime) {
                double deltaTime = (currentTime - lastTime) / 1000000000.0;
                lastTime = currentTime;

                gsm.update(deltaTime);
                gsm.render(gc);
                gsm.handleInput(); // Handle input once per frame
            }
        }.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
