package kirya.viewmodel;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

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
            var expectedAnswers = ObservableList.class;
            var actualAnswers = viewmodel.getAnswersObservableList();

            assertAll("Members",
                    () -> assertInstanceOf(expectedQuestionObject, actualQuestionObject),
                    () -> assertInstanceOf(expectedQuestionType, actualQuestionType),
                    () -> assertInstanceOf(expectedQuestion, actualQuestion),
                    () -> assertInstanceOf(expectedAnswer, actualAnswer),
                    () -> assertInstanceOf(expectedAnswers, actualAnswers)
            );
        }
    }
}
