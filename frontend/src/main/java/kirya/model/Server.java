package kirya.model;

import java.io.IOException;
import java.util.Collection;

import kirya.utils.DisplayableStudyGuide;

public interface Server {

    /**
     * Attempts to check if the given username is already taken on the server.
     *
     * @param username the username to check
     * @return true if the username is taken, false otherwise
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public boolean isUsernameTaken(String username) throws IOException, InterruptedException;

    /**
     * Attempts to create a new account on the server with the given username and password.
     *
     * @param username the desired username for the new account
     * @param password the desired password for the new account
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public void createAccount(String username, String password) throws IOException, InterruptedException;

    /**
     * Attempts to log in to the server with the given username and password.
     *
     * @param username the username of the account to log in
     * @param password the password of the account to log in
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public void login(String username, String password) throws IOException, InterruptedException;

    /**
     * Attempts to log user out from the server.
     *
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public void logout() throws IOException, InterruptedException;

    /**
     * Attempts to upload a studyguide to the server.
     *
     * @param studyguide the studyguide to upload
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public void uploadStudyguide(DisplayableStudyGuide studyguide) throws IOException, InterruptedException;

    /**
     * Attempts to delete a studyguide from the server.
     *
     * @param studyguide the studyguide to delete
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public void deleteStudyguide(DisplayableStudyGuide studyguide) throws IOException, InterruptedException;

    /**
     * Attempts to search for studyguides on the server based on the provided search term and pagination parameters.
     *
     * @param search the search term to filter studyguides
     * @param page   the page number for pagination (starting from 0)
     * @param max    the maximum number of studyguides to return
     * @return a collection of DisplayableStudyGuide objects matching the search criteria
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public Collection<DisplayableStudyGuide> searchForStudyguides(String search, int page, int max)
            throws IOException, InterruptedException;
}
