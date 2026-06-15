package kirya.model.request;

import kirya.utils.DisplayableStudyGuide;

/**
 * Database request query information
 */
abstract class Request {
    public final String username;
    public final String password;
    public final String search;
    public final DisplayableStudyGuide studyguide;

    Request(String username, String password, String search, DisplayableStudyGuide studyguide) {
        this.username = username;
        this.password = password;
        this.search = search;
        this.studyguide = studyguide;
    }
}
