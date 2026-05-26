package kirya.model;

import java.sql.SQLException;

/**
 * Represents a database type that can manipulate account information
 */
public abstract class Database {

    /**
     * {@return {@code true} if an account with {@code username} exists,
     * {@code false} otherwise}
     * 
     * @param username The username to look for
     * @throws SQLException
     */
    public abstract boolean hasAccountWithUsername(String username) throws SQLException;

    /**
     * Adds a new account with {@code username} and {@code password}.
     * 
     * @param username The username of the account
     * @param password The password of the account
     * @throws SQLException
     */
    public abstract void addAccount(String username, String password) throws SQLException;
}
