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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Grupo;
import modelo.Gym;
import modelo.SesionTipo;

/**
 * FXML Controller class
 *
 * @author Stéphane
 */
public class AddGroupController implements Initializable {
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField codeField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField templateField;
    @FXML
    private Button searchButton;
    @FXML
    private Label templateLabel;
    @FXML
    private Label codeLabel;
    
    private boolean error = true;
    
    private BooleanProperty aux = new SimpleBooleanProperty(true);
    
    private boolean eTemplate = true;
    private boolean eCode = true;
    
    private int index;
    private int mode;
    
    private ObservableList<Grupo> groups = FXCollections.observableArrayList();
    
    private SesionTipo template = new SesionTipo();
    
    private ArrayList<SesionTipo> templates;
    
    private int sel;
    
    private Alert alert;
    @FXML
    private Text title;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Gym gym = AccesoBD.getInstance().getGym();
        templates = gym.getTiposSesion();
        
        codeField.textProperty().addListener((observable, oldVal, newVal) -> { 
            if(mode != EDIT){
                eCode = checkCode(newVal);
                updateError();
            }
        });
        codeField.focusedProperty().addListener((observable, oldVal, newVal) -> { 
            if(mode != EDIT && !newVal){
                eCode = checkCode(codeField.getText());
                updateError();
            }
        });
        templateField.textProperty().addListener((observable, oldVal, newVal) -> { 
            checkTemplate(newVal);
        });
        
        alert= new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Done");
        
        okButton.disableProperty().bind(aux);
    }    

    @FXML
    private void buttonHandler(ActionEvent event) throws IOException {
        switch(((Node)event.getSource()).getId()){
            case "okButton": 
                if(mode == ADD){
                    
                    Grupo s = new Grupo();
                    s.setCodigo(codeField.getText());
                    s.setDescripcion(descriptionField.getText());
                    
                    if(!eTemplate)
                        s.setDefaultTipoSesion(template);

                    groups.add(s);
                    alert.setContentText("You successfully added a GROUP.");
                } else if(mode == EDIT){
                    
                    Grupo s = new Grupo();
                    s.setCodigo(codeField.getText());
                    s.setDescripcion(descriptionField.getText());
                    
                    if(!eTemplate)
                        s.setDefaultTipoSesion(template);
                    
                    groups.set(index, s);
                    alert.setContentText("You successfully modified a GROUP.");
                }
                
                alert.showAndWait();
                exit();
                break;
                
            case "cancelButton": 
                exit();
                break;
                
            case "searchButton":
                createWindow(1);
                if(sel != -1){
                    template = templates.get(sel);
                    eTemplate = false;
                    templateLabel.setText("");
                    templateField.setText(template.getCodigo());
                }
                break;
        }
    }

    void initData(int index, ObservableList<Grupo> groups, int mode) {
        this.index = index;
        this.groups = groups;
        this.mode = mode;
        
        if (index >= 0){
            title.setText("Add Group");
            Grupo grupo = groups.get(index);
            codeField.setText(grupo.getCodigo());
            
            descriptionField.setText(grupo.getDescripcion());
            
            if(grupo.getDefaultTipoSesion() != null)
                templateField.setText(grupo.getDefaultTipoSesion().getCodigo());            
            
            if(mode == DETAILS){
                
                codeField.setEditable(false);
                descriptionField.setEditable(false);
                templateField.setEditable(false);
            }
            if(mode == EDIT){
                title.setText("Edit Group");
                codeField.setEditable(false);
                eCode = false;
            }
        } else {
            //title.setText("New Template");
        }
    }
    
    private void exit(){
        cancelButton.getScene().getWindow().hide();
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
            templateLabel.setText("It doesn't exist");
        } else {
            templateLabel.setText("");
        }
    }
    
    private void updateError(){
        error = false;
        error = eCode;
        aux.setValue(error);
    }
    
    private void createWindow(int mode) throws IOException{
        FXMLLoader myLoader = new FXMLLoader(getClass().getResource("templates.fxml"));
        Parent root = (Parent) myLoader.load();
        TemplatesController templatesController = myLoader.<TemplatesController>getController();   
        templatesController.initMode(mode, this, null);
        Scene scene = new Scene(root);
        Stage aNewStage = new Stage();
        aNewStage.setScene(scene);
        aNewStage.initModality(Modality.APPLICATION_MODAL);
        aNewStage.showAndWait();
    }
    
    public void selected(int sel){
        this.sel = sel;
    }
    
    private boolean checkCode(String data){
        boolean res = false;
        
        if(data.equals("")){
            res = true;
            codeLabel.setText("You can't leave the field in blank");
        }
        for(int i = 0;i < groups.size() && !res;i++){
            Grupo s = groups.get(i);
            String code = s.getCodigo();
            if(code.equals(data)){
                res = true;
                codeLabel.setText("This code is already in use");
            }
        }
        
        if(!res){
            codeLabel.setText("");
        }
        
        return res;
    }
}
