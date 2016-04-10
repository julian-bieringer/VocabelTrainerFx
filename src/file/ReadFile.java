package file;

import controller.FilePathHelper;
import java.io.IOException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.JsonParseException;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.io.File;
import org.codehaus.jackson.map.ObjectMapper;
import model.Lesson;

public class ReadFile {

    public static Lesson readJsonFile(final String fileName) throws JsonParseException, JsonMappingException, IOException {
        final ObjectMapper mapper = new ObjectMapper();
        Lesson lesson = new Lesson();
        final String path = String.format("%s/%s", FilePathHelper.getUserDirectory(), fileName);
        final File f = new File(path);
     
        if (f.exists()) {
            final BufferedReader br = Files.newBufferedReader(Paths.get(path, new String[0]), StandardCharsets.UTF_8);
            lesson = mapper.readValue(br, Lesson.class);
            br.close();
        } else {
            System.out.println("File does not exist at " + f.toString());
        }
        return lesson;
    }
}
