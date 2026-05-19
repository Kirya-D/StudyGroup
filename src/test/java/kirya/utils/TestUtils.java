package kirya.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

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

    @Nested
    public class TestCapitalizeString {
        
        @Test
        public void throwsWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                Utils.capitalizeString(null);
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "    "})
        public void testWhenBlankString(String expected) {
            var actual = Utils.capitalizeString(expected);
            assertEquals(expected, actual);
        }

        @ParameterizedTest
        @CsvSource({
            "goodbye, Goodbye",
            "Tractor, Tractor",
            "fOREVER, FOREVER"
        })
        public void testWhenNonBlankString(String input, String expected) {
            var actual = Utils.capitalizeString(input);

            assertEquals(expected, actual);
        }

        @Test
        public void testWhenFirstCharacterNotaLetter() {
            var expected = " hello";
            var actual = Utils.capitalizeString(expected);

            assertEquals(expected, actual);
        }
    }
}
