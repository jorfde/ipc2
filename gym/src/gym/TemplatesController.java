/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gym;

import accesoBD.AccesoBD;
import static gym.MainController.ADD;
import static gym.MainController.DEFAULT;
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
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Gym;
import modelo.SesionTipo;

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
    @FXML
    private TextField filterField;
    @FXML
    private HBox selectBox;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    
    private Stage primaryStage;
    private Scene prevScene;
    private String prevTitle;
    
    private Gym gym;
    
    private ObservableList<SesionTipo> templates = FXCollections.observableArrayList();   
    
    private SortedList<SesionTipo> sortedData;
    
    private int mode;
    
    private SesionTipo template = new SesionTipo();
    
    private AddGroupController agc;
    
    private SettingsController sg;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Initialization of the columns of the TableView
        codeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        
        gym = AccesoBD.getInstance().getGym();
        templates = FXCollections.observableList( gym.getTiposSesion() );
        
        FilteredList<SesionTipo> filteredData = new FilteredList<>(templates, p -> true);
        
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(template -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();
                
                if (template.getCodigo().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } 
                return false; // Does not match.
            });
        });
        
        sortedData = new SortedList<>(filteredData);
        
        sortedData.comparatorProperty().bind(templateView.comparatorProperty());
        
        templateView.setItems(sortedData);
        
        detailsButton.disableProperty().bind(Bindings.equal(-1,templateView.getSelectionModel().selectedIndexProperty()));
        
        okButton.disableProperty().bind(Bindings.equal(-1,templateView.getSelectionModel().selectedIndexProperty()));
    }    

    @FXML
    private void buttonHandler(ActionEvent event) throws IOException {
        int index = templateView.getSelectionModel().selectedIndexProperty().getValue();
        
        if(index !=-1)
            index = sortedData.getSourceIndexFor(templates, index);
        
        switch(((Node)event.getSource()).getId()){
            case "addButton": createWindow(index, ADD);break;
            case "detailsButton": createWindow(index, DETAILS);break;
            case "returnButton": createScene(MAIN);break;
            case "okButton": 
                if(agc != null)
                    agc.selected(index);
                if(sg != null)
                    sg.selected(index);
                exit();
                break;
            case "cancelButton": exit();break;
        }
    }

    void initStage(Stage stage) {
        primaryStage = stage;
        prevScene = stage.getScene();
        prevTitle = stage.getTitle();
        primaryStage.setTitle("Window 1");
    }
    
    public void initMode(int mode, AddGroupController agc, SettingsController sg){
        this.mode = mode;
        this.agc = agc;
        this.sg = sg;
        
        if(mode == DEFAULT){
            invisibleBox();
        } else {
            returnButton.setVisible(false);
        }
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
    
    private void invisibleBox(){
        selectBox.setVisible(false);
        okButton.setPrefHeight(0);
        cancelButton.setPrefHeight(0);
        selectBox.setPrefHeight(0);
    }
    
    private void exit(){
        cancelButton.getScene().getWindow().hide();
    }
}
