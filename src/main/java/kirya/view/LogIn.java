package kirya.view;

import java.io.IOException;
import java.sql.SQLException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
import kirya.view.enums.Page;
import kirya.view.events.PageRequestEvent;
import kirya.viewmodel.LogInViewmodel;

/**
 * Code-behind for login.fxml
 */
public class LogIn extends GridPane {

    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;

    private BooleanProperty loggedInProperty = new SimpleBooleanProperty(false);
    private LogInViewmodel viewmodel;

    /**
     * Initializes a new LogIn component.
     */
    public LogIn() {
        var loader = new FXMLLoader(this.getClass().getResource("login.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@return the logged in {@link BooleanProperty}}
     */
    public BooleanProperty LoggedInProperty() {
        return this.loggedInProperty;
    }

    /**
     * Sets the viewmodel of this component.
     * 
     * @param viewmodel the new viewmodel
     */
    public void setViewmodel(LogInViewmodel viewmodel) {
        this.viewmodel = viewmodel;
        this.bindToViewmodel();
        this.setupWarningLabels();
    }

    private void bindToViewmodel() {
        this.usernameTextField.textProperty().bindBidirectional(this.viewmodel.getUsernameProperty());
        this.passwordField.textProperty().bindBidirectional(this.viewmodel.getPasswordProperty());
    }

    private void setupWarningLabels() {
        this.addWarningLabel(this.usernameTextField, this.viewmodel.getIncorrectFieldProperty());
        this.addWarningLabel(this.passwordField, this.viewmodel.getIncorrectFieldProperty());
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
    private void onLogInButtonClick() {
        var success = false;

        try {
            success = this.viewmodel.attemptLogIn();
        } catch (SQLException err) {
            var alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Database error");
            alert.setContentText(err.getMessage());
            alert.showAndWait();
        }

        if (success) {
            var pageRequestEvent = new PageRequestEvent(Page.HOME);
            this.fireEvent(pageRequestEvent);
        }
    }

    @FXML
    private void onRegisterHyperlinkClick() {
        var pageRequestEvent = new PageRequestEvent(Page.ACCOUNT_CREATION);
        this.fireEvent(pageRequestEvent);
    }
}
