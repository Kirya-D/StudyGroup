package kirya.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TestUtils {

    @Nested
    public class TestGetValueOfKeyInEnum {

        @Test
        public void testWhenKeyInEnum() {
            var expectedEnumValue = QuestionType.FREE_RESPONSE;
            var enumName = expectedEnumValue.name();
            var actualEnumValue = Utils.getValueOfKeyInEnum(enumName, QuestionType.class);

            assertEquals(expectedEnumValue, actualEnumValue);
        }

        @Test
        public void testWhenKeyNotInEnum() {
            Object expectedEnumValue = null;
            var enumName = "This name does not exist";
            var actualEnumValue = Utils.getValueOfKeyInEnum(enumName, QuestionType.class);

            assertEquals(expectedEnumValue, actualEnumValue);
        }
    }
}
