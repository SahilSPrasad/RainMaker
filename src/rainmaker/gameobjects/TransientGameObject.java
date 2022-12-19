package rainmaker.gameobjects;

import javafx.geometry.Point2D;
import rainmaker.Created;
import rainmaker.TransientState;

public class TransientGameObject extends GameObject {
    TransientState transientState;
    private double vx;
    private double vy;
    private Point2D velocity;

    TransientGameObject() {
        transientState = new Created(this);
        velocity = new Point2D(0, 0);

    }

    public void setTransientState(TransientState transientState) {
        this.transientState = transientState;
    }

    void moveLeftToRight(double windSpeed) {
        transientState.moveLeftToRight(windSpeed);
    }

    void resetTransient() {
        velocity = new Point2D(0, 0);
        vx = 0;
        vy = 0;
        transientState = new Created(this);
    }

    @Override
    public void update(double delta) {
    }
}


