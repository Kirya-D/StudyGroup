package kirya.viewmodel;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import kirya.utils.SessionData;

public class TestLogInViewmodel {

    public MockServer mockServer;
    public LogInViewmodel viewmodel;

    @BeforeEach
    public void setup() throws IOException, InterruptedException {
        SessionData.logOut();
        this.mockServer = new MockServer();
        this.viewmodel = new LogInViewmodel(this.mockServer);
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
        public void testSuccessfulWhenOneRegisteredAccount() throws IOException, InterruptedException {
            var workingUsername = "Testing";
            var workingPassword = "Password123";
            mockServer.createAccount(workingUsername, workingPassword);
            viewmodel.getUsernameProperty().set(workingUsername);
            viewmodel.getPasswordProperty().set(workingPassword);
            viewmodel.attemptLogIn();

            boolean success = viewmodel.attemptLogIn();
            var actualLoggedInUsername = mockServer.getLoggedInUser();

            assertAll(
                    () -> assertEquals(workingUsername, actualLoggedInUsername),
                    () -> assertTrue(success));
        }

        @Test
        public void testSuccessfulWhenMultipleRegisteredAccounts() throws IOException, InterruptedException {
            var workingUsername = "Testing";
            var workingPassword = "Password123";

            mockServer.createAccount(workingUsername, workingPassword);
            for (int i = 0; i < 4; i++) {
                mockServer.createAccount(workingUsername + i, workingPassword + i);
            }
            viewmodel.getUsernameProperty().set(workingUsername);
            viewmodel.getPasswordProperty().set(workingPassword);

            boolean success = viewmodel.attemptLogIn();
            var actualLoggedInUsername = mockServer.getLoggedInUser();

            assertAll(
                    () -> assertEquals(workingUsername, actualLoggedInUsername),
                    () -> assertTrue(success));
        }

        @Test
        public void testWhenUsernameDoesNotMatch() throws IOException, InterruptedException {
            var workingUsername = "Testing";
            var notWorkingUsername = "Testing1";
            var workingPassword = "Password123";

            mockServer.createAccount(workingUsername, workingPassword);
            viewmodel.getUsernameProperty().set(notWorkingUsername);
            viewmodel.getPasswordProperty().set(workingPassword);

            boolean success = viewmodel.attemptLogIn();
            var actualLoggedInUsername = mockServer.getLoggedInUser();

            assertAll(
                    () -> assertNull(actualLoggedInUsername),
                    () -> assertFalse(success));
        }

        @Test
        public void testWhenPasswordDoesNotMatch() throws IOException, InterruptedException {
            var workingUsername = "Testing";
            var workingPassword = "Password123";
            var notWorkingPassword = "Password456";

            mockServer.createAccount(workingUsername, workingPassword);
            viewmodel.getUsernameProperty().set(workingUsername);
            viewmodel.getPasswordProperty().set(notWorkingPassword);

            boolean success = viewmodel.attemptLogIn();
            var actualLoggedInUsername = mockServer.getLoggedInUser();

            assertAll(
                    () -> assertNull(actualLoggedInUsername),
                    () -> assertFalse(success));
        }

        @Test
        public void testWhenNoRegisteredAccounts() throws IOException, InterruptedException {
            var workingUsername = "Testing";
            var workingPassword = "Password123";
            viewmodel.getUsernameProperty().set(workingUsername);
            viewmodel.getPasswordProperty().set(workingPassword);

            boolean success = viewmodel.attemptLogIn();
            var actualLoggedInUsername = mockServer.getLoggedInUser();

            assertAll(
                    () -> assertNull(actualLoggedInUsername),
                    () -> assertFalse(success));
        }

        @Test
        public void testWhenOneRegisteredAccountDoesNotMatchCredentials() throws IOException, InterruptedException {
            var workingUsername = "Testing";
            var workingPassword = "Password123";
            var nonWorkingUsername = "Testing1";

            mockServer.createAccount(workingUsername, workingPassword);
            viewmodel.getUsernameProperty().set(nonWorkingUsername);
            viewmodel.getPasswordProperty().set(workingPassword);

            boolean success = viewmodel.attemptLogIn();
            var actualLoggedInUsername = mockServer.getLoggedInUser();

            assertAll(
                    () -> assertNull(actualLoggedInUsername),
                    () -> assertFalse(success));
        }

        @Test
        public void testWhenUsernameDoesNotMultipleRegisteredAccounts() throws IOException, InterruptedException {
            var workingUsername = "Testing";
            var workingPassword = "Password123";
            var nonWorkingUsername = "Testing1";

            mockServer.createAccount(workingUsername, workingPassword);
            for (int i = 0; i < 3; i++) {
                mockServer.createAccount(workingUsername + i, workingPassword + i);
            }
            viewmodel.getUsernameProperty().set(nonWorkingUsername);
            viewmodel.getPasswordProperty().set(workingPassword);

            boolean success = viewmodel.attemptLogIn();
            var actualLoggedInUsername = mockServer.getLoggedInUser();

            assertAll(
                    () -> assertNull(actualLoggedInUsername),
                    () -> assertFalse(success));
        }
    }
}
