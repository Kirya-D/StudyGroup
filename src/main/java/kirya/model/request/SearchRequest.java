package kirya.model.request;

/**
 * A request with information to search with
 */
public class SearchRequest extends Request {
    public SearchRequest(String search) {
        this(null, search);
    }

    public SearchRequest(String searcherUsername, String search) {
        var regexSearch = "%" + search + "%";
        super(searcherUsername, null, regexSearch, null);
    }
}
