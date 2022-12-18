package rainmaker.gameobjects;

import javafx.scene.paint.Paint;
import javafx.scene.text.Text;


public class GameText extends GameObject {
    Text gameText = new Text();

    GameText(String text, Paint color, int x, int y) {
        gameText.setText(text);
        gameText.setStroke(color);
        gameText.setX(x);
        gameText.setY(y);
        gameText.setScaleY(-1);

        this.getChildren().add(gameText);
    }

    public void setGameText(String text) {
        gameText.setText(text);
    }

    public void update(double delta) {
    }
}


