package rainmaker;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import rainmaker.gameobjects.*;

import java.util.ArrayList;
import java.util.Iterator;

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

        for (Blimp blimp : blimps) {
            blimp.resetBlimp();
        }

        blimps.clear();
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
            double maxDistance = ponds.get(j).getMaxDistance();
            Point2D cloudPoint =
                    new Point2D(clouds.get(i).getBoundsInParent().getCenterX(),
                            clouds.get(i).getBoundsInParent().getCenterY());
            Point2D pondPoint =
                    new Point2D(ponds.get(j).getBoundsInParent().getCenterX(),
                            ponds.get(j).getBoundsInParent().getCenterY());
            double distance = cloudPoint.distance(pondPoint);

            if (distance < maxDistance) {
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
        int max = 3000;
        int min = 1;
        int range = max - min + 1;

        int rand = (int) (Math.random() * range) + min;
        if (rand == 50) {
            System.out.println("BLIMP ADDED");
            Blimp tmp = new Blimp();
            blimps.add(tmp);
            this.getChildren().add(6, tmp);
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


