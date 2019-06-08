/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gym;

import accesoBD.AccesoBD;
import static gym.MainController.DEFAULT;
import static gym.MainController.GROUP;
import static gym.MainController.MAIN;
import static gym.MainController.START;
import static gym.MainController.TEMPLATE;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Grupo;
import modelo.Sesion;
import modelo.SesionTipo;

/**
 * FXML Controller class
 *
 * @author Stéphane
 */
public class StatsController implements Initializable {
    @FXML
    private TextField groupField;
    @FXML
    private Button searchButton;
    @FXML
    private HBox windowBox;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    @FXML
    private LineChart<String, Number> lineChart;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private CheckBox workingTime;
    @FXML
    private CheckBox restTime;
    @FXML
    private CheckBox realTime;
    @FXML
    private Button returnButton;
    @FXML
    private Label labelGroup;
    @FXML
    private TextField sessionsField;
    @FXML
    private Label labelSessions;
    
    private Stage primaryStage;
    private Scene prevScene;
    private String prevTitle;
    
    private boolean eGroup;
    
    private ArrayList<Grupo> groups;
    
    private Grupo group = new Grupo();
    
    private int sel;
    
    private XYChart.Series<String,Number> working;
    private XYChart.Series<String,Number> rest;
    private XYChart.Series<String,Number> real;
    
    private ArrayList<Sesion> sesions;
    
    private int mode;
    
    private GroupsController gc;
    
    private boolean eSession = true;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        groupField.textProperty().addListener((observable, oldVal, newVal) -> { 
            checkGroup(newVal);
            if(!eGroup) {
                sesions = group.getSesiones();
                if(workingTime.isSelected()){
                    addSeries(1, sesions.size());
                } 

                if(restTime.isSelected()){
                    addSeries(2, sesions.size());
                }
                    
                if(realTime.isSelected()){
                    addSeries(3, sesions.size());
                } 
            }
        });
        
        sessionsField.textProperty().addListener((observable, oldVal, newVal) -> { 
            eSession = checkNum(newVal);
            if(eSession) {
                labelSessions.setText("Integer numbers only");
            } else {
                labelSessions.setText("");
                int n = Integer.parseInt(sessionsField.getText());
                n = (int) Math.min(n, sesions.size());
                if(workingTime.isSelected()){
                    deleteSeries(1);
                    addSeries(1, n);
                } 

                if(restTime.isSelected()){
                    deleteSeries(2);
                    addSeries(2, n);
                }
                    
                if(realTime.isSelected()){
                    deleteSeries(3);
                    addSeries(3, n);
                } 
            }
        });
        
        modelo.Gym gym = AccesoBD.getInstance().getGym();
        groups = gym.getGrupos();
        
        working = new XYChart.Series();
        rest = new XYChart.Series();
        real = new XYChart.Series();
        
        working.setName("Working Time");
        rest.setName("Rest Time");
        real.setName("Real Time");
    }    

    @FXML
    private void buttonHandler(ActionEvent event) throws IOException {
        switch(((Node)event.getSource()).getId()){
            case "searchButton": 
                createWindow(1);
                if(sel != -1){
                    group = groups.get(sel);
                    eGroup = false;
                    labelGroup.setText("");
                    groupField.setText(group.getCodigo());
                }
                break;
                
            case "cancelButton": 
                exit();
                break;
                
            case "okButton": 
                exit();
                break;
                
            case "returnButton": 
                createScene(MAIN);break;
        }
    }

    @FXML
    private void checkBoxHandler(ActionEvent event) {
        switch(((Node)event.getSource()).getId()){
            case "workingTime": 
                if(!workingTime.isSelected()){
                    deleteSeries(1);
                } else {
                    addSeries(1, sesions.size());
                }
                break;
                
            case "restTime": 
                if(!restTime.isSelected()){
                    deleteSeries(2);
                } else {
                    addSeries(2, sesions.size());
                }
                break;
                
            case "realTime": 
                if(!realTime.isSelected()){
                    deleteSeries(3);
                } else {
                    addSeries(3, sesions.size());
                }
                break;
        }
    }

    void initStage(Stage stage) {
        primaryStage = stage;
        prevScene = stage.getScene();
        prevTitle = stage.getTitle();
        primaryStage.setTitle("");
    }
    
    private void checkGroup(String newVal){
        boolean fin = false;
        eGroup = true;
        for(int i = 0;i < groups.size() && !fin;i++){
            Grupo s = groups.get(i);
            String code = s.getCodigo();
            if(code.equals(newVal)){
                eGroup = false;
                group = s;
                fin = true;
            }
        }
        if (eGroup){
            labelGroup.setText("It doesn't exist");
        } else {
            labelGroup.setText("");
        }
    }
    
    private void exit(){
        cancelButton.getScene().getWindow().hide();
    }
    
    private void createWindow(int mode) throws IOException{
        FXMLLoader myLoader = new FXMLLoader(getClass().getResource("groups.fxml"));
        Parent root = (Parent) myLoader.load();
        GroupsController groupsController = myLoader.<GroupsController>getController();   
        groupsController.initMode(mode, null, this);
        Scene scene = new Scene(root);
        Stage aNewStage = new Stage();
        aNewStage.setScene(scene);
        aNewStage.initModality(Modality.APPLICATION_MODAL);
        aNewStage.showAndWait();
    }
   
    void selected(int index) {
        this.sel = index;
    }
    
    private void introduceData(int n){
        lineChart.getData().removeAll(working);
        lineChart.getData().removeAll(rest);
        lineChart.getData().removeAll(real);
        
        working = new XYChart.Series();
        rest = new XYChart.Series();
        real = new XYChart.Series();
        
        working.setName("Working Time");
        rest.setName("Rest Time");
        real.setName("Real Time");
        
        
        
        for(int i=0;i < n;i++){
            Sesion s = sesions.get(i);
            SesionTipo st = s.getTipo();
            real.getData().add(new XYChart.Data<>(s.getFecha().toString(),s.getDuracion().getSeconds()));
            working.getData().add(new XYChart.Data<>(s.getFecha().toString(), getWorking(st)));
            rest.getData().add(new XYChart.Data<>(s.getFecha().toString(), getRest(st)));
        }
        
        lineChart.getData().addAll(real, working, rest);
    }
    
    private int getWorking(SesionTipo s){
        return s.getNum_circuitos() * s.getNum_ejercicios() * s.getT_ejercicio();
    }
    
    private int getRest(SesionTipo s){
        return ((s.getNum_circuitos() - 1)* s.getD_circuito()) + ((s.getNum_ejercicios() - 1) * s.getD_ejercicio() * s.getNum_circuitos());
    }
    
    private void invisibleBox(){
        windowBox.setVisible(false);
        okButton.setPrefHeight(0);
        cancelButton.setPrefHeight(0);
        windowBox.setPrefHeight(0);
    }
    
    public void initMode(int mode, GroupsController gc){
        this.mode = mode;
        this.gc = gc;
        
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
    
    private void deleteSeries(int s){
        switch(s){
            case 1: lineChart.getData().removeAll(working);
                break;
            case 2: lineChart.getData().removeAll(rest);
                break;
            case 3: lineChart.getData().removeAll(real);
                break;
        }
    }
    
    private void addSeries(int s, int n){
        switch(s){
            case 1:
                working = new XYChart.Series();
                working.setName("Working Time");
                for(int i=0;i < n;i++){
                    Sesion se = sesions.get(i);
                    SesionTipo st = se.getTipo();
                    working.getData().add(new XYChart.Data<>(se.getFecha().toString(), getWorking(st)));
                }
                lineChart.getData().addAll(working);
                break;
                
            case 2:
                rest = new XYChart.Series();
                rest.setName("Rest Time");
                for(int i=0;i < n;i++){
                    Sesion se = sesions.get(i);
                    SesionTipo st = se.getTipo();
                    rest.getData().add(new XYChart.Data<>(se.getFecha().toString(), getRest(st)));
                }
                lineChart.getData().addAll(rest);
                break;
                
            case 3:
                real = new XYChart.Series();
                real.setName("Real Time");
                for(int i=0;i < n;i++){
                    Sesion se = sesions.get(i);
                    SesionTipo st = se.getTipo();
                    real.getData().add(new XYChart.Data<>(se.getFecha().toString(),se.getDuracion().getSeconds()));
                }
                lineChart.getData().addAll(real);
                break;
        }
    }
    
    public void initData(int index){
        group = groups.get(index);
        groupField.setText(group.getCodigo());
        eGroup = false;
    }
    
    private boolean checkNum(String data){
        boolean res = true;
        
        if(data.matches("[0-9]+")){
            res = false;
        } 
        
        return res;
    }
}
