/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gym;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Grupo;
import modelo.SesionTipo;

/**
 * FXML Controller class
 *
 * @author Stéphane
 */
public class MainController implements Initializable {
    @FXML
    private Button startButton;
    @FXML
    private Button templateButton;
    @FXML
    private Button groupButton;
    @FXML
    private Button statsButton;
    
    private Stage primaryStage;
    
    public static final int START = 0;
    public static final int TEMPLATE = 1;
    public static final int GROUP = 2;
    public static final int MAIN = 3;
    public static final int STATS = 4;
    public static final int ADD = 0;
    public static final int DETAILS = 1;
    public static final int EDIT = 3;
    public static final int DEFAULT = -1;
    

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
            case "startButton": createScene(START);break;
            case "templateButton": createScene(TEMPLATE);break;
            case "groupButton": createScene(GROUP);break;
            case "statsButton": createScene(STATS);break;
        }
    }
    
    private void onExit(ActionEvent event) {
        primaryStage.hide();
    }
    
    
    public void initStage(Stage stage) {
        primaryStage = stage;
    }
    
    private void createScene(int mode) throws IOException{
        FXMLLoader myLoader;
        Parent root;
        Scene scene;
        
        switch(mode){
            case START:
                Stage aNewStage = new Stage();
                myLoader = new FXMLLoader(getClass().getResource("settings.fxml"));
                root = (Parent) myLoader.load();
                SettingsController settingsController = myLoader.<SettingsController>getController();
                settingsController.passController(this);
                scene = new Scene(root);
                aNewStage.setScene(scene);
                aNewStage.initModality(Modality.APPLICATION_MODAL);
                aNewStage.showAndWait();
                break;
                
            case TEMPLATE:  
                myLoader = new FXMLLoader(getClass().getResource("templates.fxml"));
                root = (Parent) myLoader.load();
                TemplatesController templateController = myLoader.<TemplatesController>getController();
                templateController.initStage(primaryStage);
                templateController.initMode(DEFAULT, null, null);
                scene = new Scene(root);
                primaryStage.setScene(scene);
                break;
                
            case GROUP:
                myLoader = new FXMLLoader(getClass().getResource("groups.fxml"));
                root = (Parent) myLoader.load();
                GroupsController groupController = myLoader.<GroupsController>getController();
                groupController.initStage(primaryStage);
                groupController.initMode(DEFAULT, null, null);
                scene = new Scene(root);
                primaryStage.setScene(scene);
                break;
                
            case STATS:
                myLoader = new FXMLLoader(getClass().getResource("stats.fxml"));
                root = (Parent) myLoader.load();
                StatsController statsController = myLoader.<StatsController>getController();
                statsController.initStage(primaryStage);
                statsController.initMode(DEFAULT, null);
                scene = new Scene(root);
                primaryStage.setScene(scene);
                break;
        }
    }
    
    public void ok(boolean res, Grupo g, SesionTipo st) throws IOException{
        if(res){
            FXMLLoader myLoader;
            Parent root;
            Scene scene;
            
            myLoader = new FXMLLoader(getClass().getResource("inSession.fxml"));
            root = (Parent) myLoader.load();
            InSessionController inSessionController = myLoader.<InSessionController>getController();
            inSessionController.initStage(primaryStage);
            inSessionController.initData(g, st);
            scene = new Scene(root);
            primaryStage.setScene(scene);
        }
    }
}
