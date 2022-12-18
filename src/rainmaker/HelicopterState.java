package rainmaker;

import javafx.geometry.Point2D;
import rainmaker.gameobjects.GameText;
import rainmaker.gameobjects.Helicopter;
import rainmaker.gameobjects.HeloBlade;

import java.math.BigDecimal;


public interface HelicopterState {

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

