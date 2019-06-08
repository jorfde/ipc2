/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gym;

import accesoBD.AccesoBD;
import java.util.Optional;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;



/**
 *
 * @author Stéphane
 */
public class Gym extends Application {
    private ButtonType buttonTypeOK;
    private ButtonType buttonTypeCancel;
    
    @Override
    public void start(Stage stage) throws Exception {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        MainController maincontroller = loader.<MainController>getController();
        maincontroller.initStage(stage);
        
        stage.setOnCloseRequest((WindowEvent event) ->{
            Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
            conf.setTitle("Confirmation");
            conf.setHeaderText("You are about to exit");
            conf.setContentText("Do you want to continue?");
            
            buttonTypeOK = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            conf.getButtonTypes().setAll(buttonTypeOK, buttonTypeCancel);
            
            Optional<ButtonType> result = conf.showAndWait();
            if (result.isPresent() && result.get() == buttonTypeOK){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Save DataBase");
                alert.setHeaderText("Saving data in DB");
                alert.setContentText("The application is saving the changes in the data into the database. This action can expend some minutes.");
                alert.getButtonTypes().setAll(buttonTypeOK);
                alert.show();
                AccesoBD.getInstance().salvar();
            } else {
                event.consume();
            }
        });
        stage.setTitle("");
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.setScene(scene);
        stage.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
