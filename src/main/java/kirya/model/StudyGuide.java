package kirya.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import kirya.utils.DisplayableQuestion;
import kirya.utils.DisplayableStudyGuide;

public class StudyGuide extends DisplayableStudyGuide {

    private boolean isFavorited;
    private String title;
    private String description;
    private final Set<Question> questions;

    public StudyGuide() {
        this.isFavorited = false;
        this.title = "";
        this.description = "";
        this.questions = new LinkedHashSet<>();
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    public void addQuestions(Collection<Question> questions) {
        this.questions.addAll(questions);
    }

    public void setQuestions(Collection<Question> questions) {
        this.questions.clear();
        this.questions.addAll(questions);
    }

    public void setFavorited(boolean b) {
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
