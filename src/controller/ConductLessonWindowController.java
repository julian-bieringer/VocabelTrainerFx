/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import file.WriteFile;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.Lesson;
import model.Vocabel;
import vocabelfx.VocabelFX;

/**
 * FXML Controller class
 *
 * @author JBiering
 */
public class ConductLessonWindowController implements Initializable {
    @FXML
    private TableColumn<Vocabel, String> columnForeignWord;
    @FXML
    private TableColumn<Vocabel, String> columnVocabelState;
    @FXML
    private TableColumn<Vocabel, String> columnGermanWord;
    @FXML
    private TableView<Vocabel> tableViewVocabel;
    @FXML
    private TextField textFieldForeignWord;
    @FXML
    private Label labelGermanWord;
    @FXML
    private Label lablePercent;
    @FXML

    private Label lableProgress;
    private Lesson lesson;
    private int index = 0;
    private Scene scene;

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
        this.columnForeignWord.setText(lesson.getLanguage().toLowerCase());
        this.labelGermanWord.setText(lesson.getVocabels().get(index).getGermanWord());
        lableProgress.setText(String.format("Vocabel %s of %s", Integer.toString(index), Integer.toString(lesson.getVocabels().size())));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
       this.columnForeignWord.setCellValueFactory(new PropertyValueFactory("foreignWord"));
       this.columnGermanWord.setCellValueFactory(new PropertyValueFactory("germanWord"));
       this.columnVocabelState.setCellValueFactory(new PropertyValueFactory("anwserStateText"));
       Platform.runLater(() -> {
           textFieldForeignWord.requestFocus();
       });
    }   

    @FXML
    private void textFieldForeignWordEnterPressed(ActionEvent event) {
        if(this.scene == null)
            this.scene =  ((Node)(event.getSource())).getScene();
        addVocabelToTabelView();
        if(index < lesson.getVocabels().size())
            changeToNextVocabel();
        else
            showProperAlert();
    }
    
    private void showProperAlert(){
        lablePercent.setText(String.format("Vocabels Correct: %s%%", Integer.toString((int)(calcCorrectVocabelsInPercent()*100))));
        Alert alert = null;
        if(((int)calcCorrectVocabelsInPercent()) == 1){
            alert = new Alert(Alert.AlertType.INFORMATION, String.format("You had %s%% of the vocabels correct", Integer.toString((int)(calcCorrectVocabelsInPercent()*100))));
            alert.showAndWait();
        }else{
            alert = new Alert(Alert.AlertType.CONFIRMATION, String.format("You had %s%% of the vocabels correct\nWould you like to create a new file with the wrong vocabels?", Integer.toString((int)(calcCorrectVocabelsInPercent()*100))));
            alert.showAndWait();
            if(alert.getResult().equals(ButtonType.OK)){
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
                    String currLessonName = lesson.getLessonName();
                    if(currLessonName.contains("incorrect-vocabels"))
                        currLessonName = currLessonName.replaceAll("\\s+incorrect-vocabels.+", "");
                    String lessonName = String.format("%s incorrect-vocabels %s", currLessonName, sdf.format(new Date()));
                    lesson.setLessonName(lessonName);
                    Lesson tmp = filterWrongVocabels(lesson, lessonName);
                    WriteFile.writeJsonFile(tmp, 0);
                } catch (IOException ex) {
                    Logger.getLogger(ConductLessonWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } 
        }
        loadStartLessonWindow();
    }

    private void changeToNextVocabel(){
            this.labelGermanWord.setText(lesson.getVocabels().get(index).getGermanWord());
            this.textFieldForeignWord.setText("");
            tableViewVocabel.scrollTo(index-1);
            lableProgress.setText(String.format("Vocabel %s of %s", Integer.toString(index+1), Integer.toString(lesson.getVocabels().size())));
            lablePercent.setText(String.format("Vocabels Correct: %s%%", Integer.toString((int)(calcCorrectVocabelsInPercent()*100))));
    }
    
    private void addVocabelToTabelView(){
        String answerStateText = null;
        Vocabel vocabel = lesson.getVocabels().get(index);
        if(vocabel.getForeignWord().equals(textFieldForeignWord.getText().trim())){
            answerStateText = "✓";
            lesson.getVocabels().get(index).setVocabelCorrect(true);
        }
        else{
            answerStateText = String.format("should be: %s", vocabel.getForeignWord());
            lesson.getVocabels().get(index).setVocabelCorrect(false);
        }
        tableViewVocabel.getItems().add(new Vocabel(vocabel.getGermanWord(), this.textFieldForeignWord.getText(), answerStateText));
        index++;
    }
    
    private double calcCorrectVocabelsInPercent(){
        int correctVocabels = 0;
        for(int i = 0; i < index; i++){
            if (lesson.getVocabels().get(i).isVocabelCorrect())
                correctVocabels++;
        }
        return correctVocabels/((double)index);
    }

    private void loadStartLessonWindow() {
        try {
            Parent root;
            root = FXMLLoader.load(getClass().getResource("/view/LessonsWindow.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Lesson Selection");
            stage.setScene(new Scene(root, 750, 267));
            String image = VocabelFX.class.getResource("/images/translation.jpg").toExternalForm();
            root.setStyle("-fx-background-image: url('" + image + "'); " +
           "-fx-background-position: center center; " +
           "-fx-background-color: white; " +
           "-fx-background-repeat: stretch;"); 
            stage.getIcons().add(new Image(VocabelFX.class.getResource("/images/TranslationIcon.png").toExternalForm())); 
            stage.show();
            this.scene.getWindow().hide();
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Lesson filterWrongVocabels(Lesson lesson, String lessonName) {
       Lesson tmp = new Lesson();
       tmp.setLessonName(lessonName);
       tmp.setLanguage(lesson.getLanguage());
       
       List<Vocabel> vocabels = new ArrayList<>();
       
       lesson.getVocabels().stream().filter((vocabel) -> (!vocabel.isVocabelCorrect())).forEach((vocabel) -> {
           vocabels.add(vocabel);
        });
       tmp.setVocabels(vocabels);
       
       return tmp;
    }
}
