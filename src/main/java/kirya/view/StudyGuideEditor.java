package kirya.view;

import java.io.IOException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import kirya.utils.DisplayableQuestion;
import kirya.viewmodel.StudyGuideEditorViewmodel;

public class StudyGuideEditor extends GridPane {

    @FXML
    private TextField titleTextField;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private ListView<QuestionEditor> questionsListView;
    private StudyGuideEditorViewmodel viewmodel;
    private boolean initialized = false;
    private ListProperty<DisplayableQuestion> questionsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());

    public BooleanProperty cancelledEdits = new SimpleBooleanProperty(false);
    public BooleanProperty confirmedEdits = new SimpleBooleanProperty(false);

    public StudyGuideEditor() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("studyguideeditor.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public StudyGuideEditor(StudyGuideEditorViewmodel viewmodel) {
        this();
        this.setViewmodel(viewmodel);
    }

    public final void setViewmodel(StudyGuideEditorViewmodel viewmodel) {
        this.viewmodel = viewmodel;
        this.bindToViewmodel();
    }

    @FXML
    private void initialize() {
        this.initialized = true;
        this.setupCellFactory();
        this.bindToViewmodel();
    }
    
    private void setupCellFactory() {
        this.questionsListView.setCellFactory(listview -> new ListCell<QuestionEditor>() {
            @Override
            protected void updateItem(QuestionEditor editor, boolean empty) {
                super.updateItem(editor, empty);
                if (empty || editor == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setPadding(Insets.EMPTY);
                    setText(null);
                    setGraphic(editor);
                }
            }
        });
    }

    private void bindToViewmodel() {
        if (this.initializedAndViewmodelExists()) {
            this.titleTextField.textProperty().bindBidirectional(this.viewmodel.getTitleProperty());
            this.descriptionTextArea.textProperty().bindBidirectional(this.viewmodel.getDescriptionProperty());
            this.questionsProperty.bindBidirectional(this.viewmodel.getQuestionsProperty());
            this.loadExistingQuestions();
        }
    }

    private void loadExistingQuestions() {
        this.questionsListView.getItems().clear();
        for (var questionViewmodel : this.viewmodel.getExistingQuestionEditorViewmodels()) {
            var newQuestionEditor = new QuestionEditor(questionViewmodel);
            this.addNewQuestionEditor(newQuestionEditor);
        }
    }
    
    @FXML
    private void onNewQuestionButtonClick() {
        var items = this.questionsListView.getItems();
        var count = items.size();
        var question = "Question " + (count + 1);
        var questionEditor = new QuestionEditor(question);
        this.addNewQuestionEditor(questionEditor);
        
    }

    private void addNewQuestionEditor(QuestionEditor questionEditor) {
        this.questionsListView.getItems().add(questionEditor);
        this.questionsProperty.add(questionEditor.getQuestion());
    }
    
    @FXML
    private void onConfirmButtonClick() {
        if (this.initializedAndViewmodelExists()) {
            this.viewmodel.confirmEditChanges();
            this.confirmedEdits.set(true);
        }
    }

    private boolean initializedAndViewmodelExists() {
        return this.initialized && this.viewmodel != null;
    }
}
