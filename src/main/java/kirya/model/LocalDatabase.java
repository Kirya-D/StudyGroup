package kirya.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;

/**
 * A locally hosted {@link Database}
 */
public class LocalDatabase extends Database {

    private final String url = "jdbc:sqlite:";
    private final String setupFile = LocalDatabase.class.getResource("localdbsetup.sql").getPath().replaceFirst("/",
            "");
    private final Connection connection;
    private final String queryForUsernameTaken = "SELECT EXISTS (SELECT * FROM accounts WHERE username = ?) as 'taken'";
    private final String queryToAddAccount = "INSERT INTO accounts (username, password) VALUES (?, ?)";
    private final PreparedStatement getUsernameIsTaken;
    private final PreparedStatement addAccount;

    /**
     * Initializes a new local database.
     */
    public LocalDatabase() throws SQLException, SQLTimeoutException {
        this.connection = DriverManager.getConnection(this.url);

        try {
            var dbSetupPath = Path.of(this.setupFile);
            var setupCommands = Files.readString(dbSetupPath);
            var statement = this.connection.createStatement();
            statement.execute(setupCommands);
        } catch (IOException err) {
            throw new SQLException(err.getMessage());
        }

        this.getUsernameIsTaken = this.connection.prepareStatement(this.queryForUsernameTaken);
        this.addAccount = this.connection.prepareStatement(this.queryToAddAccount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasAccountWithUsername(String username) throws SQLException {
        var usernameTaken = false;

        this.getUsernameIsTaken.setString(1, username);
        var isResultSet = this.getUsernameIsTaken.execute();
        if (isResultSet) {
            ResultSet resultSet = this.getUsernameIsTaken.getResultSet();
            while (resultSet.next()) {
                usernameTaken = resultSet.getBoolean("taken");
            }
        }

        return usernameTaken;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAccount(String username, String password) throws SQLException {
        this.addAccount.setString(1, username);
        this.addAccount.setString(2, password);
        this.addAccount.execute();
    }

}
