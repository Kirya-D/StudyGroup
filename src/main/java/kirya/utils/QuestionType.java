package kirya.utils;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public enum QuestionType {
    MULTIPLE_CHOICE,
    FREE_RESPONSE;

    private static final Map<String, QuestionType> nameMap = new TreeMap<>();
    
    static {
        nameMap.put("Multiple Choice", MULTIPLE_CHOICE);
        nameMap.put("Free Response", FREE_RESPONSE);
    }

    public static Collection<String> getNames() {
        return nameMap.keySet();
    }
    
    public static QuestionType getTypeFromName(String name) {
        return nameMap.get(name);
    }

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
