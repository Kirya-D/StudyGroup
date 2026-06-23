/**
 * My project.
 */
module kirya {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires tools.jackson.databind;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;
    requires com.microsoft.sqlserver.jdbc;

    opens kirya.view to javafx.fxml;
    opens kirya.model to tools.jackson.databind;

    exports kirya;
    exports kirya.model;
    exports kirya.model.request;
    exports kirya.utils;
    exports kirya.viewmodel;
}
