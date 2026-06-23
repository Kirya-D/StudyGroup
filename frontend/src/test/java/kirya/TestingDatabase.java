package kirya;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import io.github.cdimascio.dotenv.Dotenv;
import kirya.model.RemoteDatabase;

public class TestingDatabase extends RemoteDatabase {

    private static final String SETUP_FILEPATH = TestingDatabase.class.getResource("testingdatabase.sql").getPath()
            .replaceFirst("/", "");

    public TestingDatabase() throws SQLException, IOException {
        super();
    }

    /**
     * {@inheritDoc}
     */
    protected void setupDatabase() throws SQLException {
        var env = Dotenv.configure().directory("../").load();
        var url = env.get("TESTING_DB_URL");
        this.dbConnection = DriverManager.getConnection(url);
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
        try {
            statement.executeBatch();
        } catch (SQLException err) {
            this.clearDatabase();
            this.setupDatabase();
        }
    }

    private void clearDatabase() throws SQLException {
        var statement = this.dbConnection.createStatement();
        var stringBuilder = new StringBuilder();

        stringBuilder.append("DROP TABLE Choice;");
        stringBuilder.append("DROP TABLE Question;");
        stringBuilder.append("DROP TABLE AccountStudyguideStatus;");
        stringBuilder.append("DROP TABLE Studyguide;");
        stringBuilder.append("DROP TABLE Account;");

        statement.execute(stringBuilder.toString());
    }
}