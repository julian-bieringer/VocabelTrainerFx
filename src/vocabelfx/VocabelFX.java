/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vocabelfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;


/**
 *
 * @author JBiering
 */
public class VocabelFX extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainWindow.fxml"));
        
       String image = VocabelFX.class.getResource("/images/translation.jpg").toExternalForm();
       root.setStyle("-fx-background-image: url('" + image + "'); " +
       "-fx-background-position: top; " +
       "-fx-background-color: white; " +
       "-fx-background-repeat: stretch;");  
        stage.setScene(new Scene(root, 750, 267));
        stage.setTitle("Vocabel-Trainer");
        stage.getIcons().add(new Image(VocabelFX.class.getResource("/images/TranslationIcon.png").toExternalForm())); 
        stage.setResizable(false);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
