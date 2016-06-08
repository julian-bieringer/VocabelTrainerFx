package file;

import helper.FilePathHelper;
import controller.LessonsWindowController;
import java.io.IOException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.JsonParseException;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    public static Lesson[] readAllJsonFiles(){
        List<Lesson> lessons = new ArrayList<>();
        final File folder = new File(FilePathHelper.getUserDirectory());
        final File[] listOfFiles = folder.listFiles();
        File[] array;
        Lesson first = null;
        
        if(listOfFiles != null){
            for (int length = (array = listOfFiles).length, i = 0; i < length; ++i) {
                final File file = array[i];
                if (file.isFile() && !file.getAbsolutePath().contains("VocabelFile")) {
                    try {
                        Lesson lesson = ReadFile.readJsonFile(file.getName());
                        lessons.add(lesson);
                    } catch (JsonMappingException ex) {
                        Logger.getLogger(LessonsWindowController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(LessonsWindowController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        Lesson[] lessonArray = new Lesson[lessons.size()];
        return lessons.toArray(lessonArray);
    }
}
