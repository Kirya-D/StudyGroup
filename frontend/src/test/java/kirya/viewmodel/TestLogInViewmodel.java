package kirya.viewmodel;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import kirya.TestingDatabase;
import kirya.model.AuthDatabase;
import kirya.model.request.CredentialsRequest;

public class TestLogInViewmodel {

    private AuthDatabase localDb;
    private LogInViewmodel viewmodel;

    @BeforeEach
    public void setup() throws SQLException, IOException {
        this.localDb = new TestingDatabase();
        this.viewmodel = new LogInViewmodel(this.localDb);
    }

    @Nested
    public class TestConstructor {

        @Test
        public void testMembers() {
            var expectedUsername = "";
            var expectedPassword = "";
            var expectedIncorrectField = "";

            var actualUsername = viewmodel.getUsernameProperty().get();
            var actualPassword = viewmodel.getPasswordProperty().get();
            var actualIncorrectField = viewmodel.getIncorrectFieldProperty().get();

            assertAll("member checks",
                    () -> assertEquals(expectedUsername, actualUsername),
                    () -> assertEquals(expectedPassword, actualPassword),
                    () -> assertEquals(expectedIncorrectField, actualIncorrectField));
        }
    }

    @Nested
    public class TestAttemptLogIn {

        @Test
        public void testSuccessfulWhenOneRegisteredAccount() throws SQLException {
            var workingUsername = "Testing";
            var workingPassword = "Password123";
            var credentialRequest = new CredentialsRequest(workingUsername, workingPassword);
            localDb.attemptCreateAccount(credentialRequest);
            viewmodel.getUsernameProperty().set(workingUsername);
            viewmodel.getPasswordProperty().set(workingPassword);

            var expectedSuccess = true;
            var expectedIncorrectFieldText = "";
            var actualSuccess = viewmodel.attemptLogIn();
            var actualIncorrectFieldText = viewmodel.getIncorrectFieldProperty().get();

            assertAll("member check",
                    () -> assertEquals(expectedIncorrectFieldText, actualIncorrectFieldText),
                    () -> assertEquals(expectedSuccess, actualSuccess));
        }

        @Test
        public void testSuccessfulWhenMultipleRegisteredAccounts() throws SQLException {
            var workingUsername = "Testing";
            var workingPassword = "Password123";

            var credentialRequestVar1 = new CredentialsRequest("Username1", workingPassword + "alteration");
            var workingCredentialRequest = new CredentialsRequest(workingUsername, workingPassword);
            var credentialRequestVar2 = new CredentialsRequest("Username4", workingPassword + "boo");
            var credentialRequestVar3 = new CredentialsRequest("Username27", workingPassword + "another boo");

            localDb.attemptCreateAccount(credentialRequestVar1);
            localDb.attemptCreateAccount(workingCredentialRequest);
            localDb.attemptCreateAccount(credentialRequestVar2);
            localDb.attemptCreateAccount(credentialRequestVar3);
            viewmodel.getUsernameProperty().set(workingUsername);
            viewmodel.getPasswordProperty().set(workingPassword);

            var expectedSuccess = true;
            var expectedIncorrectFieldText = "";
            var actualSuccess = viewmodel.attemptLogIn();
            var actualIncorrectFieldText = viewmodel.getIncorrectFieldProperty().get();

            assertAll("member check",
                    () -> assertEquals(expectedIncorrectFieldText, actualIncorrectFieldText),
                    () -> assertEquals(expectedSuccess, actualSuccess));
        }

        @Test
        public void testFailureWhenUsernameDoesNotMatch() throws SQLException {
            var workingUsername = "Testing";
            var notWorkingUsername = "Testing1";
            var workingPassword = "Password123";
            var credentialRequest = new CredentialsRequest(workingUsername, workingPassword);

            localDb.attemptCreateAccount(credentialRequest);
            viewmodel.getUsernameProperty().set(notWorkingUsername);
            viewmodel.getPasswordProperty().set(workingPassword);

            var expectedSuccess = false;
            var expectedIncorrectFieldText = LogInViewmodel.INCORRECT_CREDENTIALS;
            var actualSuccess = viewmodel.attemptLogIn();
            var actualIncorrectFieldText = viewmodel.getIncorrectFieldProperty().get();

            assertAll("member check",
                    () -> assertEquals(expectedIncorrectFieldText, actualIncorrectFieldText),
                    () -> assertEquals(expectedSuccess, actualSuccess));
        }

        @Test
        public void testFailureWhenPasswordDoesNotMatch() throws SQLException {
            var workingUsername = "Testing";
            var workingPassword = "Password123";
            var notWorkingPassword = "Password456";
            var credentialRequest = new CredentialsRequest(workingUsername, workingPassword);

            localDb.attemptCreateAccount(credentialRequest);
            viewmodel.getUsernameProperty().set(workingUsername);
            viewmodel.getPasswordProperty().set(notWorkingPassword);

            var expectedSuccess = false;
            var expectedIncorrectFieldText = LogInViewmodel.INCORRECT_CREDENTIALS;
            var actualSuccess = viewmodel.attemptLogIn();
            var actualIncorrectFieldText = viewmodel.getIncorrectFieldProperty().get();

            assertAll("member check",
                    () -> assertEquals(expectedIncorrectFieldText, actualIncorrectFieldText),
                    () -> assertEquals(expectedSuccess, actualSuccess));
        }

        @Test
        public void testFailureWhenNoRegisteredAccounts() throws SQLException {
            var workingUsername = "Testing";
            var workingPassword = "Password123";
            viewmodel.getUsernameProperty().set(workingUsername);
            viewmodel.getPasswordProperty().set(workingPassword);

            var expectedSuccess = false;
            var expectedIncorrectFieldText = LogInViewmodel.INCORRECT_CREDENTIALS;
            var actualSuccess = viewmodel.attemptLogIn();
            var actualIncorrectFieldText = viewmodel.getIncorrectFieldProperty().get();

            assertAll("member check",
                    () -> assertEquals(expectedIncorrectFieldText, actualIncorrectFieldText),
                    () -> assertEquals(expectedSuccess, actualSuccess));
        }

        @Test
        public void testFailureWhenOneRegisteredAccounts() throws SQLException {
            var workingUsername = "Testing";
            var workingPassword = "Password123";
            var nonWorkingUsername = "Testing1";
            var credentialRequest = new CredentialsRequest(workingUsername, workingPassword);

            localDb.attemptCreateAccount(credentialRequest);
            viewmodel.getUsernameProperty().set(nonWorkingUsername);
            viewmodel.getPasswordProperty().set(workingPassword);

            var expectedSuccess = false;
            var expectedIncorrectFieldText = LogInViewmodel.INCORRECT_CREDENTIALS;
            var actualSuccess = viewmodel.attemptLogIn();
            var actualIncorrectFieldText = viewmodel.getIncorrectFieldProperty().get();

            assertAll("member check",
                    () -> assertEquals(expectedIncorrectFieldText, actualIncorrectFieldText),
                    () -> assertEquals(expectedSuccess, actualSuccess));
        }

        @Test
        public void testFailureWhenMultipleRegisteredAccounts() throws SQLException {
            var workingUsername = "Testing";
            var workingPassword = "Password123";
            var nonWorkingUsername = "Testing1";

            var credentialRequestVar1 = new CredentialsRequest("Username1", workingPassword + "alteration");
            var workingCredentialRequest = new CredentialsRequest(workingUsername, workingPassword);
            var credentialRequestVar2 = new CredentialsRequest("Username4", workingPassword + "boo");
            var credentialRequestVar3 = new CredentialsRequest("Username27", workingPassword + "another boo");

            localDb.attemptCreateAccount(credentialRequestVar1);
            localDb.attemptCreateAccount(workingCredentialRequest);
            localDb.attemptCreateAccount(credentialRequestVar2);
            localDb.attemptCreateAccount(credentialRequestVar3);
            viewmodel.getUsernameProperty().set(nonWorkingUsername);
            viewmodel.getPasswordProperty().set(workingPassword);

            var expectedSuccess = false;
            var expectedIncorrectFieldText = LogInViewmodel.INCORRECT_CREDENTIALS;
            var actualSuccess = viewmodel.attemptLogIn();
            var actualIncorrectFieldText = viewmodel.getIncorrectFieldProperty().get();

            assertAll("member check",
                    () -> assertEquals(expectedIncorrectFieldText, actualIncorrectFieldText),
                    () -> assertEquals(expectedSuccess, actualSuccess));
        }
    }
}
