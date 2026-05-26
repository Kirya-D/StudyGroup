package kirya;

import java.io.IOException;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kirya.model.Database;
import kirya.model.RemoteDatabase;
import kirya.view.Root;
import kirya.viewmodel.AccountCreationViewmodel;

/**
 * JavaFX kirya.javaproject.App
 */
public class App extends Application {

    private Database local = null;
    private Database remote = null;

    @Override
    public void start(Stage stage) throws IOException {
        this.connectToDatabases();
        this.showGui(stage);
    }

    private void connectToDatabases() {
        try {
            this.remote = new RemoteDatabase();
        } catch (SQLException err) {
            err.printStackTrace();
        }
    }

    private void showGui(Stage stage) {
        Root root = new Root();
        Scene scene = new Scene(root);

        var accountCreationViewmodel = new AccountCreationViewmodel(this.remote);
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