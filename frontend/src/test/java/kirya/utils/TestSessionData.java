package kirya.utils;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javafx.beans.property.ListProperty;

public class TestSessionData {

    @BeforeEach
    public void perTestSetup() {
        SessionData.logOut();
    }

    @Nested
    public class TestGetFavoritedStudyguides {
        @Test
        public void testExpected() {
            var expectedType = ListProperty.class;
            var actual = SessionData.getFavoritedStudyguides();

            assertInstanceOf(expectedType, actual);
        }
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
    public class TestGetUploadedStudyguides {
        @Test
        public void testExpected() {
            var expectedType = ListProperty.class;
            var actual = SessionData.getUploadedStudyguides();

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
        public void testWhenPreviouslyAGuest() {
            SessionData.continueAsGuest();
            var expectedUsername = "myUser123";

            SessionData.logInAs(expectedUsername);

            var actualUsername = SessionData.getLoggedInUsername();

            assertAll(
                    () -> assertEquals(expectedUsername, actualUsername),
                    () -> assertFalse(SessionData.getIsGuest()));
            ;
        }

        @Test
        public void throwsWhenNullUsername() {
            assertThrows(IllegalArgumentException.class, () -> {
                SessionData.logInAs(null);
            });
        }
    }

    @Nested
    public class TestContinueAsGuest {

        @Test
        public void throwsWhenAlreadyLoggedInWithAccount() {
            SessionData.logInAs("filler name");

            assertThrows(IllegalStateException.class, () -> {
                SessionData.continueAsGuest();
            });
        }

        @Test
        public void testWhenNotLoggedIn() {
            SessionData.continueAsGuest();
            var actualUsername = SessionData.getLoggedInUsername();

            assertAll(
                    () -> assertNull(actualUsername),
                    () -> assertTrue(SessionData.getIsGuest()));
        }
    }

    @Nested
    public class TestLogOut {

        @Test
        public void testWhenNoLoggedInUser() {
            SessionData.logOut();
            var actualUsername = SessionData.getLoggedInUsername();

            assertAll(
                    () -> assertNull(actualUsername),
                    () -> assertFalse(SessionData.getIsGuest()));
        }

        @Test
        public void testWhenPreviouslyLoggedIn() {
            SessionData.logInAs("AnotherUser456");
            SessionData.logOut();
            var actualUsername = SessionData.getLoggedInUsername();

            assertAll(
                    () -> assertNull(actualUsername),
                    () -> assertFalse(SessionData.getIsGuest()));
        }

        @Test
        public void testWhenPreviouslyAGuest() {
            SessionData.continueAsGuest();
            SessionData.logOut();
            var actualUsername = SessionData.getLoggedInUsername();

            assertAll(
                    () -> assertNull(actualUsername),
                    () -> assertFalse(SessionData.getIsGuest()));
        }
    }
}
