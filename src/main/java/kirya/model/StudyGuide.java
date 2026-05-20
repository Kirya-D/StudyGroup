package kirya.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import kirya.utils.DisplayableQuestion;
import kirya.utils.DisplayableStudyGuide;

/**
 * A dataclass that stores the necessary information for a study guide.
 */
public class StudyGuide extends DisplayableStudyGuide {

    private boolean isDownloaded;
    private boolean isFavorited;
    private boolean isUploaded;
    private String title;
    private String description;
    private final Set<Question> questions;

    /**
     * Initializes a StudyGuide with default state.
     * <p>
     * Postcondition: {@link StudyGuide#getIsDownloaded()} == false
     * <p>
     * Postcondition: {@link StudyGuide#getIsFavorited()} == false
     * <p>
     * Postcondition: {@link StudyGuide#getIsUploaded()} == false
     * <p>
     * Postcondition: {@link StudyGuide#getTitle()} == ""
     * <p>
     * Postcondition: {@link StudyGuide#getDescription()} == ""
     * <p>
     * Postcondition: {@link StudyGuide#getQuestions()} ==
     * {@link Collections#emptyList()}
     */
    public StudyGuide() {
        this.isDownloaded = false;
        this.isFavorited = false;
        this.isUploaded = false;
        this.title = "";
        this.description = "";
        this.questions = new LinkedHashSet<>();
    }

    /**
     * Sets the downloaded state of this study guide.
     * 
     * @param downloaded bool for downloaded state
     */
    public void setIsDownloaded(boolean downloaded) {
        this.isDownloaded = downloaded;
    }

    /**
     * Sets if this studyguide is favorited.
     * 
     * @param favorited The new favorited state
     */
    public void setIsFavorited(boolean favorited) {
        this.isFavorited = favorited;
    }

    /**
     * Sets if this studyguide is uploaded.
     * 
     * @param uploaded The new uploaded state
     */
    public void setIsUploaded(boolean uploaded) {
        this.isUploaded = uploaded;
    }

    /**
     * Sets the title.
     * 
     * @param title The new non-null && not-blank title
     * @throws IllegalArgumentException If title is null or blank
     */
    public void setTitle(String title) {
        if (title == null) {
            throw new IllegalArgumentException("title can't be null");
        }
        if (title.isBlank()) {
            throw new IllegalArgumentException("title can't be blank");
        }
        this.title = title;
    }

    /**
     * Sets the description.
     * 
     * @param description The new non-null description
     * @throws IllegalArgumentException If description is null
     */
    public void setDescription(String description) {
        if (description == null) {
            throw new IllegalArgumentException("description can't be null");
        }
        this.description = description;
    }

    /**
     * Sets the questions.
     * 
     * @param questions The new non-null questions
     * @throws IllegalArgumentException If questions is null
     * @throws IllegalArgumentException If questions {@link Collection#isEmpty()}
     */
    public void setQuestions(Collection<Question> questions) {
        if (questions == null) {
            throw new IllegalArgumentException("questions can't be null");
        }
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("There must be at least one question");
        }
        this.questions.clear();
        this.questions.addAll(questions);
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public Collection<DisplayableQuestion> getQuestions() {
        return new ArrayList<>(this.questions);
    }

    @Override
    public boolean getIsDownloaded() {
        return this.isDownloaded;
    }

    @Override
    public boolean getIsFavorited() {
        return this.isFavorited;
    }

    @Override
    public boolean getIsUploaded() {
        return this.isUploaded;
    }
}
