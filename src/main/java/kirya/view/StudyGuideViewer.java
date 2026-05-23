package kirya.view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import kirya.utils.DisplayableQuestion;
import kirya.utils.DisplayableStudyGuide;

/**
 * Code-behind for studyguideviewer.fxml
 */
public class StudyGuideViewer extends GridPane {
    @FXML
    private TilePane jumpToQuestionsTilePane;
    @FXML
    private ListView<DisplayableQuestion> questionsListView;

    private DisplayableStudyGuide studyGuide;

    /**
     * Initializes a new StudyGuideViewer component.
     */
    public StudyGuideViewer() {
        var loader = new FXMLLoader(this.getClass().getResource("studyguideviewer.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
            this.setupCellFactory();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupCellFactory() {
        this.questionsListView.setCellFactory(listview -> new ListCell<DisplayableQuestion>() {
            private final QuestionViewer questionViewer = new QuestionViewer();

            @Override
            public void updateItem(DisplayableQuestion question, boolean empty) {
                super.updateItem(question, empty);
                if (empty || question == null) {
                    this.setText(null);
                    this.setGraphic(null);
                } else {
                    setPadding(Insets.EMPTY);
                    setGraphic(this.questionViewer);
                    this.questionViewer.setQuestion(question);
                }
            }
        });
    }

    /**
     * Sets the study guide to view.
     * 
     * @param studyGuide The new study guide to view
     */
    public void setStudyGuide(DisplayableStudyGuide studyGuide) {
        this.studyGuide = studyGuide;
        this.refreshDisplay();
    }

    private void refreshDisplay() {
        this.questionsListView.getItems().clear();
        this.questionsListView.getItems().setAll(this.studyGuide.getQuestions());
        this.refreshJumpTilePane();
    }

    private void refreshJumpTilePane() {
        this.jumpToQuestionsTilePane.getChildren().clear();
        var questionsList = this.questionsListView.getItems();
        for (int index = 0; index < questionsList.size(); index++) {
            var associatedQuestion = questionsList.get(index);
            var jumpButton = new Button("" + (index + 1));
            var toolTip = new Tooltip(associatedQuestion.getQuestion());
            jumpButton.setTooltip(toolTip);
            jumpButton.setOnAction(handler -> {
                jumpToQuestion(associatedQuestion);
                handler.consume();
            });
            this.jumpToQuestionsTilePane.getChildren().add(jumpButton);
        }
    }

    private void jumpToQuestion(DisplayableQuestion question) {
        this.questionsListView.scrollTo(question);
    }

    @FXML
    private void onFinishButtonClick() {
        this.fireEvent(new StudyGuideEvent(null, StudyGuideEvent.CLOSE));
    }
}
