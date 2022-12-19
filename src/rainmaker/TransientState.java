package rainmaker;

import javafx.geometry.Point2D;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import rainmaker.gameobjects.Blimp;
import rainmaker.gameobjects.TransientGameObject;

public interface TransientState {
    void moveLeftToRight(double windSpeed);
}

class InView implements TransientState {
    TransientGameObject transientGameObject;
    int r, g, b;
    Color cloudColor;
    private double vx;
    private double vy;
    private Point2D velocity;
    private final double seedPercentage;
    private AudioClip transientAudioClip;

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

        if (transientGameObject instanceof Blimp) {
            transientAudioClip = new AudioClip(this.getClass()
                    .getResource("\\blimpsound.mp3")
                    .toExternalForm());
            transientAudioClip.setVolume(.03);
            transientAudioClip.play();
        }
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


