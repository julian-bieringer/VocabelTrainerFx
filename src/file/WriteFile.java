package file;

import org.apache.pdfbox.pdmodel.PDDocument;
import helper.FilePathHelper;
import java.io.IOException;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.io.File;
import org.codehaus.jackson.map.ObjectMapper;
import model.Lesson;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class WriteFile
{
    private static int maxVocabelsPerPage = 16;

    public static boolean writeJsonFile(final Lesson lesson, final int autoSave) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final File dir = new File(FilePathHelper.getUserDirectory());
        dir.mkdir();
        final String path = String.format("%s/%s", FilePathHelper.getUserDirectory(), lesson.getLessonName());
        final File file = new File(path);

        if (!file.exists() || autoSave == 1) {
            final BufferedWriter bw = Files.newBufferedWriter(Paths.get(path, new String[0]), StandardCharsets.UTF_8, new OpenOption[0]);
            mapper.writeValue(bw, lesson);
            bw.close();
            return true;
        }
        return false;
    }
    
    public static void writeStructuredVocabelFile(String[][] lines, String lessonName, String language) throws IOException, COSVisitorException{
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
            // PDPage.PAGE_SIZE_LETTER is also possible
            PDRectangle rect = page.getMediaBox();
            // rect can be used to get the page width and height
            document.addPage(page);
            final String path;
            PDPageContentStream cos = new PDPageContentStream(document, page);
            
            PDFont fontPlain = PDType1Font.HELVETICA;
            int line = 0;
            // Define a text content stream using the selected font, move the cursor and draw some text
            cos.beginText();
            cos.moveTextPositionByAmount(100, rect.getHeight() - 50*(++line));
            final File dir = new File(FilePathHelper.getUserDirectory());
            dir.mkdir();
            path = String.format("%s/%s", FilePathHelper.getUserDirectory(), lessonName + " - VocabelFile.pdf");
            cos.setFont(fontPlain, 12);
            cos.drawString(language.toLowerCase());
            cos.endText();
            cos.beginText();
            cos.setFont(fontPlain, 12);
            cos.moveTextPositionByAmount(300, rect.getHeight() - 50*(line));
            cos.drawString("german");
            cos.endText();

            for(String[] curr : lines){
                cos.beginText();
                cos.setFont(fontPlain, 12);
                cos.moveTextPositionByAmount(100, rect.getHeight() - 50*(++line));
                cos.drawString(curr[0]);
                cos.endText();
                cos.beginText();
                cos.setFont(fontPlain, 12);
                cos.moveTextPositionByAmount(300, rect.getHeight() - 50*(line));
                cos.drawString(curr[1]);
                cos.endText();

                if(line >= maxVocabelsPerPage){
                    cos.close();
                    page = new PDPage(PDPage.PAGE_SIZE_A4);
                    rect = page.getMediaBox();
                    document.addPage(page);
                    cos = new PDPageContentStream(document, page);
                    line = 0;
                }
            }
            cos.beginText();
            cos.setFont(fontPlain, 12);
            cos.moveTextPositionByAmount(100, rect.getHeight() - 50*(++line));
            cos.drawString("written by");
            cos.endText();
            cos.beginText();
            cos.setFont(fontPlain, 12);
            cos.moveTextPositionByAmount(300, rect.getHeight() - 50*(line));
            cos.drawString(System.getProperty("user.name"));
            cos.endText();
            cos.close();
            document.save(path);
        }
    }
}