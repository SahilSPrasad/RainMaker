package rainmaker;

import rainmaker.gameobjects.GameText;
import rainmaker.gameobjects.Helicopter;
import rainmaker.gameobjects.HeloBlade;

public class Off implements HelicopterState {
    private final Helicopter helicopter;
    private final HeloBlade blade;
    private final GameText fuelText;
    private final double fuel;
    private final double bladeSpeed;


    public Off(Helicopter helicopter, HeloBlade blade,
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
