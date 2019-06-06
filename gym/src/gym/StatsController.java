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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

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
    private LineChart<?, ?> lineChart;
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

    @FXML
    private void checkBoxHandler(ActionEvent event) {
    }

    void initStage(Stage stage) {
        primaryStage = stage;
        prevScene = stage.getScene();
        prevTitle = stage.getTitle();
        primaryStage.setTitle("");
    }
    
}
