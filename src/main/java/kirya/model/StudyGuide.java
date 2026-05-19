package kirya.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import kirya.utils.DisplayableQuestion;
import kirya.utils.DisplayableStudyGuide;

/**
 * A dataclass that stores the necessary information for a study guide.
 */
public class StudyGuide extends DisplayableStudyGuide {

    private boolean isFavorited;
    private String title;
    private String description;
    private final Set<Question> questions;

    /**
     * Initializes a StudyGuide with default state.
     * 
     * <p>Postcondition: this.getIsFavorited() == false
     * <p>Postcondition: this.getTitle() == ""
     * <p>Postcondition: this.getDescription() == ""
     * <p>Postcondition: this.getQuestions() == {}
     */
    public StudyGuide() {
        this.isFavorited = false;
        this.title = "";
        this.description = "";
        this.questions = new LinkedHashSet<>();
    }

    /**
     * Sets the title.
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

    /**
     * Sets if this studyguide is favorited.
     * @param favorited The new non-null favorited state
     */
    public void setIsFavorited(boolean favorited) {
        this.isFavorited = favorited;
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
    public boolean getIsFavorited() {
        return this.isFavorited;
    }
}
