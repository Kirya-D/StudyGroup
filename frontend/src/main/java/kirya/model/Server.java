package kirya.model;

import java.io.IOException;
import java.util.Collection;

import kirya.utils.DisplayableStudyGuide;

public interface Server {

    /**
     * Validates the username and returns a string describing what invalidates it.
     * 
     * @param username The username to valid
     * @throws IOException
     * @throws InterruptedException
     * @return A string describing the invalidating aspects of the username
     */
    public String validateUsername(String username) throws IOException, InterruptedException;

    /**
     * Validates the password and returns a string describing what invalidates it.
     * 
     * @param password The password to validate
     * @throws IOException
     * @throws InterruptedException
     * @return A string describing the invalidating aspects of the password
     */
    public String validatePassword(String password) throws IOException, InterruptedException;

    /**
     * Attempts to create a new account on the server with the given username and
     * password.
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
    public boolean login(String username, String password) throws IOException, InterruptedException;

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
     * Attempts to search for studyguides on the server based on the provided search
     * term and pagination parameters.
     *
     * @param search the search term to filter studyguides
     * @param page   the page number for pagination (starting from 0)
     * @param max    the maximum number of studyguides to return
     * @return a collection of DisplayableStudyGuide objects matching the search
     *         criteria
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public Collection<DisplayableStudyGuide> searchForStudyguides(String search, int page, int max)
            throws IOException, InterruptedException;
}
