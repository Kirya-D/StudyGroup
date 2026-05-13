package kirya.viewmodel;

import java.util.ArrayList;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import kirya.model.FileIO;
import kirya.model.StudyGuide;
import kirya.utils.DisplayableStudyGuide;

/**
 * Viewmodel of the RootDiplay view class
 */
public class RootDisplayViewmodel {

    private final ListProperty<DisplayableStudyGuide> favoritedStudyGuidesProperty;
    private final ListProperty<DisplayableStudyGuide> downloadedStudyGuidesProperty;

    /**
     * Initializes a new RootDisplayViewmodel.
     */
    public RootDisplayViewmodel() {
        this.favoritedStudyGuidesProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.downloadedStudyGuidesProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    /**
     * Saves the downloaded study guides to file.
     */
    public void save() {
        var toWrite = new ArrayList<StudyGuide>();
        for (var displayableGuide : this.downloadedStudyGuidesProperty.get()) {
            if (displayableGuide instanceof StudyGuide concreteGuide) {
                toWrite.add(concreteGuide);
            }
        }
        FileIO.Write(toWrite);
    }

    /**
     * Loads the downloaded study guides from file.
     */
    public void load() {
        var loadedStudyGuides = FileIO.Read();
        this.downloadedStudyGuidesProperty.addAll(loadedStudyGuides);
    }

    /**
     * Creates and returns a new study guide and sets it as the current editing study guide.
     * @return The new study guide
     */
    public DisplayableStudyGuide createNewStudyGuide() {
        return new StudyGuide();
    }

    /**
     * Add the given study guide to the downloaded collection if its not already in it.
     * @param studyGuide The study guide to download
     */
    public void downloadStudyGuide(DisplayableStudyGuide studyGuide) {
        if (studyGuide instanceof StudyGuide concreteGuide) {
            if (!this.downloadedStudyGuidesProperty.contains(concreteGuide)) {
                this.downloadedStudyGuidesProperty.add(concreteGuide);
            }
        }
    }

    /**
     * Removes the given study guide from the downloaded study guides list property
     * @param studyGuide The study guide to delete
     */
    public void deleteStudyGuide(DisplayableStudyGuide studyGuide) {
        if (this.downloadedStudyGuidesProperty.contains(studyGuide)) {
            this.downloadedStudyGuidesProperty.remove(studyGuide);
        }
    }

    /**
     * Get the list property of favorited studyguides.
     * @return The favorited studyguides property
     */
    public ListProperty<DisplayableStudyGuide> getFavoritedStudyGuidesProperty() {
        return this.favoritedStudyGuidesProperty;
    }

    /**
     * Get the list property of downloaded studyguides.
     * @return The downloaded studyguides property
     */
    public ListProperty<DisplayableStudyGuide> getDownloadedStudyGuidesProperty() {
        return this.downloadedStudyGuidesProperty;
    }
}
