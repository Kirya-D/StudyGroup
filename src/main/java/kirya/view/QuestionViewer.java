package kirya.view;

import java.io.IOException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import kirya.utils.DisplayableQuestion;

public class QuestionViewer extends GridPane {

    @FXML
    private Label questionLabel;
    @FXML
    private GridPane freeResponseAnswerGridPane;
    @FXML
    private TextArea userAnswerTextArea;
    @FXML
    private Label answerLabel;
    @FXML
    private ListView<String> multipleChoiceAnswerListView;

    public ObjectProperty<DisplayableQuestion> questionProperty = new SimpleObjectProperty<>(null);
    
    public QuestionViewer() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("questionviewer.fxml"));
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
        this.bindProperties();
        this.addListeners();
    }

    private void setupCellFactory() {
        this.multipleChoiceAnswerListView.setCellFactory(listview -> new ListCell<String>() {
            @Override
            protected void updateItem(String answer, boolean empty) {
                super.updateItem(answer, empty);
                if (empty || answer == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setPadding(Insets.EMPTY);
                    setText(answer);
                }
            }
        });
    }

    private void bindProperties() {
        this.freeResponseAnswerGridPane.managedProperty().bind(this.freeResponseAnswerGridPane.visibleProperty());
        this.answerLabel.managedProperty().bind(this.answerLabel.visibleProperty());
        this.multipleChoiceAnswerListView.managedProperty().bind(this.multipleChoiceAnswerListView.visibleProperty());
    }

    private void addListeners() {
        this.questionProperty.addListener((_, _, newVal) -> {
            this.questionLabel.setText(newVal.getQuestion());
            var fullAnswerText = String.join(System.lineSeparator(), newVal.getAnswers());
            this.answerLabel.setText(fullAnswerText);
        });
    }
    
    @FXML
    private void onRevealAnswerButtonClick() {
        this.answerLabel.setVisible(!this.answerLabel.isVisible());
    }

}
