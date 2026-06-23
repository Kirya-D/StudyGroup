package kirya.model;

import java.util.ArrayList;
import java.util.Collections;
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
     * Initializes a new {@link Question} object with default state.
     *
     * <p>
     * Postcondition: this.getQuestionType() == {@link QuestionType#FREE_RESPONSE}
     * <p>
     * Postcondition: this.getQuestion() == ""
     * <p>
     * Postcondition: this.getChoices() == {@link Collections#emptyList()}
     * <p>
     * Postcondition: this.getAnswers() == {@link Collections#emptyList()}
     */
    public Question() {
        this.questionType = QuestionType.FREE_RESPONSE;
        this.question = "";
        this.choices = new ArrayList<>();
        this.answers = new ArrayList<>();
    }

    /**
     * Initializes a new {@link #Question()} where
     * {@link Question#getQuestion()}.equals({@code question})
     * 
     * @param question the non-null && non-blank initial question text
     * @throws IllegalArgumentException If {@code question} == null or
     *                                  {@link String#isBlank()}
     */
    public Question(String question) {
        this();
        this.setQuestion(question);
    }

    /**
     * Sets the type of question this Question is.
     *
     * @param questionType The new type of question
     * @throws IllegalArgumentException If {@code questionType} == {@code null}
     */
    public void setQuestionType(QuestionType questionType) {
        if (questionType == null) {
            throw new IllegalArgumentException("questionType can't be null");
        }
        this.questionType = questionType;
    }

    /**
     * Sets the question
     *
     * @param question The new question being asked
     * @throws IllegalArgumentException If {@code question} == {@code null} or
     *                                  {@link String#isBlank()}
     */
    public void setQuestion(String question) {
        if (question == null) {
            throw new IllegalArgumentException("question can't be null");
        }
        if (question.isBlank()) {
            throw new IllegalArgumentException("question can't be blank");
        }
        this.question = question;
    }

    /**
     * Sets the answer choices.
     *
     * @param answerChoices The new answer choices
     * @throws IllegalArgumentException If {@code answerChoices == null}
     * @throws IllegalArgumentException If {@code answerChoices.isEmpty()}
     * @throws IllegalArgumentException if any answer choice
     *                                  {@link String#isBlank()}
     */
    public void setChoices(SequencedCollection<String> answerChoices) {
        if (answerChoices == null) {
            throw new IllegalArgumentException("answerChoices can't be null");
        }
        if (answerChoices.isEmpty()) {
            throw new IllegalArgumentException("There must be at least one answer choice");
        }
        for (var choice : answerChoices) {
            if (choice.isBlank()) {
                throw new IllegalArgumentException("Answer choice can't be blank");
            }
        }
        this.choices = answerChoices;
    }

    /**
     * Sets the correct answers.
     *
     * @param correctAnswers The new non-null correct answers size > 0
     * @throws IllegalArgumentException If {@code correctAnswers == null}
     * @throws IllegalArgumentException If {@code correctAnswers.isEmpty()}
     * @throws IllegalArgumentException if any answer {@link String#isBlank()}
     */
    public void setAnswers(SequencedCollection<String> correctAnswers) {
        if (correctAnswers == null) {
            throw new IllegalArgumentException("correctAnswers can't be null");
        }
        if (correctAnswers.isEmpty()) {
            throw new IllegalArgumentException("There must be at least one correct answer");
        }
        for (var answer : correctAnswers) {
            if (answer.isBlank()) {
                throw new IllegalArgumentException("Answer can't be blank");
            }
        }
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Question other) {
            var sortedChoices = new ArrayList<>(choices);
            var otherSortedChoices = new ArrayList<>(other.choices);
            var sortedAnswers = new ArrayList<>(answers);
            var otherSortedAnswers = new ArrayList<>(other.answers);
            Collections.sort(sortedChoices);
            Collections.sort(otherSortedChoices);
            Collections.sort(sortedAnswers);
            Collections.sort(otherSortedAnswers);

            var typesMatch = questionType == other.questionType;
            var questionMatch = question.equals(other.question);
            var choiceMatch = sortedChoices.equals(otherSortedChoices);
            var answerMatch = sortedAnswers.equals(otherSortedAnswers);

            return typesMatch
                    && questionMatch
                    && choiceMatch
                    && answerMatch;
        }

        return false;
    }
}
