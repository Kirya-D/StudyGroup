package kirya;

import java.io.IOException;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kirya.model.Database;
import kirya.model.LocalDatabase;
import kirya.view.Root;
import kirya.viewmodel.AccountCreationViewmodel;

/**
 * JavaFX kirya.javaproject.App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Root root = new Root();
        Scene scene = new Scene(root);

        Database localDb = null;
        Database remoteDb = null;

        try {
            localDb = new LocalDatabase();
        } catch (SQLException err) {
            err.printStackTrace();
        }

        var accountCreationViewmodel = new AccountCreationViewmodel(localDb);
        root.accountCreation.setViewmodel(accountCreationViewmodel);

        stage.setTitle("StudyGroup");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
    }

    /**
     * Application entry-point.
     * 
     * @param args App args
     */
    public static void main(String[] args) {
        launch();
    }

}