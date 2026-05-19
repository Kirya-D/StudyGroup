package kirya.view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import kirya.utils.DisplayableQuestion;
import kirya.utils.DisplayableStudyGuide;
import kirya.utils.Utils;
import kirya.viewmodel.StudyGuideEditorViewmodel;

/**
 * Code-behind for studyguideeditor.fxml
 */
public class StudyGuideEditor extends GridPane {

    @FXML
    private TextField titleTextField;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private ListView<DisplayableQuestion> questionsListView;

    private StudyGuideEditorViewmodel viewmodel = new StudyGuideEditorViewmodel();

    /**
     * Initializes a new StudyGuideEditor component.
     */
    public StudyGuideEditor() {
        var loader = new FXMLLoader(this.getClass().getResource("studyguideeditor.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
            this.setupCellFactory();
            this.bindToViewmodel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupCellFactory() {
        this.questionsListView.setCellFactory(listview -> new ListCell<DisplayableQuestion>() {
            private final QuestionEditor questionEditor = new QuestionEditor();

            @Override
            public void updateItem(DisplayableQuestion question, boolean empty) {
                super.updateItem(question, empty);
                if (empty || question == null) {
                    this.setText(null);
                    this.setGraphic(null);
                } else {
                    setPadding(Insets.EMPTY);
                    setGraphic(this.questionEditor);
                    this.questionEditor.setQuestion(question);
                }
            }
        });
    }

    private void bindToViewmodel() {
        this.titleTextField.textProperty().bindBidirectional(this.viewmodel.getTitleProperty());
        this.descriptionTextArea.textProperty().bindBidirectional(this.viewmodel.getDescriptionProperty());
        this.questionsListView.setItems(this.viewmodel.getQuestionsObservableList());
    }

    /**
     * Sets the study guide to edit.
     *
     * @param studyGuide The study guide to edit
     */
    public void setStudyGuide(DisplayableStudyGuide studyGuide) {
        this.viewmodel.getStudyGuideProperty().set(studyGuide);
    }

    @FXML
    private void onCancelButtonClick() {
        this.fireEditEvent(false);
    }

    @FXML
    private void onNewQuestionButtonClick() {
        this.viewmodel.addNewQuestion();
    }

    @FXML
    private void onConfirmButtonClick() {
        Alert alert = null;

        try {
            this.viewmodel.applyStudyGuideChanges();
            this.fireEditEvent(true);
        }
        catch (IllegalArgumentException e) {
            var warningMessage = Utils.capitalizeString(e.getMessage());
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(warningMessage);
        }
        catch (NullPointerException e) {
            var errorMessage = Utils.capitalizeString(e.getMessage());
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(errorMessage);
        }

        if (alert != null) {
            this.viewmodel.syncPropertiesToStudyGuideState();
            alert.showAndWait();
        }
    }

    private void fireEditEvent(boolean appliedChanges) {
        var studyGuide = this.viewmodel.getStudyGuideProperty().get();
        this.fireEvent(new StudyGuideEvent(studyGuide, appliedChanges));
    }
}
