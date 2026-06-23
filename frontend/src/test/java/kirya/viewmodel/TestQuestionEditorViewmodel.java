package kirya.viewmodel;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import kirya.model.Question;
import kirya.utils.AnswerChoice;
import kirya.utils.QuestionType;

public class TestQuestionEditorViewmodel {

    QuestionEditorViewmodel viewmodel;

    @BeforeEach
    public void setup() {
        this.viewmodel = new QuestionEditorViewmodel();
    }

    @Nested
    public class TestConstructor {

        @Test
        public void testParameterlessDefaultMemberValues() {
            var expectedQuestionObject = ObjectProperty.class;
            var actualQuestionObject = viewmodel.getQuestionObjectProperty();
            var expectedQuestionType = ObjectProperty.class;
            var actualQuestionType = viewmodel.getQuestionTypeProperty();
            var expectedQuestion = StringProperty.class;
            var actualQuestion = viewmodel.getQuestionProperty();
            var expectedAnswer = StringProperty.class;
            var actualAnswer = viewmodel.getAnswerProperty();
            var expectedMultChoices = ObservableList.class;
            var actualMultChoices = viewmodel.getMultChoiceOptionsObservableList();

            assertAll("Members",
                    () -> assertInstanceOf(expectedQuestionObject, actualQuestionObject),
                    () -> assertInstanceOf(expectedQuestionType, actualQuestionType),
                    () -> assertInstanceOf(expectedQuestion, actualQuestion),
                    () -> assertInstanceOf(expectedAnswer, actualAnswer),
                    () -> assertInstanceOf(expectedMultChoices, actualMultChoices));
        }
    }

    @Nested
    public class TestSyncPropertiesToQuestionState {

        @Test
        public void testWhenSomeAnswersNotChoices() {
            var questionObj = new Question();
            questionObj.setChoices(List.of("Choice 1", "Choice 2", "Choice 3"));
            questionObj.setAnswers(List.of("Choice 4"));
            viewmodel.getQuestionObjectProperty().set(questionObj);

            var expectedQuestionType = questionObj.getQuestionType();
            var expectedQuestion = questionObj.getQuestion();
            var expectedAnswer = String.join("", questionObj.getAnswers());
            var expectedChoices = Stream.concat(questionObj.getChoices().stream(), questionObj.getAnswers().stream())
                    .toList();
            var expectedMultChoiceAnswers = questionObj.getAnswers();

            viewmodel.syncPropertiesToQuestionState();

            var actualQuestionType = viewmodel.getQuestionTypeProperty().get();
            var actualQuestion = viewmodel.getQuestionProperty().get();
            var actualAnswer = viewmodel.getAnswerProperty().get();
            var actualChoices = viewmodel.getMultChoiceOptionsObservableList().stream().map(o -> o.getText()).toList();
            var actualMultChoiceAnswers = viewmodel.getMultChoiceOptionsObservableList().stream()
                    .filter(o -> o.getIsCorrect()).map(o -> o.getText()).toList();

            assertAll("Member checks",
                    () -> assertEquals(expectedQuestionType, actualQuestionType),
                    () -> assertEquals(expectedQuestion, actualQuestion),
                    () -> assertEquals(expectedAnswer, actualAnswer),
                    () -> assertEquals(expectedChoices, actualChoices),
                    () -> assertEquals(expectedMultChoiceAnswers, actualMultChoiceAnswers));
        }

        @Test
        public void testWhenQuestionHasChoices() {
            var questionObj = new Question();
            questionObj.setChoices(List.of("Choice 1", "Choice 2", "Choice 3"));
            questionObj.setAnswers(List.of("Choice 1"));
            viewmodel.getQuestionObjectProperty().set(questionObj);

            var expectedQuestionType = questionObj.getQuestionType();
            var expectedQuestion = questionObj.getQuestion();
            var expectedAnswer = String.join("", questionObj.getAnswers());
            var expectedChoices = questionObj.getChoices();
            var expectedMultChoiceAnswers = questionObj.getAnswers();

            viewmodel.syncPropertiesToQuestionState();

            var actualQuestionType = viewmodel.getQuestionTypeProperty().get();
            var actualQuestion = viewmodel.getQuestionProperty().get();
            var actualAnswer = viewmodel.getAnswerProperty().get();
            var actualChoices = viewmodel.getMultChoiceOptionsObservableList().stream().map(o -> o.getText()).toList();
            var actualMultChoiceAnswers = viewmodel.getMultChoiceOptionsObservableList().stream()
                    .filter(o -> o.getIsCorrect()).map(o -> o.getText()).toList();

            assertAll("Member checks",
                    () -> assertEquals(expectedQuestionType, actualQuestionType),
                    () -> assertEquals(expectedQuestion, actualQuestion),
                    () -> assertEquals(expectedAnswer, actualAnswer),
                    () -> assertEquals(expectedChoices, actualChoices),
                    () -> assertEquals(expectedMultChoiceAnswers, actualMultChoiceAnswers));
        }

        @Test
        public void testWhenQuestionHasNoChoices() {
            var questionObj = new Question();
            viewmodel.getQuestionObjectProperty().set(questionObj);

            var expectedQuestionType = questionObj.getQuestionType();
            var expectedQuestion = questionObj.getQuestion();
            var expectedAnswer = String.join("", questionObj.getAnswers());
            var expectedChoices = questionObj.getChoices();
            var expectedMultChoiceAnswers = questionObj.getAnswers();

            viewmodel.syncPropertiesToQuestionState();

            var actualQuestionType = viewmodel.getQuestionTypeProperty().get();
            var actualQuestion = viewmodel.getQuestionProperty().get();
            var actualAnswer = viewmodel.getAnswerProperty().get();
            var actualChoices = viewmodel.getMultChoiceOptionsObservableList().stream().map(o -> o.getText()).toList();
            var actualMultChoiceAnswers = viewmodel.getMultChoiceOptionsObservableList().stream()
                    .filter(o -> o.getIsCorrect()).map(o -> o.getText()).toList();

            assertAll("Member checks",
                    () -> assertEquals(expectedQuestionType, actualQuestionType),
                    () -> assertEquals(expectedQuestion, actualQuestion),
                    () -> assertEquals(expectedAnswer, actualAnswer),
                    () -> assertEquals(expectedChoices, actualChoices),
                    () -> assertEquals(expectedMultChoiceAnswers, actualMultChoiceAnswers));
        }

        @Test
        public void testWhenQuestionIsNull() {
            var expectedQuestionType = QuestionType.FREE_RESPONSE;
            var expectedQuestion = "";
            var expectedAnswer = "";
            var expectedChoices = Collections.emptyList();
            var expectedMultChoiceAnswers = Collections.emptyList();

            viewmodel.syncPropertiesToQuestionState();

            var actualQuestionType = viewmodel.getQuestionTypeProperty().get();
            var actualQuestion = viewmodel.getQuestionProperty().get();
            var actualAnswer = viewmodel.getAnswerProperty().get();
            var actualChoices = viewmodel.getMultChoiceOptionsObservableList().stream().filter(o -> !o.getIsCorrect())
                    .map(o -> o.getText()).toList();
            var actualMultChoiceAnswers = viewmodel.getMultChoiceOptionsObservableList().stream()
                    .filter(o -> o.getIsCorrect()).map(o -> o.getText()).toList();

            assertAll("Member checks",
                    () -> assertEquals(expectedQuestionType, actualQuestionType),
                    () -> assertEquals(expectedQuestion, actualQuestion),
                    () -> assertEquals(expectedAnswer, actualAnswer),
                    () -> assertEquals(expectedChoices, actualChoices),
                    () -> assertEquals(expectedMultChoiceAnswers, actualMultChoiceAnswers));
        }
    }

    @Nested
    public class TestApplyQuestionChanges {

        @BeforeEach
        public void setup() {
            viewmodel.getQuestionObjectProperty().set(new Question());
        }

        @Test
        public void testWhenSuccessfulFreeResponseQuestion() {
            var freeResponseAnswer = "This is a valid answer";

            var expectedQuestionType = QuestionType.FREE_RESPONSE;
            var expectedQuestion = "This is a valid question";
            var expectedChoices = List.of(freeResponseAnswer);
            var expectedAnswers = List.of(freeResponseAnswer);

            viewmodel.getQuestionTypeProperty().set(expectedQuestionType);
            viewmodel.getQuestionProperty().set(expectedQuestion);
            viewmodel.getAnswerProperty().set(freeResponseAnswer);

            viewmodel.applyQuestionChanges();

            var questionObj = viewmodel.getQuestionObjectProperty().get();
            var actualQuestionType = questionObj.getQuestionType();
            var actualQuestion = questionObj.getQuestion();
            var actualChoices = questionObj.getChoices();
            var actualAnswers = questionObj.getAnswers();

            assertAll("Member checks",
                    () -> assertEquals(expectedQuestionType, actualQuestionType),
                    () -> assertEquals(expectedQuestion, actualQuestion),
                    () -> assertEquals(expectedChoices, actualChoices),
                    () -> assertEquals(expectedAnswers, actualAnswers));
        }

        @Test
        public void testWhenSuccessfulMultipleChoiceQuestion() {
            var expectedQuestionType = QuestionType.MULTIPLE_CHOICE;
            var expectedQuestion = "This is a valid question";
            var expectedChoices = List.of("True", "False");
            var expectedAnswers = List.of("True");

            viewmodel.getQuestionTypeProperty().set(expectedQuestionType);
            viewmodel.getQuestionProperty().set(expectedQuestion);
            var choices = List.of(new AnswerChoice("True", true), new AnswerChoice("False", false));
            viewmodel.getMultChoiceOptionsObservableList().setAll(choices);

            viewmodel.applyQuestionChanges();

            var questionObj = viewmodel.getQuestionObjectProperty().get();
            var actualQuestionType = questionObj.getQuestionType();
            var actualQuestion = questionObj.getQuestion();
            var actualChoices = questionObj.getChoices();
            var actualAnswers = questionObj.getAnswers();

            assertAll("Member checks",
                    () -> assertEquals(expectedQuestionType, actualQuestionType),
                    () -> assertEquals(expectedQuestion, actualQuestion),
                    () -> assertEquals(expectedChoices, actualChoices),
                    () -> assertEquals(expectedAnswers, actualAnswers));
        }

        @Test
        public void throwsWhenQuestionObjectIsNull() {
            viewmodel.getQuestionObjectProperty().set(null);

            assertThrows(NullPointerException.class, () -> {
                viewmodel.applyQuestionChanges();
            });
        }

        @Test
        public void throwsWhenQuestionTypeIsNull() {
            viewmodel.getQuestionTypeProperty().set(null);

            assertThrows(IllegalArgumentException.class, () -> {
                viewmodel.applyQuestionChanges();
            });
        }

        @ParameterizedTest
        @ValueSource(strings = { "", " ", " " })
        public void throwsWhenQuestionIsBlank(String question) {
            viewmodel.getQuestionTypeProperty().set(QuestionType.FREE_RESPONSE);
            viewmodel.getQuestionProperty().set(question);

            assertThrows(IllegalArgumentException.class, () -> {
                viewmodel.applyQuestionChanges();
            });
        }

        @Test
        public void throwsWhenFreeResponseAnswerIsEmpty() {
            viewmodel.getQuestionTypeProperty().set(QuestionType.FREE_RESPONSE);
            viewmodel.getQuestionProperty().set("This is a valid question");
            viewmodel.getAnswerProperty().set("");

            assertThrows(IllegalArgumentException.class, () -> {
                viewmodel.applyQuestionChanges();
            });
        }

        @Test
        public void throwsWhenMultipleChoiceChoicesIsEmpty() {
            viewmodel.getQuestionTypeProperty().set(QuestionType.MULTIPLE_CHOICE);
            viewmodel.getQuestionProperty().set("This is a valid question");
            viewmodel.getMultChoiceOptionsObservableList().clear();

            assertThrows(IllegalArgumentException.class, () -> {
                viewmodel.applyQuestionChanges();
            });
        }

        @Test
        public void throwsWhenMultipleChoiceAnswersIsEmpty() {
            viewmodel.getQuestionTypeProperty().set(QuestionType.MULTIPLE_CHOICE);
            viewmodel.getQuestionProperty().set("This is a valid question");
            var choices = new AnswerChoice[] { new AnswerChoice("valid", false) };
            viewmodel.getMultChoiceOptionsObservableList().setAll(choices);

            assertThrows(IllegalArgumentException.class, () -> {
                viewmodel.applyQuestionChanges();
            });
        }
    }
}
