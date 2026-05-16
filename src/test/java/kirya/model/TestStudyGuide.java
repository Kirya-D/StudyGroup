package kirya.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

            var expectedFavorited = false;
            var actualFavorited = parameterlessStudyGuide.getIsFavorited();
            var expectedTitle = "";
            var actualTitle = parameterlessStudyGuide.getTitle();
            var expectedDescription = "";
            var actualDescription = parameterlessStudyGuide.getDescription();
            var expectedQuestions = List.of();
            var actualQuestions = parameterlessStudyGuide.getQuestions();

            assertAll("Members",
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
            SequencedCollection<Question> emptyQuestions = List.of();
            SequencedCollection<Question> oneQuestion = List.of(new Question());
            SequencedCollection<Question> someQuestions = List.of(new Question(), new Question());

            var studyGuide2 = new StudyGuide();
            var studyGuide3 = new StudyGuide();

            this.studyGuide.setQuestions(emptyQuestions);
            studyGuide2.setQuestions(oneQuestion);
            studyGuide3.setQuestions(someQuestions);

            assertAll("Varying collection lengths",
                    () -> {
                        assertEquals(emptyQuestions, this.studyGuide.getQuestions());
                    },
                    () -> {
                        assertEquals(oneQuestion, studyGuide2.getQuestions());
                    },
                    () -> {
                        assertEquals(someQuestions, studyGuide3.getQuestions());
                    }
            );
        }

        @Test
        public void throwsWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                this.studyGuide.setQuestions(null);
            });
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
