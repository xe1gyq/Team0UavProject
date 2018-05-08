package gcs.appmain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import gcs.hud.HudViewController;
import gcs.mission.MissionController;
import gcs.mission.WayPoint;
import gcs.mjpgstream.MjpgStream;
import gcs.network.Network;
import gcs.network.UAV;
import gcs.speech.SpeechRecognition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AppMainController implements Initializable {
	public static AppMainController instance;

	//메뉴 필드
	@FXML private MenuItem miConnConfig;
	@FXML private MenuItem miThemeConfig;
	@FXML private MenuItem miTtsConfig;
	@FXML private ImageView imgMission;
	@FXML private ImageView imgConnection;
	@FXML private Label lblNetworkType;

	//뷰 패인 필드
	@FXML private StackPane hudPane;
	
	@FXML private TabPane tabPane;
	@FXML private Tab tabCamera;
	@FXML private StackPane cameraPane;
	@FXML private Canvas cameraCanvas;
	
	@FXML private StackPane mapPane;

	//버튼 필드
	@FXML private Button btnUAVConnect;
	@FXML private Button btnArm;
	@FXML private Button btnTakeoff;
	@FXML private Button btnLand;	
	@FXML private Button btnRtl;

	//센서 정보 필드
	@FXML private Label txtRoll;
	@FXML private Label txtPitch;
	@FXML private Label txtYaw;
	@FXML private Label txtLat;
	@FXML private Label txtLng;
	@FXML private Label txtAlt;
	@FXML private Label txtHead;
	@FXML private Label txtAspeed;
	@FXML private Label txtGspeed;
	@FXML private Label txtOpticalFlowQuality;	
	@FXML private Label txtRangeFinderDistance;

	//고도 입력 필드
	@FXML private TextField txtTakeoffHeight;

	//음성 안내 및 음성 인식 필드
	public SpeechRecognition speechRecognition;
	
	public MjpgStream mjpgStream;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {		
		AppMainController.instance = this;
		initSpeechRecognition();
		initMenu();
		initHudPane();
		initTabPane();
		initMap();
		initCamera();
		initButton();			
	}
	
	public void initMenu() {
		miConnConfig.setGraphic(new ImageView(getClass().getResource("../images/menu_connection.png").toExternalForm()));
		miThemeConfig.setGraphic(new ImageView(getClass().getResource("../images/menu_theme.png").toExternalForm()));
		miTtsConfig.setGraphic(new ImageView(getClass().getResource("../images/menu_tts.png").toExternalForm()));
	
		imgMission.setImage(new Image(getClass().getResource("../images/mission.png").toExternalForm()));
		imgConnection.setImage(new Image(getClass().getResource("../images/wifi.png").toExternalForm()));
		
		miConnConfig.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					Stage dialog = new Stage();
					dialog.setTitle("NetworkConfig");
					dialog.initModality(Modality.APPLICATION_MODAL);
					dialog.initOwner(AppMain.instance.primaryStage);
					
					Parent parent = FXMLLoader.load(getClass().getResource("../network/networkconfig.fxml"));
					Scene scene = new Scene(parent);
					scene.getStylesheets().add(getClass().getResource("../style/style_dark_dialog.css").toExternalForm());
					dialog.setScene(scene);
					dialog.setResizable(false);
					dialog.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		miThemeConfig.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					Stage dialog = new Stage();
					dialog.setTitle("Theme");
					dialog.initModality(Modality.APPLICATION_MODAL);
					dialog.initOwner(AppMain.instance.primaryStage);
					
					Parent parent = FXMLLoader.load(getClass().getResource("../style/style.fxml"));
					Scene scene = new Scene(parent);
					scene.getStylesheets().add(getClass().getResource("../style/style_dark_dialog.css").toExternalForm());
					dialog.setScene(scene);
					dialog.setResizable(false);
					dialog.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void initHudPane() {
		try {
			hudPane.getChildren().add(FXMLLoader.load(getClass().getResource("../hud/hudview.fxml")));
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}
	
	public void initTabPane() {
		tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
				if(newValue == tabCamera) {
					try {
						mjpgStream = new MjpgStream(
							"tcp://" + Network.mqttIp + ":" + Network.mqttPort, 
							Network.uavCameraTopic, 
							cameraCanvas);
						mjpgStream.start();
					} catch(Exception e) {
						e.printStackTrace();
					}
				} else {
					if(mjpgStream != null) {
						mjpgStream.stop();
					}
				}
			}
		});
		tabPane.getSelectionModel().select(2);
	}
	
	public void initMap() {
		try {
			mapPane.getChildren().add(FXMLLoader.load(getClass().getResource("../mission/mission.fxml")));
		} catch(Exception e) {
			e.printStackTrace();
		}				
	}
	
	public void initCamera() {
		try {
			cameraCanvas.widthProperty().bind(cameraPane.widthProperty());
			cameraCanvas.heightProperty().bind(cameraPane.heightProperty());
		} catch(Exception e) {
			e.printStackTrace();
		}				
	}
	
	public void initSpeechRecognition() {
		speechRecognition = new SpeechRecognition();
	}
	
	public void initButton() {
		btnUAVConnect.setGraphic(new Circle(5, Color.RED));
		btnArm.setGraphic(new Circle(5, Color.rgb(0x35, 0x35, 0x35)));
		
		btnUAVConnect.setOnAction((e)-> {handleBtnUAVConnect(e);});
		btnArm.setOnAction((e)-> {handleBtnArm(e);});
		btnTakeoff.setOnAction((e)-> {handleBtnTakeoff(e);});
		btnLand.setOnAction((e)-> {handleBtnLand(e);});
		btnRtl.setOnAction((e)-> {handleBtnRtl(e);});
	}
	
	public void handleBtnUAVConnect(ActionEvent event) {
		if(btnUAVConnect.getText().equals("UAV 연결")) {
			Network.connect();
		} else {
			Network.getUav().disconnect();
		}	
	}

	public void handleBtnArm(ActionEvent e) {
		if(btnArm.getText().equals("시동 걸기")) {
			Network.getUav().arm();
		} else {
			Network.getUav().disarm();
		}
	}

	public void handleBtnTakeoff(ActionEvent e) {
		SpeechRecognition.instance.speech("이륙시키겠습니다");
		int altitude = Integer.parseInt(txtTakeoffHeight.getText());
		Network.getUav().takeoff(altitude);
	}

	public void handleBtnLand(ActionEvent e) {
		SpeechRecognition.instance.speech("착륙시키겠습니다");
		Network.getUav().land();	
	}
	
	public void handleBtnRtl(ActionEvent e) {
		SpeechRecognition.instance.speech("복귀시키겠습니다");
		Network.getUav().rtl();
		MissionController.instance.rtl();
	}

	public void viewStatus(UAV uav) {
		try {
			setStatus(uav);
			HudViewController.instance.setStatus(uav);
			MissionController.instance.setStatus(uav);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setStatus(UAV uav) {
		Platform.runLater(() -> {
			txtRoll.setText(String.valueOf(uav.roll));
			txtPitch.setText(String.valueOf(uav.pitch));
			txtYaw.setText(String.valueOf(uav.yaw));
			txtLat.setText(String.valueOf(uav.latitude));
			txtLng.setText(String.valueOf(uav.longitude));
			txtAlt.setText(String.valueOf(uav.altitude));
			txtHead.setText(String.valueOf(uav.heading) + " (0~360)");
			txtAspeed.setText(String.valueOf(uav.airSpeed));
			txtGspeed.setText(String.valueOf(uav.groundSpeed));
			txtOpticalFlowQuality.setText(String.valueOf(uav.opticalFlowQuality) + " (MAX:255, " + Math.round(uav.opticalFlowQuality*100/255) + "%)");
			txtRangeFinderDistance.setText(String.valueOf(uav.rangeFinderDistance));
			
			if(uav.connected) {
				btnUAVConnect.setText("UAV 연결 끊기");
				if(uav.armed) {
					btnArm.setText("시동 끄기");
					btnArm.setGraphic(new Circle(5, Color.RED)); 
				} else {
					btnArm.setText("시동 걸기");
					btnArm.setGraphic(new Circle(5, Color.rgb(0x35, 0x35, 0x35)));
				}
			} else {
				btnUAVConnect.setText("UAV 연결");
			}
		});
	}
}
