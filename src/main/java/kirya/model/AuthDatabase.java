package kirya.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Represents a database that can view and manipulate account information
 */
public abstract class AuthDatabase {

    private static final String USERNAME_EXISTS_QUERY = "SELECT username FROM Account WHERE username = ?";
    private static final String CORRECT_CREDENTIALS_QUERY = "SELECT username, password FROM Account WHERE username = ?";
    private static final String CREATE_ACCOUNT_QUERY = "INSERT INTO Account (username, password) VALUES (?, ?)";
    protected Connection dbConnection;
    private final PreparedStatement getUsernameIsTaken;
    private final PreparedStatement getAccountWithCredentials;
    private final PreparedStatement createAccount;

    public AuthDatabase() throws SQLException {
        this.setupDatabase();
        this.getUsernameIsTaken = this.dbConnection.prepareStatement(USERNAME_EXISTS_QUERY);
        this.getAccountWithCredentials = this.dbConnection.prepareStatement(CORRECT_CREDENTIALS_QUERY);
        this.createAccount = this.dbConnection.prepareStatement(CREATE_ACCOUNT_QUERY);
    }

    /**
     * Sets up the database connection
     * 
     * @throws SQLException if a database error occurs
     */
    protected abstract void setupDatabase() throws SQLException;

    /**
     * {@return {@code true} if an account with {@code username} exists,
     * otherwise {@code false}}
     * 
     * @param username The username to look for
     * @throws SQLException
     */
    public final boolean hasAccountWithUsername(String username) throws SQLException {
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
     * {@return {@code true} if an account with {@code username} and
     * {@code password} exists,
     * otherwise {@code false}}
     * 
     * @param username The username to compare against
     * @param password the password to compare against
     * @throws SQLException
     */
    public final boolean hasAccountWithCredentials(String username, String password) throws SQLException {
        this.getAccountWithCredentials.setString(1, username);
        var results = this.getAccountWithCredentials.executeQuery();
        var foundAccountWithCredentials = false;

        while (results.next()) {
            var resultUsername = results.getString("username");
            var resultPassword = results.getString("password");
            var usernameMatches = username.equals(resultUsername);
            var passwordMatches = password.equals(resultPassword);
            if (usernameMatches && passwordMatches) {
                foundAccountWithCredentials = true;
                break;
            }
        }

        return foundAccountWithCredentials;
    }

    /**
     * Attmepts to create a new account with {@code username} and {@code password}.
     * 
     * @param username the username to use
     * @param password the password to use
     * 
     * @throws SQLException If a database access error occurs
     */
    public final void attemptCreateAccount(String username, String password) throws SQLException {
        this.createAccount.setString(1, username);
        this.createAccount.setString(2, password);
        this.createAccount.execute();
    }
}
