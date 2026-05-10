package kirya.model;

import java.util.ArrayList;
import java.util.SequencedCollection;

import kirya.utils.DisplayableQuestion;
import kirya.utils.QuestionType;

public class Question extends DisplayableQuestion {

    private QuestionType questionType;
    private String question;
    private SequencedCollection<String> choices;
    private SequencedCollection<String> answers;

    public Question() {
        this.questionType = QuestionType.FREE_RESPONSE;
        this.question = "";
        this.choices = new ArrayList<>();
        this.answers = new ArrayList<>();
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setChoices(SequencedCollection<String> answerChoices) {
        this.choices = answerChoices;
    }

    public void setAnswers(SequencedCollection<String> correctAnswers) {
        this.answers = correctAnswers;
    }

    @Override
    public QuestionType getQuestionType() {
        return this.questionType;
    }

    @Override
    public String getQuestion() {
        return this.question;
    }

    @Override
    public SequencedCollection<String> getChoices() {
        return this.choices;
    }

    @Override
    public SequencedCollection<String> getAnswers() {
        return this.answers;
    }
}
