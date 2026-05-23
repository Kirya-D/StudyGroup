package kirya.utils;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;

public class TestAnswerChoice {

    @Nested
    public class TestConstructor {
        @Test
        public void testExpected() {
            var expectedTextPropertyType = StringProperty.class;
            var expectedCorrectnessPropertyType = BooleanProperty.class;
            var expectedText = "Standard text";
            var expectedCorrectness = true;

            var newAnswerChoice = new AnswerChoice(expectedText, expectedCorrectness);

            assertAll("Member checks",
                    () -> assertInstanceOf(expectedTextPropertyType, newAnswerChoice.textProperty()),
                    () -> assertEquals(expectedText, newAnswerChoice.getText()),
                    () -> assertInstanceOf(expectedCorrectnessPropertyType, newAnswerChoice.isCorrectProperty()),
                    () -> assertEquals(expectedCorrectness, newAnswerChoice.getIsCorrect()));
        }
    }

    @Nested
    public class TestSetText {
        @Test
        public void testExpected() {
            var newAnswerChoice = new AnswerChoice("default", false);

            var expectedText = "Not default!";

            newAnswerChoice.setText(expectedText);

            var actualText = newAnswerChoice.getText();

            assertEquals(expectedText, actualText);
        }
    }

    @Nested
    public class TestSetIsCorrect {
        @Test
        public void testExpected() {
            var newAnswerChoice = new AnswerChoice("default", false);

            var expectedCorrectness = true;

            newAnswerChoice.setIsCorrect(expectedCorrectness);

            var actualCorrectness = newAnswerChoice.getIsCorrect();

            assertEquals(expectedCorrectness, actualCorrectness);
        }
    }
}
