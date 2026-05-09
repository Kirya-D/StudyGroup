package kirya.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kirya.model.StudyGuide;
import kirya.utils.DisplayableStudyGuide;

public class WindowViewmodel {

    private final ObservableList<StudyGuide> savedStudyGuidesProperty;
    private final ObservableList<StudyGuide> favoritedStudyGuidesProperty;
    private final BooleanProperty editingStudyGuide;
    private StudyGuide newStudyGuide;

    public WindowViewmodel() {
        this.savedStudyGuidesProperty = FXCollections.observableArrayList();
        this.favoritedStudyGuidesProperty = FXCollections.observableArrayList();
        this.editingStudyGuide = new SimpleBooleanProperty(false);
        this.newStudyGuide = null;
        this.addListeners();
    }

    private void addListeners() {
        this.editingStudyGuide.addListener((_, oldVal, newVal) -> {
            var justStoppedEditing = oldVal && !newVal;
            if (this.newStudyGuide != null && justStoppedEditing) {
                this.savedStudyGuidesProperty.add(this.newStudyGuide);
                this.newStudyGuide = null;
            }
        });
    }

    public StudyGuideEditorViewmodel createNewStudyGuide() {
        this.newStudyGuide = new StudyGuide();
        return new StudyGuideEditorViewmodel(this.newStudyGuide);
    }

    public ObservableList<? extends DisplayableStudyGuide> getSavedStudyGuidesProperty() {
        return this.savedStudyGuidesProperty;
    }

    public ObservableList<? extends DisplayableStudyGuide> getFavoritedStudyGuidesProperty() {
        return this.favoritedStudyGuidesProperty;
    }

    public BooleanProperty getEditingStudyGuide() {
        return this.editingStudyGuide;
    }
}
