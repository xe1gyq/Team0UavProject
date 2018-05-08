package gcs.appmain;

import gcs.network.Network;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppMain extends Application {	
	public static AppMain instance;
	public Stage primaryStage;
	public Scene scene;
	public String theme;

	@Override
	public void start(Stage primaryStage) throws Exception {
		instance = this;
		this.primaryStage = primaryStage;
		
		Parent root = FXMLLoader.load(getClass().getResource("appmain.fxml"));
		scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("../style/style_dark.css").toExternalForm());
		theme = "dark";
		
		primaryStage.setTitle("UAV Ground Control Station");
		primaryStage.setScene(scene);
		
		//primaryStage.setWidth(1300);
		//primaryStage.setHeight(870);
		
		primaryStage.setMaximized(true);
		
		primaryStage.show();
	}
	
	@Override
	public void stop() {
		Network.getUav().disconnect();
		System.exit(0);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}


