package kirya.utils;

import java.util.Collection;

/**
 * Abstract class that represents a UI displayable study guide.
 */
public abstract class DisplayableStudyGuide {
    /**
     * Gets the title.
     * @return The title
     */
    public abstract String getTitle();
    /**
     * Gets the description.
     * @return The description
     */
    public abstract String getDescription();
    /**
     * Gets the questions
     * @return The questions
     */
    public abstract Collection<DisplayableQuestion> getQuestions();
    /**
     * Gets if the study guide is favorited.
     * @return The favorited status
     */
    public abstract boolean getIsFavorited();

    @Override
    public String toString() {
        return this.getTitle();
    }
}
