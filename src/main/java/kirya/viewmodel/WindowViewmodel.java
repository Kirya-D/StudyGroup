package kirya.viewmodel;

import java.util.ArrayList;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kirya.model.FileIO;
import kirya.model.StudyGuide;
import kirya.utils.DisplayableStudyGuide;

public class WindowViewmodel {

    private final ObservableList<StudyGuide> downloadedStudyGuidesProperty;
    private final ObservableList<StudyGuide> favoritedStudyGuidesProperty;
    private final BooleanProperty currentlyEditingStudyGuide;
    private StudyGuide freshStudyGuide;

    public WindowViewmodel() {
        this.downloadedStudyGuidesProperty = FXCollections.observableArrayList();
        this.favoritedStudyGuidesProperty = FXCollections.observableArrayList();
        this.currentlyEditingStudyGuide = new SimpleBooleanProperty(false);
        this.freshStudyGuide = null;
        this.addListeners();
    }

    private void addListeners() {
        this.currentlyEditingStudyGuide.addListener((_, oldVal, newVal) -> {
            var justStoppedEditing = oldVal && !newVal;
            if (this.freshStudyGuide != null && justStoppedEditing) {
                this.downloadedStudyGuidesProperty.add(this.freshStudyGuide);
                this.freshStudyGuide = null;
            }
        });
    }

    public StudyGuideEditorViewmodel createNewStudyGuide() {
        this.freshStudyGuide = new StudyGuide();
        return new StudyGuideEditorViewmodel(this.freshStudyGuide);
    }

    public StudyGuideEditorViewmodel editStudyGuide(DisplayableStudyGuide studyGuide) {
        var existingStudyGuide = studyGuide instanceof StudyGuide ? (StudyGuide) studyGuide : null;
        return new StudyGuideEditorViewmodel(existingStudyGuide);
    }

    public void save() {
        var downloadedStudyGuides = new ArrayList<>(this.downloadedStudyGuidesProperty);
        FileIO.Write(downloadedStudyGuides);
    }

    public void load() {
        var downloadedStudyGuides = FileIO.Read();
        this.downloadedStudyGuidesProperty.setAll(downloadedStudyGuides);
    }

    public ObservableList<? extends DisplayableStudyGuide> getDownloadedStudyGuidesProperty() {
        return this.downloadedStudyGuidesProperty;
    }

    public ObservableList<? extends DisplayableStudyGuide> getFavoritedStudyGuidesProperty() {
        return this.favoritedStudyGuidesProperty;
    }

    public BooleanProperty getCurrentlyEditingStudyGuide() {
        return this.currentlyEditingStudyGuide;
    }
}
