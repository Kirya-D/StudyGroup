package kirya.viewmodel;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kirya.model.Question;
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
    private final ObservableList<String> choicesObservableList;
    private final ObservableList<String> answersObservableList;

    /**
     * Initializes a new QuestionEditorViewmodel.
     */
    public QuestionEditorViewmodel() {
        this.questionObjectProperty = new SimpleObjectProperty<>(null);
        this.questionTypeProperty = new SimpleObjectProperty<>(null);
        this.questionProperty = new SimpleStringProperty();
        this.answerProperty = new SimpleStringProperty();
        this.choicesObservableList = FXCollections.observableArrayList();
        this.answersObservableList = FXCollections.observableArrayList();

        this.setupQuestionOjectListener();
    }

    private void setupQuestionOjectListener() {
        this.questionObjectProperty.addListener((_, _, newQuestionObject) -> {
            this.syncPropertiesToQuestionState();
        });
    }

    /**
     * Syncs the display property contents to match the state of the associated question.
     */
    public void syncPropertiesToQuestionState() {
        var questionObject = this.questionObjectProperty.get();
        
        if (questionObject != null) {
            this.questionTypeProperty.set(questionObject.getQuestionType());
            this.questionProperty.set(questionObject.getQuestion());
            this.answerProperty.set(String.join("", questionObject.getAnswers()));
            this.choicesObservableList.setAll(questionObject.getChoices());
            this.answersObservableList.setAll(questionObject.getAnswers());
        } else {
            this.questionTypeProperty.set(QuestionType.FREE_RESPONSE);
            this.questionProperty.set("");
            this.answerProperty.set("");
            this.choicesObservableList.clear();
            this.answersObservableList.clear();
        }
    }

    /**
     * Updates the members of the associated Question object.
     *
     * <p>
     * postcondition: Associated question object new question is the
     * questionProperty's value if it's not blank otherwise the
     * questionProperty's value gets set to the associated question object's
     * question value.
     * 
     * @throws NullPointerException If the associated question object == null.
     *
     * @throws IllegalArgumentException If the new question is blank.
     */
    public void applyQuestionChanges() {
        var questionObj = this.getQuestion();
        if (questionObj == null) {
            throw new NullPointerException("Associated question object is not set");
        }
        var newQuestionType = this.questionTypeProperty.get();
        var newQuestion = this.questionProperty.get();
        var newFreeResponseAnswer = List.of(this.answerProperty.get());
        var newMultChoiceChoices = new ArrayList<>(this.choicesObservableList);
        var newMultChoiceAnswers = new ArrayList<>(this.answersObservableList);

        questionObj.setQuestionType(newQuestionType);
        questionObj.setQuestion(newQuestion);
        switch (questionObj.getQuestionType()) {
            case FREE_RESPONSE -> {
                questionObj.setChoices(newFreeResponseAnswer);
                questionObj.setAnswers(newFreeResponseAnswer);
            }
            case MULTIPLE_CHOICE -> {
                questionObj.setChoices(newMultChoiceChoices);
                questionObj.setAnswers(newMultChoiceAnswers);
            }
        }
    }

    private Question getQuestion() {
        var object = this.questionObjectProperty.get();
        return object instanceof Question question ? question : null;
    }

    /**
     * {@return the question object property}
     */
    public ObjectProperty<DisplayableQuestion> getQuestionObjectProperty() {
        return this.questionObjectProperty;
    }

    /**
     * {@return the question type property}
     */
    public ObjectProperty<QuestionType> getQuestionTypeProperty() {
        return this.questionTypeProperty;
    }

    /**
     * {@return the question StringProperty}
     */
    public StringProperty getQuestionProperty() {
        return this.questionProperty;
    }

    /**
     * {@return the answer StringProperty}
     */
    public StringProperty getAnswerProperty() {
        return this.answerProperty;
    }

    /**
     * {@return the choices ObservableList}
     */
    public ObservableList<String> getChoicesObservableList() {
        return this.choicesObservableList;
    }

    /**
     * {@return the answers ObservableList}
     */
    public ObservableList<String> getAnswersObservableList() {
        return this.answersObservableList;
    }
}
