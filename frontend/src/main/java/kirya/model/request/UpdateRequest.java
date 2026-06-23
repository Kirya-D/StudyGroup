package kirya.model.request;

import kirya.utils.DisplayableStudyGuide;

/**
 * A request with information to modify a studyguide
 */
public class UpdateRequest extends Request {
    public UpdateRequest(String username, DisplayableStudyGuide studyguide) {
        super(username, null, null, studyguide);
    }
}
