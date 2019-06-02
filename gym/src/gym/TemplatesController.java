/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gym;

import accesoBD.AccesoBD;
import static gym.MainController.ADD;
import static gym.MainController.DETAILS;
import static gym.MainController.GROUP;
import static gym.MainController.MAIN;
import static gym.MainController.START;
import static gym.MainController.TEMPLATE;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.SesionTipo;
import modelo.Gym;

/**
 * FXML Controller class
 *
 * @author Stéphane
 */
public class TemplatesController implements Initializable {
    @FXML
    private TableView<SesionTipo> templateView;
    @FXML
    private TableColumn<SesionTipo, String> codeColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button detailsButton;
    @FXML
    private Button returnButton;
    
    private Stage primaryStage;
    private Scene prevScene;
    private String prevTitle;
    
    private Gym gym;
    
    private ObservableList<SesionTipo> templates = FXCollections.observableArrayList();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Initialization of the columns of the TableView
        codeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        
        gym = AccesoBD.getInstance().getGym();
        templates = FXCollections.observableList( gym.getTiposSesion() );
        templateView.setItems(templates);
        
        detailsButton.disableProperty().bind(Bindings.equal(-1,templateView.getSelectionModel().selectedIndexProperty()));
    }    

    @FXML
    private void buttonHandler(ActionEvent event) throws IOException {
        int index = templateView.getSelectionModel().selectedIndexProperty().getValue();
        
        switch(((Node)event.getSource()).getId()){
            case "addButton": createWindow(index, ADD);break;
            case "detailsButton": createWindow(index, DETAILS);break;
            case "returnButton": createScene(MAIN);break;
        }
    }

    void initStage(Stage stage) {
        primaryStage = stage;
        prevScene = stage.getScene();
        prevTitle = stage.getTitle();
        primaryStage.setTitle("Window 1");
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
                settingsController.initStage(primaryStage);
                scene = new Scene(root);
                aNewStage.setScene(scene);
                aNewStage.initModality(Modality.APPLICATION_MODAL);
                aNewStage.show();
                break;
                
            case TEMPLATE:  
                myLoader = new FXMLLoader(getClass().getResource("templates.fxml"));
                root = (Parent) myLoader.load();
                TemplatesController templateController = myLoader.<TemplatesController>getController();
                templateController.initStage(primaryStage);
                scene = new Scene(root);
                primaryStage.setScene(scene);
                break;
                
            case GROUP:
                myLoader = new FXMLLoader(getClass().getResource("groups.fxml"));
                root = (Parent) myLoader.load();
                GroupsController groupController = myLoader.<GroupsController>getController();
                groupController.initStage(primaryStage);
                scene = new Scene(root);
                primaryStage.setScene(scene);
                break;
                
            case MAIN: 
                myLoader = new FXMLLoader(getClass().getResource("main.fxml"));
                root = (Parent) myLoader.load();
                MainController mainController = myLoader.<MainController>getController();
                mainController.initStage(primaryStage);
                scene = new Scene(root);
                primaryStage.setScene(scene);
                break;
        }
    }
    
    private void createWindow(int index, int mode) throws IOException{
        FXMLLoader myLoader = new FXMLLoader(getClass().getResource("addTemplate.fxml"));
        Parent root = (Parent) myLoader.load();
        AddTemplateController addTemplateController = myLoader.<AddTemplateController>getController();
        addTemplateController.initData(index, templates, mode);       
        Scene scene = new Scene(root);
        Stage aNewStage = new Stage();
        aNewStage.setScene(scene);
        aNewStage.initModality(Modality.APPLICATION_MODAL);
        aNewStage.show();
    }
}
