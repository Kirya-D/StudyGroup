package kirya.model;

import java.util.ArrayList;
import java.util.SequencedCollection;

import kirya.utils.DisplayableQuestion;
import kirya.utils.QuestionType;

/**
 * A dataclass that stores the necessary information for a question.
 */
public class Question extends DisplayableQuestion {

    private QuestionType questionType;
    private String question;
    private SequencedCollection<String> choices;
    private SequencedCollection<String> answers;

    /**
     * Initializes a new Question object with default state.
     * 
     * <p>Postcondition: this.getQuestionType() == QuestionType.FREE_RESPONSE
     * <p>Postcondition: this.getQuestion() == ""
     * <p>Postcondition: this.getChoices() == {}
     * <p>Postcondition: this.getAnswers() == {}
     */
    public Question() {
        this.questionType = QuestionType.FREE_RESPONSE;
        this.question = "";
        this.choices = new ArrayList<>();
        this.answers = new ArrayList<>();
    }

    /**
     * Sets the type of question this Question is.
     * @param questionType The new type of question
     */
    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    /**
     * Sets the question
     * @param question The new question being asked
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * Sets the answer choices.
     * @param answerChoices The new answer choices
     */
    public void setChoices(SequencedCollection<String> answerChoices) {
        this.choices = answerChoices;
    }

    /**
     * Sets the correct answers.
     * @param correctAnswers The new correct answers
     */
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
