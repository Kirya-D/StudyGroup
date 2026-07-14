package kirya.viewmodel;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import kirya.utils.SessionData;

public class TestAccountCreationViewmodel {

    public MockServer mockServer;
    public AccountCreationViewmodel viewmodel;

    @BeforeEach
    public void setup() throws IOException, InterruptedException {
        SessionData.logOut();
        this.mockServer = new MockServer();
        this.viewmodel = new AccountCreationViewmodel(this.mockServer);
    }

    @Nested
    public class TestConstructor {

        @Test
        public void testMembers() {
            var expectedUsername = "";
            var expectedPassword = "";
            var expectedUsernameIssue = AccountCreationViewmodel.REQUIRED_FIELD;
            var expectedPasswordIssue = AccountCreationViewmodel.REQUIRED_FIELD;

            var actualUsernameIsFinalized = viewmodel.getUsernameFinalizedProperty().get();
            var actualUsername = viewmodel.getUsernameProperty().get();
            var actualPassword = viewmodel.getPasswordProperty().get();
            var actualUsernameIssue = viewmodel.getUsernameIssueProperty().get();
            var actualPasswordIssue = viewmodel.getPasswordIssueProperty().get();

            assertAll("member checks",
                    () -> assertFalse(actualUsernameIsFinalized),
                    () -> assertEquals(expectedUsername, actualUsername),
                    () -> assertEquals(expectedPassword, actualPassword),
                    () -> assertEquals(expectedUsernameIssue, actualUsernameIssue),
                    () -> assertEquals(expectedPasswordIssue, actualPasswordIssue));
        }
    }

    @Nested
    public class TestAttemptCreateAccount {

        @Test
        public void throwsWhenUsernameNotAvailable() throws IOException, InterruptedException {
            var takenUsername = "TakenUsername";
            viewmodel.getUsernameProperty().set(takenUsername);
            viewmodel.getPasswordProperty().set("password32");

            viewmodel.attemptCreateAccount();

            assertThrows(IOException.class, () -> {
                viewmodel.attemptCreateAccount();
            });
        }

        @Test
        public void testWhenSuccessful() throws IOException, InterruptedException {
            var username = "ValidUsername";
            var password = "ValidPassword";
            viewmodel.getUsernameProperty().set(username);
            viewmodel.getPasswordProperty().set(password);

            viewmodel.attemptCreateAccount();

            assertThrows(IOException.class, () -> {
                viewmodel.attemptCreateAccount();
            });
        }
    }

    @Nested
    public class TestUsernameIssues {

        @Test
        public void testWhenEmpty() {
            viewmodel.getUsernameProperty().set("");
            viewmodel.getUsernameFinalizedProperty().set(true);

            var expectedIssue = AccountCreationViewmodel.REQUIRED_FIELD;
            var actualIssue = viewmodel.getUsernameIssueProperty().get();

            assertEquals(expectedIssue, actualIssue);
        }

        @Test
        public void testWhenTooShort() {
            viewmodel.getUsernameProperty().set("a");
            viewmodel.getUsernameFinalizedProperty().set(true);

            var actualIssue = viewmodel.getUsernameIssueProperty().get();

            assertNotEquals(AccountCreationViewmodel.VALID_FIELD, actualIssue);
        }

        @Test
        public void testWhenTooLong() {
            var tooLongCollection = Collections.nCopies(50, "a");
            var charString = String.join("", tooLongCollection);
            viewmodel.getUsernameProperty().set(charString);
            viewmodel.getUsernameFinalizedProperty().set(true);

            var actualIssue = viewmodel.getUsernameIssueProperty().get();

            assertNotEquals(AccountCreationViewmodel.VALID_FIELD, actualIssue);
        }

        @Test
        public void testWhenNotAlphanumeric() {
            viewmodel.getUsernameProperty().set("notValid..");
            viewmodel.getUsernameFinalizedProperty().set(true);

            var actualIssue = viewmodel.getUsernameIssueProperty().get();

            assertNotEquals(AccountCreationViewmodel.VALID_FIELD, actualIssue);
        }

        @Test
        public void testWhenNoIssues() {
            viewmodel.getUsernameProperty().set("ValidUsername");
            viewmodel.getUsernameFinalizedProperty().set(true);

            var expectedIssue = AccountCreationViewmodel.VALID_FIELD;
            var actualIssue = viewmodel.getUsernameIssueProperty().get();

            assertEquals(expectedIssue, actualIssue);
        }
    }

    @Nested
    public class TestPasswordIssues {

        @Test
        public void testWhenEmpty() {
            viewmodel.getPasswordProperty().set("");
            viewmodel.getPasswordFinalizedProperty().set(true);

            var expectedIssue = AccountCreationViewmodel.REQUIRED_FIELD;
            var actualIssue = viewmodel.getPasswordIssueProperty().get();

            assertEquals(expectedIssue, actualIssue);
        }

        @Test
        public void testWhenTooShort() {
            viewmodel.getPasswordProperty().set("a");
            viewmodel.getPasswordFinalizedProperty().set(true);

            var actualIssue = viewmodel.getPasswordIssueProperty().get();

            assertNotEquals(AccountCreationViewmodel.VALID_FIELD, actualIssue);
        }

        @Test
        public void testWhenTooLong() {
            var tooLongCollection = Collections.nCopies(50, "a");
            var charString = String.join("", tooLongCollection);
            viewmodel.getPasswordProperty().set(charString);
            viewmodel.getPasswordFinalizedProperty().set(true);

            var actualIssue = viewmodel.getPasswordIssueProperty().get();

            assertNotEquals(AccountCreationViewmodel.VALID_FIELD, actualIssue);
        }

        @Test
        public void testWhenContainSpaces() {
            viewmodel.getPasswordProperty().set("notValid ..");
            viewmodel.getPasswordFinalizedProperty().set(true);

            var actualIssue = viewmodel.getPasswordIssueProperty().get();

            assertNotEquals(AccountCreationViewmodel.VALID_FIELD, actualIssue);
        }

        @Test
        public void testWhenNoIssues() {
            viewmodel.getPasswordProperty().set("validPassword");
            viewmodel.getPasswordFinalizedProperty().set(true);

            var expectedIssue = AccountCreationViewmodel.VALID_FIELD;
            var actualIssue = viewmodel.getPasswordIssueProperty().get();

            assertEquals(expectedIssue, actualIssue);
        }
    }
}
