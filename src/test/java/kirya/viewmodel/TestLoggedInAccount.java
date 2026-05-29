package kirya.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TestLoggedInAccount {

    @BeforeEach
    public void perTestSetup() {
        LoggedInAccount.LogOut();
    }

    @AfterAll
    public static void perSuiteCleanup() {
        LoggedInAccount.LogOut();
    }

    @Nested
    public class TestLogInAs {

        @Test
        public void testWhenValidUsername() {
            var expectedUsername = "myUser123";

            LoggedInAccount.LogInAs(expectedUsername);

            var actualUsername = LoggedInAccount.Username();

            assertEquals(expectedUsername, actualUsername);
        }

        @Test
        public void throwsWhenNullUsername() {
            assertThrows(IllegalArgumentException.class, () -> {
                LoggedInAccount.LogInAs(null);
            });
        }
    }

    @Nested
    public class TestLogOut {

        @Test
        public void testWhenNoLoggedInUser() {
            LoggedInAccount.LogOut();
            var actualUsername = LoggedInAccount.Username();

            assertNull(actualUsername);
        }

        @Test
        public void testWhenPreviouslyLoggedIn() {
            LoggedInAccount.LogInAs("AnotherUser456");
            LoggedInAccount.LogOut();
            var actualUsername = LoggedInAccount.Username();

            assertNull(actualUsername);
        }
    }
}
