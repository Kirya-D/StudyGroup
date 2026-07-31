package kirya.utils;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class SessionData {
    public static final String GUEST_NAME = "Guest";
    private static boolean isGuest = false;
    private static String loggedInUsername = null;
    private static ListProperty<DisplayableStudyGuide> favoritedStudyguides = new SimpleListProperty<>(
            FXCollections.observableArrayList());
    private static ListProperty<DisplayableStudyGuide> downloadedStudyguides = new SimpleListProperty<>(
            FXCollections.observableArrayList());
    private static ListProperty<DisplayableStudyGuide> uploadedStudyguides = new SimpleListProperty<>(
            FXCollections.observableArrayList());

    /**
     * {@return the guest status of the current user}
     */
    public static boolean getIsGuest() {
        return SessionData.isGuest;
    }

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
        SessionData.isGuest = false;
    }

    /**
     * Sets the current user to be treated as a guest user.
     * 
     * <p>
     * Postcondition: {@link SessionData#getIsGuest()} == {@code true}
     * </p>
     * 
     * @throws IllegalStateException If a user is already logged in with an account.
     */
    public static void continueAsGuest() {
        if (SessionData.loggedInUsername != null) {
            throw new IllegalStateException("can't continue as guest while currently logged in");
        }
        SessionData.isGuest = true;
    }

    /**
     * Clears the logged in user and guest status.
     * 
     * <p>
     * Postcondition: {@link SessionData#loggedInUsername} == null
     * </p>
     * <p>
     * Postcondition: {@link SessionData#isGuest} == null
     * </p>
     */
    public static void logOut() {
        SessionData.loggedInUsername = null;
        SessionData.isGuest = false;
    }
}
