package kirya.viewmodel;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TestAccountCreationViewmodel {

    @Nested
    public class TestConstructor {

        private AccountCreationViewmodel viewmodel;

        @BeforeEach
        public void setup() {
            this.viewmodel = new AccountCreationViewmodel();
        }

        @Test
        public void testMembers() {
            var expectedUsernameIssue = AccountCreationViewmodel.REQUIRED_FIELD;
            var expectedPasswordIssue = AccountCreationViewmodel.REQUIRED_FIELD;

            var actualUsername = this.viewmodel.getUsernameProperty().get();
            var actualPassword = this.viewmodel.getPasswordProperty().get();
            var actualUsernameIssue = this.viewmodel.getUsernameIssueProperty().get();
            var actualPasswordIssue = this.viewmodel.getPasswordIssueProperty().get();

            assertAll("member checks",
                    () -> assertNull(actualUsername),
                    () -> assertNull(actualPassword),
                    () -> assertEquals(expectedUsernameIssue, actualUsernameIssue),
                    () -> assertEquals(expectedPasswordIssue, actualPasswordIssue));
        }
    }

    @Nested
    public class TestUsernameIssues {

        private AccountCreationViewmodel viewmodel;

        @BeforeEach
        public void setup() {
            this.viewmodel = new AccountCreationViewmodel();
        }

        @Test
        public void testWhenEmpty() {
            this.viewmodel.getUsernameProperty().set("not empty");
            this.viewmodel.getUsernameProperty().set("");

            var expectedIssue = AccountCreationViewmodel.REQUIRED_FIELD;
            var actualIssue = this.viewmodel.getUsernameIssueProperty().get();

            assertEquals(expectedIssue, actualIssue);
        }

        @Test
        public void testWhenTooShort() {
            this.viewmodel.getUsernameProperty().set("hi");

            var expectedIssue = AccountCreationViewmodel.USERNAME_TOO_SHORT;
            var actualIssue = this.viewmodel.getUsernameIssueProperty().get();

            assertEquals(expectedIssue, actualIssue);
        }

        @Test
        public void testWhenNotAvailable() {
        }

        @Test
        public void testWhenNoIssues() {
            this.viewmodel.getUsernameProperty().set("ValidUsername");

            var expectedIssue = AccountCreationViewmodel.VALID_FIELD;
            var actualIssue = this.viewmodel.getUsernameIssueProperty().get();

            assertEquals(expectedIssue, actualIssue);
        }
    }

    @Nested
    public class TestPasswordIssues {

        private AccountCreationViewmodel viewmodel;

        @BeforeEach
        public void setup() {
            this.viewmodel = new AccountCreationViewmodel();
        }

        @Test
        public void testWhenEmpty() {
            this.viewmodel.getPasswordProperty().set("not empty");
            this.viewmodel.getPasswordProperty().set("");

            var expectedIssue = AccountCreationViewmodel.REQUIRED_FIELD;
            var actualIssue = this.viewmodel.getPasswordIssueProperty().get();

            assertEquals(expectedIssue, actualIssue);
        }

        @Test
        public void testWhenTooShort() {
            this.viewmodel.getPasswordProperty().set("hi");

            var expectedIssue = AccountCreationViewmodel.PASSWORD_TOO_SHORT;
            var actualIssue = this.viewmodel.getPasswordIssueProperty().get();

            assertEquals(expectedIssue, actualIssue);
        }

        @Test
        public void testWhenNoIssues() {
            this.viewmodel.getPasswordProperty().set("ValidUsername");

            var expectedIssue = AccountCreationViewmodel.VALID_FIELD;
            var actualIssue = this.viewmodel.getPasswordIssueProperty().get();

            assertEquals(expectedIssue, actualIssue);
        }
    }
}
