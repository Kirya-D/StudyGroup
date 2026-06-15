package kirya.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.SequencedCollection;

import kirya.model.request.CredentialsRequest;
import kirya.model.request.SearchRequest;
import kirya.model.request.UpdateRequest;
import kirya.utils.DisplayableQuestion;
import kirya.utils.DisplayableStudyGuide;
import kirya.utils.QuestionType;

/**
 * Represents a database that can view and manipulate account information
 */
public abstract class AuthDatabase {

    protected Connection dbConnection;
    private PreparedStatement getUsernameIsTaken;
    private PreparedStatement getAccountWithCredentials;
    private PreparedStatement createAccount;
    private PreparedStatement createStudyguide;
    private PreparedStatement createQuestion;
    private PreparedStatement createChoice;
    private PreparedStatement getStudyguideContainingSubstring;
    private PreparedStatement deleteStudyguide;

    public AuthDatabase(Properties properties) throws SQLException {
        this.setupDatabase();

        var usernameExistsQuery = properties.getProperty("USERNAME_EXISTS_QUERY");
        var correctCredentialsQuery = properties.getProperty("CORRECT_CREDENTIALS_QUERY");
        var createAccountQuery = properties.getProperty("CREATE_ACCOUNT_QUERY");
        var createStudyguideQuery = properties.getProperty("CREATE_STUDYGUIDE_QUERY");
        var createQuestionQuery = properties.getProperty("CREATE_QUESTION_QUERY");
        var createChoiceQuery = properties.getProperty("CREATE_CHOICE_QUERY");
        var getStudyguideContainingSubstringQuery = properties.getProperty("GET_STUDYGUIDE_CONTAINING_SUBSTRING_QUERY");
        var deleteStudyguideQuery = properties.getProperty("DELETE_STUDYGUIDE_QUERY");

        this.getUsernameIsTaken = this.dbConnection.prepareStatement(usernameExistsQuery);
        this.getAccountWithCredentials = this.dbConnection.prepareStatement(correctCredentialsQuery);
        this.createAccount = this.dbConnection.prepareStatement(createAccountQuery);
        this.createStudyguide = this.dbConnection.prepareStatement(createStudyguideQuery);
        this.createQuestion = this.dbConnection.prepareStatement(createQuestionQuery);
        this.createChoice = this.dbConnection.prepareStatement(createChoiceQuery);
        this.getStudyguideContainingSubstring = this.dbConnection
                .prepareStatement(getStudyguideContainingSubstringQuery);
        this.deleteStudyguide = this.dbConnection.prepareStatement(deleteStudyguideQuery);
    }

    /**
     * Sets up the database connection
     * 
     * @throws SQLException If a database access error occurs
     */
    protected abstract void setupDatabase() throws SQLException;

    /**
     * {@return {@code true} if an account with {@link CredentialsRequest#username}
     * exists, otherwise {@code false}}
     * 
     * @param request the {@link CredentialsRequest} information to use
     * 
     * @throws SQLException             If a database access error occurs
     * @throws IllegalArgumentException If {@link CredentialsRequest#username} ==
     *                                  null
     */
    public final boolean hasAccountWithUsername(CredentialsRequest request) throws SQLException {
        if (request.username == null) {
            throw new IllegalArgumentException("username can't be null");
        }

        this.getUsernameIsTaken.setString(1, request.username);
        var results = this.getUsernameIsTaken.executeQuery();
        var usernameTaken = results.next();
        results.close();

        return usernameTaken;
    }

    /**
     * {@return {@code true} if an account with {@link CredentialsRequest#username}
     * and
     * {@link CredentialsRequest#password} exists, otherwise {@code false}}
     * 
     * @param request the {@link CredentialsRequest} information to use
     * 
     * @throws SQLException             If a database access error occurs
     * @throws IllegalArgumentException If {@link CredentialsRequest#username} ==
     *                                  null
     * @throws IllegalArgumentException If {@link CredentialsRequest#password} ==
     *                                  null
     */
    public final boolean hasAccountWithCredentials(CredentialsRequest request)
            throws SQLException, IllegalArgumentException {
        if (request.username == null) {
            throw new IllegalArgumentException("username can't be null");
        }
        if (request.password == null) {
            throw new IllegalArgumentException("password can't be null");
        }

        this.getAccountWithCredentials.setString(1, request.username);
        var results = this.getAccountWithCredentials.executeQuery();
        var foundAccountWithCredentials = false;

        while (results.next()) {
            var resultUsername = results.getString("username");
            var resultPassword = results.getString("password");
            var usernameMatches = request.username.equals(resultUsername);
            var passwordMatches = request.password.equals(resultPassword);
            if (usernameMatches && passwordMatches) {
                foundAccountWithCredentials = true;
                break;
            }
        }

        return foundAccountWithCredentials;
    }

    /**
     * Attempts to create a new account with the specifications from the
     * {@code request}.
     * 
     * @param request the {@link CredentialsRequest} information to use
     * 
     * @throws SQLException             If a database access error occurs
     * @throws IllegalArgumentException If {@link CredentialsRequest#username} ==
     *                                  null
     * @throws IllegalArgumentException If {@link CredentialsRequest#password} ==
     *                                  null
     */
    public final void attemptCreateAccount(CredentialsRequest request) throws SQLException, IllegalArgumentException {
        if (request.username == null) {
            throw new IllegalArgumentException("username can't be null");
        }
        if (request.password == null) {
            throw new IllegalArgumentException("password can't be null");
        }

        this.createAccount.setString(1, request.username);
        this.createAccount.setString(2, request.password);
        this.createAccount.execute();
    }

    /**
     * Attempts to upload {@link UpdateRequest#studyguide} under the account with
     * the username {@link UpdateRequest#username}
     * 
     * @param request the {@link UpdateRequest} information to use
     * 
     * @return {@code true} if successful, otherwise {@code false}
     * 
     * @throws SQLException             If a database error occurs
     * @throws IllegalArgumentException If {@link UpdateRequest#username} == null
     * @throws IllegalArgumentException If {@link UpdateRequest#studyguide} == null
     *                                  OR not an {@code instanceof}
     *                                  {@link StudyGuide}
     */
    public final boolean editStudyguide(UpdateRequest request) throws SQLException, IllegalArgumentException {
        if (request.username == null) {
            throw new IllegalArgumentException("username can't be null");
        }
        if (request.studyguide == null) {
            throw new IllegalArgumentException("studyguide can't be null");
        }
        if (!(request.studyguide instanceof StudyGuide)) {
            throw new IllegalArgumentException("studyguide must be editable");
        }

        this.getUsernameIsTaken.setString(1, request.username);
        var accountResult = this.getUsernameIsTaken.executeQuery();
        var accountId = accountResult.next() ? accountResult.getInt("id") : null;
        accountResult.close();
        if (accountId == null) {
            return false;
        }

        var oldStudyguideId = request.studyguide.getId();
        var overwriteStudyguideRow = oldStudyguideId != null;

        var newStudyguideId = this.createNewStudyguide(accountId, request.studyguide);
        if (newStudyguideId == null) {
            return false;
        }

        for (var question : request.studyguide.getQuestions()) {
            var questionId = this.createNewQuestion(newStudyguideId, question);
            if (questionId == null) {
                continue;
            }

            var answers = question.getAnswers();
            var dbChoices = question.getChoices().stream().map(c -> new DbChoice(c, answers.contains(c))).toList();
            for (var choice : dbChoices) {
                this.createChoice.setString(1, choice.text);
                this.createChoice.setBoolean(2, choice.isAnswer);
                this.createChoice.setInt(3, questionId);
                this.createChoice.executeUpdate();
            }
        }

        if (overwriteStudyguideRow) {
            this.deleteStudyguide(request);
        }

        ((StudyGuide) request.studyguide).setId(newStudyguideId);
        return true;
    }

    private final Integer createNewStudyguide(int accountId, DisplayableStudyGuide studyguide) throws SQLException {
        var title = studyguide.getTitle();
        var description = studyguide.getDescription();
        this.createStudyguide.setString(1, title);
        this.createStudyguide.setString(2, description);
        this.createStudyguide.setInt(3, accountId);

        var studyguideResult = this.createStudyguide.executeQuery();
        var studyguideId = studyguideResult.next() ? studyguideResult.getInt("id") : null;
        studyguideResult.close();

        return studyguideId;
    }

    private final Integer createNewQuestion(int studyguideId, DisplayableQuestion question) throws SQLException {
        var text = question.getQuestion();
        this.createQuestion.setString(1, text);
        this.createQuestion.setInt(2, studyguideId);

        var questionResult = this.createQuestion.executeQuery();
        var questionId = questionResult.next() ? questionResult.getInt("id") : null;
        questionResult.close();

        return questionId;
    }

    /**
     * {@return a collection of {@link DisplayableStudyGuide} that contain
     * {@link SearchRequest#search} (case insensitive) in either the title or
     * description, or its creator's username}
     * 
     * @param request the {@link SearchRequest} information to use
     * 
     * @throws SQLException             If a database error occurs
     * @throws IllegalArgumentException If {@link SearchRequest#username} == null
     * @throws IllegalArgumentException If {@link SearchRequest#search} == null
     */
    public final SequencedCollection<DisplayableStudyGuide> getStudyguidesContaining(SearchRequest request)
            throws SQLException, IllegalArgumentException {
        if (request.username == null) {
            throw new IllegalArgumentException("username can't be null");
        }
        if (request.search == null) {
            throw new IllegalArgumentException("search can't be null");
        }

        this.getStudyguideContainingSubstring.setString(1, request.username);
        this.getStudyguideContainingSubstring.setString(2, request.search);
        this.getStudyguideContainingSubstring.setString(3, request.search);
        this.getStudyguideContainingSubstring.setString(4, request.search);

        var results = this.getStudyguideContainingSubstring.executeQuery();
        var fittingGuides = this.getStudyguidesFromResultSet(results);

        return fittingGuides;
    }

    private final SequencedCollection<DisplayableStudyGuide> getStudyguidesFromResultSet(ResultSet results)
            throws SQLException {
        var studyguides = new HashMap<Integer, StudyGuide>();
        var questions = new HashMap<Integer, Question>();

        while (results.next()) {
            StudyGuide curGuide = null;
            Question curQuestion = null;
            var studyguideId = results.getInt("studyguideId");

            var studyGuideIdStored = studyguides.containsKey(studyguideId);
            if (!studyGuideIdStored) {
                var creatorUsername = results.getString("username");
                var title = results.getString("title");
                var description = results.getString("description");

                curGuide = new StudyGuide(studyguideId);
                curGuide.setCreatorUsername(creatorUsername);
                curGuide.setTitle(title);
                curGuide.setDescription(description);
                curGuide.setIsUploaded(true);
                studyguides.put(studyguideId, curGuide);
            } else {
                curGuide = studyguides.get(studyguideId);
            }

            var questionId = results.getInt("questionId");
            var questionIdStored = questions.containsKey(questionId);
            if (!questionIdStored) {
                var questionText = results.getString("questionText");
                curQuestion = new Question(questionText);
                var oldQuestions = curGuide.getQuestions();
                oldQuestions.add(curQuestion);
                var newQuestions = oldQuestions.stream().map(q -> (Question) q).toList();
                curGuide.setQuestions(newQuestions);
                questions.put(questionId, curQuestion);
            } else {
                curQuestion = questions.get(questionId);
            }
            var choiceText = results.getString("choiceText");
            var choiceIsAnswer = results.getBoolean("choiceIsAnswer");

            var oldChoices = curQuestion.getChoices();
            oldChoices.add(choiceText);
            if (choiceIsAnswer) {
                var oldAnswers = curQuestion.getAnswers();
                oldAnswers.add(choiceText);
                curQuestion.setAnswers(oldAnswers);
            }
            if (oldChoices.size() > 1 && curQuestion.getQuestionType() != QuestionType.MULTIPLE_CHOICE) {
                curQuestion.setQuestionType(QuestionType.MULTIPLE_CHOICE);
            }
        }
        results.close();

        var guides = new ArrayList<DisplayableStudyGuide>(studyguides.values());
        return guides;
    }

    /**
     * {@return Attempst to delete the studyguide with the id that matches
     * {@link UpdateRequest#studyguide}'s {@link DisplayableStudyGuide#getId()} from
     * the database and returns {@code true} if successful, otherwise
     * {@code false}.}
     * 
     * @param request the {@link UpdateRequest} to make
     * 
     * @throws SQLException             If a database access error occurs
     * @throws IllegalArgumentException If {@link UpdateRequest#studyguide} == null
     */
    public final boolean deleteStudyguide(UpdateRequest request) throws SQLException {
        if (request.studyguide == null) {
            throw new IllegalArgumentException("studyguide can't be null");
        }
        var guide = request.studyguide;

        this.deleteStudyguide.setString(1, request.username);
        this.deleteStudyguide.setInt(2, guide.getId());
        boolean success = this.deleteStudyguide.executeUpdate() > 0 ? true : false;
        return success;
    }

    /**
     * A type that represents an answer choice containing {@code text} and
     * {@code isAnswer} which corresponds to the textual content of the answer
     * choice and whether or not is is correct respectively.
     */
    private record DbChoice(String text, boolean isAnswer) {
    }
}
