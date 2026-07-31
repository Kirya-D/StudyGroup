package kirya.viewmodel;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import kirya.model.Question;
import kirya.model.ServerConnection;
import kirya.model.StudyGuide;
import kirya.utils.DisplayableStudyGuide;
import kirya.utils.QuestionType;
import kirya.utils.SessionData;

public class TestHomeViewmodel {

    public MockServer mockServer;
    public HomeViewmodel viewmodel;

    @BeforeEach
    public void setup() throws IOException, InterruptedException {
        this.mockServer = new MockServer();
        this.viewmodel = new HomeViewmodel(this.mockServer);
    }

    @Nested
    public class TestConstructor {

        @Test
        public void testParameterlessDefaultMemberValues() {
            var expectedFavoritedType = ListProperty.class;
            var actualFavoritedProperty = viewmodel.getFavoritedStudyGuidesProperty();
            var expectedDownloadedType = ListProperty.class;
            var actualDownloadedProperty = viewmodel.getDownloadedStudyGuidesProperty();
            var expectedUploadedType = ListProperty.class;
            var actualUploadedProperty = viewmodel.getUploadedStudyGuidesProperty();
            var expectedSearchType = StringProperty.class;
            var actualSearchProperty = viewmodel.getSearchProperty();
            var expectedSearchResultsType = ListProperty.class;
            var actualSearchResultsProperty = viewmodel.getSearchedStudyGuidesProperty();

            assertAll("Members",
                    () -> assertInstanceOf(expectedDownloadedType, actualDownloadedProperty),
                    () -> assertInstanceOf(expectedFavoritedType, actualFavoritedProperty),
                    () -> assertInstanceOf(expectedUploadedType, actualUploadedProperty),
                    () -> assertInstanceOf(expectedSearchType, actualSearchProperty),
                    () -> assertInstanceOf(expectedSearchResultsType, actualSearchResultsProperty));
        }
    }

    @Nested
    public class TestCreateNewStudyGuide {

        @Test
        public void testWhenUserIsGuest() throws IOException, InterruptedException {
            SessionData.continueAsGuest();

            var expectedType = DisplayableStudyGuide.class;
            var actualGuide = viewmodel.createNewStudyGuide();

            assertInstanceOf(expectedType, actualGuide);

            SessionData.logOut();
        }

        @Test
        public void testWhenUserIsNotNull() throws IOException, InterruptedException {
            SessionData.logInAs("Non-null");

            var expectedType = DisplayableStudyGuide.class;
            var actualGuide = viewmodel.createNewStudyGuide();

            assertInstanceOf(expectedType, actualGuide);

            SessionData.logOut();
        }
    }

    @Nested
    public class TestSaveChangesToStudyGuide {

        @Test
        public void testWhenNull() {
            viewmodel.saveChangesToStudyGuide(null);

            var expectedFavorited = List.of();
            var actualFavorited = viewmodel.getFavoritedStudyGuidesProperty();
            var expectedDownloaded = List.of();
            var actualDownloaded = viewmodel.getDownloadedStudyGuidesProperty();

            assertAll("Lists",
                    () -> assertEquals(expectedFavorited, actualFavorited),
                    () -> assertEquals(expectedDownloaded, actualDownloaded));
        }

        @Test
        public void testWhenFavoritedAndDownloaded() {
            var guide = new StudyGuide();

            viewmodel.toggleFavoriteStudyGuide(guide, true);
            viewmodel.toggleDownloadStudyGuide(guide, true);
            viewmodel.saveChangesToStudyGuide(guide);

            var expectedFavorited = List.of(guide);
            var actualFavorited = viewmodel.getFavoritedStudyGuidesProperty();
            var expectedDownloaded = List.of(guide);
            var actualDownloaded = viewmodel.getDownloadedStudyGuidesProperty();

            assertAll("Lists",
                    () -> assertEquals(expectedFavorited, actualFavorited),
                    () -> assertEquals(expectedDownloaded, actualDownloaded));
        }

        @Test
        public void testWhenFavoritedAndNotDownloaded() {
            var guide = new StudyGuide();

            viewmodel.toggleFavoriteStudyGuide(guide, true);
            viewmodel.toggleDownloadStudyGuide(guide, false);
            viewmodel.saveChangesToStudyGuide(guide);

            var expectedFavorited = List.of(guide);
            var actualFavorited = viewmodel.getFavoritedStudyGuidesProperty();
            var expectedDownloaded = List.of();
            var actualDownloaded = viewmodel.getDownloadedStudyGuidesProperty();

            assertAll("Lists",
                    () -> assertEquals(expectedFavorited, actualFavorited),
                    () -> assertEquals(expectedDownloaded, actualDownloaded));
        }

        @Test
        public void testWhenNotFavoritedAndDownloaded() {
            var guide = new StudyGuide();

            viewmodel.toggleFavoriteStudyGuide(guide, false);
            viewmodel.toggleDownloadStudyGuide(guide, true);
            viewmodel.saveChangesToStudyGuide(guide);

            var expectedFavorited = List.of();
            var actualFavorited = viewmodel.getFavoritedStudyGuidesProperty();
            var expectedDownloaded = List.of(guide);
            var actualDownloaded = viewmodel.getDownloadedStudyGuidesProperty();

            assertAll("Lists",
                    () -> assertEquals(expectedFavorited, actualFavorited),
                    () -> assertEquals(expectedDownloaded, actualDownloaded));
        }

        @Test
        public void testWhenNotFavoritedAndNotDownloaded() {
            var guide = new StudyGuide();

            viewmodel.toggleFavoriteStudyGuide(guide, false);
            viewmodel.toggleDownloadStudyGuide(guide, false);
            viewmodel.saveChangesToStudyGuide(guide);

            var expectedFavorited = List.of();
            var actualFavorited = viewmodel.getFavoritedStudyGuidesProperty();
            var expectedDownloaded = List.of(guide);
            var actualDownloaded = viewmodel.getDownloadedStudyGuidesProperty();

            assertAll("Lists",
                    () -> assertEquals(expectedFavorited, actualFavorited),
                    () -> assertEquals(expectedDownloaded, actualDownloaded));
        }
    }

    @Nested
    public class TestToggleDownloadStudyGuide {

        StudyGuide studyGuide;

        @BeforeEach
        public void setup() {
            this.studyGuide = new StudyGuide();
        }

        @Test
        public void testWhenToggleIsTrue() {
            viewmodel.toggleDownloadStudyGuide(this.studyGuide, true);

            var downloadedCollection = viewmodel.getDownloadedStudyGuidesProperty().get();
            var expectedCount = 1;
            var actualCount = downloadedCollection.size();

            assertAll("collection check",
                    () -> {
                        assertTrue(downloadedCollection.contains(this.studyGuide));
                    },
                    () -> {
                        assertEquals(expectedCount, actualCount);
                    });
        }

        @Test
        public void testWhenToggleIsFalse() {
            viewmodel.toggleDownloadStudyGuide(this.studyGuide, false);

            var downloadedCollection = viewmodel.getDownloadedStudyGuidesProperty().get();
            var expectedCount = 0;
            var actualCount = downloadedCollection.size();

            assertAll("collection check",
                    () -> {
                        assertFalse(downloadedCollection.contains(this.studyGuide));
                    },
                    () -> {
                        assertEquals(expectedCount, actualCount);
                    });
        }

        @Test
        public void testToggleTrueWhenOtherStudyGuidesAreAlreadyDownloaded() {
            viewmodel.toggleDownloadStudyGuide(new StudyGuide(), true);
            viewmodel.toggleDownloadStudyGuide(new StudyGuide(), true);
            viewmodel.toggleDownloadStudyGuide(this.studyGuide, true);

            var downloadedCollection = viewmodel.getDownloadedStudyGuidesProperty().get();
            var expectedCount = 3;
            var actualCount = downloadedCollection.size();

            assertAll("collection check",
                    () -> {
                        assertTrue(downloadedCollection.contains(this.studyGuide));
                    },
                    () -> {
                        assertEquals(expectedCount, actualCount);
                    });
        }

        @Test
        public void testToggleFalseWhenOtherStudyGuidesAreAlreadyDownloaded() {
            viewmodel.toggleDownloadStudyGuide(new StudyGuide(), true);
            viewmodel.toggleDownloadStudyGuide(new StudyGuide(), true);
            viewmodel.toggleDownloadStudyGuide(this.studyGuide, true);
            viewmodel.toggleDownloadStudyGuide(this.studyGuide, false);

            var downloadedCollection = viewmodel.getDownloadedStudyGuidesProperty().get();
            var expectedCount = 2;
            var actualCount = downloadedCollection.size();

            assertAll("collection check",
                    () -> {
                        assertFalse(downloadedCollection.contains(this.studyGuide));
                    },
                    () -> {
                        assertEquals(expectedCount, actualCount);
                    });
        }

        @Test
        public void throwsWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                viewmodel.toggleDownloadStudyGuide(null, true);
            });
        }
    }

    @Nested
    public class TestToggleFavoriteStudyGuide {

        StudyGuide studyGuide;

        @BeforeEach
        public void setup() {
            this.studyGuide = new StudyGuide();
        }

        @Test
        public void testWhenToggleIsTrue() {
            viewmodel.toggleFavoriteStudyGuide(this.studyGuide, true);

            var expectedFavorited = true;
            var actualFavorited = this.studyGuide.getFavorited();

            assertAll("Member checks",
                    () -> assertEquals(expectedFavorited, actualFavorited),
                    () -> assertTrue(viewmodel.getFavoritedStudyGuidesProperty().contains(this.studyGuide)));
        }

        @Test
        public void testWhenToggleIsFalse() {
            viewmodel.toggleFavoriteStudyGuide(this.studyGuide, false);

            var expectedFavorited = false;
            var actualFavorited = this.studyGuide.getFavorited();

            assertAll("Member checks",
                    () -> assertEquals(expectedFavorited, actualFavorited),
                    () -> assertFalse(viewmodel.getFavoritedStudyGuidesProperty().contains(this.studyGuide)));
        }

        @Test
        public void testToggleTrueWhenOtherStudyGuidesAreAlreadyFavorited() {
            viewmodel.toggleFavoriteStudyGuide(new StudyGuide(), true);
            viewmodel.toggleFavoriteStudyGuide(new StudyGuide(), true);
            viewmodel.toggleFavoriteStudyGuide(this.studyGuide, true);

            var favoritedCollection = viewmodel.getFavoritedStudyGuidesProperty().get();
            var expectedCount = 3;
            var actualCount = favoritedCollection.size();

            assertAll("collection check",
                    () -> assertTrue(favoritedCollection.contains(this.studyGuide)),
                    () -> assertEquals(expectedCount, actualCount));
        }

        @Test
        public void testToggleFalseWhenOtherStudyGuidesAreAlreadyFavorited() {
            viewmodel.toggleFavoriteStudyGuide(new StudyGuide(), true);
            viewmodel.toggleFavoriteStudyGuide(new StudyGuide(), true);
            viewmodel.toggleFavoriteStudyGuide(this.studyGuide, true);
            viewmodel.toggleFavoriteStudyGuide(this.studyGuide, false);

            var favoritedCollection = viewmodel.getFavoritedStudyGuidesProperty().get();
            var expectedCount = 2;
            var actualCount = favoritedCollection.size();

            assertAll("collection check",
                    () -> assertFalse(favoritedCollection.contains(this.studyGuide)),
                    () -> assertEquals(expectedCount, actualCount));
        }

        @Test
        public void throwsWhenStudyGuideIsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                viewmodel.toggleFavoriteStudyGuide(null, true);
            });
        }
    }

    @Nested
    public class TestLogout {

        @Test
        public void testWhenNoLoggedInUser() throws IOException, InterruptedException {
            viewmodel.logOut();

            String loggedUsername = SessionData.getLoggedInUsername();

            assertNull(loggedUsername);
        }

        @Test
        public void testWhenUserIsLoggedIn() throws IOException, InterruptedException {
            SessionData.logInAs("LoggedUsername");
            viewmodel.logOut();

            String loggedUsername = SessionData.getLoggedInUsername();

            assertNull(loggedUsername);
        }
    }

    @Nested
    public class TestToggleUploadStudyGuide {

        private final String testUsername = "testUser";
        private final String testPassword = "testPassword";
        StudyGuide studyGuide;

        @BeforeEach
        public void setup() throws IOException, InterruptedException {
            mockServer.createAccount(testUsername, testPassword);
            mockServer.login(testUsername, testPassword);
            this.studyGuide = new StudyGuide();
            this.studyGuide.setTitle("Test Studyguide");
            this.studyGuide.setDescription("Test description");
            var questions = new ArrayList<Question>();
            var question1 = new Question();
            question1.setQuestion("Is this a free response question?");
            question1.setChoices(List.of("Yes"));
            question1.setAnswers(List.of("Yes"));
            var question2 = new Question();
            question2.setQuestionType(QuestionType.MULTIPLE_CHOICE);
            question2.setQuestion("This a free response question.");
            question2.setChoices(List.of("True", "False"));
            question2.setAnswers(List.of("False"));
            questions.addAll(List.of(question1, question2));
            this.studyGuide.setQuestions(questions);
        }

        @Test
        public void throwsWhenNotLoggedIn() throws IOException, InterruptedException {
            mockServer.logout();
            assertThrows(IOException.class, () -> viewmodel.toggleUploadStudyGuide(this.studyGuide, true));
        }

        @Test
        public void testSucceedsWhenLoggedIn() throws IOException, InterruptedException {
            viewmodel.toggleUploadStudyGuide(this.studyGuide, true);
            var actualUploaded = this.studyGuide.getUploaded();
            var actualUploadedCollection = viewmodel.getUploadedStudyGuidesProperty().get();

            assertAll("Member checks",
                    () -> assertTrue(actualUploaded),
                    () -> assertTrue(actualUploadedCollection.contains(this.studyGuide)));
        }

        @Test
        public void testWhenStudyguideIsAlreadyUploaded() throws IOException, InterruptedException {
            viewmodel.toggleUploadStudyGuide(this.studyGuide, true);
            viewmodel.toggleUploadStudyGuide(this.studyGuide, true);
            var actualUploaded = this.studyGuide.getUploaded();

            assertAll("Member checks",
                    () -> assertTrue(actualUploaded),
                    () -> assertTrue(viewmodel.getUploadedStudyGuidesProperty().contains(this.studyGuide)));
        }

        @Test
        public void testWhenToggleIsFalse() throws IOException, InterruptedException {
            viewmodel.toggleUploadStudyGuide(this.studyGuide, true);
            viewmodel.toggleUploadStudyGuide(this.studyGuide, false);

            var expectedUploaded = false;
            var actualUploaded = this.studyGuide.getUploaded();

            assertAll("Member checks",
                    () -> assertEquals(expectedUploaded, actualUploaded),
                    () -> assertFalse(viewmodel.getUploadedStudyGuidesProperty().contains(this.studyGuide)));
        }

        @Test
        public void testToggleTrueWhenOtherStudyGuidesAreAlreadyUploaded() throws IOException, InterruptedException {
            viewmodel.toggleUploadStudyGuide(new StudyGuide(), true);
            viewmodel.toggleUploadStudyGuide(new StudyGuide(), true);
            viewmodel.toggleUploadStudyGuide(this.studyGuide, true);

            var uploadedCollection = viewmodel.getUploadedStudyGuidesProperty().get();
            var expectedCount = 3;
            var actualCount = uploadedCollection.size();

            assertAll("collection check",
                    () -> {
                        assertTrue(uploadedCollection.contains(this.studyGuide));
                    },
                    () -> {
                        assertEquals(expectedCount, actualCount);
                    });
        }

        @Test
        public void testToggleFalseWhenOtherStudyguidesAreAlreadyUploaded()
                throws IOException, InterruptedException {
            viewmodel.toggleUploadStudyGuide(new StudyGuide(), true);
            viewmodel.toggleUploadStudyGuide(new StudyGuide(), true);
            viewmodel.toggleUploadStudyGuide(this.studyGuide, true);

            viewmodel.toggleUploadStudyGuide(this.studyGuide, false);
            var uploadedCollection = viewmodel.getUploadedStudyGuidesProperty().get();
            var expectedCount = 2;
            var actualCount = uploadedCollection.size();

            assertAll("collection check",
                    () -> assertFalse(uploadedCollection.contains(this.studyGuide)),
                    () -> assertEquals(expectedCount, actualCount));
        }

        @Test
        public void throwsWhenToggleFalseOnStudyGuideThatIsNotYours() throws IOException, InterruptedException {
            viewmodel.toggleUploadStudyGuide(this.studyGuide, true);
            String otherUsername = testUsername + "1";
            String otherPassword = testPassword + "1";
            mockServer.logout();
            mockServer.createAccount(otherUsername, otherPassword);
            mockServer.login(otherUsername, otherPassword);

            assertThrows(IOException.class, () -> viewmodel.toggleUploadStudyGuide(this.studyGuide, false));
        }

        @Test
        public void throwsWhenStudyGuideIsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                viewmodel.toggleUploadStudyGuide(null, true);
            });
        }
    }

    @Nested
    public class TestSearchForStudyguides {

        private final String testUsername = "testUser";
        private final String testPassword = "testPassword";

        @BeforeEach
        public void setup() throws IOException, InterruptedException {
            mockServer.createAccount(testUsername, testPassword);
            mockServer.login(testUsername, testPassword);
        }

        @Test
        public void testWhenNoStudyguidesUploaded() throws IOException, InterruptedException {
            viewmodel.searchForStudyguides();

            var expected = Collections.emptyList();
            var actual = viewmodel.getSearchedStudyGuidesProperty().get();

            assertEquals(expected, actual);
        }

        @Test
        public void testWhenSearchCriteriaDoesNotMatchAnyStudyguide()
                throws SQLException, IOException, InterruptedException {
            var studyguides = Arrays.asList(new StudyGuide(), new StudyGuide(), new StudyGuide());
            var titles = new String[] { "Fruit", "Vegetable", "Meat" };
            var descriptions = new String[] { "Apple", "Broccoli", "Turkey" };
            populateStudyguidesWithFillerInformationAndUploadThem(studyguides, titles, descriptions);

            viewmodel.getSearchProperty().set("Banana");
            viewmodel.searchForStudyguides();

            var expected = Collections.emptyList();
            var actual = viewmodel.getSearchedStudyGuidesProperty().get();

            assertEquals(expected, actual);
        }

        @Test
        public void testWhenSearchCriteriaMatchesOneTitle() throws SQLException, IOException, InterruptedException {
            var studyguides = Arrays.asList(new StudyGuide(), new StudyGuide(), new StudyGuide());
            var titles = new String[] { "Fruit", "Vegetables", "Meat" };
            var descriptions = new String[] { "Apple", "Broccoli", "Turkey" };
            populateStudyguidesWithFillerInformationAndUploadThem(studyguides, titles, descriptions);

            viewmodel.getSearchProperty().set("veget");
            viewmodel.searchForStudyguides();

            var expectedList = List.of(studyguides.get(1));
            var actualList = viewmodel.getSearchedStudyGuidesProperty().get();

            assertAll(
                    () -> assertEquals(expectedList.size(), actualList.size()),
                    () -> assertTrue(expectedList.containsAll(actualList)),
                    () -> assertTrue(actualList.containsAll(expectedList)));
        }

        @Test
        public void testWhenSearchCriteriaMatchesMultipleTitles()
                throws SQLException, IOException, InterruptedException {
            var studyguides = Arrays.asList(new StudyGuide(), new StudyGuide(), new StudyGuide());
            var titles = new String[] { "Calculus 1", "Calculus 2", "Meat" };
            var descriptions = new String[] { "about c1", "about c2", "about meat" };
            populateStudyguidesWithFillerInformationAndUploadThem(studyguides, titles, descriptions);

            viewmodel.getSearchProperty().set("calcu");
            viewmodel.searchForStudyguides();

            var expectedList = List.of(studyguides.get(0), studyguides.get(1));
            var actualList = viewmodel.getSearchedStudyGuidesProperty().get();

            assertAll(
                    () -> assertEquals(expectedList.size(), actualList.size()),
                    () -> assertTrue(expectedList.containsAll(actualList)),
                    () -> assertTrue(actualList.containsAll(expectedList)));
        }

        @Test
        public void testWhenSearchCriteriaMatchesOneDescription()
                throws SQLException, IOException, InterruptedException {
            var studyguides = Arrays.asList(new StudyGuide(), new StudyGuide(), new StudyGuide());
            var titles = new String[] { "Fruit", "Vegetables", "Meat" };
            var descriptions = new String[] { "Apple", "Broccoli", "Turkey" };
            populateStudyguidesWithFillerInformationAndUploadThem(studyguides, titles, descriptions);

            viewmodel.getSearchProperty().set("app");
            viewmodel.searchForStudyguides();

            var expectedList = List.of(studyguides.getFirst());
            var actualList = viewmodel.getSearchedStudyGuidesProperty().get();

            assertAll(
                    () -> assertEquals(expectedList.size(), actualList.size()),
                    () -> assertTrue(expectedList.containsAll(actualList)),
                    () -> assertTrue(actualList.containsAll(expectedList)));
        }

        @Test
        public void testWhenSearchCriteriaMatchesMultipleDescriptions()
                throws SQLException, IOException, InterruptedException {
            var studyguides = Arrays.asList(new StudyGuide(), new StudyGuide(), new StudyGuide());
            var titles = new String[] { "Fruit", "Vegetables", "Meat" };
            var descriptions = new String[] { "Vegan options", "vegan options", "Turkey" };
            populateStudyguidesWithFillerInformationAndUploadThem(studyguides, titles, descriptions);

            viewmodel.getSearchProperty().set("vegan");
            viewmodel.searchForStudyguides();

            var expectedList = List.of(studyguides.get(0), studyguides.get(1));
            var actualList = viewmodel.getSearchedStudyGuidesProperty().get();

            assertAll(
                    () -> assertEquals(expectedList.size(), actualList.size()),
                    () -> assertTrue(expectedList.containsAll(actualList)),
                    () -> assertTrue(actualList.containsAll(expectedList)));
        }

        @Test
        public void testWhenSearchCriteriaMatchesLessThanPageMaximum()
                throws SQLException, IOException, InterruptedException {
            var oneUnderMax = ServerConnection.GUIDES_PER_PAGE - 1;
            var studyguides = new ArrayList<StudyGuide>(Collections.nCopies(oneUnderMax, null));
            studyguides.replaceAll(empty -> new StudyGuide());
            var titles = Collections.nCopies(oneUnderMax, "Title").toArray(String[]::new);
            var descriptions = Collections.nCopies(oneUnderMax, "Description").toArray(String[]::new);
            populateStudyguidesWithFillerInformationAndUploadThem(studyguides, titles, descriptions);

            viewmodel.getSearchProperty().set("Title");
            viewmodel.searchForStudyguides();
            var expectedList = studyguides;
            var actualList = viewmodel.getSearchedStudyGuidesProperty().get();

            assertAll(
                    () -> assertEquals(expectedList.size(), actualList.size()),
                    () -> assertTrue(expectedList.containsAll(actualList)),
                    () -> assertTrue(actualList.containsAll(expectedList)));
        }

        @Test
        public void testWhenSearchCriteriaMatchesPageMaximum() throws SQLException, IOException, InterruptedException {
            var studyguides = new ArrayList<StudyGuide>(Collections.nCopies(ServerConnection.GUIDES_PER_PAGE, null));
            studyguides.replaceAll(empty -> new StudyGuide());
            var titles = Collections.nCopies(ServerConnection.GUIDES_PER_PAGE, "Title").toArray(String[]::new);
            var descriptions = Collections.nCopies(ServerConnection.GUIDES_PER_PAGE, "Description")
                    .toArray(String[]::new);
            populateStudyguidesWithFillerInformationAndUploadThem(studyguides, titles, descriptions);

            viewmodel.getSearchProperty().set("Title");
            viewmodel.searchForStudyguides();
            var expectedList = studyguides;
            var actualList = viewmodel.getSearchedStudyGuidesProperty().get();

            assertAll(
                    () -> assertEquals(expectedList.size(), actualList.size()),
                    () -> assertTrue(expectedList.containsAll(actualList)),
                    () -> assertTrue(actualList.containsAll(expectedList)));
        }

        @Test
        public void testWhenSearchCriteriaMatchesMoreThanPageMaximum()
                throws SQLException, IOException, InterruptedException {
            var oneOverMax = ServerConnection.GUIDES_PER_PAGE + 1;
            var studyguides = new ArrayList<StudyGuide>(Collections.nCopies(oneOverMax, null));
            studyguides.replaceAll(empty -> new StudyGuide());
            var titles = Collections.nCopies(oneOverMax, "Title").toArray(String[]::new);
            var descriptions = Collections.nCopies(oneOverMax, "Description").toArray(String[]::new);
            populateStudyguidesWithFillerInformationAndUploadThem(studyguides, titles, descriptions);

            viewmodel.getSearchProperty().set("Title");
            viewmodel.searchForStudyguides();
            var expectedResultSize = ServerConnection.GUIDES_PER_PAGE;
            var actualList = viewmodel.getSearchedStudyGuidesProperty().get();

            assertAll(
                    () -> assertEquals(expectedResultSize, actualList.size()),
                    () -> assertTrue(studyguides.containsAll(actualList)));
        }
    }

    @Nested
    public class TestAttemptGetMoreResults {

        private final String testUsername = "testUser";
        private final String testPassword = "testPassword";

        @BeforeEach
        public void setup() throws IOException, InterruptedException {
            mockServer.createAccount(testUsername, testPassword);
            mockServer.login(testUsername, testPassword);
        }

        @Test
        public void testWhenNoInitialSearch() throws IOException, InterruptedException {
            viewmodel.getSearchProperty().set("testUser");
            viewmodel.attemptGetMoreResults();

            var expectedList = List.of();
            var actualList = viewmodel.getSearchedStudyGuidesProperty().get();

            assertAll(
                    () -> assertEquals(expectedList.size(), actualList.size()),
                    () -> assertEquals(expectedList, actualList));
        }

        @Test
        public void testWhenTotalResultsLessThanOnePage() throws SQLException, IOException, InterruptedException {
            var belowMax = ServerConnection.GUIDES_PER_PAGE / 2;
            var studyguides = new ArrayList<StudyGuide>(Collections.nCopies(belowMax, null));
            studyguides.replaceAll(empty -> new StudyGuide());
            var titles = Collections.nCopies(belowMax, "Title").toArray(String[]::new);
            var descriptions = Collections.nCopies(belowMax, "Description").toArray(String[]::new);
            populateStudyguidesWithFillerInformationAndUploadThem(studyguides, titles, descriptions);

            viewmodel.getSearchProperty().set("Title");
            viewmodel.searchForStudyguides();
            viewmodel.attemptGetMoreResults();
            var expectedList = studyguides;
            var actualList = viewmodel.getSearchedStudyGuidesProperty().get();

            assertAll(
                    () -> assertEquals(expectedList.size(), actualList.size()),
                    () -> assertTrue(expectedList.containsAll(actualList)),
                    () -> assertTrue(actualList.containsAll(expectedList)));
        }

        @Test
        public void testWhenTotalResultsGreaterThanOnePage() throws SQLException, InterruptedException, IOException {
            var aboveMax = ServerConnection.GUIDES_PER_PAGE * 2;
            var studyguides = new ArrayList<StudyGuide>(Collections.nCopies(aboveMax, null));
            studyguides.replaceAll(empty -> new StudyGuide());
            var titles = Collections.nCopies(aboveMax, "Title").toArray(String[]::new);
            var descriptions = Collections.nCopies(aboveMax, "Description").toArray(String[]::new);
            populateStudyguidesWithFillerInformationAndUploadThem(studyguides, titles, descriptions);

            viewmodel.getSearchProperty().set("Title");
            viewmodel.searchForStudyguides();
            viewmodel.attemptGetMoreResults();
            var expectedList = studyguides;
            var actualList = viewmodel.getSearchedStudyGuidesProperty().get();

            assertAll(
                    () -> assertEquals(expectedList.size(), actualList.size()),
                    () -> assertTrue(expectedList.containsAll(actualList)),
                    () -> assertTrue(actualList.containsAll(expectedList)));
        }
    }

    private void populateStudyguidesWithFillerInformationAndUploadThem(List<StudyGuide> studyguides,
            String[] titles, String[] descriptions) throws IOException, InterruptedException {
        for (var i = 0; i < studyguides.size(); i++) {
            var guide = studyguides.get(i);
            var title = titles[i];
            var description = descriptions[i];
            guide.setTitle(title);
            guide.setDescription(description);
            guide.setFavorited(i % 2 == 0 ? true : false);
            guide.setDownloaded(!guide.getFavorited());
            var question = new Question("question");
            question.setQuestionType(QuestionType.MULTIPLE_CHOICE);
            question.setChoices(List.of("True", "False"));
            question.setAnswers(List.of("True"));
            guide.setQuestions(List.of(question));
            viewmodel.toggleUploadStudyGuide(guide, true);
        }
    }
}
