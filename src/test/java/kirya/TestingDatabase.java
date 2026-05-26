package kirya;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import kirya.model.Database;
import kirya.model.RemoteDatabase;

/**
 * A {@link Database} class specifically for testing to fill-in for
 * {@link RemoteDatabase}
 */
public class TestingDatabase extends Database {

    private static final String SETUP_FILEPATH = TestingDatabase.class.getResource("testingdatabase.sql").getPath()
            .replaceFirst("/", "");
    private static final String USERNAME_EXISTS_QUERY = "SELECT * FROM Account WHERE username = ?";
    private static final String CREATE_ACCOUNT_QUERY = "INSERT INTO Account (username, password) VALUES (?, ?)";
    private Connection dbConnection;
    private final PreparedStatement getUsernameIsTaken;
    private final PreparedStatement createAccount;

    /**
     * Initializes a new Testing Database
     */
    public TestingDatabase() throws SQLException {
        this.dbConnection = DriverManager.getConnection("jdbc:sqlite:");
        this.setupDatabase();
        this.getUsernameIsTaken = this.dbConnection.prepareStatement(USERNAME_EXISTS_QUERY);
        this.createAccount = this.dbConnection.prepareStatement(CREATE_ACCOUNT_QUERY);
    }

    private void setupDatabase() throws SQLException {
        try {
            var dbSetupPath = Path.of(SETUP_FILEPATH);
            var setupCommands = Files.readString(dbSetupPath);
            var statement = this.dbConnection.createStatement();
            statement.execute(setupCommands);
        } catch (IOException err) {
            throw new SQLException(err.getMessage());
        }
    }

    @Override
    public boolean hasAccountWithUsername(String username) throws SQLException {
        this.getUsernameIsTaken.setString(1, username);
        var isResultSet = this.getUsernameIsTaken.execute();
        var usernameTaken = false;
        if (isResultSet) {
            var resultSet = this.getUsernameIsTaken.getResultSet();
            if (resultSet.next()) {
                usernameTaken = true;
            }
        }
        return usernameTaken;
    }

    @Override
    public void addAccount(String username, String password) throws SQLException {
        this.createAccount.setString(1, username);
        this.createAccount.setString(2, password);
        this.createAccount.execute();
    }

}
