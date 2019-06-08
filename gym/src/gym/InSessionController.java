/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gym;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import modelo.Grupo;
import modelo.Sesion;
import modelo.SesionTipo;

/**
 * FXML Controller class
 *
 * @author Stéphane
 */
public class InSessionController implements Initializable {
    @FXML
    private Text partMode;
    @FXML
    private ProgressBar exercisesProgress;
    @FXML
    private ProgressBar circuitsProgress;
    @FXML
    private Text timeText;
    @FXML
    private Button playButton;
    @FXML
    private Button skipButton;
    @FXML
    private Button restartButton;

   private Stage primaryStage;
    private Scene prevScene;
    private String prevTitle;
    
    private int warmT, exerT, exerRest, exerN, circN, circRest;
    
    private SesionTipo template;
    private Grupo grupo;
    
    private CronoService servicio;
    private Property<Boolean> iniciado = new SimpleBooleanProperty(false);
    private boolean firstime;
    
    private boolean play = false;
    
    private ArrayList<Integer> training;
    private ArrayList<Integer> part;
    public static final int WARMING = 0;
    public static final int EXERCISE = 1;
    public static final int REST = 2;
    
    private int index = 0;
    
    private int size;
    
    private long lastTime;
    
    private long initTime;
    
    Image playImg = new Image(getClass().getResource("/images/play-button.png").toString() );
    Image pauseImg = new Image(getClass().getResource("/images/pause.png").toString() );
    @FXML
    private ImageView playImage;
    
    private int countE = 0;
    private int countC = 0;
    
    private BooleanProperty aux = new SimpleBooleanProperty(false);

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        exercisesProgress.setProgress(0);
        circuitsProgress.setProgress(0);
        playImage.imageProperty().set(pauseImg);
        timeText.textProperty().addListener((observable, oldVal, newVal) -> { 
            if(index == size - 1)
                    aux.set(true);
            if(newVal.equals("00:00")){
                servicio.cancel();
                bPlaySoundOnAction();
                index++;
                if(index == size + 1){
                    aux.set(true);
                }
                if(index == size){
                    exercisesProgress.setProgress(1);
                    circuitsProgress.setProgress(1);
                    
                    lastTime = System.currentTimeMillis();
                    end();
                    
                    
                } else {
                    servicio = new CronoService(training.get(index) * 1000 + 1000);
                    servicio.setTiempo(timeText.textProperty());
                    servicio.start();
                    updatePart();
                }
                
            }
        });
        
        skipButton.disableProperty().bind(aux);
        playButton.disableProperty().bind(aux);
    }    

    @FXML
    private void buttonHandler(ActionEvent event) {
        switch(((Node)event.getSource()).getId()){
            case "playButton": 
                if(play){
                    servicio.start();
                    playImage.imageProperty().set(pauseImg);
                } else {
                    servicio.cancel();
                    servicio.reset();
                    playImage.imageProperty().set(playImg);
                }
                play = !play;
                break;
                
            case "restartButton": 
                index = 0;
                servicio.cancel();
                servicio = new CronoService(training.get(index) * 1000 + 1000);
                servicio.setTiempo(timeText.textProperty());
                servicio.start();
                play = false;
                playImage.imageProperty().set(pauseImg);
                exercisesProgress.setProgress(0);
                circuitsProgress.setProgress(0);
                countE = 0;
                countC = 0;
                aux.set(false);
                break;
                
            case "skipButton": 
                index++;
                if(index == size + 1){
                    aux.set(true);
                    timeText.setText("00:00");
                    end();
                }
                
                
                if(index < size){
                    servicio.cancel();
                    servicio = new CronoService(training.get(index) * 1000 + 1000);
                    servicio.setTiempo(timeText.textProperty());
                    servicio.start();
                    play = false;
                    playImage.imageProperty().set(pauseImg);
                }
                break;
        }
        if (index != size)
            updatePart();
    }
    
    void initStage(Stage stage) {
        primaryStage = stage;
        prevScene = stage.getScene();
        prevTitle = stage.getTitle();
        
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
        
        fillTraining();
        
        initTime = System.currentTimeMillis();
        
        servicio = new CronoService(training.get(index) * 1000);
        servicio.setTiempo(timeText.textProperty());
        servicio.start();
        
        size = part.size();
        
        updatePart();
    }
    
    private void fillTraining(){
        
        training = new ArrayList();
        part = new ArrayList();
        if(warmT != 0){
            training.add(warmT);
            part.add(WARMING);
        }
            
        for(int i = 0; i < circN;i++){
            for(int j = 0; j< exerN;j++){
                training.add(exerT);
                part.add(EXERCISE);
                if (j != exerN - 1 && exerRest != 0){
                    training.add(exerRest);
                    part.add(REST);
                }
            }
            if(i != circN -1 && circRest != 0){
                training.add(circRest);
                part.add(REST);
            }
        }
    }
    
    private void end(){
        Sesion s = new Sesion();
        s.setFecha(LocalDateTime.now());
        s.setTipo(template);
        Duration d = Duration.ofMillis(lastTime - initTime);
        s.setDuracion(d);
        grupo.getSesiones().add(s);
        
        grupo.setDefaultTipoSesion(template);
    }
    
    private void bPlaySoundOnAction() {
        AudioClip plonkSound = new AudioClip(getClass().getResource("/sounds/ZenTemplateBell.wav").toString()    );
        plonkSound.play();
    }
    
    private void updatePart(){
        switch(part.get(index)){
            case WARMING:
                partMode.setText("WARMING TIME");
                break;
                
            case EXERCISE:
                partMode.setText("EXERCISE TIME");
                if(index == size - 1){
                    partMode.setText("LAST EXERCISE TIME");
                }
                if(countE == exerN){
                    countE = 0;
                    countC++;
                    if(countC == circN){
                        countE=exerN;
                    }
                    
                } 
                updateProgress();
                countE++;
                break;
                
            case REST:
                updateProgress();
                partMode.setText("RESTING TIME");
                break;
        }
    }
    
    private void updateProgress(){
        circuitsProgress.setProgress((double)countC/circN);
        exercisesProgress.setProgress((double)countE/exerN);
    }
    
}


class CronoService extends Service<Void> {

    private static final int DELAY = 100;
    //tiempos
    private static long lastTime = 0; // guarda la hora del ultimo instante
    private static long startTime = 0;// guarda la hora del instante inicial del intervalo
    private static long stoppedTime = 0;// guarda la duracion del tiempo parados

    private boolean stopped = false;//indica si se ha parado el cronometro
    private boolean countdown = true;// indica si esta en cuenta atras
    private long countDownMilis;
    
    private ArrayList<Integer> training;
    
    private int index;

    public CronoService(long currT) {
        countDownMilis = currT;
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
                    calculaCountDown();
                }
                return null;
            }


            private boolean calculaCountDown() {
                lastTime = System.currentTimeMillis();
                Long totalTime = (lastTime - startTime) - stoppedTime;
                Duration duration = Duration.ofMillis(countDownMilis - totalTime);
                final long minutos = duration.toMinutes();
                final long segundos = duration.minusMinutes(minutos).getSeconds();

                // no se como parar en la milesima justa
                if ((segundos == 0)) {
                    Platform.runLater(() -> {
                        tiempo.setValue(String.format("%02d", minutos) + ":" + String.format("%02d", 0));
                    });
                    return true;
                } else {
                    Platform.runLater(() -> {
                        tiempo.setValue(String.format("%02d", minutos) + ":" + String.format("%02d", segundos));
                    });
                    return false;
                }
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
        startTime = 0;// guarda la hora del instante inicial del intervalo
        stoppedTime = 0;// guarda la duracion del tiempo parados

        //indica si se ha parado el cronometro
        stopped = false;
    }

}
