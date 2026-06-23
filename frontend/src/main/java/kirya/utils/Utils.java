package kirya.utils;

/**
 * Utility methods.
 */
public class Utils {
    /**
     * Works the same as enum.valueOf but returns null instead of throwing an exception if the key isn't found
     * @param key The value of an enum
     * @return The enum form of the key
     */
    public static <T extends Enum<T>> Enum<T> getValueOfKeyInEnum(String key, Class<T> enumClass) {
        for (var constant : enumClass.getEnumConstants()) {
            if (constant.name().equals(key)) {
                return constant;
            }
        }

        return null;
    }

    /**
     * {@return the given string with the first character capitalized and nothing else changed}
     * @param toCapitalize The string to capitalize
     */
    public static String capitalizeString(String toCapitalize) {
        if (toCapitalize == null) {
            throw new IllegalArgumentException("String can't be null");
        }
        if (toCapitalize.isBlank()) {
            return toCapitalize;
        }

        return toCapitalize.substring(0, 1).toUpperCase() + toCapitalize.substring(1);
    }
}
