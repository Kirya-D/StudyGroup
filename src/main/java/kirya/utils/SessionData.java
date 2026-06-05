package kirya.utils;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;

public class SessionData {
    private static String loggedInUsername = null;
    private static ListProperty<DisplayableStudyGuide> downloadedStudyguides = new SimpleListProperty<>();

    /**
     * {@return the username of the currently logged-in account}
     */
    public static String getLoggedInUsername() {
        return SessionData.loggedInUsername;
    }

    public static ListProperty<DisplayableStudyGuide> getDownloadedStudyguides() {
        return SessionData.downloadedStudyguides;
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
    public static final void logInAs(String username) {
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
    public static final void logOut() {
        SessionData.loggedInUsername = null;
    }
}
