package kirya.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TestQuestionType {

    @Nested
    public class TestGetTypeFromName {

        @ParameterizedTest
        @CsvSource({
            "Multiple Choice, MULTIPLE_CHOICE",
            "Free Response, FREE_RESPONSE"
        })
        public void testValidOutcome(String readableName, String enumName) {
            var actualEnum = QuestionType.valueOf(enumName);
            System.out.println("Testing converting from name to actual: " + readableName + " : " + actualEnum);
            var actualType = QuestionType.getTypeFromName(readableName);

            assertEquals(actualEnum, actualType);
        }
    }

    @Nested
    public class TestGetNameFromType {

        @ParameterizedTest
        @CsvSource({
            "Multiple Choice, MULTIPLE_CHOICE",
            "Free Response, FREE_RESPONSE"
        })
        public void testValidOutcome(String readableName, String enumName) {
            var actualEnum = QuestionType.valueOf(enumName);
            System.out.println("Testing converting from actual to name: " + readableName + " : " + actualEnum);
            var actualName = QuestionType.getNameFromType(actualEnum);

            assertEquals(readableName, actualName);
        }
    }
}
