package kirya.view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import kirya.utils.DisplayText;
import kirya.utils.DisplayableQuestion;
import kirya.utils.QuestionType;
import kirya.viewmodel.QuestionEditorViewmodel;

/**
 * Code-behind for questioneditor.fxml
 */
public class QuestionEditor extends VBox {
    @FXML
    private Label headerLabel;
    @FXML
    private Label dropdownLabel;
    @FXML
    private VBox configurationVBox;
    @FXML
    private ComboBox<QuestionType> questionTypeComboBox;
    @FXML
    private TextField questionTextField;
    @FXML
    private TextArea answerTextArea;

    private QuestionEditorViewmodel viewmodel = new QuestionEditorViewmodel();

    /**
     * Initializes a new QuestionEditor component.
     */
    public QuestionEditor() {
        var loader = new FXMLLoader(this.getClass().getResource("questioneditor.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
            this.setupComboBox();
            this.bindToSelf();
            this.bindToViewmodel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupComboBox() {
        this.questionTypeComboBox.getItems().setAll(QuestionType.values());
        this.questionTypeComboBox.setConverter(new StringConverter<QuestionType>() {
            @Override
            public QuestionType fromString(String string) {
                return QuestionType.getTypeFromName(string);
            }

            @Override
            public String toString(QuestionType questionType) {
                return QuestionType.getNameFromType(questionType);
            }

        });
    }

    private void bindToSelf() {
        this.configurationVBox.managedProperty().bind(this.configurationVBox.visibleProperty());
    }

    private void bindToViewmodel() {
        this.headerLabel.textProperty().bind(this.viewmodel.getQuestionProperty());
        this.questionTypeComboBox.valueProperty().bindBidirectional(this.viewmodel.getQuestionTypeProperty());
        this.questionTextField.textProperty().bindBidirectional(this.viewmodel.getQuestionProperty());
        this.answerTextArea.textProperty().bindBidirectional(this.viewmodel.getAnswerProperty());
    }

    /**
     * Sets the question to edit.
     * @param question The new question to edit
     */
    public void setQuestion(DisplayableQuestion question) {
        this.viewmodel.getQuestionObjectProperty().set(question);
    }

    @FXML
    private void onTrashButtonClick() {
        var parent = this.getParent();
        var success = false;
        while (parent != null && !success) {
            if (parent instanceof ListCell listCell) {
                var associatedQuestion = this.viewmodel.getQuestionObjectProperty().get();
                listCell.getListView().getItems().remove(associatedQuestion);
                success = true;
            }
            parent = parent.getParent();
        }
    }

    @FXML
    private void onDropdownLabelClick() {
        this.configurationVBox.setVisible(!this.configurationVBox.isVisible());
        var dropdownText = this.configurationVBox.isVisible() ? DisplayText.TRIANGLE_UP : DisplayText.TRIANGLE_DOWN;
        this.dropdownLabel.setText(dropdownText);
    }
}
