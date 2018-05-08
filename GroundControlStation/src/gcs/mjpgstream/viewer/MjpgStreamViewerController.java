package gcs.mjpgstream.viewer;

import java.net.URL;
import java.util.ResourceBundle;

import gcs.mjpgstream.MjpgStream;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;

public class MjpgStreamViewerController implements Initializable {
    @FXML private Canvas canvas;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //httpView();
        mqttView();        
    }
    
    private void httpView() {
        try {
            MjpgStream camStream = new MjpgStream("http://192.168.1.55:50005/?action=stream", canvas);
            camStream.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private void mqttView() {
        try {
            MjpgStream camStream = new MjpgStream("tcp://106.253.56.122:1883", "/mingyu-uav/camera2", canvas);
            camStream.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
