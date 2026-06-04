package kirya.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

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
    private PreparedStatement getStudyguide;
    private PreparedStatement deleteStudyguide;

    public AuthDatabase(Properties properties) throws SQLException {
        this.setupDatabase();

        var usernameExistsQuery = properties.getProperty("USERNAME_EXISTS_QUERY");
        var correctCredentialsQuery = properties.getProperty("CORRECT_CREDENTIALS_QUERY");
        var createAccountQuery = properties.getProperty("CREATE_ACCOUNT_QUERY");
        var createStudyguideQuery = properties.getProperty("CREATE_STUDYGUIDE_QUERY");
        var createQuestionQuery = properties.getProperty("CREATE_QUESTION_QUERY");
        var createChoiceQuery = properties.getProperty("CREATE_CHOICE_QUERY");
        var getStudyguideQuery = properties.getProperty("GET_STUDYGUIDE_QUERY");
        var deleteStudyguideQuery = properties.getProperty("DELETE_STUDYGUIDE_QUERY");

        this.getUsernameIsTaken = this.dbConnection.prepareStatement(usernameExistsQuery);
        this.getAccountWithCredentials = this.dbConnection.prepareStatement(correctCredentialsQuery);
        this.createAccount = this.dbConnection.prepareStatement(createAccountQuery);
        this.createStudyguide = this.dbConnection.prepareStatement(createStudyguideQuery);
        this.createQuestion = this.dbConnection.prepareStatement(createQuestionQuery);
        this.createChoice = this.dbConnection.prepareStatement(createChoiceQuery);
        this.getStudyguide = this.dbConnection.prepareStatement(getStudyguideQuery);
        this.deleteStudyguide = this.dbConnection.prepareStatement(deleteStudyguideQuery);
    }

    /**
     * Sets up the database connection
     * 
     * @throws SQLException if a database error occurs
     */
    protected abstract void setupDatabase() throws SQLException;

    /**
     * {@return {@code true} if an account with {@code username} exists,
     * otherwise {@code false}}
     * 
     * @param username The username to look for
     * @throws SQLException
     */
    public final boolean hasAccountWithUsername(String username) throws SQLException {
        this.getUsernameIsTaken.setString(1, username);
        var results = this.getUsernameIsTaken.executeQuery();
        var usernameTaken = results.next();

        return usernameTaken;
    }

    /**
     * {@return {@code true} if an account with {@code username} and
     * {@code password} exists,
     * otherwise {@code false}}
     * 
     * @param username The username to compare against
     * @param password the password to compare against
     * @throws SQLException
     */
    public final boolean hasAccountWithCredentials(String username, String password) throws SQLException {
        this.getAccountWithCredentials.setString(1, username);
        var results = this.getAccountWithCredentials.executeQuery();
        var foundAccountWithCredentials = false;

        while (results.next()) {
            var resultUsername = results.getString("username");
            var resultPassword = results.getString("password");
            var usernameMatches = username.equals(resultUsername);
            var passwordMatches = password.equals(resultPassword);
            if (usernameMatches && passwordMatches) {
                foundAccountWithCredentials = true;
                break;
            }
        }

        return foundAccountWithCredentials;
    }

    /**
     * Attempts to create a new account with {@code username} and {@code password}.
     * 
     * @param username the username to use
     * @param password the password to use
     * 
     * @throws SQLException If a database access error occurs
     */
    public final void attemptCreateAccount(String username, String password) throws SQLException {
        this.createAccount.setString(1, username);
        this.createAccount.setString(2, password);
        this.createAccount.execute();
    }

    /**
     * Attempts to upload {@code studyguide} under the account with {@code username}
     * 
     * @param username   the username of the account to upload the studyguide to
     * @param studyguide the studyguide object to upload
     * @return {@code true} if successful, otherwise {@code false}
     * @throws SQLException If a database error occurs
     */
    public final boolean editStudyguide(String username, StudyGuide studyguide) throws SQLException {
        this.getUsernameIsTaken.setString(1, username);
        var accountResult = this.getUsernameIsTaken.executeQuery();
        var accountId = accountResult.next() ? accountResult.getInt("id") : null;
        accountResult.close();
        if (accountId == null) {
            return false;
        }

        var oldStudyguideId = studyguide.getId();
        var overwriteStudyguideRow = oldStudyguideId != null;

        var newStudyguideId = this.createNewStudyguide(accountId, studyguide);
        if (newStudyguideId == null) {
            return false;
        }

        for (var question : studyguide.getQuestions()) {
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
            this.deleteStudyguide(oldStudyguideId);
        }

        studyguide.setId(newStudyguideId);
        return true;
    }

    private Integer createNewStudyguide(int accountId, DisplayableStudyGuide studyguide) throws SQLException {
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

    private Integer createNewQuestion(int studyguideId, DisplayableQuestion question) throws SQLException {
        var text = question.getQuestion();
        this.createQuestion.setString(1, text);
        this.createQuestion.setInt(2, studyguideId);

        var questionResult = this.createQuestion.executeQuery();
        var questionId = questionResult.next() ? questionResult.getInt("id") : null;
        questionResult.close();

        return questionId;
    }

    public final DisplayableStudyGuide getStudyguide(int guideId) throws SQLException {
        StudyGuide studyguide = null;

        this.getStudyguide.setInt(1, guideId);
        var result = this.getStudyguide.executeQuery();
        var questions = new HashMap<String, Question>();
        while (result.next()) {
            if (studyguide == null) {
                var id = result.getInt("id");
                var title = result.getString("title");
                var description = result.getString("description");
                studyguide = new StudyGuide(id);
                studyguide.setTitle(title);
                studyguide.setDescription(description);
                studyguide.setIsUploaded(true);
            }
            var questionText = result.getString("questionText");
            if (!questions.containsKey(questionText)) {
                var questionObj = new Question(questionText);
                questions.put(questionText, questionObj);
            }
            var choiceText = result.getString("choiceText");
            var choiceIsAnswer = result.getBoolean("choiceIsAnswer");
            var question = questions.get(questionText);

            var oldChoices = question.getChoices();
            oldChoices.add(choiceText);
            if (choiceIsAnswer) {
                var oldAnswers = question.getAnswers();
                oldAnswers.add(choiceText);
                question.setAnswers(oldAnswers);
            }
            if (oldChoices.size() > 1 && question.getQuestionType() != QuestionType.MULTIPLE_CHOICE) {
                question.setQuestionType(QuestionType.MULTIPLE_CHOICE);
            }
        }
        result.close();

        if (studyguide != null) {
            studyguide.setQuestions(questions.values());
        }
        return studyguide;
    }

    public final void deleteStudyguide(int guideId) throws SQLException {
        this.deleteStudyguide.setInt(1, guideId);
        this.deleteStudyguide.executeUpdate();
    }

    private record DbChoice(String text, boolean isAnswer) {
    }
}
