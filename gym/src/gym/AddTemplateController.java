/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gym;

import static gym.MainController.ADD;
import static gym.MainController.DETAILS;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import modelo.Grupo;
import modelo.SesionTipo;

/**
 * FXML Controller class
 *
 * @author Stéphane
 */
public class AddTemplateController implements Initializable {
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField codeField;
    @FXML
    private TextField warmingField;
    @FXML
    private TextField numberEField;
    @FXML
    private TextField workingField;
    @FXML
    private TextField restEField;
    @FXML
    private TextField numberCField;
    @FXML
    private TextField restCField;
    @FXML
    private Label labelWarming;
    @FXML
    private Label labelNumberE;
    @FXML
    private Label labelWorking;
    @FXML
    private Label labelRestE;
    @FXML
    private Label labelNumberC;
    @FXML
    private Label labelRestC;
    
    private int index;
    private ObservableList<SesionTipo> templates = FXCollections.observableArrayList();
    
    private boolean error = true;
    
    private BooleanProperty aux = new SimpleBooleanProperty(true);
    
    private boolean eCode = true;
    private boolean eWarming = true;
    private boolean eNumberE = true;
    private boolean eWorking = true;
    private boolean eRestE = true;
    private boolean eNumberC = true;
    private boolean eRestC = true;
    
    private int mode;
    
    public static final String errorMessage = "Only numbers without decimals";
    
    private Alert alert;
    @FXML
    private Label labelCode;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        codeField.textProperty().addListener((observable, oldVal, newVal) -> { 
            eCode = checkCode(newVal);
            updateError();
        });
        codeField.focusedProperty().addListener((observable, oldVal, newVal) -> { 
            if(!newVal){
                eCode = checkCode(codeField.getText());
                updateError();
            }
        });
        warmingField.textProperty().addListener((observable, oldVal, newVal) -> { 
            eWarming = checkNum(newVal);
            if(eWarming) labelWarming.setText(errorMessage);
            else labelWarming.setText("");
            updateError();
        });
        numberEField.textProperty().addListener((observable, oldVal, newVal) -> { 
            eNumberE = checkNum(newVal);
            if(eNumberE) labelNumberE.setText(errorMessage);
            else labelNumberE.setText("");
            updateError();
        });
        workingField.textProperty().addListener((observable, oldVal, newVal) -> { 
            eWorking = checkNum(newVal);
            if(eWorking) labelWorking.setText(errorMessage);
            else labelWorking.setText("");
            updateError();
        });
        restEField.textProperty().addListener((observable, oldVal, newVal) -> { 
            eRestE = checkNum(newVal);
            if(eRestE) labelRestE.setText(errorMessage);
            else labelRestE.setText("");
            updateError();
        });
        numberCField.textProperty().addListener((observable, oldVal, newVal) -> { 
            eNumberC = checkNum(newVal);
            if(eNumberC) labelNumberC.setText(errorMessage);
            else labelNumberC.setText("");
            updateError();
        });
        restCField.textProperty().addListener((observable, oldVal, newVal) -> { 
            eRestC = checkNum(newVal);
            if(eRestC) labelRestC.setText(errorMessage);
            else labelRestC.setText("");
            updateError();
        });
        
        okButton.disableProperty().bind(aux);
        
        alert= new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Done");
        alert.setContentText("You successfully added a TEMPLATE.");
    }    

    @FXML
    private void buttonHandler(ActionEvent event) {
        switch(((Node)event.getSource()).getId()){
            case "okButton": 
                if(mode == ADD){
                    SesionTipo s = new SesionTipo();
                    s.setCodigo(codeField.getText());
                    s.setT_calentamiento(Integer.parseInt(warmingField.getText()));
                    s.setNum_ejercicios(Integer.parseInt(numberEField.getText()));
                    s.setT_ejercicio(Integer.parseInt(workingField.getText()));
                    s.setD_ejercicio(Integer.parseInt(restEField.getText()));
                    s.setNum_circuitos(Integer.parseInt(numberCField.getText()));
                    s.setD_circuito(Integer.parseInt(restCField.getText()));
                    templates.add(s);
                    alert.showAndWait();
                } 
                
                exit();
                break;
                
            case "cancelButton": 
                exit();
                break;
        }
    }

    public void initData(int index, ObservableList<SesionTipo> templates, int mode) {
        this.index = index;
        this.templates = templates;
        this.mode = mode;
        
        if (index >= 0){
            //title.setText("Template's Details");
            SesionTipo template = templates.get(index);
            codeField.setText(template.getCodigo());
            
            warmingField.setText(template.getT_calentamiento()+"");
            
            numberEField.setText(template.getNum_ejercicios()+"");
            
            workingField.setText(template.getT_ejercicio()+"");
            
            restEField.setText(template.getD_ejercicio()+"");
            
            numberCField.setText(template.getNum_circuitos()+"");
            
            restCField.setText(template.getD_circuito()+"");
            
            
            if(mode == DETAILS){
                codeField.setEditable(false);
                warmingField.setEditable(false);
                numberEField.setEditable(false);
                workingField.setEditable(false);
                restEField.setEditable(false);
                numberCField.setEditable(false);
                restCField.setEditable(false);
            }
        } else {
            //title.setText("New Template");
        }
    }
    
    private void exit(){
        cancelButton.getScene().getWindow().hide();
    }
    
    private boolean checkNum(String data){
        boolean res = true;
        
        if(data.matches("[0-9]+")){
            res = false;
        } 
        
        return res;
    }
    
    private void updateError(){
        error = false;
        error = eWarming || eNumberE || eWorking || eRestE || eNumberC || eRestC || eCode;
        aux.setValue(error);
    }
    
    private boolean checkCode(String data){
        boolean res = false;
        
        if(data.equals("")){
            res = true;
            labelCode.setText("You can't leave the field in blank");
        }
        for(int i = 0;i < templates.size() && !res;i++){
            SesionTipo s = templates.get(i);
            String code = s.getCodigo();
            if(code.equals(data)){
                res = true;
                labelCode.setText("This code is already in use");
            }
        }
        
        if(!res){
            labelCode.setText("");
        }
        
        return res;
    }
}
