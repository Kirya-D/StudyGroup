package kirya.viewmodel;

import java.util.ArrayList;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import kirya.model.Question;
import kirya.utils.DisplayableQuestion;
import kirya.utils.QuestionType;

public class QuestionEditorViewmodel {

    private final Question questionObj;
    private final ObjectProperty<QuestionType> questionTypeProperty;
    private final StringProperty questionProperty;
    private final ListProperty<String> choicesProperty;
    private final ListProperty<String> answersProperty;
    private final StringProperty answerProperty;
    
    public QuestionEditorViewmodel(Question existingQuestion) {
        this.questionObj = existingQuestion;
        this.questionTypeProperty = new SimpleObjectProperty<>(this.questionObj.getQuestionType());
        this.questionProperty = new SimpleStringProperty(this.questionObj.getQuestion());
        this.choicesProperty = new SimpleListProperty<>(
                FXCollections.observableArrayList(this.questionObj.getChoices()));
        var existingAnswers = this.questionObj.getAnswers();
        var firstAnswer = existingAnswers.isEmpty() ? "" : existingAnswers.getFirst();
        this.answersProperty = new SimpleListProperty<>(FXCollections.observableArrayList(existingAnswers));
        this.answerProperty = new SimpleStringProperty(firstAnswer);
        this.addPropertyListeners();
    }
    
    public QuestionEditorViewmodel(String question) {
        var newQuestion = new Question();
        newQuestion.setQuestion(question);
        this(newQuestion);
    }
    
    private void addPropertyListeners() {
        this.questionTypeProperty.addListener((_, _, newVal) -> {
            this.questionObj.setQuestionType(newVal);
            switch (newVal) {
                case FREE_RESPONSE -> {
                        var choices = new ArrayList<String>();
                        var answers = new ArrayList<String>();
                        answers.add(QuestionEditorViewmodel.this.answerProperty.get());
                        this.questionObj.setChoices(choices);
                        this.questionObj.setAnswers(answers);
                    }
                case MULTIPLE_CHOICE -> {
                        javafx.collections.ObservableList<java.lang.String> choices = QuestionEditorViewmodel.this.choicesProperty.get();
                        javafx.collections.ObservableList<java.lang.String> answers = QuestionEditorViewmodel.this.answersProperty.get();
                        this.questionObj.setChoices(choices);
                        this.questionObj.setAnswers(answers);
                    }
                default -> {
                    throw new EnumConstantNotPresentException(newVal.getClass(), newVal.name());
                }
            }
        });
        this.questionProperty.addListener((_, _, newVal) -> {
            this.questionObj.setQuestion(newVal);
        });
        this.choicesProperty.addListener((_, _, newVal) -> {
            this.questionObj.setChoices(newVal);
        });
        this.answersProperty.addListener((_, _, newVal) -> {
            this.questionObj.setAnswers(newVal);
        });
        this.answerProperty.addListener((_, _, newVal) -> {
            var answerAsCollection = new ArrayList<String>();
            answerAsCollection.add(newVal);
            this.questionObj.setAnswers(answerAsCollection);
        });
    }

    public ObjectProperty<QuestionType> getQuestionTypeProperty() {
        return this.questionTypeProperty;
    }

    public StringProperty getQuestionProperty() {
        return this.questionProperty;
    }

    public ListProperty<String> getChoicesProperty() {
        return this.choicesProperty;
    }

    public ListProperty<String> getAnswersProperty() {
        return this.answersProperty;
    }

    public StringProperty getAnswerProperty() {
        return this.answerProperty;
    }

    public DisplayableQuestion getQuestionObject() {
        return this.questionObj;
    }
}
