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
    private final BooleanProperty editingStudyGuide;
    private StudyGuide newStudyGuide;

    public WindowViewmodel() {
        this.downloadedStudyGuidesProperty = FXCollections.observableArrayList();
        this.favoritedStudyGuidesProperty = FXCollections.observableArrayList();
        this.editingStudyGuide = new SimpleBooleanProperty(false);
        this.newStudyGuide = null;
        this.addListeners();
    }

    private void addListeners() {
        this.editingStudyGuide.addListener((_, oldVal, newVal) -> {
            var justStoppedEditing = oldVal && !newVal;
            if (this.newStudyGuide != null && justStoppedEditing) {
                this.downloadedStudyGuidesProperty.add(this.newStudyGuide);
                this.newStudyGuide = null;
            }
        });
    }

    public StudyGuideEditorViewmodel createNewStudyGuide() {
        this.newStudyGuide = new StudyGuide();
        return new StudyGuideEditorViewmodel(this.newStudyGuide);
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

    public BooleanProperty getEditingStudyGuide() {
        return this.editingStudyGuide;
    }
}
