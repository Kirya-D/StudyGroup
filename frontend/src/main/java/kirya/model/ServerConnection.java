package kirya.model;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;

import io.github.cdimascio.dotenv.Dotenv;
import kirya.utils.DisplayableStudyGuide;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * A server that can handle requests
 */
public class ServerConnection implements Server {

    private final String host;
    private final String port;
    private HttpClient client;

    public static final int GUIDES_PER_PAGE = 50;
    private static final String VALIDATION_JSON = "{\"value\": \"%s\", \"isUsername\": \"%s\"}";
    private static final String CREDENTIALS_JSON = "{\"username\": \"%s\", \"password\": \"%s\"}";
    private static final String UNEXPECTED_RESPONSE = "Expected different server response format";

    /**
     * Initializes a new {@link ServerConnection} with the host and port retrieved
     * from environment variables
     */
    public ServerConnection() {
        var dotenv = Dotenv.load();
        var host = dotenv.get("HOST");
        var port = dotenv.get("PORT");
        this(host, port);
    }

    /**
     * Initializes a new {@link ServerConnection} with the given {@code host} and
     * {@code port}
     * 
     * @param host The host to connect to
     * @param port The port to use
     */
    public ServerConnection(String host, String port) {
        this.host = host;
        this.port = port;
        var cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        this.client = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .build();
    }

    private static HttpRequest.Builder requestBuilderFactory(String host, String subPath, String port) {
        var uriPrefix = "http://";
        var uri = URI.create(uriPrefix + host + ":" + port + subPath);
        var builder = HttpRequest.newBuilder(uri)
                .header("Content-Type", "application/json");
        return builder;
    }

    @Override
    public String validateUsername(String username) throws IOException, InterruptedException {
        var json = String.format(VALIDATION_JSON, username, true);
        var request = requestBuilderFactory(this.host, Route.VALIDATE_CREDENTIAL, this.port)
                .POST(BodyPublishers.ofString(json))
                .build();

        var response = this.getResponse(request);
        var responseTree = getResponseBodyAsTree(response);
        var issueNode = responseTree.path("message");

        return issueNode.asString("Missing server response");
    }

    @Override
    public String validatePassword(String password) throws IOException, InterruptedException {
        var json = String.format(VALIDATION_JSON, password, false);
        var request = requestBuilderFactory(this.host, Route.VALIDATE_CREDENTIAL, this.port)
                .POST(BodyPublishers.ofString(json))
                .build();

        var response = this.getResponse(request);
        var responseTree = getResponseBodyAsTree(response);
        var issueNode = responseTree.path("message");

        return issueNode.asString("Missing server response");
    }

    public void createAccount(String username, String password) throws IOException, InterruptedException {
        var json = String.format(CREDENTIALS_JSON, username, password);
        var request = requestBuilderFactory(this.host, Route.ACCOUNT, this.port)
                .POST(BodyPublishers.ofString(json))
                .build();

        var response = this.getResponse(request);
        var responseTree = getResponseBodyAsTree(response);
        this.throwIfNotSuccessful(responseTree);
    }

    public boolean login(String username, String password) throws IOException, InterruptedException {
        var json = String.format(CREDENTIALS_JSON, username, password);
        var request = requestBuilderFactory(this.host, Route.SESSION, this.port)
                .POST(BodyPublishers.ofString(json))
                .build();

        var response = this.getResponse(request);
        var loggedIn = false;
        if (response.statusCode() != Status.UNAUTHORIZED) {
            var responseTree = getResponseBodyAsTree(response);
            this.throwIfNotSuccessful(responseTree);
            loggedIn = true;
        }

        return loggedIn;
    }

    public void logout() throws IOException, InterruptedException {
        var request = requestBuilderFactory(this.host, Route.SESSION, this.port)
                .DELETE()
                .build();

        var response = this.getResponse(request);
        if (response.statusCode() != Status.NO_CONTENT) {
            var responseTree = getResponseBodyAsTree(response);
            this.throwIfNotSuccessful(responseTree);
        }
    }

    public void uploadStudyguide(DisplayableStudyGuide studyguide) throws IOException, InterruptedException {
        var json = "{ \"studyguide\":" + new ObjectMapper().writeValueAsString(studyguide) + "}";
        var request = requestBuilderFactory(this.host, Route.STUDYGUIDE, this.port)
                .POST(BodyPublishers.ofString(json))
                .build();

        var response = this.getResponse(request);
        var responseTree = this.getResponseBodyAsTree(response);
        this.throwIfNotSuccessful(responseTree);
        var idNode = responseTree.path("id");
        if (this.nodeNotMissingAndNotNull(idNode) && studyguide instanceof StudyGuide concreteGuide) {
            var id = idNode.asString();
            concreteGuide.setId(id);
            concreteGuide.setUploaded(true);
        }
    }

    public void deleteStudyguide(DisplayableStudyGuide studyguide) throws IOException, InterruptedException {
        var queryParam = "?id=" + studyguide.getId();
        var request = requestBuilderFactory(this.host, Route.STUDYGUIDE + queryParam, this.port)
                .DELETE()
                .build();

        var response = this.getResponse(request);
        var status = response.statusCode();

        if (status == Status.NO_CONTENT) {
            var guide = studyguide instanceof StudyGuide cguide ? cguide : null;
            if (guide != null) {
                guide.setUploaded(false);
            }
        } else {
            var responseTree = this.getResponseBodyAsTree(response);
            this.throwIfNotSuccessful(responseTree);
        }
    }

    public Collection<DisplayableStudyGuide> searchForStudyguides(String search, int page, int max)
            throws IOException, InterruptedException {
        var queryParams = "?page=" + page + "&max=" + max;
        var subroute = Route.SEARCH + Route.STUDYGUIDE + "/" + search + queryParams;
        var request = requestBuilderFactory(this.host, subroute, this.port)
                .GET()
                .build();

        var guides = new ArrayList<DisplayableStudyGuide>();
        var response = this.getResponse(request);
        var responseTree = getResponseBodyAsTree(response);
        this.throwIfNotSuccessful(responseTree);
        var guidesNode = responseTree.path("results");

        if (this.nodeNotMissingAndNotNull(guidesNode)) {
            var mapper = new ObjectMapper();
            var typeRef = new TypeReference<ArrayList<StudyGuide>>() {
            };
            var convertedGuides = mapper.convertValue(guidesNode, typeRef);
            System.out.println(convertedGuides);
            guides.addAll(convertedGuides);
        }

        return guides;
    }

    private HttpResponse<String> getResponse(HttpRequest request) throws IOException, InterruptedException {
        return this.client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private JsonNode getResponseBodyAsTree(HttpResponse<String> response) {
        return new ObjectMapper().readTree(response.body());
    }

    private void throwIfNotSuccessful(JsonNode responseTree) throws IOException {
        var successNode = responseTree.path("success");
        var successNodeExists = this.nodeNotMissingAndNotNull(successNode);
        var success = successNodeExists ? successNode.asBoolean() : false;

        if (!success) {
            var message = responseTree.path("message");
            var messageNodeExists = !(message.isMissingNode() || message.isNull());
            var errMessage = messageNodeExists ? message.asString() : UNEXPECTED_RESPONSE;
            throw new IOException(errMessage);
        }
    }

    private boolean nodeNotMissingAndNotNull(JsonNode node) {
        var exists = !node.isMissingNode();
        var isNotNull = !node.isNull();
        return exists && isNotNull;
    }

    private class Route {
        public static final String VALIDATE_CREDENTIAL = "/validate_credential";
        public static final String SEARCH = "/search";
        public static final String SESSION = "/session";
        public static final String ACCOUNT = "/account";
        public static final String STUDYGUIDE = "/studyguide";
    }

    private class Status {
        public static final int NO_CONTENT = 204;
        public static final int UNAUTHORIZED = 401;
    }
}
