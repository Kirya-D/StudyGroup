package kirya.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * A modal confirmation dialog
 */
public class ConfirmationDialog {

    /**
     * Creates and shows a new {@link Alert} {@link AlertType#CONFIRMATION} with
     * {@code title}, {@code header}, and {@code body} and returns the result.
     * 
     * @param title  The title text
     * @param header The header text
     * @param body   The content/body text
     * @return {@code true} if the user confirmed, {@code false} otherwise
     */
    public static boolean show(String title, String header, String body) {
        var alert = new Alert(AlertType.CONFIRMATION);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setTitle(title);

        var optional = alert.showAndWait();
        var result = optional.isPresent() && optional.get() == ButtonType.OK;

        return result;
    }
}
