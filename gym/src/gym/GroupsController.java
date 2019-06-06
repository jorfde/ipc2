/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gym;

import accesoBD.AccesoBD;
import static gym.MainController.ADD;
import static gym.MainController.DETAILS;
import static gym.MainController.EDIT;
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
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Grupo;
import modelo.SesionTipo;

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
    private Button returnButton;
    @FXML
    private TextField filterField;
    @FXML
    private Button statsButton;
    
    private Stage primaryStage;
    private Scene prevScene;
    private String prevTitle;
    
    private modelo.Gym gym;
    
    private ObservableList<Grupo> groups = FXCollections.observableArrayList();   
    
    private SortedList<Grupo> sortedData;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Initialization of the columns of the TableView
        codeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        descriptionColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion()));
        templateColumn.setCellValueFactory(c -> {
                Grupo g = c.getValue();
                if(g.getDefaultTipoSesion() != null)
                    return new SimpleStringProperty(c.getValue().getDefaultTipoSesion().getCodigo());    
                else
                    return new SimpleStringProperty("");
            } 
        );
        
        gym = AccesoBD.getInstance().getGym();
        groups = FXCollections.observableList( gym.getGrupos() );
        
        FilteredList<Grupo> filteredData = new FilteredList<>(groups, p -> true);
        
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(group -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();
                
                if (group.getCodigo().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (group.getDescripcion().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (group.getDefaultTipoSesion() != null && group.getDefaultTipoSesion().getCodigo().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; // Does not match.
            });
        });
        
        sortedData = new SortedList<>(filteredData);
        
        sortedData.comparatorProperty().bind(groupView.comparatorProperty());
        
        groupView.setItems(sortedData);
        
        editButton.disableProperty().bind(Bindings.equal(-1,groupView.getSelectionModel().selectedIndexProperty()));
        statsButton.disableProperty().bind(Bindings.equal(-1,groupView.getSelectionModel().selectedIndexProperty()));    
    }    

    @FXML
    private void buttonHandler(ActionEvent event) throws IOException {    
        int index = groupView.getSelectionModel().selectedIndexProperty().getValue();
        
        if(index !=-1)
            index = sortedData.getSourceIndexFor(groups, index);
        
        switch(((Node)event.getSource()).getId()){
            case "addButton": createWindow(index, ADD);break;
            case "editButton": createWindow(index, EDIT);break;
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
        FXMLLoader myLoader = new FXMLLoader(getClass().getResource("addGroup.fxml"));
        Parent root = (Parent) myLoader.load();
        AddGroupController addGroupController = myLoader.<AddGroupController>getController();
        addGroupController.initData(index, groups, mode);       
        Scene scene = new Scene(root);
        Stage aNewStage = new Stage();
        aNewStage.setScene(scene);
        aNewStage.initModality(Modality.APPLICATION_MODAL);
        aNewStage.show();
    }
}
