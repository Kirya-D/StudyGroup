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
        for (var studyGuide : loadedStudyGuides) {
            if (studyGuide.getIsFavorited()) {
                this.favoritedStudyGuidesProperty.add(studyGuide);
            }
            if (studyGuide.getIsDownloaded()) {
                this.downloadedStudyGuidesProperty.add(studyGuide);
            }
        }
    }

    /**
     * Creates and returns a new study guide and sets it as the current editing
     * study guide.
     *
     * @return The new study guide
     */
    public DisplayableStudyGuide createNewStudyGuide() {
        return new StudyGuide();
    }

    /**
     * Add the given study guide to the downloaded collection if its not already
     * in it.
     *
     * @param studyGuide The non-null study guide to download
     * @throws IllegalArgumentException If studyGuide == null
     */
    public void downloadStudyGuide(DisplayableStudyGuide studyGuide) {
        if (studyGuide == null) {
            throw new IllegalArgumentException("studyGuide can't be null");
        }
        var concreteGuide = this.getConcreteGuide(studyGuide);
        if (concreteGuide == null) {
            return;
        }

        if (!this.downloadedStudyGuidesProperty.contains(concreteGuide)) {
            this.downloadedStudyGuidesProperty.add(concreteGuide);
        }
        concreteGuide.setIsDownloaded(true);
    }

    /**
     * Toggles the favorited state of {@code studyGuide} if toggled to true it's
     * added to the favorited study guides collection, otherwise removed.
     * 
     * @param studyGuide The study guide to toggle favorite for
     * @throws IllegalArgumentException If {@code studyGuide} == null
     */
    public void toggleFavoriteStudyGuide(DisplayableStudyGuide studyGuide) {
        if (studyGuide == null) {
            throw new IllegalArgumentException("studyGuide can't be null");
        }
        var concreteGuide = this.getConcreteGuide(studyGuide);
        if (concreteGuide == null) {
            return;
        }

        concreteGuide.setIsFavorited(!concreteGuide.getIsFavorited());
        if (concreteGuide.getIsFavorited()) {
            if (!this.favoritedStudyGuidesProperty.contains(concreteGuide)) {
                this.favoritedStudyGuidesProperty.add(concreteGuide);
            }
        } else {
            if (this.favoritedStudyGuidesProperty.contains(concreteGuide)) {
                this.favoritedStudyGuidesProperty.remove(concreteGuide);
            }
        }
    }

    /**
     * Removes {@code studyGuide} from the downloaded study guides list
     * property
     *
     * @param studyGuide The study guide to delete
     */
    public void deleteStudyGuide(DisplayableStudyGuide studyGuide) {
        if (studyGuide == null) {
            throw new IllegalArgumentException("studyGuide can't be null");
        }

        if (this.downloadedStudyGuidesProperty.contains(studyGuide)) {
            this.downloadedStudyGuidesProperty.remove(studyGuide);
        }
    }

    private StudyGuide getConcreteGuide(DisplayableStudyGuide studyGuide) {
        return studyGuide instanceof StudyGuide concreteGuide ? concreteGuide : null;
    }

    /**
     * {@return The favorited studyguides property}
     */
    public ListProperty<DisplayableStudyGuide> getFavoritedStudyGuidesProperty() {
        return this.favoritedStudyGuidesProperty;
    }

    /**
     * {@return The downloaded studyguides property}
     */
    public ListProperty<DisplayableStudyGuide> getDownloadedStudyGuidesProperty() {
        return this.downloadedStudyGuidesProperty;
    }
}
