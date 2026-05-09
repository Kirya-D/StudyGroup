package kirya.view;

import java.io.IOException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import kirya.utils.DisplayableQuestion;
import kirya.utils.DisplayableStudyGuide;

public class StudyGuideViewer extends GridPane {

    @FXML
    private TilePane jumpToQuestionsTilePane;
    @FXML
    private ScrollPane allQuestionsScrollPane;
    @FXML
    private ListView<DisplayableQuestion> questionsListView;

    @FXML
    public Button finishButton;
    public ObjectProperty<DisplayableStudyGuide> studyGuideProperty = new SimpleObjectProperty<>(null);
    
    public StudyGuideViewer() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("studyguideviewer.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @FXML
    private void initialize() {
        this.setupCellFactory();
        this.studyGuideProperty.addListener((_, _, newVal) -> {
            this.questionsListView.getItems().setAll(newVal.getQuestions());
            this.populateQuestionJumpTilePane();
        });
    }

    private void setupCellFactory() {
        this.questionsListView.setCellFactory(listview -> new ListCell<DisplayableQuestion>() {
            private final QuestionViewer questionViewer = new QuestionViewer();

            @Override
            protected void updateItem(DisplayableQuestion question, boolean empty) {
                super.updateItem(question, empty);
                if (empty || question == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    questionViewer.questionProperty.set(question);
                    setPadding(Insets.EMPTY);
                    setText(null);
                    setGraphic(questionViewer);
                }
            }
        });
    }
    
    private void populateQuestionJumpTilePane() {
        this.jumpToQuestionsTilePane.getChildren().clear();
        for (int i = 0; i < this.questionsListView.getItems().size(); i++) {
            var newButton = new Button("" + (i + 1));
            var associatedQuestion = this.questionsListView.getItems().get(i);
            newButton.setOnAction(handler -> {
                this.JumpToQuestion(associatedQuestion);
            });
            this.jumpToQuestionsTilePane.getChildren().add(newButton);
        }
    }

    private void JumpToQuestion(DisplayableQuestion question) {
        this.questionsListView.scrollTo(question);
    }

}
