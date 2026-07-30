package kirya;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kirya.model.Server;
import kirya.model.ServerConnection;
import kirya.utils.SessionData;
import kirya.view.ConfirmationDialog;
import kirya.view.Root;
import kirya.viewmodel.AccountCreationViewmodel;
import kirya.viewmodel.HomeViewmodel;
import kirya.viewmodel.LogInViewmodel;

/**
 * JavaFX kirya.javaproject.App
 */
public class App extends Application {

    private static final int MAX_AUTO_CONNECT_ATTEMPTS = 2;
    private static final String RECONNECT_TITLE = "Failed to Connect";
    private static final String RECONNECT_HEADER = "Failed to Connect to Server";
    private static final String RECONNECT_BODY = "Would you like to try connecting again?";

    private Server server = null;
    private int connectionAttempts = 0;
    private Runnable exitMethod = null;

    @Override
    public void start(Stage stage) throws IOException {
        var root = new Root();
        this.showGui(stage, root);
        this.connectToServer(root);
    }

    private void showGui(Stage stage, Root root) {
        Scene scene = new Scene(root);

        stage.setTitle("StudyGroup");
        stage.setScene(scene);
        stage.show();
    }

    private void connectToServer(Root root) {
        var connectionFuture = CompletableFuture.supplyAsync(() -> {
            this.connectionAttempts += 1;
            var server = new ServerConnection();
            return server;
        });

        connectionFuture.thenAcceptAsync(server -> {
            this.server = server;
            this.goToLogin(root);
        }, Platform::runLater);

        connectionFuture.exceptionallyAsync(exception -> {
            System.out.println(exception);
            if (this.connectionAttempts < MAX_AUTO_CONNECT_ATTEMPTS) {
                this.connectToServer(root);
            } else {
                this.promptUserToReconnect(root);
            }
            return null;
        }, Platform::runLater);
    }

    private void promptUserToReconnect(Root root) {
        var attemptReconnect = ConfirmationDialog.show(RECONNECT_TITLE, RECONNECT_HEADER, RECONNECT_BODY);
        if (attemptReconnect) {
            this.connectToServer(root);
        } else {
            this.goToLogin(root); // TODO add handling of viewmodels/views that rely on server property to not
                                  // cause errors with null server
        }
    }

    private void goToLogin(Root root) {
        this.injectServerDependency(root);
        root.goToLogin();
    }

    private void injectServerDependency(Root root) {
        var accountCreationViewmodel = new AccountCreationViewmodel(this.server);
        var logInViewmodel = new LogInViewmodel(this.server);
        var homeViewmodel = new HomeViewmodel(this.server);

        homeViewmodel.getFavoritedStudyGuidesProperty().bindBidirectional(SessionData.getFavoritedStudyguides());
        homeViewmodel.getUploadedStudyGuidesProperty().bindBidirectional(SessionData.getUploadedStudyguides());
        homeViewmodel.getDownloadedStudyGuidesProperty().bindBidirectional(SessionData.getDownloadedStudyguides());

        root.accountCreation.setViewmodel(accountCreationViewmodel);
        root.logIn.setViewmodel(logInViewmodel);
        root.home.setViewmodel(homeViewmodel);

        this.exitMethod = () -> {
            try {
                homeViewmodel.logOut();
            } catch (IOException | InterruptedException err) {
                System.out.println("Failed to log user out due to error: " + err);
            }
        };
    }

    @Override
    public void stop() {
        if (this.exitMethod != null) {
            this.exitMethod.run();
        }
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