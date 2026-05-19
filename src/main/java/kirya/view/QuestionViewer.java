package kirya.view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import kirya.utils.DisplayableQuestion;

/**
 * Code-behind for questionviewer.fxml
 */
public class QuestionViewer extends GridPane {
    @FXML
    private Label questionLabel;
    @FXML
    private GridPane freeResponseAnswerGridPane;
    @FXML
    private Label answerLabel;
    @FXML
    private ListView<String> multipleChoiceAnswerListView;

    private DisplayableQuestion displayableQuestion;

    /**
     * Initializes a new QuestionViewer component.
     */
    public QuestionViewer() {
        var loader = new FXMLLoader(this.getClass().getResource("questionviewer.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the question to view.
     * @param question The new question to view
     */
    public void setQuestion(DisplayableQuestion question) {
        this.displayableQuestion = question;
        this.refreshDisplay();
    }

    private void refreshDisplay() {
        if (this.displayableQuestion == null) {
            return;
        }

        var allAnswers = this.displayableQuestion.getAnswers();
        var singleAnswer = String.join("OR", allAnswers);

        this.questionLabel.setText(this.displayableQuestion.getQuestion());
        this.answerLabel.setText(singleAnswer);
        this.multipleChoiceAnswerListView.getItems().setAll(allAnswers);
    }

    @FXML
    private void onRevealAnswerButtonClick() {
        this.answerLabel.setVisible(!this.answerLabel.isVisible());
    }
}
