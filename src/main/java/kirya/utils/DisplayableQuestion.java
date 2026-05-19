package kirya.utils;

import java.util.SequencedCollection;

/**
 * Abstract class that represents a UI displayable question.
 */
public abstract class DisplayableQuestion {
    /**
     * {@return the question's  {@link QuestionType}}
     */
    public abstract QuestionType getQuestionType();
    
    /**
     * {@return the question as a string}
     */
    public abstract String getQuestion();
    /**
     * {@return The collection of answer choices}
     */
    public abstract SequencedCollection<String> getChoices();
    /**
     * {@return The collection of answer(s)}
     */
    public abstract SequencedCollection<String> getAnswers();

    @Override
    public String toString() {
        return this.getQuestion();
    }
}
