package kirya.utils;

import java.util.Collection;

/**
 * Abstract class that represents a UI displayable study guide.
 */
public abstract class DisplayableStudyGuide {

    /**
     * {@return the id}
     */
    public abstract Integer getId();

    /**
     * {@return the title}
     */
    public abstract String getTitle();

    /**
     * {@return the description}
     */
    public abstract String getDescription();

    /**
     * {@return the questions}
     */
    public abstract Collection<DisplayableQuestion> getQuestions();

    /**
     * {@return the downloaded status}
     */
    public abstract boolean getIsDownloaded();

    /**
     * {@return The favorited status}
     */
    public abstract boolean getIsFavorited();

    /**
     * {@return The uploaded status}
     */
    public abstract boolean getIsUploaded();

    @Override
    public String toString() {
        return this.getTitle();
    }
}
