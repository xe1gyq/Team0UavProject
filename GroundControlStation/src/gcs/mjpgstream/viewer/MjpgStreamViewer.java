package gcs.mjpgstream.viewer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MjpgStreamViewer extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("mjpgstreamviewer.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        System.exit(0);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
