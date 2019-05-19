/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crossfitmgmt;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jorge
 */
public class MainController implements Initializable {
    @FXML
    private Button startButton;
    @FXML
    private Button dataButton;
    @FXML
    private Button settingButton;
    
    private Stage mainStage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void buttonHandler(ActionEvent event) throws IOException {
        switch(((Node)event.getSource()).getId()){
            case "startButton": break;
            case "dataButton": break;
            case "settingButton": changeScene();break;
        }
    }
    
    public void initStage(Stage s){
        mainStage = s;
    }
    
    private void changeScene() throws IOException{
        FXMLLoader myLoader = new FXMLLoader(getClass().getResource("configureSession.fxml"));
        Pane root = (Pane) myLoader.load();
        ConfigureSessionController configureSessionController = myLoader.<ConfigureSessionController>getController();
        Scene newScene = new Scene (root);
        mainStage.setScene(newScene);
    }
}
