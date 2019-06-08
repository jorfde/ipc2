/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gym;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Stéphane
 */
public class InSessionController implements Initializable {
    
    private Stage primaryStage;
    private Scene prevScene;
    private String prevTitle;
    @FXML
    private Button playButton;
    @FXML
    private Button restartButton;
    @FXML
    private Text timeText;
    
    private MyTask task;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        task = new MyTask();
        Thread hiloBack = new Thread(task);
        hiloBack.setDaemon(true);
        hiloBack.start();
        timeText.textProperty().bind(task.messageProperty());
    }    
    
    void initStage(Stage stage) {
        primaryStage = stage;
        prevScene = stage.getScene();
        prevTitle = stage.getTitle();
        primaryStage.setMaximized(true);
    }

    @FXML
    private void buttonHandler(ActionEvent event) {
    }
    
    class MyTask extends Task<Void> {

        boolean started = false;
        long secTotal;
        long secActual;
        long startTime;
        long timePaused = 0;

        boolean paused = true;

        void setTimeCrono(int sec) {
            secTotal = sec;
            secActual = secTotal;
            startTime = System.currentTimeMillis();
        }

        void pause() {
            paused = true;
        }

        void play() {
            paused = false;
        }

        long calcula() {
            secActual = secTotal + timePaused - (System.currentTimeMillis() - startTime) / 1000;
            updateMessage(toMinSecFormat((int) secActual));
            updateProgress(secActual, secTotal);
            return secActual;
        }
        
        public String toMinSecFormat(int seconds){
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%d:%02d", min, sec);
        }

        @Override
        protected Void call() throws Exception {
            while (true) {
                Thread.sleep(100);
                if (isCancelled()) {
                    break;
                }
                while (paused) {
                    Thread.sleep(1000);
                    timePaused += 1;
                }
                if (calcula() < 0) {
                    return null;
                }

            }
            return null;
        }
    }
    
}
