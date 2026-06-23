package kirya.model.request;

/**
 * A request with login credentials
 */
public class CredentialsRequest extends Request {

    public CredentialsRequest(String username) {
        this(username, null);
    }

    public CredentialsRequest(String username, String password) {
        super(username, password, null, null);
    }
}
