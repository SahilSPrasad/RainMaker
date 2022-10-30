import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GameApp extends Application {

    private final static int GAME_HEIGHT = 800;
    private final static int GAME_WIDTH = 400;


    @Override
    public void start(Stage stage) throws Exception {

        Game game = new Game();
        Scene scene = new Scene(game, GAME_WIDTH, GAME_HEIGHT);

        setupWindow(game, scene);

        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    void setupWindow(Game game, Scene scene) {
        game.setScaleY(-1);
        scene.setFill(Color.BLACK);
    }
}

class Game extends Pane {

    Game() {
        createGameObjects();
    }


    void createGameObjects() {
        Pond pond = new Pond();
        Cloud cloud = new Cloud();
        Helipad helipad = new Helipad();
        this.getChildren().addAll(pond, cloud, helipad);

    }

}

abstract class GameObject extends Group {
    //im pretty sure they all share an update function
}

class Pond extends GameObject {

    Pond() {
        Circle pond = new Circle(50, 600, 15, Color.BLUE);
        this.getChildren().add(pond);
    }

}

class Cloud extends GameObject {

    Cloud() {
        Circle cloud = new Circle(100, 500, 30, Color.WHITE);
        this.getChildren().add(cloud);
    }

}

class Helipad extends GameObject {

    Helipad() {
        Rectangle border = new Rectangle(145, 40, 100, 100);
        border.setStroke(Color.WHITE);

        Circle pad = new Circle(195, 90, 40);
        pad.setStroke(Color.WHITE);

        this.getChildren().addAll(border, pad);
    }

}

class Helicopter extends GameObject {

    Helicopter() {
        Circle base = new Circle();
        Line roter = new Line();



    }


}

interface Updatable {

}

// 10-25-2022
// TODO:create helicopter object
//  be able to move it up and down


