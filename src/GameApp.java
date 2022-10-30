import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameApp extends Application {


    private final static int GAME_HEIGHT = 800;
    private final static int GAME_WIDTH = 400;

    @Override
    public void start(Stage stage) {


        Game game = new Game();
        Scene scene = new Scene(game, GAME_WIDTH, GAME_HEIGHT);
        setupWindow(game, scene);


        scene.setFill(Color.BLACK);
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
    int helipadCenterX = 195;
    int helipadCenterY = 90;

    Game() {
        createGameObjects();
    }


    void createGameObjects() {
        int fuel = 25000;

        Pond pond = new Pond();
        Cloud cloud = new Cloud();
        Helipad helipad = new Helipad(helipadCenterX, helipadCenterY);
        Helicopter helicopter = new Helicopter(fuel, helipadCenterX,
                helipadCenterY);
        this.getChildren().addAll(pond, cloud, helipad, helicopter);

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


    Helipad(int padCenterX, int padCenterY) {
        Rectangle border = new Rectangle(145, 40, 100, 100);
        border.setStroke(Color.WHITE);

        Circle pad = new Circle(padCenterX, padCenterY, 40);
        pad.setStroke(Color.WHITE);

        this.getChildren().addAll(border, pad);
    }

}

class Helicopter extends GameObject {


    Helicopter(int fuel, int helipadCenterX, int helipadCenterY) {
        Circle base = new Circle(helipadCenterX, helipadCenterY, 10,
                Color.YELLOW);

        Line roter = new Line(195, 90, 195, 120);
        roter.setStroke(Color.YELLOW);
        roter.setStrokeWidth(2);

        GameText fuelText = new GameText("F:" + fuel, Color.YELLOW, 175, 75);

        this.getChildren().addAll(base, roter, fuelText);
    }

}

interface Updatable {

}

class GameText extends GameObject {
    Text gameText = new Text();

    GameText(String text, Paint color, int x, int y) {

        gameText.setText(text);
        gameText.setStroke(color);
        gameText.setX(x);
        gameText.setY(y);
        gameText.setScaleY(-1);

        this.getChildren().add(gameText);
    }

}

// 10-25-2022
// TODO:create helicopter object
//  be able to move it up and down


