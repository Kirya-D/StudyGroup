package kirya.view;

import java.io.IOException;
import java.util.ArrayList;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import kirya.utils.AnswerChoice;
import kirya.utils.DisplayableQuestion;

/**
 * Code-behind for questionviewer.fxml
 */
public class QuestionViewer extends GridPane {
    @FXML
    private Label questionLabel;
    @FXML
    private VBox freeResponseQuestionVBox;
    @FXML
    private TextArea freeResponseAnswerTextArea;
    @FXML
    private Label freeResponseAnswerLabel;
    @FXML
    private VBox multipleChoiceQuestionVBox;
    @FXML
    private TableView<AnswerChoice> userMCTableView;
    @FXML
    private TableColumn<AnswerChoice, Boolean> userCorrectnessTableColumn;
    @FXML
    private TableColumn<AnswerChoice, String> userChoiceTableColumn;
    @FXML
    private TableColumn<AnswerChoice, Boolean> answerCorrectnessTableColumn;
    @FXML
    private TableColumn<AnswerChoice, String> answerChoiceTableColumn;
    @FXML
    private TableView<AnswerChoice> answerMCTableView;
    @FXML
    private Button toggleAnswerButton;

    private BooleanProperty answerIsHiddenProperty = new SimpleBooleanProperty(true);
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
            this.setupTableViews();
            this.bindToSelf();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupTableViews() {
        this.userChoiceTableColumn.setCellValueFactory(data -> data.getValue().textProperty());

        this.answerCorrectnessTableColumn.setCellValueFactory(data -> data.getValue().isCorrectProperty());
        this.answerChoiceTableColumn.setCellValueFactory(data -> data.getValue().textProperty());

        this.userCorrectnessTableColumn
                .setCellFactory(CheckBoxTableCell.forTableColumn(this.userCorrectnessTableColumn));
        this.answerCorrectnessTableColumn
                .setCellFactory(CheckBoxTableCell.forTableColumn(this.answerCorrectnessTableColumn));
    }

    private void bindToSelf() {
        Node[] collapsable = { this.freeResponseQuestionVBox, this.freeResponseAnswerTextArea,
                this.freeResponseAnswerLabel, this.multipleChoiceQuestionVBox, this.userMCTableView,
                this.answerMCTableView };
        for (var node : collapsable) {
            node.managedProperty().bind(node.visibleProperty());
        }

        this.freeResponseAnswerTextArea.visibleProperty().bind(this.answerIsHiddenProperty);
        this.userMCTableView.visibleProperty().bind(this.answerIsHiddenProperty);
        this.freeResponseAnswerLabel.visibleProperty().bind(this.answerIsHiddenProperty.not());
        this.answerMCTableView.visibleProperty().bind(this.answerIsHiddenProperty.not());

        this.toggleAnswerButton.textProperty().bindBidirectional(this.answerIsHiddenProperty,
                new StringConverter<Boolean>() {

                    @Override
                    public String toString(Boolean hidden) {
                        return hidden ? "Show Answer" : "Hide Answer";
                    }

                    @Override
                    public Boolean fromString(String string) {
                        return string.equals("Show Answer") ? true : false;
                    }

                });
    }

    /**
     * Sets the question to view.
     * 
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

        this.updateFreeResponseDisplay();
        this.updateTableViews();

        switch (this.displayableQuestion.getQuestionType()) {
            case FREE_RESPONSE -> {
                this.freeResponseQuestionVBox.setVisible(true);
                this.multipleChoiceQuestionVBox.setVisible(false);
            }
            case MULTIPLE_CHOICE -> {
                this.freeResponseQuestionVBox.setVisible(false);
                this.multipleChoiceQuestionVBox.setVisible(true);
            }
        }
    }

    private void updateFreeResponseDisplay() {
        var answers = this.displayableQuestion.getAnswers();
        var singleAnswer = answers.getFirst();

        this.questionLabel.setText(this.displayableQuestion.getQuestion());
        this.freeResponseAnswerLabel.setText(singleAnswer);
    }

    private void updateTableViews() {
        var choices = this.displayableQuestion.getChoices();
        var answers = this.displayableQuestion.getAnswers();
        var answerChoices = new ArrayList<AnswerChoice>();

        for (var choice : choices) {
            var isAnswer = answers.contains(choice);
            var newAnswerChoice = new AnswerChoice(choice, isAnswer);

            answerChoices.add(newAnswerChoice);
        }

        this.userMCTableView.getItems().setAll(answerChoices);
        this.answerMCTableView.getItems().setAll(answerChoices);
    }

    @FXML
    private void onRevealAnswerButtonClick() {
        var isCurrentlyHidden = this.answerIsHiddenProperty.get();
        this.answerIsHiddenProperty.set(!isCurrentlyHidden);
    }
}
