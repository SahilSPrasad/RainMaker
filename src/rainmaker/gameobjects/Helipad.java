package rainmaker.gameobjects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Helipad extends GameObject {

    public Helipad(int padCenterX, int padCenterY) {
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


