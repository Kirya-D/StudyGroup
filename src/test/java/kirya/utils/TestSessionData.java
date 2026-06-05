package kirya.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javafx.beans.property.ListProperty;

public class TestSessionData {

    @BeforeEach
    public void perTestSetup() {
        SessionData.logOut();
    }

    @AfterAll
    public static void perSuiteCleanup() {
        SessionData.logOut();
    }

    @Nested
    public class TestGetDownloadedStudyguides {
        @Test
        public void testExpected() {
            var expectedType = ListProperty.class;
            var actual = SessionData.getDownloadedStudyguides();

            assertInstanceOf(expectedType, actual);
        }
    }

    @Nested
    public class TestLogInAs {

        @Test
        public void testWhenValidUsername() {
            var expectedUsername = "myUser123";

            SessionData.logInAs(expectedUsername);

            var actualUsername = SessionData.getLoggedInUsername();

            assertEquals(expectedUsername, actualUsername);
        }

        @Test
        public void throwsWhenNullUsername() {
            assertThrows(IllegalArgumentException.class, () -> {
                SessionData.logInAs(null);
            });
        }
    }

    @Nested
    public class TestLogOut {

        @Test
        public void testWhenNoLoggedInUser() {
            SessionData.logOut();
            var actualUsername = SessionData.getLoggedInUsername();

            assertNull(actualUsername);
        }

        @Test
        public void testWhenPreviouslyLoggedIn() {
            SessionData.logInAs("AnotherUser456");
            SessionData.logOut();
            var actualUsername = SessionData.getLoggedInUsername();

            assertNull(actualUsername);
        }
    }
}
