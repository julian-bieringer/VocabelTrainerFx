/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import helper.FilePathHelper;
import file.WriteFile;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Lesson;
import model.Vocabel;
import vocabelfx.VocabelFX;

/**
 * FXML Controller class
 *
 * @author BOINC
 */
public class CreateOrAppendLessonWindowController implements Initializable {
    @FXML
    private TableColumn<Vocabel, String> columnGermanWord;
    @FXML
    private TableColumn<Vocabel, String> columnForeignWord;
    @FXML
    private Button buttonSave;
    @FXML
    private Button buttonBack;
    @FXML
    private TextField textFieldGermanWord;
    @FXML
    private TextField textFieldForeignWord;
    @FXML
    private Label labelVocabelCount;
    @FXML
    private Label labelLatestAutosave;
    @FXML
    private Label labelForeignWord;
    @FXML
    private TableView<Vocabel> tableViewVocabels;
    
    private Lesson lesson;
    private boolean newLesson;
    private boolean editVocabel;
 
    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
        if (lesson == null)
            setUpWindowForNewLesson();
        else
            setUpWindowForExistingLesson();
    }
    
    private void setUpWindowForNewLesson(){
        newLesson = true;
        this.lesson = new Lesson();
        this.lesson.setVocabels(new ArrayList());
        if(askForLanguage())
            askForName();
    }
    
    private void setUpWindowForExistingLesson(){
        newLesson = false;
        this.columnForeignWord.setText(lesson.getLanguage());
        this.labelForeignWord.setText(String.format("%s word", lesson.getLanguage()));
        this.tableViewVocabels.getItems().addAll(lesson.getVocabels());
                this.labelVocabelCount.setText(String.format("Vocabel sum: %s",Integer.toString(this.lesson.getVocabels().size())));
    }
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.columnForeignWord.setCellValueFactory(new PropertyValueFactory("foreignWord"));
        this.columnGermanWord.setCellValueFactory(new PropertyValueFactory("germanWord"));
        Platform.runLater(() -> {
           textFieldGermanWord.requestFocus();
       });
    }    

    @FXML
    private void buttonSaveClicked(ActionEvent event) {
        if(this.lesson.getVocabels().size() % 10 != 0){
            Thread t = new Thread(() -> {
                        try {
                            WriteFile.writeJsonFile(lesson, 1);
                        } catch (IOException ex) {
                            Logger.getLogger(CreateOrAppendLessonWindowController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
            t.start();
        }
        navigateBack();
    }

    @FXML
    private void buttonBackClicked(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You are about to lose your changes!");
        alert.showAndWait();
        if(alert.getResult().equals(ButtonType.OK))
            navigateBack();
    }

    private void navigateBack() {
        try {
            Parent root;
            if(newLesson)
                root = FXMLLoader.load(getClass().getResource("/view/MainWindow.fxml"));
            else
                root = FXMLLoader.load(getClass().getResource("/view/LessonsWindow.fxml"));
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
            Stage currStage = (Stage) tableViewVocabels.getScene().getWindow();
            currStage.hide();
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void textFieldForeignWordEnterHit(ActionEvent event) {
        if (textFieldForeignWord.getText().trim().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "foreign word can't be empty");
            alert.showAndWait();
        } else if (textFieldGermanWord.getText().trim().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "german word can't be empty");
            alert.showAndWait();
        }
        else{
            Vocabel vocabel = new Vocabel(textFieldGermanWord.getText(), textFieldForeignWord.getText());
            if(!editVocabel){
                this.lesson.getVocabels().add(vocabel);
                this.tableViewVocabels.getItems().add(vocabel);
            } else {
                Vocabel vocabelSelected = this.tableViewVocabels.getSelectionModel().getSelectedItem();
                int indexVocabel = this.tableViewVocabels.getSelectionModel().getSelectedIndex();
                this.lesson.getVocabels().remove(vocabelSelected);
                this.tableViewVocabels.getItems().remove(vocabelSelected);
                this.lesson.getVocabels().add(indexVocabel, vocabel);
                this.tableViewVocabels.getItems().add(indexVocabel, vocabel);
                editVocabel = false;
            }
            resetForNextInput();
        }
    }
    
    private void resetForNextInput(){
        this.tableViewVocabels.scrollTo(lesson.getVocabels().size()-1);
        this.textFieldForeignWord.setText("");
        this.textFieldGermanWord.setText("");
        this.textFieldGermanWord.requestFocus();
        this.labelVocabelCount.setText(String.format("Vocabel sum: %s",Integer.toString(this.lesson.getVocabels().size())));
        if(this.lesson.getVocabels().size()%10 == 0){
            Thread t = new Thread(() -> {
                try {
                    WriteFile.writeJsonFile(lesson, 1);
                } catch (IOException ex) {
                    Logger.getLogger(CreateOrAppendLessonWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            t.start();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            this.labelLatestAutosave.setText(String.format("Last Save: %s", sdf.format(new Date())));
        }
    }

    private boolean askForLanguage() {
        TextInputDialog dialog = new TextInputDialog("english");
        dialog.setTitle("Lesson Language");
        dialog.setHeaderText("Gathering information");
        dialog.setContentText("Please enter the language of your new lesson");
        Optional<String> result = dialog.showAndWait();
        if(!result.isPresent()){
            navigateBack();
            return false;
        }
        result.ifPresent(language -> columnForeignWord.setText(language));
        result.ifPresent(language -> this.lesson.setLanguage(language));
        result.ifPresent(language -> labelForeignWord.setText(String.format("%s word", language)));
        return true;
    }

    private void askForName() {
        boolean resultPresent;
        int count = 0;
        
        do{
            TextInputDialog dialog = new TextInputDialog("Unit ");
            dialog.setTitle("Lesson Name");
            if(count > 0)
                dialog.setHeaderText("Name does already exist");
            else
                dialog.setHeaderText("Gathering information");
            dialog.setContentText("Please enter the name of your new lesson");
            Optional<String> result = dialog.showAndWait();
            resultPresent = result.isPresent();
            if(!result.isPresent())
                navigateBack();
            result.ifPresent(name -> this.lesson.setLessonName(name));
            count++;
        }while(new File(String.format("%s/%s", FilePathHelper.getUserDirectory(), this.lesson.getLessonName())).exists() && resultPresent);
    }

    @FXML
    private void tableViewVocabelsItemClicked(MouseEvent event) {
        editVocabel = true;
        Vocabel vocabel = tableViewVocabels.getSelectionModel().getSelectedItem();
        this.textFieldForeignWord.setText(vocabel.getForeignWord());
        this.textFieldGermanWord.setText(vocabel.getGermanWord());
    }
}
