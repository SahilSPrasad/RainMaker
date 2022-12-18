package rainmaker.gameobjects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class HeloBlade extends GameObject {
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


