package kirya.view;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import kirya.view.enums.Page;
import kirya.view.events.PageRequestEvent;

/**
 * Code-behind for root.fxml
 */
public class Root extends StackPane {

    @FXML
    private Loading loading;
    @FXML
    public LogIn logIn;
    @FXML
    public AccountCreation accountCreation;
    @FXML
    public Home home;

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
        this.addEventHandler(PageRequestEvent.PAGE_REQUEST, handler -> this.switchToPage(handler.getRequestedPage()));
    }

    private void switchToPage(Page requestedPage) {
        switch (requestedPage) {
            case LOGIN -> this.logIn.setVisible(true);
            case ACCOUNT_CREATION -> this.accountCreation.setVisible(true);
            case HOME -> this.home.setVisible(true);
            default -> throw new IllegalArgumentException("Unexpected value: " + requestedPage);
        }
    }

    private void bindToSelf() {
        this.primaryNodes.addNodes(List.of(this.loading, this.logIn, this.accountCreation, this.home));
    }

    public void finishedLoading() {
        this.switchToPage(Page.LOGIN);
    }
}