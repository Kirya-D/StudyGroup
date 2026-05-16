package kirya.model;

import java.util.List;
import java.util.SequencedCollection;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import kirya.utils.QuestionType;

public class TestQuestion {

    @Nested
    public class TestConstructor {
        @Test
        public void testParameterlessDefaultMemberValues() {
            var parameterlessQuestion = new Question();

            var expectedType = QuestionType.FREE_RESPONSE;
            var actualType = parameterlessQuestion.getQuestionType();
            var expectedQuestion = "";
            var actualQuestion = parameterlessQuestion.getQuestion();
            var expectedChoices = List.of();
            var actualChoices = parameterlessQuestion.getChoices();
            var expectedAnswers = List.of();
            var actualAnswers = parameterlessQuestion.getAnswers();

            assertAll("Members",
                    () -> assertEquals(expectedType, actualType),
                    () -> assertEquals(expectedQuestion, actualQuestion),
                    () -> assertEquals(expectedChoices, actualChoices),
                    () -> assertEquals(expectedAnswers, actualAnswers));
        }
    }
    
    @Nested
    public class TestSetQuestionType {
        Question question;

        @BeforeEach
        public void setup() {
            this.question = new Question();
        }

        @Test
        public void testWhenSuccessful() {
            var validQuestionType = QuestionType.MULTIPLE_CHOICE;
            this.question.setQuestionType(validQuestionType);

            var expected = validQuestionType;
            var actual = this.question.getQuestionType();

            assertEquals(expected, actual);
        }

        @Test
        public void throwsWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                this.question.setQuestionType(null);
            });
        }
    }

    @Nested
    public class TestSetQuestion {
        Question question;

        @BeforeEach
        public void setup() {
            this.question = new Question();
        }

        @Test
        public void testWhenSuccessful() {
            var validQuestion = "valid Question ?";
            this.question.setQuestion(validQuestion);

            var expected = validQuestion;
            var actual = this.question.getQuestion();

            assertEquals(expected, actual);
        }

        @Test
        public void throwsWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                this.question.setQuestion(null);
            });
        }

        @Test
        public void throwsWhenBlank() {
            assertAll("Blank Strings",
                    () -> assertThrows(IllegalArgumentException.class, () -> {
                        this.question.setQuestion("");
                    }),
                    () -> assertThrows(IllegalArgumentException.class, () -> {
                        this.question.setQuestion(" ");
                    }),
                    () -> assertThrows(IllegalArgumentException.class, () -> {
                        this.question.setQuestion("     ");
                    })
            );
        }
    }

    @Nested
    public class TestSetChoices {
        Question question;

        @BeforeEach
        public void setup() {
            this.question = new Question();
        }

        @Test
        public void testWhenSuccessful() {
            SequencedCollection<String> emptyChoices = List.of();
            SequencedCollection<String> oneChoice = List.of("Choice 1");
            SequencedCollection<String> someChoices = List.of("Choice 1", "Choice 2");

            var question2 = new Question();
            var question3 = new Question();

            this.question.setChoices(emptyChoices);
            question2.setChoices(oneChoice);
            question3.setChoices(someChoices);

            assertAll("Varying collection lengths",
                    () -> {assertEquals(emptyChoices, this.question.getChoices());},
                    () -> {assertEquals(oneChoice, question2.getChoices());},
                    () -> {assertEquals(someChoices, question3.getChoices());}
            );
        }

        @Test
        public void throwsWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                this.question.setChoices(null);
            });
        }
    }

    @Nested
    public class TestSetAnswers {
        Question question;

        @BeforeEach
        public void setup() {
            this.question = new Question();
        }

        @Test
        public void testWhenSuccessful() {
            SequencedCollection<String> emptyAnswers = List.of();
            SequencedCollection<String> oneAnswer = List.of("Answer 1");
            SequencedCollection<String> someAnswers = List.of("Answer 1", "Answer 2");

            var question2 = new Question();
            var question3 = new Question();

            this.question.setAnswers(emptyAnswers);
            question2.setAnswers(oneAnswer);
            question3.setAnswers(someAnswers);

            assertAll("Varying collection lengths",
                    () -> {assertEquals(emptyAnswers, this.question.getAnswers());},
                    () -> {assertEquals(oneAnswer, question2.getAnswers());},
                    () -> {assertEquals(someAnswers, question3.getAnswers());}
            );
        }

        @Test
        public void throwsWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                this.question.setAnswers(null);
            });
        }
    }

}
