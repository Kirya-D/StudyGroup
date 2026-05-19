package kirya.viewmodel;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import kirya.model.Question;
import kirya.utils.QuestionType;

public class TestQuestionEditorViewmodel {

    @Nested
    public class TestConstructor {

        @Test
        public void testParameterlessDefaultMemberValues() {
            var viewmodel = new QuestionEditorViewmodel();

            var expectedQuestionObject = ObjectProperty.class;
            var actualQuestionObject = viewmodel.getQuestionObjectProperty();
            var expectedQuestionType = ObjectProperty.class;
            var actualQuestionType = viewmodel.getQuestionTypeProperty();
            var expectedQuestion = StringProperty.class;
            var actualQuestion = viewmodel.getQuestionProperty();
            var expectedAnswer = StringProperty.class;
            var actualAnswer = viewmodel.getAnswerProperty();
            var expectedChoices = ObservableList.class;
            var actualChoices = viewmodel.getChoicesObservableList();
            var expectedAnswers = ObservableList.class;
            var actualAnswers = viewmodel.getAnswersObservableList();

            assertAll("Members",
                    () -> assertInstanceOf(expectedQuestionObject, actualQuestionObject),
                    () -> assertInstanceOf(expectedQuestionType, actualQuestionType),
                    () -> assertInstanceOf(expectedQuestion, actualQuestion),
                    () -> assertInstanceOf(expectedAnswer, actualAnswer),
                    () -> assertInstanceOf(expectedChoices, actualChoices),
                    () -> assertInstanceOf(expectedAnswers, actualAnswers)
            );
        }
    }

    @Nested
    public class TestSyncPropertiesToQuestionState {

        @Test
        public void testWhenQuestionIsNotNull() {
            var questionObj = new Question();
            var viewmodel = new QuestionEditorViewmodel();
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
            var actualChoices = new ArrayList<>(viewmodel.getChoicesObservableList());
            var actualMultChoiceAnswers = new ArrayList<>(viewmodel.getAnswersObservableList());

            assertAll("Member checks",
                    () -> assertEquals(expectedQuestionType, actualQuestionType),
                    () -> assertEquals(expectedQuestion, actualQuestion),
                    () -> assertEquals(expectedAnswer, actualAnswer),
                    () -> assertEquals(expectedChoices, actualChoices),
                    () -> assertEquals(expectedMultChoiceAnswers, actualMultChoiceAnswers)
            );
        }

        @Test
        public void testWhenQuestionIsNull() {
            var viewmodel = new QuestionEditorViewmodel();

            var expectedQuestionType = QuestionType.FREE_RESPONSE;
            var expectedQuestion = "";
            var expectedAnswer = "";
            var expectedChoices = Collections.emptyList();
            var expectedMultChoiceAnswers = Collections.emptyList();

            viewmodel.syncPropertiesToQuestionState();

            var actualQuestionType = viewmodel.getQuestionTypeProperty().get();
            var actualQuestion = viewmodel.getQuestionProperty().get();
            var actualAnswer = viewmodel.getAnswerProperty().get();
            var actualChoices = new ArrayList<>(viewmodel.getChoicesObservableList());
            var actualMultChoiceAnswers = new ArrayList<>(viewmodel.getAnswersObservableList());

            assertAll("Member checks",
                    () -> assertEquals(expectedQuestionType, actualQuestionType),
                    () -> assertEquals(expectedQuestion, actualQuestion),
                    () -> assertEquals(expectedAnswer, actualAnswer),
                    () -> assertEquals(expectedChoices, actualChoices),
                    () -> assertEquals(expectedMultChoiceAnswers, actualMultChoiceAnswers)
            );
        }
    }

    @Nested
    public class TestApplyQuestionChanges {

        QuestionEditorViewmodel viewmodel;

        @BeforeEach
        public void setup() {
            this.viewmodel = new QuestionEditorViewmodel();
            this.viewmodel.getQuestionObjectProperty().set(new Question());
        }

        @Test
        public void testWhenSuccessfulFreeResponseQuestion() {
            var freeResponseAnswer = "This is a valid answer";

            var expectedQuestionType = QuestionType.FREE_RESPONSE;
            var expectedQuestion = "This is a valid question";
            var expectedChoices = List.of(freeResponseAnswer);
            var expectedAnswers = List.of(freeResponseAnswer);

            this.viewmodel.getQuestionTypeProperty().set(expectedQuestionType);
            this.viewmodel.getQuestionProperty().set(expectedQuestion);
            this.viewmodel.getAnswerProperty().set(freeResponseAnswer);

            this.viewmodel.applyQuestionChanges();

            var questionObj = this.viewmodel.getQuestionObjectProperty().get();
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

            this.viewmodel.getQuestionTypeProperty().set(expectedQuestionType);
            this.viewmodel.getQuestionProperty().set(expectedQuestion);
            this.viewmodel.getChoicesObservableList().setAll(expectedChoices);
            this.viewmodel.getAnswersObservableList().setAll(expectedAnswers);

            this.viewmodel.applyQuestionChanges();

            var questionObj = this.viewmodel.getQuestionObjectProperty().get();
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
            this.viewmodel.getQuestionObjectProperty().set(null);

            assertThrows(NullPointerException.class, () -> {
                this.viewmodel.applyQuestionChanges();
            });
        }

        @Test
        public void throwsWhenQuestionTypeIsNull() {
            this.viewmodel.getQuestionTypeProperty().set(null);

            assertThrows(IllegalArgumentException.class, () -> {
                this.viewmodel.applyQuestionChanges();
            });
        }

        @ParameterizedTest
        @ValueSource(strings = { "", " ", "  " })
        public void throwsWhenQuestionIsBlank(String question) {
            this.viewmodel.getQuestionTypeProperty().set(QuestionType.FREE_RESPONSE);
            this.viewmodel.getQuestionProperty().set(question);

            assertThrows(IllegalArgumentException.class, () -> {
                this.viewmodel.applyQuestionChanges();
            });
        }

        @Test
        public void throwsWhenFreeResponseAnswerIsEmpty() {
            this.viewmodel.getQuestionTypeProperty().set(QuestionType.FREE_RESPONSE);
            this.viewmodel.getQuestionProperty().set("This is a valid question");
            this.viewmodel.getAnswerProperty().set("");

            assertThrows(IllegalArgumentException.class, () -> {
                this.viewmodel.applyQuestionChanges();
            });
        }

        @Test
        public void throwsWhenMultipleChoiceChoicesIsEmpty() {
            this.viewmodel.getQuestionTypeProperty().set(QuestionType.MULTIPLE_CHOICE);
            this.viewmodel.getQuestionProperty().set("This is a valid question");
            this.viewmodel.getChoicesObservableList().clear();
            this.viewmodel.getAnswersObservableList().setAll("First answer");

            assertThrows(IllegalArgumentException.class, () -> {
                this.viewmodel.applyQuestionChanges();
            });
        }

        @Test
        public void throwsWhenMultipleChoiceAnswersIsEmpty() {
            this.viewmodel.getQuestionTypeProperty().set(QuestionType.MULTIPLE_CHOICE);
            this.viewmodel.getQuestionProperty().set("This is a valid question");
            this.viewmodel.getAnswersObservableList().clear();
            this.viewmodel.getChoicesObservableList().setAll("First answer");

            assertThrows(IllegalArgumentException.class, () -> {
                this.viewmodel.applyQuestionChanges();
            });
        }
    }
}
