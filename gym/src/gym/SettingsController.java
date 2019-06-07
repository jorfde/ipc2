/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gym;

import accesoBD.AccesoBD;
import static gym.MainController.ADD;
import static gym.MainController.EDIT;
import static gym.MainController.GROUP;
import static gym.MainController.MAIN;
import static gym.MainController.TEMPLATE;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
public class SettingsController implements Initializable {
    @FXML
    private TextField templateField;
    @FXML
    private Button templateButton;
    @FXML
    private TextField groupField;
    @FXML
    private Button groupButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label labelGroup;
    @FXML
    private Label labelTemplate;
    @FXML
    private Button okButton;
    
    private int sel;
    private int sel2;
    
    private SesionTipo template = new SesionTipo();
    
    private ArrayList<SesionTipo> templates;
    
    private ArrayList<Grupo> groups;
    
    private boolean eTemplate = true;
    
    private BooleanProperty aux = new SimpleBooleanProperty(true);
    
    private boolean error = true;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        modelo.Gym gym = AccesoBD.getInstance().getGym();
        templates = gym.getTiposSesion();
        groups = gym.getGrupos();
        
        templateField.textProperty().addListener((observable, oldVal, newVal) -> { 
            checkTemplate(newVal);
            updateError();
        });
        
        okButton.disableProperty().bind(aux);
    }    

    @FXML
    private void buttonHandler(ActionEvent event) throws IOException {
        switch(((Node)event.getSource()).getId()){
            case "groupButton": 
                createWindow(1, GROUP);
                if(sel2 != -1){
                    template = templates.get(sel);
                    eTemplate = false;
                    labelTemplate.setText("");
                    templateField.setText(template.getCodigo());
                }
                break;
                
            case "templateButton": 
                createWindow(1, TEMPLATE);
                if(sel != -1){
                    template = templates.get(sel);
                    eTemplate = false;
                    labelTemplate.setText("");
                    templateField.setText(template.getCodigo());
                }
                break;
                
            case "cancelButton": 
                exit();
                break;
                
            case "okButton": 
                exit();
                break;
        }
    }
    
    private void createWindow(int mode, int t) throws IOException{
        FXMLLoader myLoader;
        Parent root;
        Scene scene = null;
        
        switch(t){
            case TEMPLATE:
                myLoader = new FXMLLoader(getClass().getResource("templates.fxml"));
                root = (Parent) myLoader.load();
                TemplatesController templatesController = myLoader.<TemplatesController>getController();   
                templatesController.initMode(mode, null, this);
                scene = new Scene(root);
                
            case GROUP:
                myLoader = new FXMLLoader(getClass().getResource("groups.fxml"));
                root = (Parent) myLoader.load();
        }
        Stage aNewStage = new Stage();
        aNewStage.setScene(scene);
        aNewStage.initModality(Modality.APPLICATION_MODAL);
        aNewStage.showAndWait();
    }

    void selected(int index) {
        this.sel = index;
    }
    
    void selected2(int index) {
        this.sel2 = index;
    }
    
    private void checkTemplate(String newVal){
        boolean fin = false;
        eTemplate = true;
        for(int i = 0;i < templates.size() && !fin;i++){
            SesionTipo s = templates.get(i);
            String code = s.getCodigo();
            if(code.equals(newVal)){
                eTemplate = false;
                template = s;
                fin = true;
            }
        }
        if (eTemplate){
            labelTemplate.setText("It doesn't exist");
        } else {
            labelTemplate.setText("");
        }
    }
    
    private void updateError(){
        error = false;
        error = eTemplate;
        aux.setValue(error);
    }
    
    private void exit(){
        cancelButton.getScene().getWindow().hide();
    }
}
