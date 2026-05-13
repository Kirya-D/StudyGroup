package kirya.viewmodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kirya.utils.DisplayableQuestion;
import kirya.utils.QuestionType;

/**
 * Viewmodel of the QuestionEditor view class
 */
public class QuestionEditorViewmodel {

    private final ObjectProperty<DisplayableQuestion> questionObjectProperty;
    private final ObjectProperty<QuestionType> questionTypeProperty;
    private final StringProperty questionProperty;
    private final StringProperty answerProperty;
    private final ObservableList<String> answersObservableList;

    /**
     * Initializes a new QuestionEditorViewmodel.
     */
    public QuestionEditorViewmodel() {
        this.questionObjectProperty = new SimpleObjectProperty<>(null);
        this.questionTypeProperty = new SimpleObjectProperty<>(null);
        this.questionProperty = new SimpleStringProperty();
        this.answerProperty = new SimpleStringProperty();
        this.answersObservableList = FXCollections.observableArrayList();

        this.setupQuestionOjectListener();
    }

    private void setupQuestionOjectListener() {
        this.questionObjectProperty.addListener((_, _, newValue) -> {
            if (newValue != null) {
                this.questionTypeProperty.set(newValue.getQuestionType());
                this.questionProperty.set(newValue.getQuestion());
                this.answerProperty.set(String.join("OR", newValue.getAnswers()));
                this.answersObservableList.setAll(newValue.getAnswers());
            }
            else {
                this.questionProperty.set("");
                this.answerProperty.set("");
                this.answersObservableList.clear();
            }
        });
    }

    /**
     * Gets the ObjectProperty for the question.
     * @return The question object property
     */
    public ObjectProperty<DisplayableQuestion> getQuestionObjectProperty() {
        return this.questionObjectProperty;
    }

    /**
     * Gets the ObjectProperty for the question type.
     * @return The question type property
     */
    public ObjectProperty<QuestionType> getQuestionTypeProperty() {
        return this.questionTypeProperty;
    }

    /**
     * Gets the StringProperty for the question.
     * @return The question StringProperty
     */
    public StringProperty getQuestionProperty() {
        return this.questionProperty;
    }

    /**
     * Gets the StringProperty for the answer.
     * @return The answer StringProperty
     */
    public StringProperty getAnswerProperty() {
        return this.answerProperty;
    }

    /**
     * Gets the ObservableList for the answers.
     * @return The answers ObservableList
     */
    public ObservableList<String> getAnswersObservableList() {
        return this.answersObservableList;
    }
}
