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
                    () -> assertEquals(expectedAnswers, actualAnswers)
            );
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
            SequencedCollection<String> oneChoice = List.of("Choice 1");
            SequencedCollection<String> someChoices = List.of("Choice 1", "Choice 2");

            var question2 = new Question();

            this.question.setChoices(oneChoice);
            question2.setChoices(someChoices);

            assertAll("Varying collection lengths",
                    () -> {
                        assertEquals(oneChoice, this.question.getChoices());
                    },
                    () -> {
                        assertEquals(someChoices, question2.getChoices());
                    }
            );
        }

        @Test
        public void throwsWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                this.question.setChoices(null);
            });
        }

        @Test
        public void throwsWhenEmpty() {
            assertThrows(IllegalArgumentException.class, () -> {
                this.question.setChoices(Collections.emptyList());
            });
        }

        @Test
        public void throwsWhenBlankChoiceInChoices() {
            var onlyBlankChoice = List.of("");
            var firstBlankChoice = List.of("", "Valid middle choice", "Valid last choice");
            var middleBlankChoice = List.of("Valid first choice", "", "Valid last choice");
            var lastBlankChoice = List.of("Valid first choice", "Valid middle choice", "");

            assertAll("Blank choice variations",
                () -> assertThrows(IllegalArgumentException.class, () -> {
                    this.question.setChoices(onlyBlankChoice);
                }),
                () -> assertThrows(IllegalArgumentException.class, () -> {
                    this.question.setChoices(firstBlankChoice);
                }),
                () -> assertThrows(IllegalArgumentException.class, () -> {
                    this.question.setChoices(middleBlankChoice);
                }),
                () -> assertThrows(IllegalArgumentException.class, () -> {
                    this.question.setChoices(lastBlankChoice);
                })
            );
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
            SequencedCollection<String> oneAnswer = List.of("Answer 1");
            SequencedCollection<String> someAnswers = List.of("Answer 1", "Answer 2");

            var question2 = new Question();

            this.question.setAnswers(oneAnswer);
            question2.setAnswers(someAnswers);

            assertAll("Varying collection lengths",
                    () -> {
                        assertEquals(oneAnswer, this.question.getAnswers());
                    },
                    () -> {
                        assertEquals(someAnswers, question2.getAnswers());
                    }
            );
        }

        @Test
        public void throwsWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                this.question.setAnswers(null);
            });
        }

        @Test
        public void throwsWhenEmpty() {
            assertThrows(IllegalArgumentException.class, () -> {
                this.question.setAnswers(Collections.emptyList());
            });
        }

        @Test
        public void throwsWhenBlankAnswerInAnswers() {
            var onlyBlankChoice = List.of("");
            var firstBlankChoice = List.of("", "Valid middle choice", "Valid last choice");
            var middleBlankChoice = List.of("Valid first choice", "", "Valid last choice");
            var lastBlankChoice = List.of("Valid first choice", "Valid middle choice", "");

            assertAll("Blank choice variations",
                () -> assertThrows(IllegalArgumentException.class, () -> {
                    this.question.setAnswers(onlyBlankChoice);
                }),
                () -> assertThrows(IllegalArgumentException.class, () -> {
                    this.question.setAnswers(firstBlankChoice);
                }),
                () -> assertThrows(IllegalArgumentException.class, () -> {
                    this.question.setAnswers(middleBlankChoice);
                }),
                () -> assertThrows(IllegalArgumentException.class, () -> {
                    this.question.setAnswers(lastBlankChoice);
                })
            );
        }
    }
}
