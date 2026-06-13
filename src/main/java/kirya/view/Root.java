package kirya.view;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import kirya.utils.DisplayText;
import kirya.view.enums.Page;
import kirya.view.events.PageRequestEvent;
import kirya.view.events.StudyGuideEvent;

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
    @FXML
    private StudyGuideEditor studyGuideEditor;
    @FXML
    private StudyGuideViewer studyGuideViewer;

    private NodeGroup primaryNodes = new NodeGroup();
    private final String cancelEditHeader = "You're about to discard your changes";
    private final String cancelEditContent = "You have unsaved changes that you will lose if you continue!";

    /**
     * Initializes a new Root component.
     */
    public Root() {
        var loader = new FXMLLoader(this.getClass().getResource("root.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
            this.bindToSelf();
            this.addInternalListeners();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void bindToSelf() {
        this.primaryNodes.addNodes(List.of(this.loading, this.logIn, this.accountCreation, this.home,
                this.studyGuideEditor, this.studyGuideViewer));
    }

    private void addInternalListeners() {
        this.addEventHandler(StudyGuideEvent.VIEW, handler -> this.viewStudyGuideHandler(handler));
        this.addEventHandler(StudyGuideEvent.START_EDIT, handler -> this.startEditingHandler(handler));
        this.addEventHandler(StudyGuideEvent.FINISH_EDIT, handler -> this.finishEditStudyGuideHandler(handler));
        this.addEventHandler(StudyGuideEvent.CLOSE, handler -> this.switchToPage(Page.HOME));

        this.addEventHandler(PageRequestEvent.PAGE_REQUEST, handler -> this.switchToPage(handler.getRequestedPage()));
    }

    private void viewStudyGuideHandler(StudyGuideEvent handler) {
        var guide = handler.getStudyGuide();
        this.studyGuideViewer.setStudyGuide(guide);
        this.switchToPage(Page.STUDYGUIDE_VIEWER);
    }

    private void startEditingHandler(StudyGuideEvent handler) {
        var guide = handler.getStudyGuide();
        this.studyGuideEditor.setStudyGuide(guide);
        this.switchToPage(Page.STUDYGUIDE_EDITOR);
    }

    private void finishEditStudyGuideHandler(StudyGuideEvent handler) {
        var returnToHome = true;
        var studyGuide = handler.getStudyGuide();

        if (handler.getSavedChanges() && studyGuide != null) {
            this.home.saveChangesToStudyGuide(studyGuide);
        } else {
            returnToHome = ConfirmationDialog.show(DisplayText.ARE_YOU_SURE, this.cancelEditHeader,
                    this.cancelEditContent);
        }

        if (returnToHome) {
            this.fireEvent(new PageRequestEvent(Page.HOME));
        }
    }

    private void switchToPage(Page requestedPage) {
        switch (requestedPage) {
            case LOGIN -> this.logIn.setVisible(true);
            case ACCOUNT_CREATION -> this.accountCreation.setVisible(true);
            case HOME -> this.home.setVisible(true);
            case STUDYGUIDE_EDITOR -> this.studyGuideEditor.setVisible(true);
            case STUDYGUIDE_VIEWER -> this.studyGuideViewer.setVisible(true);
            default -> throw new IllegalArgumentException("Unhandled value: " + requestedPage);
        }
    }

    /**
     * Go to login page.
     */
    public void goToLogin() {
        this.switchToPage(Page.LOGIN);
    }
}