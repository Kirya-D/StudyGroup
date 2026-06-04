package kirya.viewmodel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import kirya.model.AuthDatabase;
import kirya.model.FileIO;
import kirya.model.StudyGuide;
import kirya.utils.DisplayableStudyGuide;

/**
 * Viewmodel of the Home view class
 */
public class HomeViewmodel {

    private final AuthDatabase database;
    private final ListProperty<DisplayableStudyGuide> downloadedStudyGuidesProperty;
    private final ListProperty<DisplayableStudyGuide> favoritedStudyGuidesProperty;
    private final ListProperty<DisplayableStudyGuide> uploadedStudyGuidesProperty;

    /**
     * Initializes a new HomeViewmodel.
     * 
     * @param database the authentication database to rely on
     */
    public HomeViewmodel(AuthDatabase database) {
        this.database = database;
        this.downloadedStudyGuidesProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.favoritedStudyGuidesProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.uploadedStudyGuidesProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
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
            if (studyGuide.getIsUploaded()) {
                this.uploadedStudyGuidesProperty.add(studyGuide);
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
     * Downloads {@code studyguide} if it is not already saved in some other format
     * 
     * @param studyGuide the studyguide to save changes for
     */
    public void saveChangesToStudyGuide(DisplayableStudyGuide studyGuide) {
        var concreteGuide = studyGuide instanceof StudyGuide cGuide ? cGuide : null;
        if (concreteGuide == null) {
            return;
        }

        var alreadyFavorited = this.favoritedStudyGuidesProperty.get().contains(concreteGuide);
        var alreadyDownloaded = this.downloadedStudyGuidesProperty.get().contains(concreteGuide);
        if (!alreadyDownloaded && !alreadyFavorited) {
            this.toggleDownloadStudyGuide(studyGuide, true);
        }
    }

    /**
     * Sets the downloaded state of {@code studyGuide} to {@code download} and
     * update
     * {@code studyGuide}'s presence in the downloaded collection accordingly
     * in it.
     *
     * @param studyGuide The non-null study guide to download
     * @param download   The new download state to set to
     * @throws IllegalArgumentException If {@code studyGuide} == null
     */
    public void toggleDownloadStudyGuide(DisplayableStudyGuide studyGuide, boolean download) {
        var concreteGuide = this.getConcreteGuide(studyGuide);
        if (concreteGuide == null) {
            throw new IllegalArgumentException("studyGuide can't be null");
        }

        Consumer<Boolean> setMethod = b -> concreteGuide.setIsDownloaded(b);
        var collection = this.downloadedStudyGuidesProperty;
        this.toggleStudyGuideMember(concreteGuide, setMethod, download, collection);
    }

    /**
     * Sets the favorited state of {@code studyGuide} to {@code favorite} and update
     * {@code studyGuide}'s presence in the favorited collection accordingly
     * in it.
     *
     * @param studyGuide The non-null study guide to favorite
     * @param favorite   The new favorite state to set to
     * @throws IllegalArgumentException If {@code studyGuide} == null
     */
    public void toggleFavoriteStudyGuide(DisplayableStudyGuide studyGuide, boolean favorite) {
        var concreteGuide = this.getConcreteGuide(studyGuide);
        if (concreteGuide == null) {
            throw new IllegalArgumentException("studyGuide can't be null");
        }

        Consumer<Boolean> setMethod = b -> concreteGuide.setIsFavorited(b);
        var collection = this.favoritedStudyGuidesProperty;
        this.toggleStudyGuideMember(concreteGuide, setMethod, favorite, collection);
    }

    /**
     * Toggles the uploaded state of {@code studyGuide} to {@code upload} and update
     * {@code studyGuide}'s presence in the uploaded collection accordingly
     * in it.
     *
     * @param studyGuide The non-null study guide to upload
     * @param upload     The new upload state to set to
     * @throws IllegalArgumentException If {@code studyGuide} == null
     * @throws SQLException             If database error occurs
     * @return {@code true} if successfully uploads studyguide to database, false
     *         otherwise
     */
    public boolean toggleUploadStudyGuide(DisplayableStudyGuide studyGuide, boolean upload) throws SQLException {
        var concreteGuide = this.getConcreteGuide(studyGuide);
        if (concreteGuide == null) {
            throw new IllegalArgumentException("studyGuide can't be null");
        }

        var loggedUsername = LoggedInAccount.Username();
        var success = false;
        if (upload) {
            success = this.database.editStudyguide(loggedUsername, concreteGuide);
        } else {
            var guideId = concreteGuide.getId();
            if (guideId != null) {
                this.database.deleteStudyguide(guideId);
                concreteGuide.setId(null);
                success = true;
            }
        }

        if (success) {
            Consumer<Boolean> setMethod = b -> concreteGuide.setIsUploaded(b);
            var collection = this.uploadedStudyGuidesProperty;
            this.toggleStudyGuideMember(concreteGuide, setMethod, upload, collection);
        }

        return success;
    }

    private void toggleStudyGuideMember(StudyGuide studyGuide, Consumer<Boolean> setMethod, boolean toggle,
            ListProperty<DisplayableStudyGuide> associatedCollection) {

        setMethod.accept(toggle);
        if (toggle) {
            if (!associatedCollection.contains(studyGuide)) {
                associatedCollection.add(studyGuide);
            }
        } else {
            if (associatedCollection.contains(studyGuide)) {
                associatedCollection.remove(studyGuide);
            }
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

    /**
     * {@return The uploaded studyguides property}
     */
    public ListProperty<DisplayableStudyGuide> getUploadedStudyGuidesProperty() {
        return this.uploadedStudyGuidesProperty;
    }
}
