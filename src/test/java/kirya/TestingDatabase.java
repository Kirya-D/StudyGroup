package kirya;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;

import kirya.model.AuthDatabase;

public class TestingDatabase extends AuthDatabase {

    private static final String SETUP_FILEPATH = TestingDatabase.class.getResource("testingdatabase.sql").getPath()
            .replaceFirst("/", "");

    public TestingDatabase() throws SQLException {
    }

    /**
     * {@inheritDoc}
     */
    protected void setupDatabase() throws SQLException {
        this.dbConnection = DriverManager.getConnection("jdbc:sqlite:");
        try {
            var dbSetupPath = Path.of(SETUP_FILEPATH);
            var setupCommands = Files.readString(dbSetupPath);
            var statement = this.dbConnection.createStatement();
            statement.execute(setupCommands);
        } catch (IOException err) {
            throw new SQLException(err.getMessage());
        }
    }
}