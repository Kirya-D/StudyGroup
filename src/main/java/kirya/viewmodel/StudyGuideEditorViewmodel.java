package kirya.viewmodel;

import java.util.Collection;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import kirya.model.Question;
import kirya.model.StudyGuide;
import kirya.utils.DisplayableQuestion;

public class StudyGuideEditorViewmodel {

    private final StudyGuide studyGuide;
    private final StringProperty titleProperty;
    private final StringProperty descriptionProperty;
    private final ListProperty<DisplayableQuestion> questionsProperty;

    public StudyGuideEditorViewmodel(StudyGuide studyGuide) {
        this.studyGuide = studyGuide;
        this.titleProperty = new SimpleStringProperty(this.studyGuide.getTitle());
        this.descriptionProperty = new SimpleStringProperty(this.studyGuide.getDescription());
        this.questionsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.questionsProperty.addAll(this.studyGuide.getQuestions());
    }

    public Collection<QuestionEditorViewmodel> getExistingQuestionEditorViewmodels() {
        return this.studyGuide.getQuestions().stream().map(displayQ -> new QuestionEditorViewmodel((Question) displayQ))
                .toList();
    }

    public void confirmEditChanges() {
        var newTitle = this.titleProperty.get();
        var newDescription = this.descriptionProperty.get();
        var newQuestions = this.questionsProperty.stream().filter(displayQ -> displayQ instanceof Question)
                .map(displayQ -> ((Question) displayQ)).toList();
        
        this.studyGuide.setTitle(newTitle);
        this.studyGuide.setDescription(newDescription);
        this.studyGuide.addQuestions(newQuestions);
    }

    public StringProperty getTitleProperty() {
        return this.titleProperty;
    }

    public StringProperty getDescriptionProperty() {
        return this.descriptionProperty;
    }

    public ListProperty<DisplayableQuestion> getQuestionsProperty() {
        return this.questionsProperty;
    }

}
