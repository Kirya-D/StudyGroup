package kirya.viewmodel;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import kirya.TestingDatabase;
import kirya.model.AuthDatabase;
import kirya.model.Question;
import kirya.model.StudyGuide;
import kirya.utils.DisplayableQuestion;
import kirya.utils.DisplayableStudyGuide;
import kirya.utils.QuestionType;
import kirya.utils.SessionData;

public class TestHomeViewmodel {

    public AuthDatabase db;
    public HomeViewmodel viewmodel;

    @BeforeEach
    public void setup() throws SQLException, IOException {
        SessionData.logOut();
        this.db = new TestingDatabase();
        this.viewmodel = new HomeViewmodel(db);
    }

    @AfterAll
    public static void teardown() {
        SessionData.logOut();
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
        public void testWhenSuccessful() {
            var expectedType = DisplayableStudyGuide.class;
            var actual = viewmodel.createNewStudyGuide();

            assertInstanceOf(expectedType, actual);
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
            var actualFavorited = this.studyGuide.getIsFavorited();

            assertAll("Member checks",
                    () -> assertEquals(expectedFavorited, actualFavorited),
                    () -> assertTrue(viewmodel.getFavoritedStudyGuidesProperty().contains(this.studyGuide)));
        }

        @Test
        public void testWhenToggleIsFalse() {
            viewmodel.toggleFavoriteStudyGuide(this.studyGuide, false);

            var expectedFavorited = false;
            var actualFavorited = this.studyGuide.getIsFavorited();

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
    public class TestToggleUploadStudyGuide {

        StudyGuide studyGuide;

        @BeforeEach
        public void setup() {
            SessionData.logInAs("testUser");
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

        @AfterAll
        public static void teardown() {
            SessionData.logOut();
        }

        @Test
        public void testFailsWhenNoLoggedInAccount() throws SQLException {
            SessionData.logOut();
            var actualSuccess = viewmodel.toggleUploadStudyGuide(this.studyGuide, true);
            var actualUploaded = this.studyGuide.getIsUploaded();

            assertAll("Member checks",
                    () -> assertFalse(actualSuccess),
                    () -> assertFalse(actualUploaded),
                    () -> assertFalse(viewmodel.getUploadedStudyGuidesProperty().contains(this.studyGuide)));
        }

        @Test
        public void testSucceedsWhenLoggedInAccount() throws SQLException {
            var actualSuccess = viewmodel.toggleUploadStudyGuide(this.studyGuide, true);
            var actualUploaded = this.studyGuide.getIsUploaded();
            var uploadedStudyguide = db.getStudyguideFromId(this.studyGuide.getId()) instanceof StudyGuide concreteSg
                    ? concreteSg
                    : null;

            assertAll("Member checks",
                    () -> assertTrue(actualSuccess),
                    () -> assertTrue(actualUploaded),
                    () -> assertTrue(viewmodel.getUploadedStudyGuidesProperty().contains(this.studyGuide)),
                    () -> assertStudyguideEquals(this.studyGuide, uploadedStudyguide));
        }

        @Test
        public void testWhenStudyguideIsAlreadyUploaded() throws SQLException {
            viewmodel.toggleUploadStudyGuide(this.studyGuide, true);
            var actualSuccess = viewmodel.toggleUploadStudyGuide(this.studyGuide, true);
            var actualUploaded = this.studyGuide.getIsUploaded();

            assertAll("Member checks",
                    () -> assertTrue(actualSuccess),
                    () -> assertTrue(actualUploaded),
                    () -> assertTrue(viewmodel.getUploadedStudyGuidesProperty().contains(this.studyGuide)));
        }

        @Test
        public void testWhenToggleIsFalse() throws SQLException {
            viewmodel.toggleUploadStudyGuide(this.studyGuide, false);

            var expectedUploaded = false;
            var actualUploaded = this.studyGuide.getIsUploaded();

            assertAll("Member checks",
                    () -> assertEquals(expectedUploaded, actualUploaded),
                    () -> assertFalse(viewmodel.getUploadedStudyGuidesProperty().contains(this.studyGuide)));
        }

        @Test
        public void testToggleTrueWhenOtherStudyGuidesAreAlreadyUploaded() throws SQLException {
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
        public void testRemovingUploadedStudyguideWhenOtherStudyguidesAreAlreadyUploaded() throws SQLException {
            viewmodel.toggleUploadStudyGuide(new StudyGuide(), true);
            viewmodel.toggleUploadStudyGuide(new StudyGuide(), true);
            viewmodel.toggleUploadStudyGuide(this.studyGuide, true);

            var actualSuccess = viewmodel.toggleUploadStudyGuide(this.studyGuide, false);
            var uploadedCollection = viewmodel.getUploadedStudyGuidesProperty().get();
            var expectedCount = 2;
            var actualCount = uploadedCollection.size();

            assertAll("collection check",
                    () -> assertTrue(actualSuccess),
                    () -> assertFalse(uploadedCollection.contains(this.studyGuide)),
                    () -> assertEquals(expectedCount, actualCount));
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

        @Test
        public void testWhenNoStudyguidesUploaded() throws SQLException {
            viewmodel.searchForStudyguides();

            var expected = Collections.emptyList();
            var actual = viewmodel.getSearchedStudyGuidesProperty().get();

            assertEquals(expected, actual);
        }

        @Test
        public void testWhenSearchCriteriaDoesNotMatchAnyStudyguide() throws SQLException {
            SessionData.logInAs("testUser");
            var studyguides = new StudyGuide[] { new StudyGuide(), new StudyGuide(), new StudyGuide() };
            var titles = new String[] { "Fruit", "Vegetable", "Meat" };
            var descriptions = new String[] { "Apple", "Broccoli", "Turkey" };

            for (var i = 0; i < studyguides.length; i++) {
                var guide = studyguides[i];
                var title = titles[i];
                var description = descriptions[i];
                guide.setTitle(title);
                guide.setDescription(description);
                guide.setQuestions(List.of(new Question("question")));
                viewmodel.toggleUploadStudyGuide(guide, true);
            }
            viewmodel.getSearchProperty().set("Banana");
            viewmodel.searchForStudyguides();

            var expected = Collections.emptyList();
            var actual = viewmodel.getSearchedStudyGuidesProperty().get();

            assertEquals(expected, actual);
        }

        @Test
        public void testWhenSearchCriteriaMatchesUsername() throws SQLException {
            SessionData.logInAs("testUser");
            var studyguides = new StudyGuide[] { new StudyGuide(), new StudyGuide(), new StudyGuide() };
            var titles = new String[] { "Fruit", "Vegetables", "Meat" };
            var descriptions = new String[] { "Apple", "Broccoli", "Turkey" };

            for (var i = 0; i < studyguides.length; i++) {
                var guide = studyguides[i];
                var title = titles[i];
                var description = descriptions[i];
                guide.setTitle(title);
                guide.setDescription(description);
                var question = new Question("question");
                question.setQuestionType(QuestionType.MULTIPLE_CHOICE);
                question.setChoices(List.of("True", "False"));
                question.setAnswers(List.of("True"));
                guide.setQuestions(List.of(question));
                viewmodel.toggleUploadStudyGuide(guide, true);
            }
            viewmodel.getSearchProperty().set("user");
            viewmodel.searchForStudyguides();

            List<DisplayableStudyGuide> expectedList = List.of(studyguides);
            var actualList = viewmodel.getSearchedStudyGuidesProperty().get();

            assertAll(
                    () -> assertEquals(expectedList.size(), actualList.size()),
                    () -> {
                        var smallerMax = expectedList.size() < actualList.size() ? expectedList.size()
                                : actualList.size();
                        for (var i = 0; i < smallerMax; i++) {
                            var expectedItem = expectedList.get(i);
                            var actualItem = actualList.get(i);
                            assertStudyguideEquals(actualItem, expectedItem);
                        }
                    });
        }

        @Test
        public void testWhenSearchCriteriaMatchesOneTitle() throws SQLException {
            SessionData.logInAs("testUser");
            var studyguides = new StudyGuide[] { new StudyGuide(), new StudyGuide(), new StudyGuide() };
            var titles = new String[] { "Fruit", "Vegetables", "Meat" };
            var descriptions = new String[] { "Apple", "Broccoli", "Turkey" };

            for (var i = 0; i < studyguides.length; i++) {
                var guide = studyguides[i];
                var title = titles[i];
                var description = descriptions[i];
                guide.setTitle(title);
                guide.setDescription(description);
                var question = new Question("question");
                question.setQuestionType(QuestionType.MULTIPLE_CHOICE);
                question.setChoices(List.of("True", "False"));
                question.setAnswers(List.of("True"));
                guide.setQuestions(List.of(question));
                viewmodel.toggleUploadStudyGuide(guide, true);
            }
            viewmodel.getSearchProperty().set("veget");
            viewmodel.searchForStudyguides();

            List<DisplayableStudyGuide> expectedList = List.of(studyguides[1]);
            var actualList = viewmodel.getSearchedStudyGuidesProperty().get();

            assertAll(
                    () -> assertEquals(expectedList.size(), actualList.size()),
                    () -> {
                        var smallerMax = expectedList.size() < actualList.size() ? expectedList.size()
                                : actualList.size();
                        for (var i = 0; i < smallerMax; i++) {
                            var expectedItem = expectedList.get(i);
                            var actualItem = actualList.get(i);
                            assertStudyguideEquals(actualItem, expectedItem);
                        }
                    });
        }

        @Test
        public void testWhenSearchCriteriaMatchesMultipleTitles() throws SQLException {
            SessionData.logInAs("testUser");
            var studyguides = new StudyGuide[] { new StudyGuide(), new StudyGuide(), new StudyGuide() };
            var titles = new String[] { "Calculus 1", "Calculus 2", "Meat" };
            var descriptions = new String[] { "about c1", "about c2", "about meat" };

            for (var i = 0; i < studyguides.length; i++) {
                var guide = studyguides[i];
                var title = titles[i];
                var description = descriptions[i];
                guide.setTitle(title);
                guide.setDescription(description);
                var question = new Question("question");
                question.setQuestionType(QuestionType.MULTIPLE_CHOICE);
                question.setChoices(List.of("True", "False"));
                question.setAnswers(List.of("True"));
                guide.setQuestions(List.of(question));
                viewmodel.toggleUploadStudyGuide(guide, true);
            }
            viewmodel.getSearchProperty().set("calcu");
            viewmodel.searchForStudyguides();

            List<DisplayableStudyGuide> expectedList = List.of(studyguides[0], studyguides[1]);
            var actualList = viewmodel.getSearchedStudyGuidesProperty().get();

            assertAll(
                    () -> assertEquals(expectedList.size(), actualList.size()),
                    () -> {
                        var smallerMax = expectedList.size() < actualList.size() ? expectedList.size()
                                : actualList.size();
                        for (var i = 0; i < smallerMax; i++) {
                            var expectedItem = expectedList.get(i);
                            var actualItem = actualList.get(i);
                            assertStudyguideEquals(actualItem, expectedItem);
                        }
                    });
        }

        @Test
        public void testWhenSearchCriteriaMatchesOneDescription() throws SQLException {
            SessionData.logInAs("testUser");
            var studyguides = new StudyGuide[] { new StudyGuide(), new StudyGuide(), new StudyGuide() };
            var titles = new String[] { "Fruit", "Vegetables", "Meat" };
            var descriptions = new String[] { "Apple", "Broccoli", "Turkey" };

            for (var i = 0; i < studyguides.length; i++) {
                var guide = studyguides[i];
                var title = titles[i];
                var description = descriptions[i];
                guide.setTitle(title);
                guide.setDescription(description);
                var question = new Question("question");
                question.setQuestionType(QuestionType.MULTIPLE_CHOICE);
                question.setChoices(List.of("True", "False"));
                question.setAnswers(List.of("True"));
                guide.setQuestions(List.of(question));
                viewmodel.toggleUploadStudyGuide(guide, true);
            }
            viewmodel.getSearchProperty().set("app");
            viewmodel.searchForStudyguides();

            List<DisplayableStudyGuide> expectedList = List.of(studyguides[0]);
            var actualList = viewmodel.getSearchedStudyGuidesProperty().get();

            assertAll(
                    () -> assertEquals(expectedList.size(), actualList.size()),
                    () -> {
                        var smallerMax = expectedList.size() < actualList.size() ? expectedList.size()
                                : actualList.size();
                        for (var i = 0; i < smallerMax; i++) {
                            var expectedItem = expectedList.get(i);
                            var actualItem = actualList.get(i);
                            assertStudyguideEquals(actualItem, expectedItem);
                        }
                    });
        }

        @Test
        public void testWhenSearchCriteriaMatchesMultipleDescriptions() throws SQLException {
            SessionData.logInAs("testUser");
            var studyguides = new StudyGuide[] { new StudyGuide(), new StudyGuide(), new StudyGuide() };
            var titles = new String[] { "Fruit", "Vegetables", "Meat" };
            var descriptions = new String[] { "Vegan options", "vegan options", "Turkey" };

            for (var i = 0; i < studyguides.length; i++) {
                var guide = studyguides[i];
                var title = titles[i];
                var description = descriptions[i];
                guide.setTitle(title);
                guide.setDescription(description);
                var question = new Question("question");
                question.setQuestionType(QuestionType.MULTIPLE_CHOICE);
                question.setChoices(List.of("True", "False"));
                question.setAnswers(List.of("True"));
                guide.setQuestions(List.of(question));
                viewmodel.toggleUploadStudyGuide(guide, true);
            }
            viewmodel.getSearchProperty().set("vegan");
            viewmodel.searchForStudyguides();

            List<DisplayableStudyGuide> expectedList = List.of(studyguides[0], studyguides[1]);
            var actualList = viewmodel.getSearchedStudyGuidesProperty().get();

            assertAll(
                    () -> assertEquals(expectedList.size(), actualList.size()),
                    () -> {
                        var smallerMax = expectedList.size() < actualList.size() ? expectedList.size()
                                : actualList.size();
                        for (var i = 0; i < smallerMax; i++) {
                            var expectedItem = expectedList.get(i);
                            var actualItem = actualList.get(i);
                            assertStudyguideEquals(actualItem, expectedItem);
                        }
                    });
        }
    }

    private void assertStudyguideEquals(DisplayableStudyGuide obj1, DisplayableStudyGuide obj2) {
        var obj1Questions = new ArrayList<>(obj1.getQuestions());
        var obj2Questions = new ArrayList<>(obj2.getQuestions());
        obj1Questions.sort(Comparator.comparing(DisplayableQuestion::getQuestion));
        obj2Questions.sort(Comparator.comparing(DisplayableQuestion::getQuestion));

        assertAll(
                () -> assertEquals(obj1.getIsFavorited(), obj2.getIsFavorited()),
                () -> assertEquals(obj1.getIsDownloaded(), obj2.getIsDownloaded()),
                () -> assertEquals(obj1.getIsUploaded(), obj2.getIsUploaded()),
                () -> assertEquals(obj1.getTitle(), obj2.getTitle()),
                () -> assertEquals(obj1Questions, obj2Questions));
    }
}
