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

    private static final int MAX_AUTO_CONNECT_ATTEMPTS = 2;

    private AuthDatabase remote = null;
    private int connectionAttempts = 0;

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
        var completableFuture = CompletableFuture.supplyAsync(() -> {
            this.connectionAttempts += 1;
            AuthDatabase database = null;
            try {
                database = new RemoteDatabase();
            } catch (SQLException err) {
                err.printStackTrace();
            } catch (IOException err) {
                err.printStackTrace();
            }
            return database;
        });
        completableFuture.thenAcceptAsync(database -> {
            this.remote = database;
            this.goToLogin(root);
        }, Platform::runLater);
        completableFuture.exceptionallyAsync(exception -> {
            if (connectionAttempts < MAX_AUTO_CONNECT_ATTEMPTS) {
                this.connectToDatabases(root);
            } else {
                this.promptUserToReconnect(root);
            }
            return null;
        }, Platform::runLater);
    }

    private void promptUserToReconnect(Root root) {
        var attemptReconnect = root.promptReconnectionAttempt();
        if (attemptReconnect) {
            this.connectToDatabases(root);
        } else {
            this.goToLogin(root); // TODO add handling of viewmodels/views that rely on database property to not
                                  // cause errors with null database
        }
    }

    private void goToLogin(Root root) {
        this.bindToDatabases(root);
        root.goToLogin();
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