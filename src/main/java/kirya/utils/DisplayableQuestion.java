package kirya.utils;

import java.util.SequencedCollection;

public abstract class DisplayableQuestion {
    /**
     * Gets this questions' question type.
     * @return The question type as a QuestionType
     */
    public abstract QuestionType getQuestionType();
    /**
     * Gets this questions' question.
     * @return The question as a string
     */
    public abstract String getQuestion();
    /**
     * Gets this questions' answer choices.
     * @return The answer choices as a list of strings
     */
    public abstract SequencedCollection<String> getChoices();
    /**
     * Gets this questions' answer(s)
     * @return The answer(s) as a list of strings
     */
    public abstract SequencedCollection<String> getAnswers();

    @Override
    public String toString() {
        return this.getQuestion();
    }
}
