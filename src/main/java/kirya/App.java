package kirya;

import java.io.IOException;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX kirya.javaproject.App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("view/rootdisplay.fxml")));

        Scene scene = new Scene(root);
        stage.setTitle("StudyGroup");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Application entry-point.
     * @param args App args
     */
    public static void main(String[] args) {
        launch();
    }

}