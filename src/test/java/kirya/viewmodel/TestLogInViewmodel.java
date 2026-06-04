package kirya.viewmodel;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import kirya.TestingDatabase;
import kirya.model.AuthDatabase;

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
            var expectedIncorrectField = "";

            var actualUsername = viewmodel.getUsernameProperty().get();
            var actualPassword = viewmodel.getPasswordProperty().get();
            var actualIncorrectField = viewmodel.getIncorrectFieldProperty().get();

            assertAll("member checks",
                    () -> assertNull(actualUsername),
                    () -> assertNull(actualPassword),
                    () -> assertEquals(expectedIncorrectField, actualIncorrectField));
        }
    }

    @Nested
    public class TestAttemptLogIn {

        @Test
        public void testSuccessfulWhenOneRegisteredAccount() throws SQLException {
            var workingUsername = "Testing";
            var workingPassword = "Password123";
            localDb.attemptCreateAccount(workingUsername, workingPassword);
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
            localDb.attemptCreateAccount("Username1", workingPassword + "alteration");
            localDb.attemptCreateAccount(workingUsername, workingPassword);
            localDb.attemptCreateAccount("Username4", workingPassword + "boo");
            localDb.attemptCreateAccount("Username27", workingPassword + "another boo");
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
            localDb.attemptCreateAccount(workingUsername, workingPassword);
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
            localDb.attemptCreateAccount(workingUsername, workingPassword);
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
            localDb.attemptCreateAccount(workingUsername, workingPassword);
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
            localDb.attemptCreateAccount("Username1", workingPassword + "alteration");
            localDb.attemptCreateAccount(workingUsername, workingPassword);
            localDb.attemptCreateAccount("Username4", workingPassword + "boo");
            localDb.attemptCreateAccount("Username27", workingPassword + "another boo");
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
