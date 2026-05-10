package kirya.utils;

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
}
