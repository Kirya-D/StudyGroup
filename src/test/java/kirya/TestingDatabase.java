package kirya;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import kirya.model.AuthDatabase;

public class TestingDatabase extends AuthDatabase {

    private static final String SETUP_FILEPATH = TestingDatabase.class.getResource("testingdatabase.sql").getPath()
            .replaceFirst("/", "");

    public TestingDatabase() throws SQLException, IOException {
        var properties = new Properties();
        var propertyResource = TestingDatabase.class.getResourceAsStream("testingauthqueries.properties");
        properties.load(propertyResource);
        super(properties);
    }

    /**
     * {@inheritDoc}
     */
    protected void setupDatabase() throws SQLException {
        this.dbConnection = DriverManager.getConnection("jdbc:sqlite:");
        var commands = new ArrayList<String>();
        try (var reader = new BufferedReader(new FileReader(SETUP_FILEPATH))) {
            var queryBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                queryBuilder.append(line);
                if (line.endsWith(";")) {
                    var cmd = queryBuilder.toString();
                    commands.add(cmd);
                    queryBuilder.setLength(0);
                }
            }
        } catch (IOException err) {
        }

        var statement = this.dbConnection.createStatement();
        for (var cmd : commands) {
            statement.addBatch(cmd);
        }
        statement.executeBatch();
    }
}