package kirya.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import java.util.SequencedCollection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TestStudyGuide {

    StudyGuide studyguide;

    @BeforeEach
    public void setup() {
        this.studyguide = new StudyGuide();
    }

    @Nested
    public class TestConstructor {

        private void expectedDefaultParameters(StudyGuide studyguide, String expectedId) {
            var actualId = studyguide.getId();
            var expectedDownloaded = false;
            var actualDownloaded = studyguide.getDownloaded();
            var expectedFavorited = false;
            var actualFavorited = studyguide.getFavorited();
            var expectedUploaded = false;
            var actualUploaded = studyguide.getUploaded();
            var expectedUsername = "";
            var actualUsername = studyguide.getCreatorUsername();
            var expectedTitle = "";
            var actualTitle = studyguide.getTitle();
            var expectedDescription = "";
            var actualDescription = studyguide.getDescription();
            var expectedQuestions = List.of();
            var actualQuestions = studyguide.getQuestions();

            assertAll("Members",
                    () -> assertEquals(expectedId, actualId),
                    () -> assertEquals(expectedDownloaded, actualDownloaded),
                    () -> assertEquals(expectedFavorited, actualFavorited),
                    () -> assertEquals(expectedUploaded, actualUploaded),
                    () -> assertEquals(expectedUsername, actualUsername),
                    () -> assertEquals(expectedTitle, actualTitle),
                    () -> assertEquals(expectedDescription, actualDescription),
                    () -> assertEquals(expectedQuestions, actualQuestions));
        }

        @Test
        public void testParameterlessDefaultMemberValues() {
            var parameterlessStudyGuide = new StudyGuide();
            this.expectedDefaultParameters(parameterlessStudyGuide, null);
        }

        @Test
        public void testOneParameterValues() {
            var expectedId = "24";
            var paremeterStudyguide = new StudyGuide(expectedId);
            this.expectedDefaultParameters(paremeterStudyguide, expectedId);
        }
    }

    @Nested
    public class TestSetId {
        @Test
        public void testWhenNotNull() {
            var expected = "25";
            studyguide.setId(expected);

            var actual = studyguide.getId();

            assertEquals(expected, actual);
        }

        @Test
        public void testWhenNull() {
            studyguide.setId(null);

            var actual = studyguide.getId();

            assertNull(actual);
        }
    }

    @Nested
    public class TestSetIsDownloaded {

        @Test
        public void testWhenSuccessful() {
            var downloaded = true;
            studyguide.setDownloaded(downloaded);

            var expected = downloaded;
            var actual = studyguide.getDownloaded();

            assertEquals(expected, actual);
        }
    }

    @Nested
    public class TestSetIsFavorited {

        @Test
        public void testWhenSuccessful() {
            var favorited = true;
            studyguide.setFavorited(favorited);

            var expected = favorited;
            var actual = studyguide.getFavorited();

            assertEquals(expected, actual);
        }
    }

    @Nested
    public class TestSetIsUploaded {

        @Test
        public void testWhenSuccessful() {
            var favorited = true;
            studyguide.setUploaded(favorited);

            var expected = favorited;
            var actual = studyguide.getUploaded();

            assertEquals(expected, actual);
        }
    }

    @Nested
    public class TestSetCreatorUsername {

        @Test
        public void testWhenNotNull() {
            var validUser = "Valid username";
            studyguide.setCreatorUsername(validUser);

            var expected = validUser;
            var actual = studyguide.getCreatorUsername();

            assertEquals(expected, actual);
        }

        @Test
        public void testWhenNull() {
            String validUser = null;
            studyguide.setCreatorUsername(validUser);

            var actual = studyguide.getCreatorUsername();

            assertNull(actual);
        }
    }

    @Nested
    public class TestSetTitle {

        @Test
        public void testWhenSuccessful() {
            var validTitle = "Valid Title";
            studyguide.setTitle(validTitle);

            var expected = validTitle;
            var actual = studyguide.getTitle();

            assertEquals(expected, actual);
        }

        @Test
        public void throwsWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                studyguide.setTitle(null);
            });
        }

        @Test
        public void throwsWhenBlank() {
            assertAll("Blank Strings",
                    () -> assertThrows(IllegalArgumentException.class, () -> {
                        studyguide.setTitle("");
                    }),
                    () -> assertThrows(IllegalArgumentException.class, () -> {
                        studyguide.setTitle(" ");
                    }),
                    () -> assertThrows(IllegalArgumentException.class, () -> {
                        studyguide.setTitle("     ");
                    }));
        }
    }

    @Nested
    public class TestSetDescription {

        @Test
        public void testSuccessfulWhenBlank() {
            var validDescription = "";
            studyguide.setDescription(validDescription);

            var expected = validDescription;
            var actual = studyguide.getDescription();

            assertEquals(expected, actual);
        }

        @Test
        public void testSuccessfulWhenNotBlank() {
            var validDescription = "Valid Description";
            studyguide.setDescription(validDescription);

            var expected = validDescription;
            var actual = studyguide.getDescription();

            assertEquals(expected, actual);
        }

        @Test
        public void throwsWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                studyguide.setDescription(null);
            });
        }
    }

    @Nested
    public class TestSetQuestions {

        @Test
        public void testWhenSuccessful() {
            SequencedCollection<Question> oneQuestion = List.of(new Question());
            SequencedCollection<Question> someQuestions = List.of(new Question(), new Question());

            var studyGuide2 = new StudyGuide();

            studyguide.setQuestions(oneQuestion);
            studyGuide2.setQuestions(someQuestions);

            assertAll("Varying collection lengths",
                    () -> {
                        assertEquals(oneQuestion, studyguide.getQuestions());
                    },
                    () -> {
                        assertEquals(someQuestions, studyGuide2.getQuestions());
                    });
        }

        @Test
        public void throwsWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                studyguide.setQuestions(null);
            });
        }

        @Test
        public void throwsWhenEmpty() {
            assertThrows(IllegalArgumentException.class, () -> {
                studyguide.setQuestions(Collections.emptyList());
            });
        }
    }
}
