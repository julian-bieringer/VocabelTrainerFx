/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import helper.FilePathHelper;
import file.ReadFile;
import file.WriteFile;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Lesson;
import model.Vocabel;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.codehaus.jackson.map.JsonMappingException;
import vocabelfx.VocabelFX;

/**
 * FXML Controller class
 *
 * @author JBiering
 */
public class LessonsWindowController implements Initializable {
    @FXML
    private Label labelLanguage;
    @FXML
    private Label labelVocabelsCount;
    @FXML
    private ListView<Lesson> listViewFiles;
    @FXML
    private Button buttonDelete;
    @FXML
    private Button buttonGenerateVocabelFile;
    @FXML
    private Button buttonBack;
    @FXML
    private Button buttonStart;
    @FXML
    private Button buttonEdit;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadFileNames();
    }    

    private void loadFileNames() {
        Lesson[] lessons = ReadFile.readAllJsonFiles();
        Lesson first = lessons[0];
        listViewFiles.getItems().addAll(lessons);
        if(first != null){
            labelLanguage.setText(first.getLanguage());
            labelVocabelsCount.setText("Vocabels: " + Integer.toString(first.getVocabels().size()));
            listViewFiles.getSelectionModel().select(first);
        }
    }

    @FXML
    private void listViewFilesSelectionChanged(MouseEvent event) {
        Lesson lesson = listViewFiles.getSelectionModel().getSelectedItem();
        labelLanguage.setText(lesson.getLanguage());
        labelVocabelsCount.setText("Vocabels: " + Integer.toString(lesson.getVocabels().size()));
    }

    @FXML
    private void buttonStartClicked(ActionEvent event) {
        try {
            Parent root;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ConductLessonWindow.fxml"));
            root = (Parent)loader.load();  
            ConductLessonWindowController controller = loader.<ConductLessonWindowController>getController();
            Lesson lesson = listViewFiles.getSelectionModel().getSelectedItem();
            controller.setLesson(lesson);
            Stage stage = new Stage();
            stage.setTitle("Lesson in Progress");
            stage.setScene(new Scene(root, 1200, 267));
            String image = VocabelFX.class.getResource("/images/translation.jpg").toExternalForm();
            root.setStyle("-fx-background-image: url('" + image + "'); " +
           "-fx-background-position: center center; " +
           "-fx-background-color: white; " +
           "-fx-background-repeat: stretch;"); 
            stage.getIcons().add(new Image(VocabelFX.class.getResource("/images/TranslationIcon.png").toExternalForm())); 
            stage.show();
            ((Node)(event.getSource())).getScene().getWindow().hide();
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void buttonDeleteClicked(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You are about to delete");
        alert.showAndWait();
        if(alert.getResult().equals(ButtonType.OK)){
            Lesson lesson = listViewFiles.getSelectionModel().getSelectedItem();
            final File file = new File(String.format("%s/%s", FilePathHelper.getUserDirectory(), lesson.getLessonName()));
            listViewFiles.getItems().remove(lesson);
            if(file.exists())
                file.delete();
        }
    }

    @FXML
    private void buttonGenerateVocabelFileClicked(ActionEvent event) {
        Lesson lesson = this.listViewFiles.getSelectionModel().getSelectedItem();
        String[][] lines = new String[lesson.getVocabels().size()][2];
        int index = 0;
        for(Vocabel vocabel : lesson.getVocabels()){
            lines[index][0] = vocabel.getForeignWord();
            lines[index][1] = vocabel.getGermanWord();
            index++;
        }
        Thread t = new Thread(() -> {
            try {
                WriteFile.writeStructuredVocabelFile(lines, lesson.getLessonName(), lesson.getLanguage());
            } catch (IOException | COSVisitorException ex) {
                Logger.getLogger(LessonsWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        t.start();
    }

    @FXML
    private void buttonBackClicked(ActionEvent event) {
        try {
            Parent root;
            root = FXMLLoader.load(getClass().getResource("/view/MainWindow.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Vocabel-Trainer");
            stage.setScene(new Scene(root, 750, 267));
            String image = VocabelFX.class.getResource("/images/translation.jpg").toExternalForm();
            root.setStyle("-fx-background-image: url('" + image + "'); " +
           "-fx-background-position: center center; " +
           "-fx-background-color: white; " +
           "-fx-background-repeat: stretch;"); 
            stage.getIcons().add(new Image(VocabelFX.class.getResource("/images/TranslationIcon.png").toExternalForm())); 
            stage.show();
            ((Node)(event.getSource())).getScene().getWindow().hide();
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void buttonEditClicked(ActionEvent event) {
         try {
            Parent root;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CreateOrAppendLessonWindow.fxml"));
            root = (Parent)loader.load();  
            CreateOrAppendLessonWindowController controller = loader.<CreateOrAppendLessonWindowController>getController();
            Lesson lesson = listViewFiles.getSelectionModel().getSelectedItem();
            controller.setLesson(lesson);
            Stage stage = new Stage();
            stage.setTitle("Lesson in Progress");
            stage.setScene(new Scene(root, 750, 350));
            String image = VocabelFX.class.getResource("/images/translation.jpg").toExternalForm();
            root.setStyle("-fx-background-image: url('" + image + "'); " +
           "-fx-background-position: top; " +
           "-fx-background-color: white; " +
           "-fx-background-repeat: no-repeat"); 
            stage.getIcons().add(new Image(VocabelFX.class.getResource("/images/TranslationIcon.png").toExternalForm())); 
            stage.show();
            ((Node)(event.getSource())).getScene().getWindow().hide();
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
