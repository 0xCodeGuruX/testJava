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

    public static final int WIDTH = 500;
    public static final int HEIGHT = 560;

    private GameStateManager gsm;
    private long lastTime = 0;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tron");

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        gsm = GameStateManager.getInstance();
        gsm.push(new MenuState(gsm));
        
        lastTime = System.nanoTime();
        
        new AnimationTimer() {
            @Override
            public void handle(long currentTime) {
                double deltaTime = (currentTime - lastTime) / 1000000000.0;
                lastTime = currentTime;

                gsm.update(deltaTime);
                gsm.render(gc);
                gsm.handleInput();
            }
        }.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
