package kirya.model;

import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * A remote-hosted {@link AuthDatabase}
 */
public class RemoteDatabase extends AuthDatabase {

    /**
     * Initializes a new remote database
     */
    public RemoteDatabase() throws SQLException {
    }

    @Override
    protected void setupDatabase() throws SQLException {
        var env = Dotenv.load();
        var url = env.get("DB_URL");
        this.dbConnection = DriverManager.getConnection(url);
    }
}
