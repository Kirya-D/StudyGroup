package kirya.viewmodel;

import java.util.ArrayList;
import java.util.Collection;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kirya.model.Question;
import kirya.model.StudyGuide;
import kirya.utils.DisplayableQuestion;
import kirya.utils.DisplayableStudyGuide;

/**
 * Viewmodel of the StudyGuideEditor view class.
 */
public class StudyGuideEditorViewmodel {
    private final ObjectProperty<DisplayableStudyGuide> studyGuideProperty;
    private final StringProperty titleProperty;
    private final StringProperty descriptionProperty;
    private final ObservableList<DisplayableQuestion> questionsObservableList;

    /**
     * Initializes a new StudyGuideEditorViewmodel.
     */
    public StudyGuideEditorViewmodel() {
        this.studyGuideProperty = new SimpleObjectProperty<>(null);
        this.titleProperty = new SimpleStringProperty("");
        this.descriptionProperty = new SimpleStringProperty("");
        this.questionsObservableList = FXCollections.observableArrayList();

        this.setupStudyGuideListener();
    }

    private void setupStudyGuideListener() {
        this.studyGuideProperty.addListener((_, _, newValue) -> {
            if (newValue != null) {
                this.titleProperty.set(newValue.getTitle());
                this.descriptionProperty.set(newValue.getDescription());
                this.questionsObservableList.setAll(newValue.getQuestions());
            } else {
                this.titleProperty.set("");
                this.descriptionProperty.set("");
                this.questionsObservableList.clear();
            }
        });
    }

    /**
     * Adds a new question to the questions ObservableList.
     */
    public void addNewQuestion() {
        var question = new Question();
        var questionCount = this.questionsObservableList.size() + 1;
        question.setQuestion("Question " + questionCount);
        this.questionsObservableList.add(question);
    }
    
    /**
     * Applies the changes made to the study guide in the editor to the actual object.
     */
    public void applyChanges() {
        var studyGuide = this.getStudyGuide();
        if (studyGuide == null) {
            return;
        }

        Collection<Question> concreteQuestions = new ArrayList<>();
        for (var displayQ : this.questionsObservableList) {
            if (displayQ instanceof Question realQuestion) {
                concreteQuestions.add(realQuestion);
            }
        }

        studyGuide.setTitle(this.titleProperty.get());
        studyGuide.setDescription(this.descriptionProperty.get());
        studyGuide.setQuestions(concreteQuestions);
    }

    private StudyGuide getStudyGuide() {
        var guide = this.studyGuideProperty.get();
        return guide != null && guide instanceof StudyGuide realGuide ? realGuide : null;
    }

    /**
     * Gets the studyGuideProperty.
     * @return The studyGuideProperty
     */
    public ObjectProperty<DisplayableStudyGuide> getStudyGuideProperty() {
        return this.studyGuideProperty;
    }

    /**
     * Gets the title StringProperty.
     * @return The title StringProperty
     */
    public StringProperty getTitleProperty() {
        return this.titleProperty;
    }

    /**
     * Gets the description StringProperty.
     * @return The description StringProperty
     */
    public StringProperty getDescriptionProperty() {
        return this.descriptionProperty;
    }

    /**
     * Gets the questions ObservableList.
     * @return The observable list containing the questions
     */
    public ObservableList<DisplayableQuestion> getQuestionsObservableList() {
        return this.questionsObservableList;
    }
}
