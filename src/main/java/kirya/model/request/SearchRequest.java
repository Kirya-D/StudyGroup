package kirya.model.request;

/**
 * A request with information to search with
 */
public class SearchRequest extends Request {
    public final int pageNum;

    public SearchRequest(String searcherUsername, String search) {
        this(searcherUsername, search, 0);
    }

    public SearchRequest(String searcherUsername, String search, int pageNum) {
        this.pageNum = pageNum;
        var regexSearch = "%" + search + "%";
        super(searcherUsername, null, regexSearch, null);
    }
}
