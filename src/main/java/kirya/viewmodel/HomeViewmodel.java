package kirya.viewmodel;

import java.sql.SQLException;
import java.util.function.Consumer;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import kirya.model.AuthDatabase;
import kirya.model.StudyGuide;
import kirya.model.request.SearchRequest;
import kirya.model.request.UpdateRequest;
import kirya.utils.DisplayableStudyGuide;
import kirya.utils.SessionData;

/**
 * Viewmodel of the Home view class
 */
public class HomeViewmodel {

    private final AuthDatabase database;
    private String lastEnteredSearch;
    private int uninterruptedSearchRequests;
    private boolean allowMoreRequests;

    private final ListProperty<DisplayableStudyGuide> downloadedStudyGuidesProperty;
    private final ListProperty<DisplayableStudyGuide> favoritedStudyGuidesProperty;
    private final ListProperty<DisplayableStudyGuide> uploadedStudyGuidesProperty;
    private final StringProperty searchProperty;
    private final ListProperty<DisplayableStudyGuide> searchedStudyGuidesProperty;

    /**
     * Initializes a new HomeViewmodel.
     * 
     * @param database the authentication database to rely on
     */
    public HomeViewmodel(AuthDatabase database) {
        this.database = database;
        this.lastEnteredSearch = null;
        this.uninterruptedSearchRequests = 0;
        this.allowMoreRequests = true;

        this.downloadedStudyGuidesProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.favoritedStudyGuidesProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.uploadedStudyGuidesProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.searchProperty = new SimpleStringProperty("");
        this.searchedStudyGuidesProperty = new SimpleListProperty<>(FXCollections.observableArrayList());

        this.bindToSelf();
    }

    private void bindToSelf() {
        this.searchedStudyGuidesProperty.addListener((_, _, newList) -> {
            newList.forEach(searchedGuide -> {
                var matchingFavorites = this.favoritedStudyGuidesProperty.stream()
                        .filter(sg -> searchedGuide.getId() == sg.getId()).toList();
                var matchingDownloads = this.downloadedStudyGuidesProperty.stream()
                        .filter(sg -> searchedGuide.getId() == sg.getId()).toList();
                var matchingUploads = this.uploadedStudyGuidesProperty.stream()
                        .filter(sg -> searchedGuide.getId() == sg.getId()).toList();

                for (var favMatch : matchingFavorites) {
                    var index = this.favoritedStudyGuidesProperty.indexOf(favMatch);
                    this.favoritedStudyGuidesProperty.set(index, searchedGuide);
                }
                for (var downloadMatch : matchingDownloads) {
                    var index = this.downloadedStudyGuidesProperty.indexOf(downloadMatch);
                    this.downloadedStudyGuidesProperty.set(index, searchedGuide);
                }
                for (var uploadMatch : matchingUploads) {
                    var index = this.uploadedStudyGuidesProperty.indexOf(uploadMatch);
                    this.uploadedStudyGuidesProperty.set(index, searchedGuide);
                }
            });
        });
    }

    /**
     * {@return a newly created study guide}
     * 
     * @throws IllegalArgumentException If {@link SessionData#getLoggedInUsername()}
     *                                  == {@code null}
     */
    public DisplayableStudyGuide createNewStudyGuide() {
        var newGuide = new StudyGuide();
        newGuide.setCreatorUsername(SessionData.getLoggedInUsername());
        return newGuide;
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
     * 
     * @throws IllegalArgumentException If {@code studyGuide} == null
     */
    public void toggleDownloadStudyGuide(DisplayableStudyGuide studyGuide, boolean download)
            throws IllegalArgumentException {
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
     * 
     * @throws IllegalArgumentException If {@code studyGuide} == null
     */
    public void toggleFavoriteStudyGuide(DisplayableStudyGuide studyGuide, boolean favorite)
            throws IllegalArgumentException {
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
     * 
     * @throws IllegalArgumentException If {@code studyGuide} == null
     * @throws IllegalArgumentException If there is no currently logged-in user
     *                                  (user == null)
     * @throws SQLException             If database error occurs
     * 
     */
    public void toggleUploadStudyGuide(DisplayableStudyGuide studyGuide, boolean upload) throws SQLException {
        var concreteGuide = this.getConcreteGuide(studyGuide);
        if (concreteGuide == null) {
            throw new IllegalArgumentException("studyGuide can't be null");
        }

        var loggedUsername = SessionData.getLoggedInUsername();
        var success = false;
        var uploadRequest = new UpdateRequest(loggedUsername, studyGuide);
        if (upload) {
            success = this.database.editStudyguide(uploadRequest);
        } else {
            var guideId = concreteGuide.getId();
            if (guideId != null) {
                success = this.database.deleteStudyguide(uploadRequest);
            }
        }

        if (success) {
            Consumer<Boolean> setMethod = b -> concreteGuide.setIsUploaded(b);
            var collection = this.uploadedStudyGuidesProperty;
            this.toggleStudyGuideMember(concreteGuide, setMethod, upload, collection);
        }
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

    /**
     * Searches for study guides that contains
     * {@link HomeViewmodel#getSearchProperty()}'s value
     * 
     * @throws SQLException If a database error occurs
     */
    public void searchForStudyguides() throws SQLException {
        var searchString = this.searchProperty.get();
        if (searchString == null || searchString.isBlank()) {
            return;
        }
        this.uninterruptedSearchRequests = 0;
        this.lastEnteredSearch = searchString;

        var searchRequest = new SearchRequest(SessionData.getLoggedInUsername(), searchString);
        var results = this.database.getStudyguidesContaining(searchRequest);
        this.searchedStudyGuidesProperty.setAll(results);
    }

    /**
     * Requests more results from the most recent search query from
     * {@link HomeViewmodel#searchForStudyguides()} unless the most recent call to
     * {@link HomeViewmodel#attemptGetMoreResults()} retrieved 0 results, in which
     * case no more attempts are made until a new search is initiated.
     * 
     * @throws SQLException If a database error occurs
     */
    public void attemptGetMoreResults() throws SQLException {
        if (this.lastEnteredSearch == null || !this.allowMoreRequests) {
            return;
        }
        this.uninterruptedSearchRequests++;

        var search = this.lastEnteredSearch;
        var pageNum = this.uninterruptedSearchRequests;
        var searchRequest = new SearchRequest(SessionData.getLoggedInUsername(), search, pageNum);
        var results = this.database.getStudyguidesContaining(searchRequest);
        this.searchedStudyGuidesProperty.addAll(results);

        this.allowMoreRequests = results.size() > 0;
    }

    private StudyGuide getConcreteGuide(DisplayableStudyGuide studyGuide) {
        return studyGuide instanceof StudyGuide concreteGuide ? concreteGuide : null;
    }

    /**
     * {@return the favorited studyguides property}
     */
    public ListProperty<DisplayableStudyGuide> getFavoritedStudyGuidesProperty() {
        return this.favoritedStudyGuidesProperty;
    }

    /**
     * {@return the downloaded studyguides property}
     */
    public ListProperty<DisplayableStudyGuide> getDownloadedStudyGuidesProperty() {
        return this.downloadedStudyGuidesProperty;
    }

    /**
     * {@return the uploaded studyguides property}
     */
    public ListProperty<DisplayableStudyGuide> getUploadedStudyGuidesProperty() {
        return this.uploadedStudyGuidesProperty;
    }

    /**
     * {@return the search text property}
     */
    public StringProperty getSearchProperty() {
        return this.searchProperty;
    }

    /**
     * {@return the searched studyguides property}
     */
    public ListProperty<DisplayableStudyGuide> getSearchedStudyGuidesProperty() {
        return this.searchedStudyGuidesProperty;
    }
}
