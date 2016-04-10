/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

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
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.Lesson;
import vocabelfx.VocabelFX;

/**
 *
 * @author JBiering
 */
public class MainWindowController implements Initializable {
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    @FXML
    private void buttonNewLessonClicked(ActionEvent event) {
        try {
            Parent root;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CreateOrAppendLessonWindow.fxml"));
            root = (Parent)loader.load();  
            CreateOrAppendLessonWindowController controller = loader.<CreateOrAppendLessonWindowController>getController();
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
            Lesson lesson = null;
            controller.setLesson(lesson);
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void buttonShowLessonClicked(ActionEvent event) {
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
            ((Node)(event.getSource())).getScene().getWindow().hide();
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void buttonExitClicked(ActionEvent event) {
        System.exit(0);
    }   
}
