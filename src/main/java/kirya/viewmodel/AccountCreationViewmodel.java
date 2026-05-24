package kirya.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Viewmodel of the AccountCreation view class
 */
public class AccountCreationViewmodel {

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MIN_PASSWORD_LENGTH = 8;
    public static final String VALID_FIELD = "";
    public static final String REQUIRED_FIELD = "Required";
    public static final String USERNAME_UNAVAILABLE = "Username is unavailable";
    public static final String USERNAME_TOO_SHORT = "Must be at least " + MIN_USERNAME_LENGTH + " characters long";
    public static final String PASSWORD_TOO_SHORT = "Must be at least " + MIN_PASSWORD_LENGTH + " characters long";
    private StringProperty usernameProperty;
    private StringProperty passwordProperty;
    private StringProperty usernameIssueProperty;
    private StringProperty passwordIssueProperty;

    /**
     * Initializes a new AccountCreationViewmodel.
     */
    public AccountCreationViewmodel() {
        this.usernameProperty = new SimpleStringProperty();
        this.passwordProperty = new SimpleStringProperty();
        this.usernameIssueProperty = new SimpleStringProperty(REQUIRED_FIELD);
        this.passwordIssueProperty = new SimpleStringProperty(REQUIRED_FIELD);

        this.addPropertyListeners();
    }

    private void addPropertyListeners() {
        this.usernameProperty.addListener((_, oldText, newText) -> {
            var empty = this.fieldIsEmpty(oldText, newText);
            var tooShort = this.textIsTooShort(newText, MIN_USERNAME_LENGTH);
            var usernameIsTaken = false; // TODO Implement checking if username is available
            if (empty) {
                this.usernameIssueProperty.set(REQUIRED_FIELD);
            } else if (tooShort) {
                this.usernameIssueProperty.set(USERNAME_TOO_SHORT);
            } else if (usernameIsTaken) {
                this.usernameIssueProperty.set(USERNAME_UNAVAILABLE);
            } else {
                this.usernameIssueProperty.set(VALID_FIELD);
            }
        });
        this.passwordProperty.addListener((_, oldText, newText) -> {
            var empty = this.fieldIsEmpty(oldText, newText);
            var tooShort = this.textIsTooShort(newText, MIN_PASSWORD_LENGTH);
            if (empty) {
                this.passwordIssueProperty.set(REQUIRED_FIELD);
            } else if (tooShort) {
                this.passwordIssueProperty.set(PASSWORD_TOO_SHORT);
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

    private boolean textIsTooShort(String text, int minimumLength) {
        var tooShort = false;

        if (text.length() < minimumLength) {
            tooShort = true;
        }

        return tooShort;
    }

    public void createAccount() {
        // TODO implement account creation
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
