package rainmaker.gameobjects;

import javafx.scene.paint.Color;
import rainmaker.HelicopterState;
import rainmaker.Off;


public class Helicopter extends GameObject {
    GameText fuelText;

    private final int centerX;
    private final int centerY;
    int fuel;
    boolean refueling = false;

    HeloBody body;
    HeloBlade blade;

    HelicopterState off;
    HelicopterState helicopterState;


    public Helicopter(int fuel, int helipadCenterX, int helipadCenterY) {
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

    public void setHelicopterState(HelicopterState newHelicopterState) {
        helicopterState = newHelicopterState;
    }

    public void setRefuelingStatus(boolean status) {
        refueling = status;
    }

    public void increaseHelicopterSpeed() {
        helicopterState.increaseHelicopterSpeed();
    }

    public void decreaseHelicopterSpeed() {
        helicopterState.decreaseHelicopterSpeed();

    }

    public HelicopterState getCurrentState() {
        return helicopterState;
    }

    public void moveHelicopterRight() {
        helicopterState.moveRight(centerX, centerY);
    }

    public void moveHelicopterLeft() {
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


    public void resetHelicopter() {
        blade.setRotate(0);
        setHelicopterState(new Off(this, blade, fuelText, 25000));
        this.rotate(getMyRotation() - getMyRotation(), centerX, centerY);
        this.getChildren().clear();
        this.getChildren().addAll(body, blade, fuelText);
    }

    public void toggleIgnition() {
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


