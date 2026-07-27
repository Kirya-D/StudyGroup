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

    private String id;
    private boolean downloaded;
    private boolean favorited;
    private boolean uploaded;
    private String creatorUsername;
    private String title;
    private String description;
    private final Set<Question> questions;

    /**
     * Initializes a StudyGuide with default state.
     * <p>
     * Postcondition: {@link StudyGuide#getDownloaded()} == false
     * <p>
     * Postcondition: {@link StudyGuide#getFavorited()} == false
     * <p>
     * Postcondition: {@link StudyGuide#getUploaded()} == false
     * <p>
     * Postcondition: {@link StudyGuide#getCreatorUsername()} == ""
     * <p>
     * Postcondition: {@link StudyGuide#getTitle()} == ""
     * <p>
     * Postcondition: {@link StudyGuide#getDescription()} == ""
     * <p>
     * Postcondition: {@link StudyGuide#getQuestions()} ==
     * {@link Collections#emptyList()}
     */
    public StudyGuide() {
        this.id = null;
        this.downloaded = false;
        this.favorited = false;
        this.uploaded = false;
        this.creatorUsername = "";
        this.title = "";
        this.description = "";
        this.questions = new LinkedHashSet<>();
    }

    /**
     * Initializes a {@link #StudyGuide()} with {@code id}.
     * 
     * @param id the id of the studyguide
     */
    public StudyGuide(String id) {
        this();
        this.id = id;
    }

    /**
     * Sets the id of this studyguide
     * 
     * @param id the new id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the downloaded state of this study guide.
     * 
     * @param downloaded bool for downloaded state
     */
    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    /**
     * Sets if this studyguide is favorited.
     * 
     * @param favorited The new favorited state
     */
    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    /**
     * Sets if this studyguide is uploaded.
     * 
     * @param uploaded The new uploaded state
     */
    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    /**
     * Sets the username of the creator of this study guide.
     * 
     * @param username The new non-null username
     */
    public void setCreatorUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("username can't be null");
        }
        this.creatorUsername = username;
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
    public String getId() {
        return this.id;
    }

    @Override
    public String getCreatorUsername() {
        return this.creatorUsername;
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
    public boolean getDownloaded() {
        return this.downloaded;
    }

    @Override
    public boolean getFavorited() {
        return this.favorited;
    }

    @Override
    public boolean getUploaded() {
        return this.uploaded;
    }
}
