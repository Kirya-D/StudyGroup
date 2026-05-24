package kirya.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TestLocalDatabase {

    Database database;

    @BeforeEach
    private void setup() throws SQLException {
        this.database = new LocalDatabase();
    }

    @Nested
    public class TestHasAccountWithUsername {

        @Test
        public void testWhenNoAccountsInDatabase() throws SQLException {
            var actual = database.hasAccountWithUsername("FreeUsername");

            assertFalse(actual);
        }

        @Test
        public void testWhenOneAccountDoesntMatchUsername() throws SQLException {
            database.addAccount("TakenName1", "bleh");
            var actual = database.hasAccountWithUsername("FreeUsername");

            assertFalse(actual);
        }

        @Test
        public void testWhenSomeAccountsButNoMatches() throws SQLException {
            database.addAccount("TakenName1", "bleh");
            database.addAccount("TakenName2", "bleh");
            database.addAccount("TakenName3", "bleh");
            var actual = database.hasAccountWithUsername("FreeUsername");

            assertFalse(actual);
        }

        @Test
        public void testWhenOneAccountAndNameIsTaken() throws SQLException {
            database.addAccount("TakenName1", "bleh");
            var actual = database.hasAccountWithUsername("TakenName1");

            assertTrue(actual);
        }

        @Test
        public void testWhenSomeAccountsAndNameIsTaken() throws SQLException {
            database.addAccount("TakenName1", "bleh");
            database.addAccount("TakenName52", "bleh");
            database.addAccount("TakenName36", "bleh");
            var actual = database.hasAccountWithUsername("TakenName52");

            assertTrue(actual);
        }
    }

    @Nested
    public class TestAddAccount {

        @Test
        public void testWhenUsernameAvailable() throws SQLException {
            var username = "freeUsername";
            database.addAccount("freeUsername", "pasword");

            var expected = database.hasAccountWithUsername(username);

            assertTrue(expected);
        }

        @Test
        public void testWhenUsernameNotAvailable() throws SQLException {
            var username = "takenUsername";
            database.addAccount("takenUsername", "pasword");
            assertThrows(SQLException.class, () -> {
                database.addAccount(username, "anotherPassword");
            });
        }
    }
}
