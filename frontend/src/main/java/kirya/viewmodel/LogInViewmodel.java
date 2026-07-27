package kirya.viewmodel;

import java.io.IOException;
import java.util.SequencedCollection;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import kirya.model.FileIO;
import kirya.model.Server;
import kirya.model.StudyGuide;
import kirya.utils.SessionData;

public class LogInViewmodel {

    public static final String INCORRECT_CREDENTIALS = "Username or Password is incorrect";
    private Server server;
    private StringProperty usernameProperty;
    private StringProperty passwordProperty;
    private StringProperty incorrectFieldProperty;

    /**
     * Initializes a new LogInViewmodel.
     * 
     * @param server the authentication server to rely on
     */
    public LogInViewmodel(Server server) {
        this.server = server;
        this.usernameProperty = new SimpleStringProperty("");
        this.passwordProperty = new SimpleStringProperty("");
        this.incorrectFieldProperty = new SimpleStringProperty("");
    }

    /**
     * Attempts to log into the account with the credentials entered in the username
     * and password properties and returns {@code true} if successful, otherwise
     * {@code false}.
     * 
     * @return {@code true} if successful, otherwise {@code false}
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean attemptLogIn() throws IOException, InterruptedException {
        var accountUsername = this.usernameProperty.get();
        var accountPassword = this.passwordProperty.get();

        boolean loggedIn = this.server.login(accountUsername, accountPassword);
        if (loggedIn) {
            this.incorrectFieldProperty.set("");
            SequencedCollection<StudyGuide> downloadedStudyguides = FileIO.Read(accountUsername);
            SequencedCollection<StudyGuide> favoritedStudyguides = downloadedStudyguides.stream()
                    .filter(sg -> sg.getFavorited()).toList();
            SequencedCollection<StudyGuide> uploadedStudyguides = downloadedStudyguides.stream()
                    .filter(sg -> sg.getUploaded()).toList();

            SessionData.logInAs(accountUsername);
            SessionData.getDownloadedStudyguides().setAll(downloadedStudyguides);
            SessionData.getFavoritedStudyguides().setAll(favoritedStudyguides);
            SessionData.getUploadedStudyguides().setAll(uploadedStudyguides);
        } else {
            this.incorrectFieldProperty.set(INCORRECT_CREDENTIALS);
        }

        return loggedIn;
    }

    /**
     * {@return the username {@link StringProperty}}
     */
    public StringProperty getUsernameProperty() {
        return this.usernameProperty;
    }

    /**
     * {@return the password {@link StringProperty}}
     */
    public StringProperty getPasswordProperty() {
        return this.passwordProperty;
    }

    /**
     * {@return the incorrect field {@link StringProperty}}
     */
    public StringProperty getIncorrectFieldProperty() {
        return this.incorrectFieldProperty;
    }
}
