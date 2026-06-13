package kirya;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kirya.model.AuthDatabase;
import kirya.model.FileIO;
import kirya.model.RemoteDatabase;
import kirya.model.StudyGuide;
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
    private static final String RECONNECT_HEADER = "Failed to Connect to Database";
    private static final String RECONNECT_BODY = "Would you like to try connecting again?";

    private AuthDatabase remote = null;
    private int connectionAttempts = 0;

    @Override
    public void start(Stage stage) throws IOException {
        var root = new Root();
        this.showGui(stage, root);
        this.connectToDatabases(root);
        this.load();
    }

    private void showGui(Stage stage, Root root) {
        Scene scene = new Scene(root);

        stage.setTitle("StudyGroup");
        stage.setScene(scene);
        stage.show();
    }

    private void connectToDatabases(Root root) {
        var connectionFuture = CompletableFuture.supplyAsync(() -> {
            this.connectionAttempts += 1;
            AuthDatabase database = null;
            try {
                database = new RemoteDatabase();
            } catch (SQLException err) {
                throw new RuntimeException(err);
            } catch (IOException err) {
                throw new RuntimeException(err);
            }
            return database;
        });

        connectionFuture.thenAcceptAsync(database -> {
            this.remote = database;
            this.goToLogin(root);
        }, Platform::runLater);

        connectionFuture.exceptionallyAsync(exception -> {
            if (this.connectionAttempts < MAX_AUTO_CONNECT_ATTEMPTS) {
                this.connectToDatabases(root);
            } else {
                this.promptUserToReconnect(root);
            }
            return null;
        }, Platform::runLater);
    }

    private void promptUserToReconnect(Root root) {
        var attemptReconnect = ConfirmationDialog.show(RECONNECT_TITLE, RECONNECT_HEADER, RECONNECT_BODY);
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

    private void bindToDatabases(Root root) {
        var accountCreationViewmodel = new AccountCreationViewmodel(this.remote);
        var logInViewmodel = new LogInViewmodel(this.remote);
        var homeViewmodel = new HomeViewmodel(this.remote);

        homeViewmodel.getFavoritedStudyGuidesProperty().bindBidirectional(SessionData.getFavoritedStudyguides());
        homeViewmodel.getUploadedStudyGuidesProperty().bindBidirectional(SessionData.getUploadedStudyguides());
        homeViewmodel.getDownloadedStudyGuidesProperty().bindBidirectional(SessionData.getDownloadedStudyguides());

        root.accountCreation.setViewmodel(accountCreationViewmodel);
        root.logIn.setViewmodel(logInViewmodel);
        root.home.setViewmodel(homeViewmodel);

    }

    private void load() {
        var downloadedStudyguides = FileIO.Read();
        var favoritedStudyguides = downloadedStudyguides.stream().filter(sg -> sg.getIsFavorited()).toList();
        var uploadedStudyguides = downloadedStudyguides.stream().filter(sg -> sg.getIsUploaded()).toList();

        SessionData.getDownloadedStudyguides().setAll(downloadedStudyguides);
        SessionData.getFavoritedStudyguides().setAll(favoritedStudyguides);
        SessionData.getUploadedStudyguides().setAll(uploadedStudyguides);
    }

    @Override
    public void stop() {
        this.save();
    }

    private void save() {
        var toWrite = SessionData.getDownloadedStudyguides().stream()
                .filter(sg -> sg instanceof StudyGuide)
                .map(sg -> (StudyGuide) sg).toList();

        FileIO.Write(toWrite);
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