package kirya.viewmodel;

import java.io.IOException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import kirya.model.Server;

/**
 * Viewmodel of the AccountCreation view class
 */
public class AccountCreationViewmodel {

    public static final String VALID_FIELD = "";
    public static final String REQUIRED_FIELD = "Field is required";
    private Server server;
    private BooleanProperty usernameFinalizedProperty;
    private BooleanProperty passwordFinalizedProperty;
    private StringProperty usernameProperty;
    private StringProperty passwordProperty;
    private StringProperty usernameIssueProperty;
    private StringProperty passwordIssueProperty;

    /**
     * Initializes a new AccountCreationViewmodel.
     * 
     * @param server the authentication server to rely on
     */
    public AccountCreationViewmodel(Server server) {
        this.server = server;
        this.usernameFinalizedProperty = new SimpleBooleanProperty(false);
        this.passwordFinalizedProperty = new SimpleBooleanProperty(false);
        this.usernameProperty = new SimpleStringProperty("");
        this.passwordProperty = new SimpleStringProperty("");
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
            if (currentText.isEmpty()) {
                this.usernameIssueProperty.set(REQUIRED_FIELD);
            } else {
                var invalidationReason = VALID_FIELD;
                try {
                    invalidationReason = this.server.validateUsername(currentText);
                } catch (IOException | InterruptedException err) {
                    invalidationReason = err.getLocalizedMessage();
                    System.out.println(err);
                } finally {
                    this.usernameIssueProperty.set(invalidationReason);
                }
            }
        });
        this.passwordFinalizedProperty.addListener((_, _, finalized) -> {
            var currentText = this.passwordProperty.get();
            if (currentText.isEmpty()) {
                this.passwordIssueProperty.set(REQUIRED_FIELD);
            } else {
                var invalidationReason = VALID_FIELD;
                try {
                    invalidationReason = this.server.validatePassword(currentText);
                } catch (IOException | InterruptedException err) {
                    invalidationReason = err.getLocalizedMessage();
                    System.out.println(err);
                } finally {
                    this.passwordIssueProperty.set(invalidationReason);
                }
            }
        });
    }

    /**
     * Attempts to create a new account
     */
    public void attemptCreateAccount() throws IOException, InterruptedException {
        var accountUsername = this.usernameProperty.get();
        var accountPassword = this.passwordProperty.get();

        this.server.createAccount(accountUsername, accountPassword);

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
     * {@return the password finalized {@link BooleanProperty}}
     */
    public BooleanProperty getPasswordFinalizedProperty() {
        return this.passwordFinalizedProperty;
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
