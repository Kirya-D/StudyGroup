package kirya.viewmodel;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.SQLException;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import kirya.model.LocalDatabase;

public class TestAccountCreationViewmodel {

    private AccountCreationViewmodel viewmodel;

    @BeforeEach
    public void setup() throws SQLException {
        var localDb = new LocalDatabase();
        this.viewmodel = new AccountCreationViewmodel(localDb);
    }

    @Nested
    public class TestConstructor {

        @Test
        public void testMembers() {
            var expectedUsernameIssue = AccountCreationViewmodel.REQUIRED_FIELD;
            var expectedPasswordIssue = AccountCreationViewmodel.REQUIRED_FIELD;

            var actualUsername = viewmodel.getUsernameProperty().get();
            var actualPassword = viewmodel.getPasswordProperty().get();
            var actualUsernameIssue = viewmodel.getUsernameIssueProperty().get();
            var actualPasswordIssue = viewmodel.getPasswordIssueProperty().get();

            assertAll("member checks",
                    () -> assertNull(actualUsername),
                    () -> assertNull(actualPassword),
                    () -> assertEquals(expectedUsernameIssue, actualUsernameIssue),
                    () -> assertEquals(expectedPasswordIssue, actualPasswordIssue));
        }
    }

    @Nested
    public class TestUsernameIssues {

        @Test
        public void testWhenEmpty() {
            viewmodel.getUsernameProperty().set("not empty");
            viewmodel.getUsernameProperty().set("");

            var expectedIssue = AccountCreationViewmodel.REQUIRED_FIELD;
            var actualIssue = viewmodel.getUsernameIssueProperty().get();

            assertEquals(expectedIssue, actualIssue);
        }

        @Test
        public void testWhenTooShort() {
            viewmodel.getUsernameProperty().set("hi");

            var expectedIssue = AccountCreationViewmodel.INVALID_USERNAME_LENGTH;
            var actualIssue = viewmodel.getUsernameIssueProperty().get();

            assertEquals(expectedIssue, actualIssue);
        }

        @Test
        public void testWhenTooLong() {
            var thirtyThreeCharacters = Collections.nCopies(33, "x");
            var charString = String.join("", thirtyThreeCharacters);
            viewmodel.getUsernameProperty().set(charString);

            var expectedIssue = AccountCreationViewmodel.INVALID_USERNAME_LENGTH;
            var actualIssue = viewmodel.getUsernameIssueProperty().get();

            assertEquals(expectedIssue, actualIssue);
        }

        @Test
        public void testWhenNotAvailable() throws SQLException {
            viewmodel.getUsernameProperty().set("takenUsername");
            viewmodel.getPasswordProperty().set("password123");
            viewmodel.createAccount();

            var expectedIssue = AccountCreationViewmodel.USERNAME_UNAVAILABLE;
            var actualIssue = viewmodel.getUsernameIssueProperty().get();

            assertEquals(expectedIssue, actualIssue);
        }

        @Test
        public void testWhenNoIssues() {
            viewmodel.getUsernameProperty().set("ValidUsername");

            var expectedIssue = AccountCreationViewmodel.VALID_FIELD;
            var actualIssue = viewmodel.getUsernameIssueProperty().get();

            assertEquals(expectedIssue, actualIssue);
        }
    }

    @Nested
    public class TestPasswordIssues {

        @Test
        public void testWhenEmpty() {
            viewmodel.getPasswordProperty().set("not empty");
            viewmodel.getPasswordProperty().set("");

            var expectedIssue = AccountCreationViewmodel.REQUIRED_FIELD;
            var actualIssue = viewmodel.getPasswordIssueProperty().get();

            assertEquals(expectedIssue, actualIssue);
        }

        @Test
        public void testWhenTooShort() {
            viewmodel.getPasswordProperty().set("hi");

            var expectedIssue = AccountCreationViewmodel.INVALID_PASSWORD_LENGTH;
            var actualIssue = viewmodel.getPasswordIssueProperty().get();

            assertEquals(expectedIssue, actualIssue);
        }

        @Test
        public void testWhenTooLong() {
            var thirtyThreeCharacters = Collections.nCopies(33, "x");
            var charString = String.join("", thirtyThreeCharacters);
            viewmodel.getPasswordProperty().set(charString);

            var expectedIssue = AccountCreationViewmodel.INVALID_PASSWORD_LENGTH;
            var actualIssue = viewmodel.getPasswordIssueProperty().get();

            assertEquals(expectedIssue, actualIssue);
        }

        @Test
        public void testWhenNoIssues() {
            viewmodel.getPasswordProperty().set("ValidUsername");

            var expectedIssue = AccountCreationViewmodel.VALID_FIELD;
            var actualIssue = viewmodel.getPasswordIssueProperty().get();

            assertEquals(expectedIssue, actualIssue);
        }
    }
}
