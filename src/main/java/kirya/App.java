package kirya;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kirya.model.AuthDatabase;
import kirya.model.RemoteDatabase;
import kirya.view.Root;
import kirya.viewmodel.AccountCreationViewmodel;
import kirya.viewmodel.HomeViewmodel;
import kirya.viewmodel.LogInViewmodel;

/**
 * JavaFX kirya.javaproject.App
 */
public class App extends Application {

    private AuthDatabase remote = null;

    @Override
    public void start(Stage stage) throws IOException {
        var root = new Root();
        this.showGui(stage, root);
        this.connectToDatabases(root);
    }

    private void showGui(Stage stage, Root root) {
        Scene scene = new Scene(root);

        stage.setTitle("StudyGroup");
        stage.setScene(scene);
        stage.show();
    }

    private void bindToDatabases(Root root) {
        var accountCreationViewmodel = new AccountCreationViewmodel(this.remote);
        var logInViewmodel = new LogInViewmodel(this.remote);
        var homeViewmodel = new HomeViewmodel(this.remote);

        root.accountCreation.setViewmodel(accountCreationViewmodel);
        root.logIn.setViewmodel(logInViewmodel);
        root.home.setViewmodel(homeViewmodel);
    }

    private void connectToDatabases(Root root) {
        CompletableFuture.supplyAsync(() -> {
            AuthDatabase database = null;
            try {
                database = new RemoteDatabase();
            } catch (SQLException err) {
                err.printStackTrace();
            }
            return database;
        }).thenAcceptAsync(database -> {
            this.remote = database;
            this.bindToDatabases(root);
            root.finishedLoading();
        }, Platform::runLater).exceptionally(exception -> {
            exception.printStackTrace();
            return null;
        });
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