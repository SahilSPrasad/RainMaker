package rainmaker.gameobjects;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.QuadCurve;

import java.util.ArrayList;

class BezierOval extends Group {
    Ellipse ellipse;
    Ellipse perimeter;
    double radiusX;
    double radiusY;
    int increment;

    ArrayList<QuadCurve> curves = new ArrayList<>();

    double perimeterRadiusX;
    double perimeterRadiusY;

    BezierOval() {
        ellipse = new Ellipse(getRandomNumber(-600, -50), getRandomNumber(200,
                700), getRandomNumber(40, 80), getRandomNumber(30, 50));
        ellipse.setFill(Color.WHITE);

        perimeter = new Ellipse(ellipse.getCenterX(), ellipse.getCenterY(),
                ellipse.getRadiusX() + 20, ellipse.getRadiusY() + 20);
        perimeter.setFill(Color.TRANSPARENT);

        this.radiusX = ellipse.getRadiusX();
        this.radiusY = ellipse.getRadiusY();

        this.perimeterRadiusX = perimeter.getRadiusX();
        this.perimeterRadiusY = perimeter.getRadiusY();
        this.increment = getRandomNumber(20, 50);
        createCurvesOnCloud();
        this.getChildren().addAll(ellipse, perimeter);

    }

    void setFill(Color color) {
        ellipse.setFill(color);
        for (QuadCurve curve : curves) {
            curve.setFill(color);
        }
    }

    double getCenterX() {
        return ellipse.getCenterX();
    }

    double getCenterY() {
        return ellipse.getCenterY();
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    Point2D createPointOnEllipse(double theta) {
        return getPoint2D(theta, radiusX, radiusY);
    }

    Point2D createControlPoint(double theta) {
        return getPoint2D(theta, perimeterRadiusX, perimeterRadiusY);
    }

    private Point2D getPoint2D(double theta, double perimeterRadiusX,
                               double perimeterRadiusY) {
        Point2D point = new Point2D(ellipse.getCenterX(),
                ellipse.getCenterY());
        double x = Math.sin(Math.toRadians(theta)) * perimeterRadiusX;
        double y = Math.cos(Math.toRadians(theta)) * perimeterRadiusY;
        point = point.add(x, y);
        return point;
    }

    void createBezierCurve(double theta) {
        QuadCurve curve = new QuadCurve();
        curves.add(curve);
        curve.setFill(Color.WHITE);
        //curve.setStroke(Color.BLACK);
        Point2D start = createPointOnEllipse(theta);
        Point2D controlPoint = createControlPoint(theta + increment);
        Point2D end = createPointOnEllipse(theta + increment);

        curve.setStartX(start.getX());
        curve.setStartY(start.getY());
        curve.setEndX(end.getX());
        curve.setEndY(end.getY());
        curve.setControlX(controlPoint.getX());
        curve.setControlY(controlPoint.getY());

        this.getChildren().add(curve);
    }

    void createCurvesOnCloud() {
        for (int i = 0; i < 400; i += increment)
            createBezierCurve(i);
    }
}


