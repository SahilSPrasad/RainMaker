import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
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
        setupWindow(game);

        AnimationTimer timer = new AnimationTimer() {
            double old = -1;

            @Override
            public void handle(long nano) {
                if (old < 0) old = nano;
                double delta = (nano - old) / 1e9;
                old = nano;

                game.updateGameBounds();

                for (Node n : game.getChildren()) {
                    if (n instanceof Updatable)
                        ((Updatable) n).update(delta);
                }


                if (game.checkPondWin() || game.checkFuelLost()) {
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
                case SPACE -> game.updateCloud();
                default -> {
                }
            }
        });

    }
}

class Game extends Pane {
    int helipadCenterX = 195;
    int helipadCenterY = 90;
    final int fuel = 25000;
    Cloud cloud = new Cloud();
    Pond pond = new Pond(cloud);
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

    //add init method
    //clear

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
        cloud.resetCloud();
        pond.resetPond();
    }

    void ignition() {
        if (helipad.getBoundsInParent().contains(helicopter.getBoundsInParent())) {
            helicopter.toggleIgnition();
        }
    }

    void updateCloud() {
        if (cloud.getBoundsInParent().intersects(helicopter.getBoundsInParent
                ())) {
            cloud.seedCloud();
        }


    }

    boolean checkPondWin() {
        //System.out.println(pond.getWaterPercentage());
        return (int) pond.getWaterPercentage() == 100;
    }

    boolean checkFuelLost() {
        //System.out.println(helicopter.getFuel());
        return helicopter.getFuel() < 0;
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

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public double getMyRotation() {
        return myRotation.getAngle();
    }
}

class Pond extends GameObject {
    private double waterPercentage;
    private GameText waterAmountText;
    double scaleX;
    double scaleY;
    Circle pond;
    Cloud relationship;
    boolean isWinner = false;

    Pond(Cloud c) {
        this.relationship = c;
        pond = new Circle(getRandomNumber(100, 300), getRandomNumber(500, 700),
                getRandomNumber(15, 30), Color.BLUE);
        this.waterPercentage = getRandomNumber(0, 25);
        this.scaleX = 1.0;
        this.scaleY = 1.0;
        waterAmountText = new GameText((int) waterPercentage + "%", Color.WHITE,
                (int) pond.getCenterX() - 7,
                (int) pond.getCenterY() + 5);
        this.getChildren().addAll(pond, waterAmountText);
    }

    void increaseWaterAmount(double delta) {
        if (relationship.getSeedPercentage() > 30 && waterPercentage < 100) {
            scaleX += delta * .10;
            scaleY += delta * .10;

            waterPercentage = waterPercentage + delta * 10;
            waterAmountText.setGameText((int) waterPercentage + "%");
            pond.setScaleX(scaleX);
            pond.setScaleY(scaleY);

        }
    }

    public void update(double delta) {
        increaseWaterAmount(delta);
        isWinner = gameWon();
    }

    void resetPond() {
        scaleX = 1.0;
        scaleY = 1.0;
        pond.setScaleX(scaleX);
        pond.setScaleY(scaleY);
        waterPercentage = getRandomNumber(0, 25);
        this.getChildren().clear();
        pond = new Circle(getRandomNumber(100, 300), getRandomNumber(500, 700),
                getRandomNumber(15, 30), Color.BLUE);
        waterAmountText = new GameText((int) waterPercentage + "%", Color.WHITE,
                (int) pond.getCenterX() - 7,
                (int) pond.getCenterY() + 5);
        this.getChildren().addAll(pond, waterAmountText);
    }

    public double getWaterPercentage() {
        return waterPercentage;
    }

    boolean gameWon() {
        return (int) waterPercentage == 100;
    }
}


class Cloud extends GameObject {
    private double seedPercentage = 0;
    Color cloudColor;

    Circle cloud;
    int r, g, b;
    private GameText cloudSeedText;

    Cloud() {
        this.r = 255;
        this.g = 255;
        this.b = 255;
        cloudColor = Color.rgb(250, 250, 250);
        cloud = new Circle(getRandomNumber(100, 300), getRandomNumber(500, 700),
                getRandomNumber(30, 45), Color.WHITE);
        cloudSeedText = new GameText(seedPercentage + "%", Color.BLACK,
                (int) cloud.getCenterX() - 7,
                (int) cloud.getCenterY() + 5);
        this.getChildren().addAll(cloud, cloudSeedText);
    }

    void seedCloud() {
        if (seedPercentage < 100) {
            seedPercentage++;
            r -= 2;
            g -= 2;
            b -= 2;
            cloudColor = Color.rgb(r, g, b);
            cloud.setFill(cloudColor);
            cloud.setStroke(cloudColor);
        }
    }

    void resetCloud() {
        seedPercentage = 0;
        cloudColor = Color.rgb(250, 250, 250);
        r = 255;
        g = 255;
        b = 255;
        cloud.setFill(cloudColor);
        this.getChildren().clear();
        cloud = new Circle(getRandomNumber(100, 300), getRandomNumber(400, 700),
                getRandomNumber(30, 45), cloudColor);
        cloudSeedText = new GameText(seedPercentage + "%", Color.BLACK,
                (int) cloud.getCenterX() - 7,
                (int) cloud.getCenterY() + 5);
        this.getChildren().addAll(cloud, cloudSeedText);

    }

    public void update(double delta) {
        double prev = seedPercentage;

        if (seedPercentage > 0) {
            seedPercentage = seedPercentage - delta;

            cloudColor = Color.rgb(r, g, b);
            cloud.setFill(cloudColor);
        }

        if ((int) seedPercentage < (int) prev && r < 255) {
            r += 2;
            g += 2;
            b += 2;
        }

        cloudSeedText.setGameText((int) seedPercentage + "%");
    }

    int getSeedPercentage() {
        return (int) seedPercentage;
    }

}

class Helipad extends GameObject {

    Helipad(int padCenterX, int padCenterY) {
        Rectangle border = new Rectangle(145, 40, 100, 100);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.WHITE);

        Circle pad = new Circle(padCenterX, padCenterY, 40);
        pad.setFill(Color.TRANSPARENT);
        pad.setStroke(Color.WHITE);

        this.getChildren().addAll(border, pad);
    }

    public void update(double delta) {
    }
}

class Helicopter extends GameObject {
    Point2D velocity, position;
    GameText fuelText;

    // Using BigDecimal here because double didn't have precise arithmetic
    // Use math.min instead
    BigDecimal speed = BigDecimal.valueOf(0);
    BigDecimal changeSpeed = BigDecimal.valueOf(0.1);
    double vx = 0.0;
    double vy = 0.0;

    int heading = 90;
    int centerX;
    int centerY;
    int fuel;

    boolean ignition = false;

    HeloBlade blade;


    Helicopter(int fuel, int helipadCenterX, int helipadCenterY) {
        this.fuel = fuel;
        centerX = helipadCenterX;
        centerY = helipadCenterY;
        position = new Point2D(0, 0);
//        Circle base = new Circle(centerX, centerY, 10,
//                Color.YELLOW);

        HeloBody body = new HeloBody(centerX, centerY);
        blade = new HeloBlade(centerX, centerY);

        Line rotor = new Line(195, 90, 195, 120);
        rotor.setStroke(Color.YELLOW);
        rotor.setStrokeWidth(2);

        fuelText = new GameText("F:" + fuel, Color.YELLOW, 175, 55);

        velocity = new Point2D(0, 0);

        this.getChildren().addAll(body, blade, fuelText);
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
        if (ignition && speed.doubleValue() != 0) {
            heading -= 15;
            this.rotate(this.getMyRotation() - 15, centerX, centerY);
        }
    }

    void moveHelicopterLeft() {
        if (ignition && speed.doubleValue() != 0) {
            heading += 15;
            this.rotate(this.getMyRotation() + 15, centerX, centerY);
        }
    }

    void updateHelicopter(double delta) {

        if (ignition) {

            if (speed.doubleValue() != 0) {
                vx = speed.doubleValue() * Math.cos(Math.toRadians(heading));
                vy = speed.doubleValue() * Math.sin(Math.toRadians(heading));
                velocity = velocity.add(vx, vy);
            } else {
                velocity = velocity.multiply(1 - .8 * delta);
            }
            updateFuel();
        }
        //System.out.println(speed.doubleValue());
        ///position = position.add(velocity.multiply(delta));
        this.translate(velocity.getX(), velocity.getY());
    }

    // magnitude of velocity vector is its speed
    // vector = <x, y>
    // press w <x, y + 1>
    // so we are given its speed to begin with by pressing w/up
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

        vx = 0;
        vy = 0;
        fuel = 25000;
        fuelText.setGameText("F:" + fuel);
        velocity = new Point2D(0, 0);
        position = new Point2D(0, 0);
        heading = 90;
        speed = BigDecimal.valueOf(0);
        ignition = false;
        blade.resetBlade();
        this.rotate(getMyRotation() - getMyRotation(), centerX, centerY);
    }

    void toggleIgnition() {
        if (speed.doubleValue() == 0.0) {
            ignition = !ignition;
            //System.out.println("called");
        }

        //if the helicopter is on the helipad and not moving(velocity = 0)
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

    int getFuel() {
        return fuel;
    }

    public void update(double delta) {
        this.updateBounds();
        updateHelicopter(delta);
        if (ignition) blade.update(delta);
    }
}


class HeloBody extends GameObject {
    Image body;
    ImageView imageView;

    HeloBody(int x, int y) {
        body = new Image("helibody2.png");
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setImage(body);
        imageView.setScaleY(-1);
        imageView.setX(x - 30);
        imageView.setY(y - 50);
        imageView.fitHeightProperty();
        imageView.setFitHeight(100);
        imageView.setFitWidth(170);
        this.getChildren().add(imageView);
    }

    @Override
    public void update(double delta) {

    }
}

class HeloBlade extends GameObject {
    Rectangle blade;
    double bladeCenterX;
    double bladeCenterY;

    HeloBlade(int x, int y) {
        blade = new Rectangle(70, 5, Color.WHITE);
        bladeCenterX = x;
        bladeCenterY = y;
        blade.setX(bladeCenterX - 33);
        blade.setY(bladeCenterY);
        this.getChildren().add(blade);
    }

    void resetBlade() {
        blade.setRotate(0);
    }

    @Override
    public void update(double delta) {
        blade.setRotate(blade.getRotate() + 5);
    }
}


interface Updatable {
    void update(double delta);
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


    public void update(double delta) {
    }
}

// 10-25-2022
// TODO:create helicopter object
//  be able to move it up and down

// 11-06-2022
// TODO: add fuel to helicopter object
//  create stop logic


