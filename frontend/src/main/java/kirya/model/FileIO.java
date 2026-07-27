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

/**
 * Class to handle reading and writing object information to and from file.
 */
public class FileIO {

    private final static String STORAGE_DIRECTORY = Path.of(System.getenv("LOCALAPPDATA"), "StudyGroup").toString();

    /**
     * Serializes the given collection of StudyGuides to a file with the given name.
     * Creates the necessary directories and file if needed.
     * 
     * @param fileName   The name of the file.
     * @param collection The collection of StudyGuides to write to file.
     */
    public static void Write(String fileName, SequencedCollection<StudyGuide> collection) {
        ObjectMapper mapper = new ObjectMapper();
        File file = getFileFromName(fileName);
        mapper.writeValue(file, collection);
    }

    /**
     * Reads from the file with the given name and returns a collection of
     * StudyGuides.
     * 
     * @param fileName The name of the file.
     * @return The deserialized StudyGuides collection
     */
    public static SequencedCollection<StudyGuide> Read(String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        File file = getFileFromName(fileName);
        List<StudyGuide> readValue = new ArrayList<>();

        if (file.length() > 0) {
            readValue = mapper.readValue(file, new TypeReference<List<StudyGuide>>() {
            });
        }

        return readValue;
    }

    private static File getFileFromName(String name) {
        String filePath = Path.of(FileIO.STORAGE_DIRECTORY, name).toString();
        File file = new File(filePath);
        FileIO.verifyFileExists(file);

        return file;
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
