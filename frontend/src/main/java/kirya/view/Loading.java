package kirya.view;

import java.io.IOException;

import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.util.Duration;

/**
 * Code-behind for loading.fxml
 */
public class Loading extends StackPane {

    @FXML
    private CubicCurve CubicCurve;
    @FXML
    private Circle circle;

    private RotateTransition rotateTransition;

    /**
     * Initializes a new Loading component.
     */
    public Loading() {
        var loader = new FXMLLoader(this.getClass().getResource("loading.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
            this.addAnimation();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addAnimation() {
        this.rotateTransition = new RotateTransition();
        this.rotateTransition.setDuration(Duration.seconds(1));
        this.rotateTransition.setToAngle(360);
        this.rotateTransition.setCycleCount(RotateTransition.INDEFINITE);
        this.rotateTransition.setNode(this.circle);
        this.rotateTransition.play();

        this.sceneProperty().addListener((_, _, scene) -> {
            scene.windowProperty().addListener((_, _, window) -> {
                window.focusedProperty().addListener((_, _, focused) -> {
                    if (focused) {
                        this.rotateTransition.play();
                    } else {
                        this.rotateTransition.pause();
                    }
                });
            });
        });
    }
}
