package kirya.viewmodel;

import java.sql.SQLException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import kirya.model.AuthDatabase;
import kirya.utils.SessionData;

public class LogInViewmodel {

    public static final String INCORRECT_CREDENTIALS = "Username or Password is incorrect";
    private AuthDatabase database;
    private StringProperty usernameProperty;
    private StringProperty passwordProperty;
    private StringProperty incorrectFieldProperty;

    /**
     * Initializes a new LogInViewmodel.
     * 
     * @param database the authentication database to rely on
     */
    public LogInViewmodel(AuthDatabase database) {
        this.database = database;
        this.usernameProperty = new SimpleStringProperty();
        this.passwordProperty = new SimpleStringProperty();
        this.incorrectFieldProperty = new SimpleStringProperty("");
    }

    /**
     * Attempts to log into the account with the credentials entered in the username
     * and password properties and returns {@code true} if successful, otherwise
     * {@code false}.
     * 
     * @return {@code true} if successful, otherwise {@code false}
     * @throws SQLException
     */
    public boolean attemptLogIn() throws SQLException {
        var accountUsername = this.usernameProperty.get();
        var accountPassword = this.passwordProperty.get();

        var success = this.database.hasAccountWithCredentials(accountUsername, accountPassword);
        if (success) {
            SessionData.logInAs(accountUsername);
        }
        var propertyText = success ? "" : INCORRECT_CREDENTIALS;
        this.incorrectFieldProperty.set(propertyText);
        return success;
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
