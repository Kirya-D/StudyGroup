package kirya.utils;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class SessionData {
    private static String loggedInUsername = null;
    private static ListProperty<DisplayableStudyGuide> favoritedStudyguides = new SimpleListProperty<>(
            FXCollections.observableArrayList());
    private static ListProperty<DisplayableStudyGuide> downloadedStudyguides = new SimpleListProperty<>(
            FXCollections.observableArrayList());
    private static ListProperty<DisplayableStudyGuide> uploadedStudyguides = new SimpleListProperty<>(
            FXCollections.observableArrayList());

    /**
     * {@return the username of the currently logged-in account}
     */
    public static String getLoggedInUsername() {
        return SessionData.loggedInUsername;
    }

    /**
     * {@return the favorited study guides in the current session}
     */
    public static ListProperty<DisplayableStudyGuide> getFavoritedStudyguides() {
        return SessionData.favoritedStudyguides;
    }

    /**
     * {@return the downloaded study guides in the current session}
     */
    public static ListProperty<DisplayableStudyGuide> getDownloadedStudyguides() {
        return SessionData.downloadedStudyguides;
    }

    /**
     * {@return the uploaded study guides in the current session}
     */
    public static ListProperty<DisplayableStudyGuide> getUploadedStudyguides() {
        return SessionData.uploadedStudyguides;
    }

    /**
     * Sets the currently logged in {@code username}.
     * 
     * <p>
     * Postcondition: {@link SessionData#getLoggedInUsername} == {@code username}
     * </p>
     * 
     * @param username the non-null username of the account to log in.
     * @throws IllegalArgumentException If {@code username} is null
     */
    public static void logInAs(String username) {
        if (username == null) {
            throw new IllegalArgumentException("username can not be null");
        }
        SessionData.loggedInUsername = username;
    }

    /**
     * Clears the logged in user
     * 
     * <p>
     * Postcondition: {@link SessionData#Username} == null
     * </p>
     */
    public static void logOut() {
        SessionData.loggedInUsername = null;
    }
}
