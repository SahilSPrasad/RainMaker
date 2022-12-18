package rainmaker.gameobjects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


class HeloBody extends GameObject {
    Image body;
    ImageView imageView;

    HeloBody(int x, int y) {
        body = new Image("helibody2.png");
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setImage(body);
        imageView.setScaleY(-1);
        imageView.setX(x - 30);
        imageView.setY(y - 50);
        imageView.fitHeightProperty();
        imageView.setFitHeight(100);
        imageView.setFitWidth(170);
        this.getChildren().add(imageView);
    }

    @Override
    public void update(double delta) {
    }
}


