package kirya.utils;

import java.util.Map;
import java.util.TreeMap;

/**
 * An enum for unique question types.
 */
public enum QuestionType {
    /**
     * A multiple-choice question.
     */
    MULTIPLE_CHOICE,
    /**
     * A free-response question.
     */
    FREE_RESPONSE;

    private static final Map<String, QuestionType> nameMap = new TreeMap<>();
    
    static {
        nameMap.put("Multiple Choice", MULTIPLE_CHOICE);
        nameMap.put("Free Response", FREE_RESPONSE);
    }
    
    /**
     * Gets the enum constant associated with the given name.
     * @param name The name associated with a constant of this enum
     * @return The constant associated with the name
     */
    public static QuestionType getTypeFromName(String name) {
        return nameMap.get(name);
    }

    /**
     * Gets the name associated with the given constant.
     * @param questionType The constant to get the name of
     * @return The name of the given constant
     */
    public static String getNameFromType(QuestionType questionType) {
        String name = "";
        
        for (var entry : nameMap.entrySet()) {
            var typeName = entry.getKey();
            var val = entry.getValue();
            if (val == questionType) {
                name = typeName;
                break;
            }
        }

        return name;
    }
}
