package rainmaker.gameobjects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Pond extends GameObject {
    double scaleX;
    double scaleY;
    Circle pond;
    boolean isWinner = false;
    private double waterPercentage;
    private GameText waterAmountText;

    public Pond() {
        pond = new Circle(getRandomNumber(200, 600), getRandomNumber(250, 700),
                getRandomNumber(35, 50), Color.BLUE);
        this.waterPercentage = getRandomNumber(0, 25);
        this.scaleX = 1.0;
        this.scaleY = 1.0;
        waterAmountText = new GameText((int) waterPercentage + "%", Color.WHITE,
                (int) pond.getCenterX() - 7,
                (int) pond.getCenterY() + 5);
        this.getChildren().addAll(pond, waterAmountText);
    }

    public void increaseWaterAmount(double delta) {
        if (waterPercentage <= 100) {
            scaleX += delta * .10;
            scaleY += delta * .10;

            waterPercentage = waterPercentage + delta * 10;
            waterAmountText.setGameText((int) waterPercentage + "%");
            pond.setScaleX(scaleX);
            pond.setScaleY(scaleY);
        }

    }

    public double getMaxDistance() {
        return pond.getRadius() * 4;
    }

    public void update(double delta) {
        isWinner = gameWon();
    }

    public void resetPond() {
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

    public double getWaterPercentage() {
        return waterPercentage;
    }

    boolean gameWon() {
        return (int) waterPercentage == 100;
    }
}


