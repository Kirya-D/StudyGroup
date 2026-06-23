package kirya.model;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * A remote-hosted {@link AuthDatabase}
 */
public class RemoteDatabase extends AuthDatabase {

    /**
     * Initializes a new remote database
     */
    public RemoteDatabase() throws SQLException, IOException {
        var properties = new Properties();
        var propertyResource = RemoteDatabase.class.getResourceAsStream("authqueries.properties");
        properties.load(propertyResource);
        super(properties);
    }

    @Override
    protected void setupDatabase() throws SQLException {
        var env = Dotenv.configure().directory("../").load();
        var url = env.get("DB_URL");
        this.dbConnection = DriverManager.getConnection(url);
    }
}
