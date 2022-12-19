package rainmaker.gameobjects;

import javafx.scene.paint.Color;
import rainmaker.Observer;
import rainmaker.Subject;

public class Cloud extends TransientGameObject implements Observer {
    Color cloudColor;
    BezierOval cloud;
    int r, g, b;
    private double seedPercentage = 0;
    private double windSpeed;
    private GameText cloudSeedText;

    private final Subject windSubject;

    public Cloud(Subject windSubject) {
        super();
        this.r = 255;
        this.g = 255;
        this.b = 255;
        cloudColor = Color.rgb(250, 250, 250);
        cloud = new BezierOval();
        cloudSeedText = new GameText(seedPercentage + "%", Color.BLACK,
                (int) cloud.getCenterX() - 7,
                (int) cloud.getCenterY() + 5);
        this.windSubject = windSubject;

        windSubject.register(this);

        this.getChildren().addAll(cloud, cloudSeedText);
    }

    public void seedCloud() {
        if (seedPercentage < 100) {
            seedPercentage++;
            r -= 2;
            g -= 2;
            b -= 2;
            cloudColor = Color.rgb(r, g, b);
            cloud.setFill(cloudColor);
        }
    }

    public void resetCloud() {
        resetTransient();
        this.getChildren().clear();
        seedPercentage = 0;
        cloudColor = Color.rgb(250, 250, 250);
        r = 255;
        g = 255;
        b = 255;
        cloud.setFill(cloudColor);
        cloud = new BezierOval();
        cloudSeedText = new GameText(seedPercentage + "%", Color.BLACK,
                (int) cloud.getCenterX() - 7,
                (int) cloud.getCenterY() + 5);

        this.getChildren().addAll(cloud, cloudSeedText);
    }


    public void update(double delta) {
        moveLeftToRight(windSpeed);
        double prev = seedPercentage;

        if (seedPercentage > 0) {
            seedPercentage = seedPercentage - delta;

            cloudColor = Color.rgb(r, g, b);
            cloud.setFill(cloudColor);
        }

        if ((int) seedPercentage < (int) prev && r < 255) {
            r += 2;
            g += 2;
            b += 2;
        }

        cloudSeedText.setGameText((int) seedPercentage + "%");
    }

    public int getSeedPercentage() {
        return (int) seedPercentage;
    }

    @Override
    public void updateObserver(double windSpeed) {
        this.windSpeed = windSpeed;
    }
}


