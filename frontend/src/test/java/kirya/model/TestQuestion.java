package kirya.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SequencedCollection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import kirya.utils.QuestionType;

public class TestQuestion {

    Question question;

    @BeforeEach
    public void setup() {
        this.question = new Question();
    }

    @Nested
    public class TestConstructor {

        @Test
        public void testParameterless() {
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

        @Test
        public void testValidOneParameter() {
            var expectedQuestion = "Valid Question";
            var parameterlessQuestion = new Question(expectedQuestion);

            var expectedType = QuestionType.FREE_RESPONSE;
            var actualType = parameterlessQuestion.getQuestionType();
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

        @Test
        public void throwsWhenNullParameter() {
            assertThrows(IllegalArgumentException.class, () -> new Question(null));
        }

        @Test
        public void throwsWhenBlankParameter() {
            assertThrows(IllegalArgumentException.class, () -> new Question("   "));
        }
    }

    @Nested
    public class TestSetQuestionType {

        @Test
        public void testWhenSuccessful() {
            var validQuestionType = QuestionType.MULTIPLE_CHOICE;
            question.setQuestionType(validQuestionType);

            var expected = validQuestionType;
            var actual = question.getQuestionType();

            assertEquals(expected, actual);
        }

        @Test
        public void throwsWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                question.setQuestionType(null);
            });
        }
    }

    @Nested
    public class TestSetQuestion {

        @Test
        public void testWhenSuccessful() {
            var validQuestion = "valid Question ?";
            question.setQuestion(validQuestion);

            var expected = validQuestion;
            var actual = question.getQuestion();

            assertEquals(expected, actual);
        }

        @Test
        public void throwsWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                question.setQuestion(null);
            });
        }

        @Test
        public void throwsWhenBlank() {
            assertAll("Blank Strings",
                    () -> assertThrows(IllegalArgumentException.class, () -> {
                        question.setQuestion("");
                    }),
                    () -> assertThrows(IllegalArgumentException.class, () -> {
                        question.setQuestion(" ");
                    }),
                    () -> assertThrows(IllegalArgumentException.class, () -> {
                        question.setQuestion("     ");
                    }));
        }
    }

    @Nested
    public class TestSetChoices {

        Question question;

        @BeforeEach
        public void setup() {
            question = new Question();
        }

        @Test
        public void testWhenSuccessful() {
            SequencedCollection<String> oneChoice = List.of("Choice 1");
            SequencedCollection<String> someChoices = List.of("Choice 1", "Choice 2");

            var question2 = new Question();

            question.setChoices(oneChoice);
            question2.setChoices(someChoices);

            assertAll("Varying collection lengths",
                    () -> {
                        assertEquals(oneChoice, question.getChoices());
                    },
                    () -> {
                        assertEquals(someChoices, question2.getChoices());
                    });
        }

        @Test
        public void throwsWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                question.setChoices(null);
            });
        }

        @Test
        public void throwsWhenEmpty() {
            assertThrows(IllegalArgumentException.class, () -> {
                question.setChoices(Collections.emptyList());
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
                        question.setChoices(onlyBlankChoice);
                    }),
                    () -> assertThrows(IllegalArgumentException.class, () -> {
                        question.setChoices(firstBlankChoice);
                    }),
                    () -> assertThrows(IllegalArgumentException.class, () -> {
                        question.setChoices(middleBlankChoice);
                    }),
                    () -> assertThrows(IllegalArgumentException.class, () -> {
                        question.setChoices(lastBlankChoice);
                    }));
        }
    }

    @Nested
    public class TestSetAnswers {

        @Test
        public void testWhenSuccessful() {
            SequencedCollection<String> oneAnswer = List.of("Answer 1");
            SequencedCollection<String> someAnswers = List.of("Answer 1", "Answer 2");

            var question2 = new Question();

            question.setAnswers(oneAnswer);
            question2.setAnswers(someAnswers);

            assertAll("Varying collection lengths",
                    () -> {
                        assertEquals(oneAnswer, question.getAnswers());
                    },
                    () -> {
                        assertEquals(someAnswers, question2.getAnswers());
                    });
        }

        @Test
        public void throwsWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                question.setAnswers(null);
            });
        }

        @Test
        public void throwsWhenEmpty() {
            assertThrows(IllegalArgumentException.class, () -> {
                question.setAnswers(Collections.emptyList());
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
                        question.setAnswers(onlyBlankChoice);
                    }),
                    () -> assertThrows(IllegalArgumentException.class, () -> {
                        question.setAnswers(firstBlankChoice);
                    }),
                    () -> assertThrows(IllegalArgumentException.class, () -> {
                        question.setAnswers(middleBlankChoice);
                    }),
                    () -> assertThrows(IllegalArgumentException.class, () -> {
                        question.setAnswers(lastBlankChoice);
                    }));
        }
    }

    @Nested
    public class TestEquals {

        @Test
        public void testWhenEqual() {
            var otherQuestion = new Question();
            var actual = question.equals(otherQuestion);

            assertTrue(actual);
        }

        @Test
        public void testWhenNull() {
            var actual = question.equals(null);

            assertFalse(actual);
        }

        @Test
        public void testWhenDifferentQuestionType() {
            var otherQuestion = new Question();
            var value = question.getQuestionType();
            if (value == QuestionType.FREE_RESPONSE) {
                otherQuestion.setQuestionType(QuestionType.MULTIPLE_CHOICE);
            }

            var actual = question.equals(otherQuestion);

            assertFalse(actual);
        }

        @Test
        public void testWhenDifferentQuestionText() {
            var otherQuestion = new Question();
            var value = question.getQuestion();
            otherQuestion.setQuestion(value + " more question?");

            var actual = question.equals(otherQuestion);

            assertFalse(actual);
        }

        @Test
        public void testWhenDifferentChoices() {
            var otherQuestion = new Question();
            var value = new ArrayList<>(question.getChoices());
            value.add("More choice");
            otherQuestion.setChoices(value);

            var actual = question.equals(otherQuestion);

            assertFalse(actual);
        }

        @Test
        public void testWhenDifferentAnswers() {
            var otherQuestion = new Question();
            var value = new ArrayList<>(question.getAnswers());
            value.add("More answer");
            otherQuestion.setAnswers(value);

            var actual = question.equals(otherQuestion);

            assertFalse(actual);
        }
    }
}
