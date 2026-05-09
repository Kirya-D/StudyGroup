/**
 * My project.
 */
module kirya {
    requires javafx.controls;
    requires javafx.fxml;

    opens kirya.view to javafx.fxml;

    exports kirya;
}
