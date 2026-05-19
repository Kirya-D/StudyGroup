package kirya.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import java.util.SequencedCollection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TestStudyGuide {

    @Nested
    public class TestConstructor {

        @Test
        public void testParameterlessDefaultMemberValues() {
            var parameterlessStudyGuide = new StudyGuide();

            var expectedDownloaded = false;
            var actualDownloaded = parameterlessStudyGuide.getIsDownloaded();
            var expectedFavorited = false;
            var actualFavorited = parameterlessStudyGuide.getIsFavorited();
            var expectedTitle = "";
            var actualTitle = parameterlessStudyGuide.getTitle();
            var expectedDescription = "";
            var actualDescription = parameterlessStudyGuide.getDescription();
            var expectedQuestions = List.of();
            var actualQuestions = parameterlessStudyGuide.getQuestions();

            assertAll("Members",
                    () -> assertEquals(expectedDownloaded, actualDownloaded),
                    () -> assertEquals(expectedFavorited, actualFavorited),
                    () -> assertEquals(expectedTitle, actualTitle),
                    () -> assertEquals(expectedDescription, actualDescription),
                    () -> assertEquals(expectedQuestions, actualQuestions));
        }
    }

    @Nested
    public class TestSetTitle {

        StudyGuide studyGuide;

        @BeforeEach
        public void setup() {
            this.studyGuide = new StudyGuide();
        }

        @Test
        public void testWhenSuccessful() {
            var validTitle = "Valid Title";
            this.studyGuide.setTitle(validTitle);

            var expected = validTitle;
            var actual = this.studyGuide.getTitle();

            assertEquals(expected, actual);
        }

        @Test
        public void throwsWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                this.studyGuide.setTitle(null);
            });
        }

        @Test
        public void throwsWhenBlank() {
            assertAll("Blank Strings",
                    () -> assertThrows(IllegalArgumentException.class, () -> {
                        this.studyGuide.setTitle("");
                    }),
                    () -> assertThrows(IllegalArgumentException.class, () -> {
                        this.studyGuide.setTitle(" ");
                    }),
                    () -> assertThrows(IllegalArgumentException.class, () -> {
                        this.studyGuide.setTitle("     ");
                    }));
        }
    }

    @Nested
    public class TestSetDescription {

        StudyGuide studyGuide;

        @BeforeEach
        public void setup() {
            this.studyGuide = new StudyGuide();
        }

        @Test
        public void testSuccessfulWhenBlank() {
            var validDescription = "";
            this.studyGuide.setDescription(validDescription);

            var expected = validDescription;
            var actual = this.studyGuide.getDescription();

            assertEquals(expected, actual);
        }

        @Test
        public void testSuccessfulWhenNotBlank() {
            var validDescription = "Valid Description";
            this.studyGuide.setDescription(validDescription);

            var expected = validDescription;
            var actual = this.studyGuide.getDescription();

            assertEquals(expected, actual);
        }

        @Test
        public void throwsWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                this.studyGuide.setDescription(null);
            });
        }
    }

    @Nested
    public class TestSetQuestions {

        StudyGuide studyGuide;

        @BeforeEach
        public void setup() {
            this.studyGuide = new StudyGuide();
        }

        @Test
        public void testWhenSuccessful() {
            SequencedCollection<Question> oneQuestion = List.of(new Question());
            SequencedCollection<Question> someQuestions = List.of(new Question(), new Question());

            var studyGuide2 = new StudyGuide();

            this.studyGuide.setQuestions(oneQuestion);
            studyGuide2.setQuestions(someQuestions);

            assertAll("Varying collection lengths",
                    () -> {
                        assertEquals(oneQuestion, this.studyGuide.getQuestions());
                    },
                    () -> {
                        assertEquals(someQuestions, studyGuide2.getQuestions());
                    });
        }

        @Test
        public void throwsWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                this.studyGuide.setQuestions(null);
            });
        }

        @Test
        public void throwsWhenEmpty() {
            assertThrows(IllegalArgumentException.class, () -> {
                this.studyGuide.setQuestions(Collections.emptyList());
            });
        }
    }

    @Nested
    public class TestSetIsDownloaded {

        StudyGuide studyGuide;

        @BeforeEach
        public void setup() {
            this.studyGuide = new StudyGuide();
        }

        @Test
        public void testWhenSuccessful() {
            var downloaded = true;
            this.studyGuide.setIsDownloaded(downloaded);

            var expected = downloaded;
            var actual = this.studyGuide.getIsDownloaded();

            assertEquals(expected, actual);
        }
    }

    @Nested
    public class TestSetIsFavorited {

        StudyGuide studyGuide;

        @BeforeEach
        public void setup() {
            this.studyGuide = new StudyGuide();
        }

        @Test
        public void testWhenSuccessful() {
            var favorited = true;
            this.studyGuide.setIsFavorited(favorited);

            var expected = favorited;
            var actual = this.studyGuide.getIsFavorited();

            assertEquals(expected, actual);
        }
    }

}
