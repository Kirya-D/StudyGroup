package kirya.utils;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Simple dataclass for a answer choice information
 */
public class AnswerChoice {

    private StringProperty textProperty;
    private BooleanProperty isCorrectProperty;

    /**
     * Initializes a new {@link AnswerChoice} with the given {@code text} and
     * {@code isCorrect}.
     * 
     * @param text      the text of the choice
     * @param isCorrect if the choice is correct
     */
    public AnswerChoice(String text, boolean isCorrect) {
        this.textProperty = new SimpleStringProperty(text);
        this.isCorrectProperty = new SimpleBooleanProperty(isCorrect);
    }

    /**
     * Sets the text of this answer choice.
     * 
     * @param value the new text
     */
    public void setText(String value) {
        this.textProperty.set(value);
    }

    /**
     * {@return the text of this answer choice}
     */
    public String getText() {
        return this.textProperty.get();
    }

    /**
     * {@return text property}
     */
    public StringProperty textProperty() {
        return this.textProperty;
    }

    /**
     * Sets the correctness of this answer choice.
     * 
     * @param value the new correctness
     */
    public void setIsCorrect(boolean value) {
        this.isCorrectProperty.set(value);
    }

    /**
     * {@return if this answer choice is correct}
     */
    public boolean getIsCorrect() {
        return this.isCorrectProperty.get();
    }

    /**
     * {@return correctness boolean property}
     */
    public BooleanProperty isCorrectProperty() {
        return this.isCorrectProperty;
    }
}
