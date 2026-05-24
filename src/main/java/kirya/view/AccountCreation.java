package kirya.view;

import java.io.IOException;
import java.sql.SQLException;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import kirya.viewmodel.AccountCreationViewmodel;

/**
 * Code-behind for accountcreation.fxml
 */
public class AccountCreation extends GridPane {

    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button createAccountButton;

    private AccountCreationViewmodel viewmodel;

    /**
     * Initializes a new AccountCreation component.
     */
    public AccountCreation() {
        var loader = new FXMLLoader(this.getClass().getResource("accountcreation.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the viewmodel of this component.
     * 
     * @param viewmodel the new viewmodel
     */
    public void setViewmodel(AccountCreationViewmodel viewmodel) {
        this.viewmodel = viewmodel;
        this.bindToViewmodel();
        this.setupWarningLabels();
    }

    private void bindToViewmodel() {
        this.usernameTextField.textProperty().bindBidirectional(this.viewmodel.getUsernameProperty());
        this.passwordField.textProperty().bindBidirectional(this.viewmodel.getPasswordProperty());

        this.createAccountButton.disableProperty()
                .bind(this.viewmodel.getUsernameIssueProperty().isNotEmpty()
                        .or(this.viewmodel.getPasswordIssueProperty().isNotEmpty()));
    }

    private void setupWarningLabels() {
        this.addWarningLabel(this.usernameTextField, this.viewmodel.getUsernameIssueProperty());
        this.addWarningLabel(this.passwordField, this.viewmodel.getPasswordIssueProperty());
    }

    private void addWarningLabel(TextField associatedField, StringProperty textProperty) {
        var parent = associatedField.getParent();
        var warningLabel = this.createWarningLabel(textProperty);
        warningLabel.visibleProperty().bind(textProperty.isNotEmpty());

        if (parent instanceof Pane pane) {
            pane.getChildren().add(warningLabel);
        }
    }

    private Node createWarningLabel(StringProperty textProperty) {
        var warningColor = Color.RED;
        var circle = new Circle(5, warningColor);
        var exclamationMark = new Text("!");
        var stackPane = new StackPane(circle, exclamationMark);
        var label = new Label();
        var hbox = new HBox(stackPane, label);

        circle.radiusProperty().bind(label.heightProperty().divide(2.5));
        label.textProperty().bind(textProperty);
        label.setTextFill(warningColor);
        hbox.setSpacing(5);
        hbox.managedProperty().bind(hbox.visibleProperty());

        return hbox;
    }

    @FXML
    private void onCreateAccountClick() {
        try {
            this.viewmodel.createAccount();
            this.usernameTextField.setText("");
            this.passwordField.setText("");
        } catch (SQLException err) {
            var alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Database error");
            alert.setContentText(err.getMessage());
            alert.showAndWait();
        }
    }
}
