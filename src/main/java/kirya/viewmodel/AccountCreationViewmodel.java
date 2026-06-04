package kirya.viewmodel;

import java.sql.SQLException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import kirya.model.AuthDatabase;

/**
 * Viewmodel of the AccountCreation view class
 */
public class AccountCreationViewmodel {

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_FIELD_LENGTH = 32;
    public static final String VALID_FIELD = "";
    public static final String REQUIRED_FIELD = "Required";
    public static final String USERNAME_UNAVAILABLE = "Username is unavailable";
    public static final String INVALID_USERNAME_LENGTH = "Must be " + MIN_USERNAME_LENGTH + "-" + MAX_FIELD_LENGTH
            + " characters long";
    public static final String INVALID_PASSWORD_LENGTH = "Must be " + MIN_PASSWORD_LENGTH + "-" + MAX_FIELD_LENGTH
            + " characters long";
    private AuthDatabase database;
    private BooleanProperty usernameFinalizedProperty;
    private StringProperty usernameProperty;
    private StringProperty passwordProperty;
    private StringProperty usernameIssueProperty;
    private StringProperty passwordIssueProperty;

    /**
     * Initializes a new AccountCreationViewmodel.
     * 
     * @param database the authentication database to rely on
     */
    public AccountCreationViewmodel(AuthDatabase database) {
        this.database = database;
        this.usernameFinalizedProperty = new SimpleBooleanProperty();
        this.usernameProperty = new SimpleStringProperty();
        this.passwordProperty = new SimpleStringProperty();
        this.usernameIssueProperty = new SimpleStringProperty(REQUIRED_FIELD);
        this.passwordIssueProperty = new SimpleStringProperty(REQUIRED_FIELD);

        this.addPropertyListeners();
    }

    private void addPropertyListeners() {
        this.usernameFinalizedProperty.addListener((_, _, finalized) -> {
            if (!finalized) {
                return;
            }
            var currentText = this.usernameProperty.get();
            var usernameIsTaken = false;
            try {
                usernameIsTaken = this.database.hasAccountWithUsername(currentText);
            } catch (SQLException err) {
                usernameIsTaken = true;
            }

            var currentIssue = this.usernameIssueProperty.get();
            if (usernameIsTaken && currentIssue.isEmpty()) {
                this.usernameIssueProperty.set(USERNAME_UNAVAILABLE);
            }
        });
        this.usernameProperty.addListener((_, oldText, newText) -> {
            var empty = this.fieldIsEmpty(oldText, newText);
            var tooShort = this.textIsInvalidLength(newText, MIN_USERNAME_LENGTH);
            if (empty) {
                this.usernameIssueProperty.set(REQUIRED_FIELD);
            } else if (tooShort) {
                this.usernameIssueProperty.set(INVALID_USERNAME_LENGTH);
            } else {
                this.usernameIssueProperty.set(VALID_FIELD);
            }
        });
        this.passwordProperty.addListener((_, oldText, newText) -> {
            var empty = this.fieldIsEmpty(oldText, newText);
            var tooShort = this.textIsInvalidLength(newText, MIN_PASSWORD_LENGTH);
            if (empty) {
                this.passwordIssueProperty.set(REQUIRED_FIELD);
            } else if (tooShort) {
                this.passwordIssueProperty.set(INVALID_PASSWORD_LENGTH);
            } else {
                this.passwordIssueProperty.set(VALID_FIELD);
            }
        });
    }

    private boolean fieldIsEmpty(String oldText, String newText) {
        var fieldIsEmpty = false;

        if (oldText != null) {
            var wasntEmpty = !oldText.isEmpty();
            var isEmpty = newText.isEmpty();
            if (wasntEmpty && isEmpty) {
                fieldIsEmpty = true;
            }
        }

        return fieldIsEmpty;
    }

    private boolean textIsInvalidLength(String text, int minimumLength) {
        var invalid = false;

        var length = text.length();
        var tooShort = length < minimumLength;
        var tooLong = length > MAX_FIELD_LENGTH;
        if (tooShort || tooLong) {
            invalid = true;
        }

        return invalid;
    }

    /**
     * Attempts to create a new account
     * 
     * @throws SQLException
     */
    public void attemptCreateAccount() throws SQLException {
        var accountUsername = this.usernameProperty.get();
        var accountPassword = this.passwordProperty.get();
        this.database.attemptCreateAccount(accountUsername, accountPassword);

        this.usernameProperty.set("");
        this.usernameProperty.set(accountUsername);
    }

    /**
     * {@return the username finalized {@link BooleanProperty}}
     */
    public BooleanProperty getUsernameFinalizedProperty() {
        return this.usernameFinalizedProperty;
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
     * {@return the username's issue {@link StringProperty}}
     */
    public StringProperty getUsernameIssueProperty() {
        return this.usernameIssueProperty;
    }

    /**
     * {@return the password's issue {@link StringProperty}}
     */
    public StringProperty getPasswordIssueProperty() {
        return this.passwordIssueProperty;
    }
}
