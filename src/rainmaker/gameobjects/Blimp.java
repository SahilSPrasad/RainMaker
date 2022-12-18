package rainmaker.gameobjects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class Blimp extends TransientGameObject {
    Image body;
    ImageView imageView;

    double fuelAmount;
    GameText fuelText;

    public Blimp() {
        super();
        body = new Image("blimp.png");
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setImage(body);
        imageView.setScaleY(-1);
        imageView.setX(-200);
        imageView.setY(getRandomNumber(200, 500));
        imageView.setFitHeight(300);
        imageView.setFitWidth(300);
        imageView.fitHeightProperty();
        fuelAmount = getRandomNumber(5000, 10000);

        fuelText = new GameText(Integer.toString((int) fuelAmount),
                Color.YELLOW,
                (int) imageView.getX() + 140
                , (int) imageView.getY() + 100);
        this.getChildren().addAll(imageView, fuelText);
    }

    public double getFuelAmount() {
        return fuelAmount;
    }

    public void removeFuel(double delta) {
        if ((int) fuelAmount > 0) {
            fuelAmount = fuelAmount - delta * 500;
            int displayFuel = (int) fuelAmount;
            fuelText.setGameText(Integer.toString(displayFuel));
        }
    }

    public void resetBlimp() {
        this.getChildren().clear();
    }


    @Override
    public void update(double delta) {
        moveLeftToRight(.3);
    }
}


