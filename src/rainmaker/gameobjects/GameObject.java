package rainmaker.gameobjects;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import rainmaker.Updatable;

abstract class GameObject extends Group implements Updatable {
    private final Translate myTranslation;
    private final Rotate myRotation;
    private final Scale myScale;

    public GameObject() {
        myTranslation = new Translate();
        myRotation = new Rotate();
        myScale = new Scale();
        this.getTransforms().addAll(myTranslation, myRotation, myScale);
    }

    public void rotate(double degrees, double x, double y) {
        myRotation.setAngle(degrees);
        myRotation.setPivotX(x);
        myRotation.setPivotY(y);
    }

    public void scale(double sx, double sy) {
        myScale.setX(sx);
        myScale.setY(sy);
    }

    public void translate(double tx, double ty) {
        myTranslation.setX(tx);
        myTranslation.setY(ty);
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public double getMyRotation() {
        return myRotation.getAngle();
    }
}


