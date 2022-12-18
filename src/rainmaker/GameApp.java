package rainmaker;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GameApp extends Application {

    final static int GAME_HEIGHT = 800;
    final static int GAME_WIDTH = 800;
    final static double WIND_SPEED = .5;

    @Override
    public void start(Stage stage) {

        Game game = new Game();
        Scene scene = new Scene(game, GAME_WIDTH, GAME_HEIGHT);
        setupWindow(game);

        AnimationTimer timer = new AnimationTimer() {
            double old = -1;

            @Override
            public void handle(long nano) {
                if (old < 0) old = nano;
                double delta = (nano - old) / 1e9;
                old = nano;

                game.checkCloudsBounds();
                game.handleNumberOfClouds();
                game.createBlimpRandomly();
                game.updateGameBounds();
                game.checkPondAndCloudDistance(delta);
                game.helicopterBlimpRefuel(delta);
                game.removeBlimpFromScene();


                for (Node n : game.getChildren()) {
                    if (n instanceof Updatable)
                        ((Updatable) n).update(delta);
                }


                if (game.checkWin() || game.checkFuelLost()) {
                    handleWinLoss(this, stage, game, scene);
                }
            }
        };

        handleKeyPresses(scene, game);

        scene.setFill(Color.BLACK);
        stage.setScene(scene);
        stage.setTitle("RainMaker");
        timer.start();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    void setupWindow(Game game) {
        BackgroundImage myBI = new BackgroundImage(new Image("output.jpg"),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        game.setBackground(new Background(myBI));
        game.setScaleY(-1);

    }

    void handleWinLoss(AnimationTimer timer, Stage stage, Game game,
                       Scene scene) {
        timer.stop();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Play again?",
                ButtonType.YES, ButtonType.NO);

        alert.setOnHidden(evt -> {
            if (alert.getResult() == ButtonType.YES) {
                System.out.println("reset");
                scene.setFill(Color.BLACK);
                game.reset();
                timer.start();
            } else
                stage.close();

        });
        alert.show();
    }

    void handleKeyPresses(Scene scene, Game game) {
        //if the up arrow is pressed move up
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case UP -> game.moveForward();
                case DOWN -> game.moveBackward();
                case RIGHT -> game.moveRight();
                case LEFT -> game.moveLeft();
                case R -> game.reset();
                case I -> game.ignition();
                case B -> game.toggleBoundVisibility();
                case SPACE -> game.seedCloud();
                default -> {
                }
            }
        });
    }
}


