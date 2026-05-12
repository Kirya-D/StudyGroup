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
     * @param title The new title
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Sets the description.
     * @param description The new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the questions.
     * @param questions The new questions
     */
    public void setQuestions(Collection<Question> questions) {
        this.questions.clear();
        this.questions.addAll(questions);
    }

    /**
     * Sets if this is favorited.
     * @param b The new favorited state
     */
    public void setIsFavorited(boolean b) {
        this.isFavorited = b;
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
