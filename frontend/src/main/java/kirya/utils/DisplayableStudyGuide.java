package kirya.utils;

import java.util.Collection;

/**
 * Abstract class that represents a UI displayable study guide.
 */
public abstract class DisplayableStudyGuide {

    /**
     * {@return the id}
     */
    public abstract String getId();

    /**
     * {@return the username of the account that created this studyguide}
     */
    public abstract String getCreatorUsername();

    /**
     * {@return the title}
     */
    public abstract String getTitle();

    /**
     * {@return the description}
     */
    public abstract String getDescription();

    /**
     * {@return the downloaded status}
     */
    public abstract boolean getDownloaded();

    /**
     * {@return The favorited status}
     */
    public abstract boolean getFavorited();

    /**
     * {@return The uploaded status}
     */
    public abstract boolean getUploaded();

    /**
     * {@return the questions}
     */
    public abstract Collection<DisplayableQuestion> getQuestions();

    /**
     * {@return the number of questions}
     */
    public abstract int getQuestionCount();

    @Override
    public String toString() {
        return this.getTitle() + " by " + this.getCreatorUsername();
    }
}
