package kirya.view;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

/**
 * Code-behind for root.fxml
 */
public class Root extends StackPane {

    @FXML
    public AccountCreation accountCreation;
    @FXML
    private Home home;

    private NodeGroup primaryNodes = new NodeGroup();

    /**
     * Initializes a new Root component.
     */
    public Root() {
        var loader = new FXMLLoader(this.getClass().getResource("root.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
            this.addInternalListeners();
            this.bindToSelf();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addInternalListeners() {

    }

    private void bindToSelf() {
        this.primaryNodes.addNodes(List.of(this.accountCreation, this.home));
    }
}