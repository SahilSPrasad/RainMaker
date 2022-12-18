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
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

public class GameApp extends Application {

    final static int GAME_HEIGHT = 800;
    final static int GAME_WIDTH = 800;
    final static double WIND_SPEED = .6;

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


//                if (game.checkWin() || game.checkFuelLost()) {
//                    handleWinLoss(this, stage, game, scene);
//                }
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

class Game extends Pane {

    int helipadCenterX = 400;
    int helipadCenterY = 90;
    final int fuel = 25000;

    ArrayList<Cloud> clouds = new ArrayList<>();
    ArrayList<Pond> ponds = new ArrayList<>();
    ArrayList<Rectangle> cloudBounds = new ArrayList<>();
    ArrayList<Blimp> blimps = new ArrayList<>();

    Helipad helipad = new Helipad(helipadCenterX, helipadCenterY);
    Helicopter helicopter = new Helicopter(fuel, helipadCenterX,
            helipadCenterY);


    private boolean toggleBounds = false;
    Rectangle helicopterBounds = new Rectangle();
    Rectangle helipadBounds = new Rectangle();
    WindSubject windSubject = new WindSubject();




    Game() {
        createGameObjects();
    }

    void createGameObjects() {

        for (int i = 0; i < 5; i++) {

            Cloud cloud = new Cloud(windSubject);

            Rectangle cloudBound = new Rectangle();

            cloudBounds.add(cloudBound);
            this.getChildren().add(cloudBound);
            clouds.add(cloud);
            this.getChildren().add(cloud);
        }

        //Set wind speed for all observers(clouds)
        windSubject.setWindSpeed(GameApp.WIND_SPEED);

        for (int i = 0; i < 3; i++) {
            Pond pond = new Pond();
            ponds.add(pond);
            this.getChildren().add(0, pond);
        }

        initializeBounds();

        this.getChildren().addAll(helipad, helicopter,
                helicopterBounds, helipadBounds);

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
        for (Cloud cloud : clouds) {
            cloud.resetCloud();
        }
        for (Pond pond : ponds) {
            pond.resetPond();
        }
    }

    void ignition() {
        if (helipad.getBoundsInParent().intersects(helicopter.getBoundsInParent())) {
            helicopter.toggleIgnition();
        }
    }

    void seedCloud() {
        for (Cloud cloud : clouds) {
            if (cloud.getBoundsInParent().intersects(helicopter.getBoundsInParent
                    ())) {
                cloud.seedCloud();
            }
        }
    }

    void checkPondAndCloudDistance(double delta) {

        for (int i = 0, j = 0; i < clouds.size(); i++, j++) {
            double distanceX =
                    clouds.get(i).getBoundsInParent().getCenterX() - ponds.get(j).getBoundsInParent().getCenterX();
            double maxDistance = ponds.get(j).getMaxDistance();

            if (distanceX > -maxDistance && distanceX < maxDistance) {
                //System.out.println("within range");
                if (clouds.get(i).getSeedPercentage() > 20) {
                    ponds.get(j).increaseWaterAmount(delta);
                }
            }

            if (j == 2) j = 0;
        }


    }

    boolean checkWin() {

        for (Pond pond : ponds) {
            if ((int) pond.getWaterPercentage() != 100) {
                return false;
            }
        }
        System.out.println("won");
        return true;
    }

    boolean checkFuelLost() {
        return helicopter.getCurrentState().getFuel() < 0;
    }


    void initializeBounds() {
        modifyBounds(helicopterBounds);
        modifyBounds(helipadBounds);
        for (Rectangle cloudBound : cloudBounds) {
            modifyBounds(cloudBound);
        }
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
        cloudBounds.get(0).setVisible(toggleBounds);
        cloudBounds.get(1).setVisible(toggleBounds);

    }

    void handleNumberOfClouds() {
        if (clouds.size() == 1 || clouds.size() == 2) {
            addCloudToScene();
        }

        if (clouds.size() == 3 || clouds.size() == 4) {
            if (Math.random() < .5) {
                addCloudToScene();
            }
        }
    }

    void checkCloudsBounds() {
        Iterator<Cloud> itr = clouds.iterator();
        while (itr.hasNext()) {
            Cloud cloudItr = itr.next();
            if (cloudItr.getBoundsInParent().getCenterX() > GameApp.GAME_WIDTH + 60) {
                itr.remove();
                this.getChildren().remove(cloudItr);
            }
        }
    }

    void addCloudToScene() {
        Cloud tmp = new Cloud(windSubject);
        windSubject.register(tmp);
        windSubject.setWindSpeed(GameApp.WIND_SPEED);
        clouds.add(tmp);
        this.getChildren().add(4, tmp);
    }

    void createBlimpRandomly() {
        int max = 2000;
        int min = 1;
        int range = max - min + 1;

        // generate random numbers within 1 to 10
        int rand = (int) (Math.random() * range) + min;
        if (rand == 50) {
            System.out.println("added");
            Blimp tmp = new Blimp();
            blimps.add(tmp);
            this.getChildren().add(6,tmp);
        }
    }

    void removeBlimpFromScene() {
        Iterator<Blimp> itr = blimps.iterator();
        while (itr.hasNext()) {
            Blimp blimpItr = itr.next();
            if (blimpItr.getBoundsInParent().getCenterX() > GameApp.GAME_WIDTH + 60) {
                itr.remove();
                this.getChildren().remove(blimpItr);
            }
        }
    }

    void helicopterBlimpRefuel(double delta) {
        HelicopterState helicopterState = helicopter.getCurrentState();
        for (Blimp blimp : blimps) {
            if (blimp.getBoundsInParent().contains(helicopter.getBoundsInParent
                    ()) && blimp.getFuelAmount() > 0) {
                helicopter.setRefuelingStatus(true);
                blimp.removeFuel(delta);
                helicopterState.addFuel(delta);
            } else {
                helicopter.setRefuelingStatus(false);
            }
        }
    }

    void updateGameBounds() {
        translateBounds(helicopterBounds, helicopter.getBoundsInParent());
        translateBounds(helipadBounds, helipad.getBoundsInParent());
//        for (int i = 0; i < clouds.size(); i++) {
//            translateBounds(cloudBounds.get(i), clouds.get(i)
//            .getBoundsInParent());
//        }
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
    boolean isWinner = false;

    Pond() {
        pond = new Circle(getRandomNumber(50, 600), getRandomNumber(250, 700),
                getRandomNumber(25, 40), Color.BLUE);
        this.waterPercentage = getRandomNumber(0, 25);
        this.scaleX = 1.0;
        this.scaleY = 1.0;
        waterAmountText = new GameText((int) waterPercentage + "%", Color.WHITE,
                (int) pond.getCenterX() - 7,
                (int) pond.getCenterY() + 5);
        this.getChildren().addAll(pond, waterAmountText);
    }

    void increaseWaterAmount(double delta) {
        if (waterPercentage <= 100) {
            scaleX += delta * .10;
            scaleY += delta * .10;

            waterPercentage = waterPercentage + delta * 10;
            waterAmountText.setGameText((int) waterPercentage + "%");
            pond.setScaleX(scaleX);
            pond.setScaleY(scaleY);
        }

    }

    double getMaxDistance() {
        return pond.getRadius() * 4;
    }

    public void update(double delta) {
        isWinner = gameWon();
    }

    void resetPond() {
        scaleX = 1.0;
        scaleY = 1.0;
        pond.setScaleX(scaleX);
        pond.setScaleY(scaleY);
        waterPercentage = getRandomNumber(0, 25);
        this.getChildren().clear();
        pond = new Circle(getRandomNumber(50, 600), getRandomNumber(250, 700),
                getRandomNumber(25, 40), Color.BLUE);
        waterAmountText = new GameText((int) waterPercentage + "%", Color.WHITE,
                (int) pond.getCenterX() - 7,
                (int) pond.getCenterY() + 5);
        this.getChildren().addAll(pond, waterAmountText);
    }

    double getWaterPercentage() {
        return waterPercentage;
    }

    boolean gameWon() {
        return (int) waterPercentage == 100;
    }
}

interface Observer {
    void updateObserver(double windSpeed);
}

interface Subject {
    void register(Observer o);

    void unregister(Observer o);

    void notifyObserver();
}

class WindSubject implements Subject {
    private ArrayList<Observer> observers;
    private double windSpeed;

    WindSubject() {
        observers = new ArrayList<>();
    }

    @Override
    public void register(Observer o) {
        observers.add(o);
    }

    @Override
    public void unregister(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObserver() {
        for (Observer observer : observers) {
            observer.updateObserver(windSpeed);
        }
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
        notifyObserver();
    }
}


class Cloud extends GameObject implements Observer {
    private double seedPercentage = 0;
    Color cloudColor;
    private double windSpeed;

    BezierOval cloud;
    int r, g, b;
    private GameText cloudSeedText;

    private double vx;
    private double vy;
    private Point2D velocity;

    private Subject windSubject;

    Cloud(Subject windSubject) {
        this.r = 255;
        this.g = 255;
        this.b = 255;
        cloudColor = Color.rgb(250, 250, 250);
        cloud = new BezierOval();
        cloudSeedText = new GameText(seedPercentage + "%", Color.BLACK,
                (int) cloud.getCenterX() - 7,
                (int) cloud.getCenterY() + 5);

        this.windSubject = windSubject;

        windSubject.register(this);

        velocity = new Point2D(0, 0);
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
            //cloud.setStroke(cloudColor);
        }
    }

    void resetCloud() {
        this.getChildren().clear();
        seedPercentage = 0;
        cloudColor = Color.rgb(250, 250, 250);
        r = 255;
        g = 255;
        b = 255;
        cloud.setFill(cloudColor);

        cloud = new BezierOval();
        cloudSeedText = new GameText(seedPercentage + "%", Color.BLACK,
                (int) cloud.getCenterX() - 7,
                (int) cloud.getCenterY() + 5);

        velocity = new Point2D(0, 0);
        vx = 0;
        vy = 0;
        this.getChildren().addAll(cloud, cloudSeedText);
    }

    void moveCloudLeftToRight() {

        vx = windSpeed * Math.cos(Math.toRadians(0));
        vy = windSpeed * Math.sin(Math.toRadians(0));

        velocity = velocity.add(vx, vy);
        this.translate(velocity.getX(), velocity.getY());
    }

    public void update(double delta) {
        moveCloudLeftToRight();
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

    @Override
    public void updateObserver(double windSpeed) {
        this.windSpeed = windSpeed;
    }
}

class BezierOval extends Group {
    Ellipse ellipse;
    Ellipse perimeter;
    double radiusX;
    double radiusY;
    int increment;

    ArrayList<QuadCurve> curves = new ArrayList<>();

    double perimeterRadiusX;
    double perimeterRadiusY;

    BezierOval() {
        ellipse = new Ellipse(getRandomNumber(-500, -50), getRandomNumber(200,
                700), getRandomNumber(40, 80), getRandomNumber(30, 50));
        ellipse.setFill(Color.WHITE);

        perimeter = new Ellipse(ellipse.getCenterX(), ellipse.getCenterY(),
                ellipse.getRadiusX() + 20, ellipse.getRadiusY() + 20);
        perimeter.setFill(Color.TRANSPARENT);

        this.radiusX = ellipse.getRadiusX();
        this.radiusY = ellipse.getRadiusY();

        this.perimeterRadiusX = perimeter.getRadiusX();
        this.perimeterRadiusY = perimeter.getRadiusY();
        this.increment = getRandomNumber(20, 50);
        createCurvesOnCloud();
        this.getChildren().addAll(ellipse, perimeter);

    }

    void setFill(Color color) {
        ellipse.setFill(color);
        for (QuadCurve curve : curves) {
            curve.setFill(color);
        }
    }

    double getCenterX() {
        return ellipse.getCenterX();
    }

    double getCenterY() {
        return ellipse.getCenterY();
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    Point2D createPointOnEllipse(double theta) {
        return getPoint2D(theta, radiusX, radiusY);
    }

    Point2D createControlPoint(double theta) {
        return getPoint2D(theta, perimeterRadiusX, perimeterRadiusY);
    }

    private Point2D getPoint2D(double theta, double perimeterRadiusX,
                               double perimeterRadiusY) {
        Point2D point = new Point2D(ellipse.getCenterX(),
                ellipse.getCenterY());
        double x = Math.sin(Math.toRadians(theta)) * perimeterRadiusX;
        double y = Math.cos(Math.toRadians(theta)) * perimeterRadiusY;
        point = point.add(x, y);
        return point;
    }

    void createBezierCurve(double theta) {
        QuadCurve curve = new QuadCurve();
        curves.add(curve);
        curve.setFill(Color.WHITE);
        //curve.setStroke(Color.BLACK);
        Point2D start = createPointOnEllipse(theta);
        Point2D controlPoint = createControlPoint(theta + increment);
        Point2D end = createPointOnEllipse(theta + increment);

        curve.setStartX(start.getX());
        curve.setStartY(start.getY());
        curve.setEndX(end.getX());
        curve.setEndY(end.getY());
        curve.setControlX(controlPoint.getX());
        curve.setControlY(controlPoint.getY());

        this.getChildren().add(curve);
    }

    void createCurvesOnCloud() {
        for (int i = 0; i < 400; i += increment)
            createBezierCurve(i);
    }
}

class Helipad extends GameObject {

    Helipad(int padCenterX, int padCenterY) {
        Rectangle border = new Rectangle(350, 40, 100, 100);
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

class Blimp extends GameObject {
    Image body;
    ImageView imageView;

    double fuelAmount;
    GameText fuelText;

    double vx;
    double vy;
    Point2D velocity;

    Blimp() {
        body = new Image("blimp.png");
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setImage(body);
        imageView.setScaleY(-1);
        imageView.setX(-100);
        imageView.setY(getRandomNumber(200,500));
        imageView.setFitHeight(300);
        imageView.setFitWidth(300);
        imageView.fitHeightProperty();
        fuelAmount = getRandomNumber(5000,10000);

        this.vx = 0;
        this.vy = 0;
        this.velocity = new Point2D(0, 0);

        fuelText = new GameText(Integer.toString((int) fuelAmount), Color.YELLOW,
                (int) imageView.getX() + 140
                , (int) imageView.getY() + 100);
        this.getChildren().addAll(imageView, fuelText);
    }

    public double getFuelAmount() {
        return fuelAmount;
    }

    void removeFuel(double delta) {
        if ((int) fuelAmount > 0) {
            fuelAmount = fuelAmount - delta * 500;
            int displayFuel = (int) fuelAmount;
            fuelText.setGameText(Integer.toString(displayFuel));
        }
    }

    void moveBlimpLeftToRight() {
        vx = .3 * Math.cos(Math.toRadians(0));
        vy = .3 * Math.sin(Math.toRadians(0));

        velocity = velocity.add(vx, vy);
        this.translate(velocity.getX(), velocity.getY());
    }

    @Override
    public void update(double delta) {
        moveBlimpLeftToRight();
    }
}


class Helicopter extends GameObject {
    GameText fuelText;

    private final int centerX;
    private final int centerY;
    int fuel;
    boolean refueling = false;

    HeloBody body;
    HeloBlade blade;

    HelicopterState off;
    HelicopterState helicopterState;


    Helicopter(int fuel, int helipadCenterX, int helipadCenterY) {
        this.fuel = fuel;
        centerX = helipadCenterX;
        centerY = helipadCenterY;

        body = new HeloBody(centerX, centerY);
        blade = new HeloBlade(centerX, centerY);

        fuelText = new GameText("F:" + fuel, Color.YELLOW, 380, 55);


        off = new Off(this, blade, fuelText, fuel);
        helicopterState = off;

        this.getChildren().addAll(body, blade, fuelText);
    }

    void setHelicopterState(HelicopterState newHelicopterState) {
        helicopterState = newHelicopterState;
    }

    void setRefuelingStatus(boolean status) {
        refueling = status;
    }

    void increaseHelicopterSpeed() {
        helicopterState.increaseHelicopterSpeed();
    }

    void decreaseHelicopterSpeed() {
        helicopterState.decreaseHelicopterSpeed();

    }

    HelicopterState getCurrentState() {
        return helicopterState;
    }

    void moveHelicopterRight() {
        helicopterState.moveRight(centerX, centerY);
    }

    void moveHelicopterLeft() {
        helicopterState.moveLeft(centerX, centerY);
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
        blade.setRotate(0);
        setHelicopterState(new Off(this, blade, fuelText, 25000));
        this.rotate(getMyRotation() - getMyRotation(), centerX, centerY);
        this.getChildren().clear();
        this.getChildren().addAll(body, blade, fuelText);
    }

    void toggleIgnition() {
        helicopterState.toggleIgnition();
    }

    public void update(double delta) {
        if (!refueling) {
            //System.out.println("not refueling");
            helicopterState.updateFuel();
        }
        helicopterState.updateBlade(delta);
        helicopterState.moveCopter();
        this.updateBounds();
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

    @Override
    public void update(double delta) {
    }
}


interface HelicopterState {

    void toggleIgnition();

    void updateFuel();

    void addFuel(double delta);

    void updateBlade(double delta);

    double getFuel();

    void moveCopter();

    void increaseHelicopterSpeed();

    void decreaseHelicopterSpeed();

    void moveLeft(int centerX, int centerY);

    void moveRight(int centerX, int centerY);


}

class Off implements HelicopterState {
    private final Helicopter helicopter;
    private final HeloBlade blade;
    private final GameText fuelText;
    private final double fuel;
    private final double bladeSpeed;


    Off(Helicopter helicopter, HeloBlade blade,
        GameText fuelText, double fuel) {
        this.helicopter = helicopter;
        this.blade = blade;
        this.fuel = fuel;
        this.fuelText = fuelText;
        this.bladeSpeed = 0;
        helicopter.translate(0, 0);
        System.out.println("OFF STATE");
        fuelText.setGameText("F:" + (int) fuel);
    }

    @Override
    public void toggleIgnition() {
        helicopter.setHelicopterState(new Starting(helicopter, blade,
                bladeSpeed,
                fuel, fuelText));
    }

    @Override
    public void updateFuel() {
    }

    @Override
    public void addFuel(double delta) {

    }

    @Override
    public void updateBlade(double delta) {
    }

    @Override
    public double getFuel() {
        return fuel;
    }

    @Override
    public void moveCopter() {
    }

    @Override
    public void increaseHelicopterSpeed() {
    }

    @Override
    public void decreaseHelicopterSpeed() {
    }

    @Override
    public void moveLeft(int centerX, int centerY) {
    }

    @Override
    public void moveRight(int centerX, int centerY) {
    }
}

class Starting implements HelicopterState {
    private final Helicopter helicopter;
    private final HeloBlade blade;
    private final GameText fuelText;
    private double fuel;
    private double bladeSpeed;

    Starting(Helicopter helicopter, HeloBlade blade, double bladeSpeed,
             double fuel,
             GameText fuelText
    ) {
        this.helicopter = helicopter;
        this.blade = blade;
        this.fuel = fuel;
        this.fuelText = fuelText;
        this.bladeSpeed = bladeSpeed;
        System.out.println("STARTING");
    }

    @Override
    public void toggleIgnition() {
        helicopter.setHelicopterState(new Stopping(helicopter, blade,
                bladeSpeed, fuel, fuelText));
    }

    @Override
    public void updateFuel() {
        fuel -= .50;
        fuelText.setGameText("F:" + (int) fuel);
    }

    @Override
    public void addFuel(double delta) {

    }

    @Override
    public void updateBlade(double delta) {
        bladeSpeed += delta;
        blade.setRotate(blade.getRotate() + bladeSpeed);
        if (bladeSpeed > 8.0) {
            helicopter.setHelicopterState(new Ready(helicopter, blade,
                    bladeSpeed, fuel, fuelText));
        }
    }

    @Override
    public double getFuel() {
        return fuel;
    }


    @Override
    public void moveCopter() {
    }

    @Override
    public void increaseHelicopterSpeed() {
    }

    @Override
    public void decreaseHelicopterSpeed() {
    }

    @Override
    public void moveLeft(int centerX, int centerY) {
    }

    @Override
    public void moveRight(int centerX, int centerY) {
    }
}

class Stopping implements HelicopterState {
    private final Helicopter helicopter;
    private final HeloBlade blade;
    private final GameText fuelText;
    private final double fuel;
    private double bladeSpeed;


    Stopping(Helicopter helicopter, HeloBlade blade, double bladeSpeed,
             double fuel,
             GameText fuelText) {
        System.out.println("STOPPING");
        this.helicopter = helicopter;
        this.blade = blade;
        this.fuelText = fuelText;
        this.bladeSpeed = bladeSpeed;
        this.fuel = fuel;
    }

    @Override
    public void toggleIgnition() {
        helicopter.setHelicopterState(new Starting(helicopter, blade,
                bladeSpeed,
                fuel, fuelText));
    }

    @Override
    public void updateFuel() {
    }

    @Override
    public void addFuel(double delta) {

    }

    @Override
    public void updateBlade(double delta) {
        if (bladeSpeed > 0) {
            bladeSpeed -= delta;
            blade.setRotate(blade.getRotate() + bladeSpeed);
        } else {
            helicopter.setHelicopterState(new Off(helicopter, blade, fuelText,
                    fuel));
        }
    }

    @Override
    public double getFuel() {
        return fuel;
    }

    @Override
    public void moveCopter() {
    }

    @Override
    public void increaseHelicopterSpeed() {

    }

    @Override
    public void decreaseHelicopterSpeed() {
    }

    @Override
    public void moveLeft(int centerX, int centerY) {
    }

    @Override
    public void moveRight(int centerX, int centerY) {
    }
}

class Ready implements HelicopterState {
    private final Helicopter helicopter;
    private final HeloBlade blade;
    private final GameText fuelText;
    private double fuel;
    private final double bladeSpeed;

    private int heading = 90;
    private double vx;
    private double vy;
    private Point2D velocity;

    private BigDecimal speed = BigDecimal.valueOf(0);
    private final BigDecimal changeSpeed = BigDecimal.valueOf(0.1);

    Ready(Helicopter helicopter, HeloBlade blade, double bladeSpeed,
          double fuel,
          GameText fuelText) {
        this.helicopter = helicopter;
        this.blade = blade;
        this.fuel = fuel;
        this.fuelText = fuelText;
        this.bladeSpeed = bladeSpeed;

        vx = 0;
        vy = 0;

        System.out.println("READY");
        velocity = new Point2D(0, 0);
    }

    @Override
    public void toggleIgnition() {
        helicopter.setHelicopterState(new Stopping(helicopter, blade,
                bladeSpeed, fuel, fuelText));
    }

    @Override
    public void updateFuel() {
        if (speed.doubleValue() == 0) {
            fuel -= 5 * .1;
        }

        fuel -= 5 * speed.doubleValue();

        fuelText.setGameText("F:" + (int) fuel);
    }

    @Override
    public void addFuel(double delta) {

        fuel = fuel + delta * 500;
        fuelText.setGameText("F:" + (int) fuel);
    }


    @Override
    public void updateBlade(double delta) {
        blade.setRotate(blade.getRotate() + bladeSpeed);
    }

    @Override
    public double getFuel() {
        return fuel;
    }

    @Override
    public void moveCopter() {
        vx = speed.doubleValue() * Math.cos(Math.toRadians(heading));
        vy = speed.doubleValue() * Math.sin(Math.toRadians(heading));

        velocity = velocity.add(vx, vy);
        helicopter.translate(velocity.getX(), velocity.getY());
    }

    @Override
    public void increaseHelicopterSpeed() {
        if (speed.doubleValue() < 10.0) {
            speed = speed.add(changeSpeed);
        }
    }

    @Override
    public void decreaseHelicopterSpeed() {
        if (speed.doubleValue() > -2) {
            speed = speed.subtract(changeSpeed);
        }
    }

    @Override
    public void moveLeft(int centerX, int centerY) {
        if (speed.doubleValue() != 0) {
            heading += 15;
            helicopter.rotate(helicopter.getMyRotation() + 15, centerX,
                    centerY);
        }
    }

    @Override
    public void moveRight(int centerX, int centerY) {
        if (speed.doubleValue() != 0) {
            heading -= 15;
            helicopter.rotate(helicopter.getMyRotation() - 15, centerX,
                    centerY);
        }
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


