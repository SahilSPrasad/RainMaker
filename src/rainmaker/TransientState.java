package rainmaker;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import rainmaker.gameobjects.TransientGameObject;

public interface TransientState {
    void moveLeftToRight(double windSpeed);
}

class InView implements TransientState {
    TransientGameObject transientGameObject;
    private double vx;
    private double vy;
    private Point2D velocity;

    int r, g, b;
    Color cloudColor;
    private double seedPercentage;

    InView(TransientGameObject transientGameObject, double vx, double vy,
           Point2D velocity) {
        this.transientGameObject = transientGameObject;
        this.vx = vx;
        this.vy = vy;
        this.velocity = velocity;
        this.seedPercentage = 0;

        this.r = 255;
        this.g = 255;
        this.b = 255;
    }

    @Override
    public void moveLeftToRight(double windSpeed) {
        vx = windSpeed * Math.cos(Math.toRadians(0));
        vy = windSpeed * Math.sin(Math.toRadians(0));

        velocity = velocity.add(vx, vy);
        transientGameObject.translate(velocity.getX(), velocity.getY());

        if (transientGameObject.getBoundsInParent().getCenterX() > GameApp.GAME_WIDTH + 60) {
            transientGameObject.setTransientState(new Dead());
        }

    }

}

class Dead implements TransientState {

    Dead() {

    }

    @Override
    public void moveLeftToRight(double windSpeed) {
    }

}


