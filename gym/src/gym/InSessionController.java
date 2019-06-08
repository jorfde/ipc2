/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gym;

import static gym.MainController.GROUP;
import static gym.MainController.TEMPLATE;
import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import modelo.Grupo;
import modelo.SesionTipo;

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
    
    private int warmT, exerT, exerRest, exerN, circN, circRest;
    
    private SesionTipo template;
    private Grupo grupo;
    
    private CronoService servicio;
    private Property<Boolean> iniciado = new SimpleBooleanProperty(false);
    private boolean firstime;
    
    private boolean play = true;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        servicio = new CronoService();
        servicio.setTiempo(timeText.textProperty());
    }    
    
    void initStage(Stage stage) {
        primaryStage = stage;
        prevScene = stage.getScene();
        prevTitle = stage.getTitle();
        primaryStage.setMaximized(true);
    }
    
    public void initData(Grupo grupo, SesionTipo template){
        this.template = template;
        this.grupo = grupo;
        
        warmT = template.getT_calentamiento();
        exerT = template.getT_ejercicio();
        exerRest = template.getD_ejercicio();
        exerN = template.getNum_ejercicios();
        circN = template.getNum_circuitos();
        circRest = template.getD_circuito();
    }

    @FXML
    private void buttonHandler(ActionEvent event) {
        switch(((Node)event.getSource()).getId()){
            case "playButton": 
                if(play){
                    servicio.start();
                    iniciado.setValue(true);
                } else {
                    servicio.cancel();
                    servicio.reset();
                    iniciado.setValue(false);
                }
                play = !play;
                break;
                
            case "restartButton": 
                servicio.restaurarInicio();
                firstime = true;
                timeText.setText("00:00:00");
                break;
                
            case "cancelButton": 
                servicio.cancel();
                servicio.reset();
                iniciado.setValue(false);
                break;
        }
    }
    
    private void fillTraining(){}

    
}

class CronoService extends Service<Void> {

    private static final int DELAY = 100;
    //tiempos
    private static long lastTime = 0; // guarda la hora del ultimo instante
    private static long startTime = 0;// guarda la hora del instante inicial del intervalo
    private static long stoppedTime = 0;// guarda la duracion del tiempo parados

    private boolean stopped = false;//indica si se ha parado el cronometro
    private boolean countdown = false;// indica si esta en cuenta atras
    private long countDownMilis;

    CronoService() {
        //cuenta atras de 30 segundos, deberia de ser configurable
        this.countDownMilis = 30 * 1000;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            protected Void call() throws Exception {
                if (!stopped) {
                    startTime = lastTime = System.currentTimeMillis();
                } else { // estabamos detenidos y nos ponemos en marcha sin cambio de estado
                    Long elapsedTime = System.currentTimeMillis() - lastTime;
                    stoppedTime = stoppedTime + elapsedTime;
                    stopped = false;
                }
                while (true) {
                    try {
                        Thread.sleep(DELAY);
                    } catch (InterruptedException ex) {
                        if (isCancelled()) {
                            break;
                        }
                    }
                    if (isCancelled()) {
                        break;
                    }
                    if (countdown) {
                        if (calculaCountDown()) {
                            break;
                        }

                    } else {
                        calculaCountUp();
                    }
                }
                return null;
            }

            private boolean calculaCountDown() {
                lastTime = System.currentTimeMillis();
                Long totalTime = (lastTime - startTime) - stoppedTime;
                Duration duration = Duration.ofMillis(countDownMilis - totalTime);
                final long minutos = duration.toMinutes();
                final long segundos = duration.minusMinutes(minutos).getSeconds();
                final long centesimas = duration.minusSeconds(segundos).toNanos() / 10000000;

                // no se como parar en la milesima justa
                if ((segundos == 0) && (centesimas < 10)) {
                    Platform.runLater(() -> {
                        tiempo.setValue(String.format("%02d", 0) + ":" + String.format("%02d", 0) + ":" + String.format("%02d", 0));
                    });
                    return true;
                } else {
                    Platform.runLater(() -> {
                        tiempo.setValue(String.format("%02d", minutos) + ":" + String.format("%02d", segundos) + ":" + String.format("%02d", centesimas));
                    });
                    return false;
                }
            }

            private void calculaCountUp() {
                lastTime = System.currentTimeMillis();
                Long totalTime = (lastTime - startTime) - stoppedTime;
                Duration duration = Duration.ofMillis(totalTime);
                final Long minutos = duration.toMinutes();
                final Long segundos = duration.minusMinutes(minutos).getSeconds();
                final Long centesimas = duration.minusSeconds(segundos).toNanos() / 10000000;
                Platform.runLater(() -> {
                    tiempo.setValue(String.format("%02d", minutos) + ":" + String.format("%02d", segundos) + ":" + String.format("%02d", centesimas));
                });
            }
        };
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        lastTime = System.currentTimeMillis();
        stopped = true;
    }

    // propiedad donde se muestra el tiempo transcurrido
    private StringProperty tiempo = new SimpleStringProperty();

    public String getTiempo() {
        return tiempo.get();
    }

    public void setTiempo(StringProperty value) {
        tiempo = value;
    }

    public StringProperty tiempoProperty() {
        return tiempo;
    }

    public void setCountDown(boolean bol) {
        countdown = bol;
    }

    public void setCountDown(int seconds) {
        this.countDownMilis = seconds * 1000;
    }

    public void restaurarInicio() {
        lastTime = 0; // guarda la hora del ultimo instante
        stoppedTime = 0;// guarda la duracion del tiempo parados

        //indica si se ha parado el cronometro
        stopped = false;
    }

}
