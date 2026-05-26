package kirya.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * A remote-hosted {@link Database}
 */
public class RemoteDatabase extends Database {

    private static final String USERNAME_EXISTS_QUERY = "SELECT * FROM Account WHERE username = ?";
    private static final String CREATE_ACCOUNT_QUERY = "INSERT INTO Account (username, password) VALUES (?, ?)";
    private Connection dbConnection;
    private final PreparedStatement getUsernameIsTaken;
    private final PreparedStatement createAccount;

    /**
     * Initializes a new remote database
     */
    public RemoteDatabase() throws SQLException {
        var env = Dotenv.load();
        var url = env.get("DB_URL");
        this.dbConnection = DriverManager.getConnection(url);
        this.getUsernameIsTaken = this.dbConnection.prepareStatement(USERNAME_EXISTS_QUERY);
        this.createAccount = this.dbConnection.prepareStatement(CREATE_ACCOUNT_QUERY);
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * Attmepts to create a new account with {@code username} and {@code password}.
     * 
     * @param username the username to use
     * @param password the password to use
     * 
     * @throws SQLException If a database access error occurs
     */
    @Override
    public void addAccount(String username, String password) throws SQLException {
        this.createAccount.setString(1, username);
        this.createAccount.setString(2, password);
        this.createAccount.execute();
    }

}
