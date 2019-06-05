/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gym;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import modelo.Grupo;

/**
 * FXML Controller class
 *
 * @author Stéphane
 */
public class GroupsController implements Initializable {
    @FXML
    private TableView<Grupo> groupView;
    @FXML
    private TableColumn<Grupo, String> codeColumn;
    @FXML
    private TableColumn<Grupo, String> descriptionColumn;
    @FXML
    private TableColumn<Grupo, String> templateColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button graphButton;
    @FXML
    private Button returnButton;
    
    private Stage primaryStage;
    private Scene prevScene;
    private String prevTitle;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void buttonHandler(ActionEvent event) {
    }

    void initStage(Stage stage) {
        primaryStage = stage;
        prevScene = stage.getScene();
        prevTitle = stage.getTitle();
        primaryStage.setTitle("Window 1");
    }
    
}
