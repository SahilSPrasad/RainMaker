import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class GameApp extends Application {

    private final static int GAME_HEIGHT = 800;
    private final static int GAME_WIDTH = 400;

    @Override
    public void start(Stage stage) {

        Game game = new Game();
        Scene scene = new Scene(game, GAME_WIDTH, GAME_HEIGHT);
        setupWindow(game, scene);

        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long nano) {
                game.updateGameBounds();

                for (Node n : game.getChildren()) {
                    if (n instanceof Updatable)
                        ((Updatable) n).update();
                }
            }
        };

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
                default -> {
                }
            }
        });

        scene.setFill(Color.BLACK);
        stage.setScene(scene);
        stage.setTitle("RainMaker");
        timer.start();
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
    int fuel = 25000;
    Pond pond = new Pond();
    Cloud cloud = new Cloud();
    Helipad helipad = new Helipad(helipadCenterX, helipadCenterY);
    Helicopter helicopter = new Helicopter(fuel, helipadCenterX,
            helipadCenterY);

    private boolean toggleBounds = false;
    Rectangle helicopterBounds = new Rectangle();
    Rectangle helipadBounds = new Rectangle();
    Rectangle cloudBounds = new Rectangle();

    Game() {
        initializeBounds();
        createGameObjects();
    }

    void createGameObjects() {
        this.getChildren().addAll(pond, cloud, helipad, helicopter,
                helicopterBounds, helipadBounds, cloudBounds);

    }

    void moveForward() {
        helicopter.increaseHelicopterSpeed();
    }

    void moveBackward() {
        helicopter.decreaseHelicopterSpeed();
    }

    void moveRight() {
        helicopter.moveHelicopterRight();
    }

    void moveLeft() {
        helicopter.moveHelicopterLeft();
    }

    void reset() {
        helicopter.resetHelicopter();
        //reset cloud
        //reset pond
    }

    void ignition() {
        helicopter.toggleIgnition();
    }

    void initializeBounds() {
        modifyBounds(helicopterBounds);
        modifyBounds(helipadBounds);
        modifyBounds(cloudBounds);
    }

    void modifyBounds(Rectangle bound) {
        bound.setFill(Color.TRANSPARENT);
        bound.setStroke(Color.YELLOW);
        bound.setVisible(false);
    }

    void translateBounds(Rectangle bound, Bounds boundsInParent) {
        bound.setHeight(boundsInParent.getHeight());
        bound.setWidth(boundsInParent.getWidth());
        double x =
                boundsInParent.getCenterX() - (boundsInParent.getWidth() / 2);
        double y =
                boundsInParent.getCenterY() - (boundsInParent.getHeight() / 2);
        bound.setTranslateX(x);
        bound.setTranslateY(y);
    }

    void toggleBoundVisibility() {
        toggleBounds = !toggleBounds;
        helicopterBounds.setVisible(toggleBounds);
        helipadBounds.setVisible(toggleBounds);
        cloudBounds.setVisible(toggleBounds);
    }

    void updateGameBounds() {
        translateBounds(helicopterBounds, helicopter.getBoundsInParent());
        translateBounds(helipadBounds, helipad.getBoundsInParent());
        translateBounds(cloudBounds, cloud.getBoundsInParent());
    }
}

abstract class GameObject extends Group implements Updatable {
    private final Translate myTranslation;
    private final Rotate myRotation;
    private final Scale myScale;

    public GameObject() {
        myTranslation = new Translate();
        myRotation = new Rotate();
        myScale = new Scale();
        this.getTransforms().addAll(myTranslation, myRotation, myScale);
    }

    public void rotate(double degrees, double x, double y) {
        myRotation.setAngle(degrees);
        myRotation.setPivotX(x);
        myRotation.setPivotY(y);
    }

    public void scale(double sx, double sy) {
        myScale.setX(sx);
        myScale.setY(sy);
    }

    public void translate(double tx, double ty) {
        myTranslation.setX(tx);
        myTranslation.setY(ty);
    }

    public double getMyRotation() {
        return myRotation.getAngle();
    }


}

class Pond extends GameObject {

    Pond() {
        Circle pond = new Circle(50, 600, 15, Color.BLUE);
        this.getChildren().add(pond);
    }

    public void update() {
    }


}

class Cloud extends GameObject {

    Cloud() {
        Circle cloud = new Circle(100, 500, 30, Color.WHITE);
        this.getChildren().add(cloud);
    }

    public void update() {
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

    public void update() {
    }
}

class Helicopter extends GameObject {
    Point2D velocity;
    GameText fuelText;

    // Using BigDecimal here because double didn't have precise arithmetic
    BigDecimal speed = BigDecimal.valueOf(0);
    BigDecimal changeSpeed = BigDecimal.valueOf(0.1);
    double vx = 0.0;
    double vy = 0.0;

    int heading = 90;
    int centerX;
    int centerY;
    int fuel;

    boolean ignition = false;


    Helicopter(int fuel, int helipadCenterX, int helipadCenterY) {
        this.fuel = fuel;
        centerX = helipadCenterX;
        centerY = helipadCenterY;
        Circle base = new Circle(centerX, centerY, 10,
                Color.YELLOW);

        Line roter = new Line(195, 90, 195, 120);
        roter.setStroke(Color.YELLOW);
        roter.setStrokeWidth(2);

        fuelText = new GameText("F:" + fuel, Color.YELLOW, 175, 75);

        velocity = new Point2D(0, 0);

        this.getChildren().addAll(base, roter, fuelText);
    }

    void increaseHelicopterSpeed() {
        if (speed.doubleValue() < 10.0 && ignition) {
            speed = speed.add(changeSpeed);
        }
    }

    void decreaseHelicopterSpeed() {
        if (speed.doubleValue() > -2 && ignition) {
            speed = speed.subtract(changeSpeed);
        }
    }


    // we need to find the center of the helicopter object
    // and on that center point we need to pivot the helicopter
    void moveHelicopterRight() {
        if (ignition && speed.doubleValue() > 0) {
            heading -= 15;
            this.rotate(this.getMyRotation() - 15, centerX, centerY);
        }

    }

    void moveHelicopterLeft() {
        if (ignition && speed.doubleValue() > 0) {
            heading += 15;
            this.rotate(this.getMyRotation() + 15, centerX, centerY);
        }
    }

    void updateHelicopter() {
        vx = speed.doubleValue() * Math.cos(Math.toRadians(heading));
        vy = speed.doubleValue() * Math.sin(Math.toRadians(heading));

        if (ignition) {
            if (speed.doubleValue() != 0) {
                velocity = velocity.add(vx, vy);
            } else {
                //fix this
                velocity = velocity.multiply(1 - 0.2);
            }
            updateFuel();
        }

        this.translate(velocity.getX(), velocity.getY());
    }

    // magnitude of velocity vector is its speed
    // vector = <x, y>
    // press w <x, y + 1>
    // so we are given it's speed to begin with by pressing w/up
    // .1 when it is first pressed
    // angle(heading) is changed by pressing a and d
    // the initial angle of the helicopter is 90
    // assuming the speed is .1
    // (.1)(cos90) = velocity in the x direction
    // (.1)(sin90) = velocity in the y direction
    // the p multiplied by the v vector moves the p vector by the changed
    // speed and direction
    // Vx = (speed) (cos(angle))
    // Vy = (speed) (sin(angle))


    void resetHelicopter() {
        fuel = 25000;
        fuelText.setGameText("F:" + fuel);
        velocity = new Point2D(0, 0);
        heading = 90;
        speed = BigDecimal.valueOf(0);
        ignition = false;

        this.rotate(getMyRotation() - getMyRotation(), centerX, centerY);
    }

    void toggleIgnition() {
        if (!ignition && speed.doubleValue() == 0.0) {
            ignition = true;
            System.out.println("ignition on");
        }

        //if the helictoper is on the helipad and not moving
        // be able to turn off the ignition
    }

    void updateFuel() {
        if (ignition) {

            if (speed.doubleValue() <= 0) {
                fuel -= .01;
            } else
                fuel -= 5 * speed.doubleValue();

            //System.out.println(fuel);
            fuelText.setGameText("F:" + fuel);
        }
    }


    public void update() {
        this.updateBounds();
        updateHelicopter();

    }

}

interface Updatable {
    void update();
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

    void setGameText(String text) {
        gameText.setText(text);
    }


    public void update() {
    }
}

// 10-25-2022
// TODO:create helicopter object
//  be able to move it up and down

// 11-06-2022
// TODO: add fuel to helicopter object
//  create stop logic


