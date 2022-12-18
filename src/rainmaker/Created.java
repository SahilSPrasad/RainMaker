package rainmaker;

import javafx.geometry.Point2D;
import rainmaker.gameobjects.TransientGameObject;

public class Created implements TransientState {
    TransientGameObject transientGameObject;
    private double vx;
    private double vy;
    private Point2D velocity;

    public Created(TransientGameObject transientGameObject) {
        velocity = new Point2D(0, 0);
        this.transientGameObject = transientGameObject;
    }

    @Override
    public void moveLeftToRight(double windSpeed) {
        vx = windSpeed * Math.cos(Math.toRadians(0));
        vy = windSpeed * Math.sin(Math.toRadians(0));

        velocity = velocity.add(vx, vy);
        transientGameObject.translate(velocity.getX(), velocity.getY());

        if (transientGameObject.getBoundsInParent().getCenterX() > 0) {
            transientGameObject.setTransientState(new InView(transientGameObject, vx, vy, velocity));
        }
    }

}
