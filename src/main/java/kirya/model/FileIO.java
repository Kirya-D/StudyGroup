package kirya.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.SequencedCollection;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

public class FileIO {

    private final static String STORAGE_DIRECTORY = Path.of(System.getenv("LOCALAPPDATA"), "StudyGroup").toString();
    private final static String DATA_PATH = Path.of(FileIO.STORAGE_DIRECTORY, "studyguides.json").toString();
    private final static String CUSTOMIZATION_PATH = Path.of(FileIO.STORAGE_DIRECTORY, "preferences.json").toString();
    private final static File DATA_FILE = new File(FileIO.DATA_PATH);
    private final static File CUSTOMIZATION_FILE = new File(FileIO.CUSTOMIZATION_PATH);

    public static void Write(SequencedCollection<StudyGuide> collection) {
        ObjectMapper mapper = new ObjectMapper();
        FileIO.verifyFileExists(FileIO.DATA_FILE);
        mapper.writeValue(FileIO.DATA_FILE, collection);
    }

    public static SequencedCollection<StudyGuide> Read() {
        ObjectMapper mapper = new ObjectMapper();
        FileIO.verifyFileExists(FileIO.DATA_FILE);
        List<StudyGuide> readValue = new ArrayList<>();

        if (FileIO.DATA_FILE.length() > 0) {
            readValue = mapper.readValue(FileIO.DATA_FILE, new TypeReference<List<StudyGuide>>() {});
        }
        
        return readValue;
    }
    
    private static void verifyFileExists(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            var parentPath = Path.of(file.getParent());
            createDirectories(parentPath);
        }
    }

    private static void createDirectories(Path dir) {
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
