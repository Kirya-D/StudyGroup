package kirya.viewmodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import kirya.model.Server;
import kirya.model.StudyGuide;
import kirya.utils.DisplayableStudyGuide;

public class MockServer implements Server {

    public static final String VALUE_TOO_SHORT = "VALUE_TOO_SHORT";
    public static final String VALUE_TOO_LONG = "VALUE_TOO_LONG";
    public static final String INVALID_CHARACTERS = "INVALID_CHARACTERS";
    public static final int MIN_LENGTH = 8;
    public static final int MAX_LENGTH = 32;

    private Entry<String, String> loggedInUser = null;
    private Map<String, String> accounts = new HashMap<>();
    private Map<String, DisplayableStudyGuide> guides = new HashMap<>();

    public String getLoggedInUser() {
        return this.loggedInUser != null ? loggedInUser.getKey() : null;
    }

    @Override
    public String validateUsername(String username) throws IOException, InterruptedException {
        if (username.length() < MIN_LENGTH || username.length() > MAX_LENGTH) {
            return username.length() < MIN_LENGTH ? VALUE_TOO_SHORT : VALUE_TOO_LONG;
        }

        if (!username.matches("^[\\p{L}\\p{N}]+$")) {
            return INVALID_CHARACTERS;
        }

        return "";
    }

    @Override
    public String validatePassword(String password) throws IOException, InterruptedException {
        if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            return password.length() < MIN_LENGTH ? VALUE_TOO_SHORT : VALUE_TOO_LONG;
        }

        if (password.matches(".*\\s.*")) {
            return INVALID_CHARACTERS;
        }

        return "";
    }

    @Override
    public void createAccount(String username, String password) throws IOException, InterruptedException {
        if (accounts.containsKey(username)) {
            throw new IOException("Username is already taken");
        }
        accounts.put(username, password);
    }

    @Override
    public boolean login(String username, String password) throws IOException, InterruptedException {
        var successful = accounts.containsKey(username) && accounts.get(username).equals(password);

        this.loggedInUser = successful ? Map.entry(username, password) : null;

        return successful;
    }

    @Override
    public void logout() throws IOException, InterruptedException {
        loggedInUser = null;
    }

    @Override
    public void uploadStudyguide(DisplayableStudyGuide studyguide) throws IOException, InterruptedException {
        if (loggedInUser == null) {
            throw new IOException("User is not logged in");
        }
        var concreteGuide = (StudyGuide) studyguide;
        if (studyguide.getId() == null) {
            String newId = UUID.randomUUID().toString();
            concreteGuide.setId(newId);
            concreteGuide.setCreatorUsername(loggedInUser.getKey());
        } else {
            if (!concreteGuide.getCreatorUsername().equals(loggedInUser.getKey())) {
                throw new IOException("User is not the creator of this study guide");
            }
        }
        guides.put(studyguide.getId(), studyguide);
    }

    @Override
    public void deleteStudyguide(DisplayableStudyGuide studyguide) throws IOException, InterruptedException {
        if (loggedInUser == null) {
            throw new IOException("User is not logged in");
        }
        if (!studyguide.getCreatorUsername().equals(loggedInUser.getKey())) {
            throw new IOException("User is not the creator of this study guide");
        }
        guides.remove(studyguide.getId());
    }

    @Override
    public Collection<DisplayableStudyGuide> searchForStudyguides(String search, int page, int max)
            throws IOException, InterruptedException {
        var results = new ArrayList<DisplayableStudyGuide>();

        for (DisplayableStudyGuide guide : guides.values()) {
            String caseInsensitiveSearch = search.toLowerCase(Locale.ROOT);
            boolean titleMatches = guide.getTitle().toLowerCase(Locale.ROOT).contains(caseInsensitiveSearch);
            boolean descriptionMatches = titleMatches ? true
                    : guide.getDescription().toLowerCase(Locale.ROOT).contains(caseInsensitiveSearch);
            if (titleMatches || descriptionMatches) {
                results.add(guide);
            }
        }

        List<DisplayableStudyGuide> finalResults = new ArrayList<DisplayableStudyGuide>();
        try {
            int startIndex = page * max;
            int finalCount = results.size() >= max ? max : results.size();
            int endIndex = startIndex + finalCount;
            finalResults = results.subList(startIndex, endIndex);
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e);
        }

        return finalResults;
    }
}