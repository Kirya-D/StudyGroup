package kirya.view;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import kirya.utils.DisplayText;
import kirya.utils.DisplayableQuestion;
import kirya.utils.QuestionType;
import kirya.viewmodel.QuestionEditorViewmodel;

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

    private QuestionEditorViewmodel viewmodel;

    public QuestionEditor(QuestionEditorViewmodel viewmodel) {
        this.viewmodel = viewmodel;
        this();
    }

    public QuestionEditor(String question) {
        this.viewmodel = new QuestionEditorViewmodel(question);
        this();
    }

    public QuestionEditor() {
        var loader = new FXMLLoader(this.getClass().getResource("questioneditor.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
            this.setup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setup() {
        this.populateComboBox();
        this.bindProperties();
        this.updateDropdownButtonDisplay();
    }
    
    private void populateComboBox() {
        this.questionTypeComboBox.setItems(FXCollections.observableArrayList(QuestionType.values()));

        this.questionTypeComboBox.setConverter(new StringConverter<QuestionType>() {
            @Override
            public String toString(QuestionType questionType) {
                return QuestionType.getNameFromType(questionType);
            }

            @Override
            public QuestionType fromString(String string) {
                return QuestionType.getTypeFromName(string);
            }
        });
    }

    private void bindProperties() {
        this.headerLabel.textProperty().bind(this.questionTextField.textProperty());
        this.questionTypeComboBox.valueProperty().bindBidirectional(this.viewmodel.getQuestionTypeProperty());
        this.questionTextField.textProperty().bindBidirectional(this.viewmodel.getQuestionProperty());
        this.answerTextArea.textProperty().bindBidirectional(this.viewmodel.getAnswerProperty());

        this.configurationVBox.managedProperty().bindBidirectional(this.configurationVBox.visibleProperty());
    }

    @FXML
    private void onDropdownLabelClick() {
        this.configurationVBox.setVisible(!this.configurationVBox.isVisible());
        this.updateDropdownButtonDisplay();
    }

    private void updateDropdownButtonDisplay() {
        var appropriateText = this.configurationVBox.isVisible() ? DisplayText.TRIANGLE_UP : DisplayText.TRIANGLE_DOWN;
        this.dropdownLabel.setText(appropriateText);
    }

    public DisplayableQuestion getQuestion() {
        return this.viewmodel.getQuestionObject();
    }
}
