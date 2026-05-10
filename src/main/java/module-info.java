/**
 * My project.
 */
module kirya {
    requires javafx.controls;
    requires javafx.fxml;
    requires tools.jackson.databind;

    opens kirya.view to javafx.fxml;
    opens kirya.model to tools.jackson.databind;

    exports kirya;
}
