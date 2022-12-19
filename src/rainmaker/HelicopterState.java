package rainmaker;

import javafx.geometry.Point2D;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
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

    void stopSounds();


}

class Starting implements HelicopterState {
    private final Helicopter helicopter;
    private final HeloBlade blade;
    private final GameText fuelText;
    private double fuel;
    private double bladeSpeed;
    private AudioClip startingSound;

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
        startingSound = new AudioClip(this.getClass()
                .getResource("\\startingSound.mp3")
                .toExternalForm());
        startingSound.setVolume(.07);
        startingSound.play();
    }

    @Override
    public void toggleIgnition() {
        helicopter.setHelicopterState(new Stopping(helicopter, blade,
                bladeSpeed, fuel, fuelText));
        startingSound.stop();
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
        bladeSpeed += delta * 1.2;
        blade.setRotate(blade.getRotate() + bladeSpeed);
        if (bladeSpeed > 8.0) {
            helicopter.setHelicopterState(new Ready(helicopter, blade,
                    bladeSpeed, fuel, fuelText));
            startingSound.stop();
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

    @Override
    public void stopSounds() {
        startingSound.stop();
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

    @Override
    public void stopSounds() {
    }
}

class Ready implements HelicopterState {
    private final Helicopter helicopter;
    private final HeloBlade blade;
    private final GameText fuelText;
    private final double bladeSpeed;
    private final BigDecimal changeSpeed = BigDecimal.valueOf(0.1);
    private double fuel;
    private int heading = 90;
    private double vx;
    private double vy;
    private Point2D velocity;
    private BigDecimal speed = BigDecimal.valueOf(0);
    private Media readySound;
    private MediaPlayer readySoundPlayer;

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
        readySound =
                new Media(this.getClass().getResource("\\readysound.mp3")
                                .toExternalForm());
        readySoundPlayer = new MediaPlayer(readySound);
        readySoundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        readySoundPlayer.setVolume(.03);
        readySoundPlayer.play();

        System.out.println("READY");
        velocity = new Point2D(0, 0);
    }

    @Override
    public void toggleIgnition() {
        helicopter.setHelicopterState(new Stopping(helicopter, blade,
                bladeSpeed, fuel, fuelText));
        readySoundPlayer.stop();
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

    @Override
    public void stopSounds() {
        readySoundPlayer.stop();
    }
}

