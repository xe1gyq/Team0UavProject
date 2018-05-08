package gcs.mission;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import gcs.appmain.AppMain;
import gcs.appmain.AppMainController;
import gcs.network.Network;
import gcs.network.UAV;
import gcs.speech.SpeechRecognition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import netscape.javascript.JSObject;

public class MissionController implements Initializable {
	public static MissionController instance;
	
	@FXML private WebView webView;
	private WebEngine webEngine;
	private JSObject jsproxy;
	
	@FXML private Slider zoomSlider;
	
	@FXML private Button btnGotoLocation;
	@FXML private Button btnMissionMake;
	@FXML private Button btnGetMissionFromMap;
	@FXML private Button btnGetMissionFromFile;
	@FXML private Button btnSaveMissionToFile;
	@FXML private Button btnMissionUpload;
	@FXML private Button btnMissionDownload;
	@FXML private Button btnMissionStart;	
	@FXML private Button btnMissionStop;
	@FXML private Button btnFenceMake;
	@FXML private Button btnFenceClear;
	@FXML private Button btnFenceUpload;
	@FXML private Button btnFenceDownload;
	@FXML private Button btnFenceEnable;
	@FXML private Button btnFenceDisable;
	@FXML private Button btnTop;
	@FXML private Button btnRight;
	@FXML private Button btnBottom;
	@FXML private Button btnLeft;
	@FXML private Button btnHeadingToNorth;
	
	@FXML private Button btnAddTakeoff;
	@FXML private Button btnAddROI;
	@FXML private Button btnAddRTL;
	@FXML private Button btnAddJump;
	@FXML private Button btnUpdateWaypoint;
	@FXML private Button btnRemoveWaypoint;
	@FXML public TableView<WayPoint> tableView;
	
	@FXML private Label lblMessage;
	
	private Thread messageThread;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
		initWebView();
		initButton();
		initTableView();
	}	
	
	private void initWebView() {		
		webEngine = webView.getEngine();
		webEngine.getLoadWorker().stateProperty().addListener(webEngineLoadStateListener);	
		webEngine.load(getClass().getResource("javascript/map.html").toExternalForm());
		zoomSlider.valueProperty().addListener(sliderValueListener);
	}
	
	private void initButton() {
		btnGotoLocation.setOnAction((event)->{handleBtnGotoLocation(event);});
		
		btnMissionMake.setOnAction((event)->{handleBtnMissionMake(event);});
		btnGetMissionFromMap.setOnAction((event)->{handleBtnGetMissionFromMap(event);});
		btnGetMissionFromFile.setOnAction((event)->{handleBtnGetMissionFromFile(event);});
		btnSaveMissionToFile.setOnAction((event)->{handleBtnSaveMissionToFile(event);});
		btnMissionUpload.setOnAction((event)->{handleBtnMissionUpload(event);});
		btnMissionDownload.setOnAction((event)->{handleBtnMissionDownload(event);});
		btnMissionStart.setOnAction((event)->{handleBtnMissionStart(event);});
		btnMissionStop.setOnAction((event)->{handleBtnMissionStop(event);});
		
		btnAddTakeoff.setOnAction((event)->{handleBtnAddTakeoff(event);});
		btnAddROI.setOnAction((event)->{handleBtnAddROI(event);});
		btnAddRTL.setOnAction((event)->{handleBtnAddRTL(event);});
		btnAddJump.setOnAction((event)->{handleBtnAddJump(event);});
		btnUpdateWaypoint.setOnAction((event)->{handleBtnUpdateWaypoint(event);});
		btnRemoveWaypoint.setOnAction((event)->{handleBtnRemoveWaypoint(event);});
		
		btnFenceMake.setOnAction((event)->{handleBtnFenceMake(event);});
		btnFenceClear.setOnAction((event)->{handleBtnFenceClear(event);});
		btnFenceUpload.setOnAction((event)->{handleBtnFenceUpload(event);});
		btnFenceDownload.setOnAction((event)->{handleBtnFenceDownload(event);});
		btnFenceEnable.setOnAction((event)->{handleBtnFenceEnable(event);});
		btnFenceDisable.setOnAction((event)->{handleBtnFenceDisable(event);});

		btnTop.setOnAction((event)->{handleBtnTop(event);});
		btnRight.setOnAction((event)->{handleBtnRight(event);});
		btnBottom.setOnAction((event)->{handleBtnBottom(event);});
		btnLeft.setOnAction((event)->{handleBtnLeft(event);});
		btnHeadingToNorth.setOnAction((event)->{handleBtnHeadingToNorth(event);});
	}
	
	public void handleBtnGotoLocation(ActionEvent e) {
		SpeechRecognition.instance.speech("지도에서 목표 지점을 클릭해야 합니다");
		gotoMake();	
	}
	
	public void handleBtnMissionMake(ActionEvent e) {
		SpeechRecognition.instance.speech("지도에서 경유지점을 클릭해야 합니다");
		missionMake();
	}
	
	public void handleBtnGetMissionFromMap(ActionEvent e) {
		getMission();
	}
	
	public void handleBtnGetMissionFromFile(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Mission Plan", "*.pln"));
		File selectedFile = fileChooser.showOpenDialog(AppMain.instance.primaryStage);
		if (selectedFile == null) return;
		try {
			FileInputStream fis = new FileInputStream(selectedFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			List<WayPoint> list = (List<WayPoint>) ois.readObject();
			ois.close();
			fis.close();
			setMission(list);
		} catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void handleBtnSaveMissionToFile(ActionEvent e) {
		ObservableList<WayPoint> observableList = tableView.getItems();
		if(observableList.size() == 0) {
			SpeechRecognition.instance.speech("저장할 미션 플랜이 없습니다");
		}
		List<WayPoint> list = observableList.stream().collect(Collectors.toList());
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Mission Plan", "*.pln"));
		File selectedFile = fileChooser.showSaveDialog(AppMain.instance.primaryStage);
		if(selectedFile == null) return;
		try {
			FileOutputStream fos = new FileOutputStream(selectedFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(list);
			oos.flush();
			oos.close();
			fos.close();
			SpeechRecognition.instance.speech("미션 플랜을 저장하였습니다");
		} catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void handleBtnMissionUpload(ActionEvent e) {
		List<WayPoint> list = tableView.getItems();
		System.out.println("올린 미션 수" + list.size());
		Network.getUav().missionUpload(list);
		SpeechRecognition.instance.speech("미션 정보를 드론에게 보냈습니다");
	}
	
	public void handleBtnMissionDownload(ActionEvent e) {
		Network.getUav().missionDownload();
	}
	
	public void handleBtnMissionStart(ActionEvent e) {
		SpeechRecognition.instance.speech("미션을 시작하겠습니다");
		Network.getUav().missionStart();
		missionStart();
	}
	
	public void handleBtnMissionStop(ActionEvent e) {
		SpeechRecognition.instance.speech("미션을 중지하겠습니다");
		Network.getUav().missionStop();
		missionStop();
	}
	
	public void handleBtnAddTakeoff(ActionEvent e) {
		addTakeoff();
	}
	
	public void handleBtnAddROI(ActionEvent e) {
		roiMake();
	}
	
	public void handleBtnAddRTL(ActionEvent e) {
		addRTL();
	}
	
	public void handleBtnAddJump(ActionEvent e) {
		addJump();
	}
	
	public void handleBtnUpdateWaypoint(ActionEvent e) {
		if(tableView.getSelectionModel().getSelectedIndex()>=0) {
			updateWaypoint();
		}
	}
	
	public void handleBtnRemoveWaypoint(ActionEvent e) {
		removeWaypoint();
	}
	
	public void handleBtnFenceMake(ActionEvent e) {
		SpeechRecognition.instance.speech("지도에서 펜스점을 클릭해야 합니다");
		fenceMake();
	}	
	
	public void handleBtnFenceClear(ActionEvent e) {
		Network.getUav().fenceClear();
		jsproxy.call("fenceClear");
	}
	
	public void handleBtnFenceUpload(ActionEvent e) {
		jsproxy.call("fenceUpload");
	}
	
	public void handleBtnFenceDownload(ActionEvent e) {
		Network.getUav().fenceDownload();
	}
	
	public void handleBtnFenceEnable(ActionEvent e) {
		Network.getUav().fenceEnable();
	}
	
	public void handleBtnFenceDisable(ActionEvent e) {
		Network.getUav().fenceDisable();
	}

	public void handleBtnTop(ActionEvent e) {
		Network.getUav().move(5, 0, 0, 0.5);
	}

	public void handleBtnBottom(ActionEvent e) {
		Network.getUav().move(-5, 0, 0, 0.5);
	}

	public void handleBtnRight(ActionEvent e) {
		Network.getUav().move(0, 5, 0, 0.5);
	}

	public void handleBtnLeft(ActionEvent e) {
		Network.getUav().move(0, -5, 0, 0.5);
	}

	public void handleBtnHeadingToNorth(ActionEvent e) {
		Network.getUav().changeHeading(0);
	}

	private void initTableView() {	
		TableColumn<WayPoint, Integer> column1 = new TableColumn<WayPoint, Integer>("순번");
		column1.setCellValueFactory(new PropertyValueFactory<WayPoint, Integer>("no"));
		column1.setPrefWidth(50);
		column1.setSortable(false);
		column1.impl_setReorderable(false); //헤더를 클릭하면 멈춤 현상을 없애기 위해
		tableView.getColumns().add(column1);
		
		TableColumn<WayPoint, String> column2 = new TableColumn<WayPoint, String>("종류");
		column2.setCellValueFactory(new PropertyValueFactory<WayPoint, String>("kind"));
		column2.setPrefWidth(150);
		column2.setSortable(false);
		column2.impl_setReorderable(false);
		tableView.getColumns().add(column2);
		
		TableColumn<WayPoint, Double> column3 = new TableColumn<WayPoint, Double>("위도/점프위치");
		column3.setCellValueFactory(new PropertyValueFactory<WayPoint, Double>("latitude"));
		column3.setPrefWidth(200);
		column3.setSortable(false);
		column3.impl_setReorderable(false);
		tableView.getColumns().add(column3);
		
		TableColumn<WayPoint, Double> column4 = new TableColumn<WayPoint, Double>("경도/반복횟수");
		column4.setCellValueFactory(new PropertyValueFactory<WayPoint, Double>("longitude"));
		column4.setPrefWidth(200);
		column4.setSortable(false);
		column4.impl_setReorderable(false);
		tableView.getColumns().add(column4);
		
		TableColumn<WayPoint, Double> column5 = new TableColumn<WayPoint, Double>("고도");
		column5.setCellValueFactory(new PropertyValueFactory<WayPoint, Double>("altitude"));
		column5.setPrefWidth(100);
		column5.setSortable(false);
		column5.impl_setReorderable(false);
		tableView.getColumns().add(column5);
	}
	
	public void setTableViewItems(List<WayPoint> list) {
		tableView.getItems().clear();
		tableView.setItems(FXCollections.observableArrayList(list));
	}
	
	private ChangeListener<State> webEngineLoadStateListener = (observable, oldValue, newValue) -> {
		if(newValue == State.SUCCEEDED) {
			Platform.runLater(() -> {
				try {
					webEngine.executeScript("console.log = function(message) { jsproxy.java.log(message); };");
					jsproxy = (JSObject) webEngine.executeScript("jsproxy");
					jsproxy.setMember("java", MissionController.this);
					//setMapSize();
				} catch(Exception e) {
					e.printStackTrace();
				}
			});
		}
	};	
	
	public void log(String message) {
		System.out.println(message);
	}	
	
	private ChangeListener<Number> sliderValueListener = (observable, oldValue, newValue) -> {
		int value = newValue.intValue();
		if(value < 3) {
			zoomSlider.setValue(3);
			return;
		}
		jsproxy.call("setMapZoom", value);	
	};	
	
	public void setStatus(UAV uav) {
		Platform.runLater(() -> {
			String lowerStatusText = uav.statusText.toLowerCase();
			if(lowerStatusText.contains("flight plan received")) {
				showMessage("미션 수신", Color.ORANGERED);
			} else if(lowerStatusText.contains("reached command")) {
				int waypointNo = Integer.parseInt(uav.statusText.substring(uav.statusText.indexOf("#")+1).trim());
				showMessage("경유점 " + waypointNo + " 도착", Color.ORANGERED);
				SpeechRecognition.instance.speech("경유점 " + waypointNo + " 에 도착하였습니다");
			} else if(lowerStatusText.contains("fence received")) {
				showMessage("울타리 수신", Color.ORANGERED);
			}
			
			if(uav.homeLat != 0.0) {
				jsproxy.call("setHomeLocation", uav.homeLat, uav.homeLng);
			}
			jsproxy.call("setUavLocation", uav.latitude, uav.longitude, uav.heading);
			
			if(uav.wayPoints.size() != 0) {
				System.out.println("가져온 미션 수:" + uav.wayPoints.size());
				setMission(uav.wayPoints);
				SpeechRecognition.instance.speech("드론의 미션 정보를 가져왔습니다");
			} 
			
			jsproxy.call("setNextWaypointNo", uav.nextWaypointNo);			
			
			if(Network.getUav().mode.equals("AUTO")) {
				for(int i=0; i<tableView.getItems().size(); i++) {
					WayPoint wp = tableView.getItems().get(i);
					if(wp.no == uav.nextWaypointNo) {
						tableView.getSelectionModel().select(wp);
					}
				}
			}
			
			if(uav.fenceEnable == 0) {
				btnFenceEnable.setGraphic(new Circle(5, Color.rgb(0x35, 0x35, 0x35)));
			} else {
				btnFenceEnable.setGraphic(new Circle(5, Color.RED)); 
			}
			
			if(uav.fencePoints.size() != 0) {
				setFence(uav.fencePoints);
				SpeechRecognition.instance.speech("펜스 정보를 가져왔습니다");
			}
		});
	}		
	
	//MapViewTest.java ���� ���
	public void setUavLocation(double latitude, double longitude, double heading) {
		if(jsproxy != null) {
			Platform.runLater(() -> {
				jsproxy.call("setUavLocation", latitude, longitude, heading);
			});
		}
	}
	
	public void setZoomSliderValue(int zoom) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				zoomSlider.setValue(zoom);
			}
		});
	}		
	
	public void gotoMake() {
		Platform.runLater(() -> {
			jsproxy.call("gotoMake");
		});
	}
	
	public void gotoStart(String data) {
		SpeechRecognition.instance.speech("목표 지점으로 이동합니다");
		Platform.runLater(() -> {
			JSONObject jsonObject = new JSONObject(data);
			double latitude = jsonObject.getDouble("lat");
			double longitude = jsonObject.getDouble("lng");
			double altitude = 10;
			Network.getUav().gotoStart(latitude, longitude, altitude);
		});
	}
	
	public void rtl() {
		Platform.runLater(() -> {
			jsproxy.call("rtlStart");
		});
	}
	
	public void missionMake() {
		Platform.runLater(() -> {
			jsproxy.call("missionMake");
		});
	}
	
	public void getMission() {
		Platform.runLater(() -> {
			jsproxy.call("getMission");
		});
	}
	
	public void getMissionResponse(String data) {
		Platform.runLater(() -> {
			List<WayPoint> list = new ArrayList<WayPoint>();			
			JSONArray jsonArray = new JSONArray(data);
			for(int i=0; i<jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				WayPoint wayPoint = new WayPoint();
				wayPoint.no = jsonObject.getInt("no");
				wayPoint.kind = jsonObject.getString("kind"); //all is "waypoint";
				wayPoint.latitude = jsonObject.getDouble("lat");
				wayPoint.longitude = jsonObject.getDouble("lng");
				wayPoint.altitude = 10;
				list.add(wayPoint);
			}
			setTableViewItems(list);
		});
	}
	
	public void setMission(List<WayPoint> wayPoints) {
		setTableViewItems(wayPoints);
		JSONArray jsonArray = new JSONArray();
		for(WayPoint wayPoint : wayPoints) {
			JSONObject jsonObject = new JSONObject();
			if(wayPoint.kind.equals("takeoff")) {
				jsonObject.put("kind",  wayPoint.kind);
				jsonObject.put("lat", Network.getUav().homeLat);
				jsonObject.put("lng", Network.getUav().homeLng);
			} else if(wayPoint.kind.equals("waypoint")) {
				jsonObject.put("kind",  wayPoint.kind);
				jsonObject.put("lat", wayPoint.latitude);
				jsonObject.put("lng", wayPoint.longitude);
			} else if(wayPoint.kind.equals("jump")) {
				jsonObject.put("kind",  wayPoint.kind);
				jsonObject.put("lat", wayPoints.get((int)wayPoint.latitude-1).latitude);
				jsonObject.put("lng", wayPoints.get((int)wayPoint.latitude-1).longitude+0.00005);
			} else if(wayPoint.kind.equals("rtl")) {
				jsonObject.put("kind",  wayPoint.kind);
				jsonObject.put("lat", Network.getUav().homeLat);
				jsonObject.put("lng", Network.getUav().homeLng+0.00005);
			} else if(wayPoint.kind.equals("roi")) {
				jsonObject.put("kind",  wayPoint.kind);
				jsonObject.put("lat", wayPoint.latitude);
				jsonObject.put("lng", wayPoint.longitude);
			}
			jsonArray.put(jsonObject);
		}
		String strMissionArr = jsonArray.toString();
		Platform.runLater(() -> {
			jsproxy.call("setMission", strMissionArr);
		});
	}
	
	public void showMessage(String message, Color color) {
		Platform.runLater(()->{
			lblMessage.setText(message);
			lblMessage.setTextFill(color);
		});
		if(messageThread != null) {
			messageThread.interrupt();
		}
		messageThread = new Thread() {
			@Override
			public void run() {
				try {Thread.sleep(3000);} catch(Exception e) {}
				Platform.runLater(()->{
					lblMessage.setText("");
				});
			}
		};
		messageThread.setDaemon(true);
		messageThread.start();
	}
	
	public void missionStart() {
		Platform.runLater(() -> {
			jsproxy.call("missionStart");
		});
	}
	
	public void missionStop() {
		Platform.runLater(() -> {
			jsproxy.call("missionStop");
		});
	}
	
	private void addTakeoff() {
		if(tableView.getItems().size() == 0) {
			SpeechRecognition.instance.speech("경유점이 최소 하나 있어야 합니다");
			return;
		}
		if(tableView.getItems().get(0).kind.equals("takeoff")) {
			SpeechRecognition.instance.speech("이륙이 이미 추가되어 있습니다");
			return;
		}
		WayPoint waypoint = new WayPoint();
		waypoint.kind = "takeoff";
		waypoint.latitude = Network.getUav().homeLat;
		waypoint.longitude = Network.getUav().homeLng;
		waypoint.altitude = 10;
		tableView.getItems().add(0, waypoint);
		for(int i=0; i<tableView.getItems().size(); i++) {
			WayPoint wp = tableView.getItems().get(i);
			wp.no = i+1;
		}
		Platform.runLater(() -> {
			jsproxy.call("addTakeoff");
		});
	}	
	
	private void addRTL() {
		if(tableView.getItems().size() == 0) {
			SpeechRecognition.instance.speech("경유점이 최소 하나 있어야 합니다");
			return;
		}
		if(tableView.getItems().get(tableView.getItems().size()-1).kind.equals("rtl")) {
			SpeechRecognition.instance.speech("착륙이 이미 추가되어 있습니다");
			return;
		}
		WayPoint waypoint = new WayPoint();
		waypoint.kind = "rtl";
		waypoint.latitude = Network.getUav().homeLat;
		waypoint.longitude = Network.getUav().homeLng;
		tableView.getItems().add(waypoint);
		waypoint.no = tableView.getItems().size();
		Platform.runLater(() -> {
			jsproxy.call("addRTL");
		});
	}	
	
	private void roiMake() {
		int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
		if(selectedIndex == -1) {
			SpeechRecognition.instance.speech("관심 위치가 추가될 행이 선택되지 않았습니다");
			return;
		}
		
		if(selectedIndex == tableView.getItems().size()-1) {
			SpeechRecognition.instance.speech("관심 위치는 맨 마지막에 추가할 수 없습니다");
			return;
		}
		
		WayPoint wp = tableView.getItems().get(selectedIndex);
		if(wp.kind.equals("roi")) {
			SpeechRecognition.instance.speech("관심 위치가 연이어 올 수 없습니다");
			return;
		}
		
		wp = tableView.getItems().get(selectedIndex+1);
		if(wp.kind.equals("roi")) {
			SpeechRecognition.instance.speech("관심 위치가 연이어 올 수 없습니다");
			return;
		}
		
		Platform.runLater(() -> {
			jsproxy.call("roiMake", selectedIndex);
		});
		SpeechRecognition.instance.speech("지도에서 관심 위치를 클릭합니다");
	}
	
	public void addROI(String data) {
		Platform.runLater(() -> {
			WayPoint waypoint = new WayPoint();
			waypoint.kind = "roi";
			JSONObject jsonObject = new JSONObject(data);
			waypoint.latitude = jsonObject.getDouble("lat");
			waypoint.longitude = jsonObject.getDouble("lng");
			int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
			if(selectedIndex != tableView.getItems().size()-1) {
				tableView.getItems().add(selectedIndex+1, waypoint);
			} else {
				tableView.getItems().add(waypoint);
			}
			for(int i=0; i<tableView.getItems().size(); i++) {
				WayPoint wp = tableView.getItems().get(i);
				wp.no = i+1;
			}
		});
	}		
	
	private void addJump() {
		WayPoint waypoint = new WayPoint();
		waypoint.kind = "jump";
		waypoint.latitude = 2;
		waypoint.longitude = -1;
		
		int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
		
		if(selectedIndex == -1) {
			SpeechRecognition.instance.speech("점프가 추가될 행을 선택하지 않았습니다");
			return;
		}
		
		WayPoint wp = tableView.getItems().get(selectedIndex);
		if(wp.kind.equals("jump")) {
			SpeechRecognition.instance.speech("점프가 연이어 올 수 없습니다");
			return;
		}
		if(selectedIndex < tableView.getItems().size()-1) {
			wp = tableView.getItems().get(selectedIndex+1);
			if(wp.kind.equals("jump")) {
				SpeechRecognition.instance.speech("점프가 연이어 올 수 없습니다");
				return;
			}
		}
		
		if(selectedIndex < tableView.getItems().size()-1) {
			tableView.getItems().add(selectedIndex+1, waypoint);
		} else {
			tableView.getItems().add(waypoint);
		}
		for(int i=0; i<tableView.getItems().size(); i++) {
			wp = tableView.getItems().get(i);
			wp.no = i+1;
		}
	}
	
	private void updateWaypoint() {
		try {
			Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.initOwner(AppMain.instance.primaryStage);
			
			Parent parent = FXMLLoader.load(getClass().getResource("updatewaypoint.fxml"));
			Scene scene = new Scene(parent);
			scene.getStylesheets().add(getClass().getResource("../style/style_dark_dialog.css").toExternalForm());
			dialog.setScene(scene);
			
			dialog.setWidth(500);
			dialog.setHeight(330);
			dialog.setResizable(false);
			dialog.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void removeWaypoint() {
		int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
		if(selectedIndex == -1) {
			return;
		}
		tableView.getItems().remove(selectedIndex);
		for(int i=0; i<tableView.getItems().size(); i++) {
			WayPoint wp = tableView.getItems().get(i);
			wp.no = i+1;
		}
		Platform.runLater(() -> {
			jsproxy.call("removeMarker", selectedIndex);
		});
	}
	
	//Fence----------------------------------------------------------
	public void fenceMake() {
		Platform.runLater(() -> {
			jsproxy.call("fenceMake");
		});
	}

	public void fenceUpload(String jsonFencePoints) {
		Network.getUav().fenceUpload(jsonFencePoints);
	}
	
	public void setFence(List<FencePoint> fencePoints) {
		JSONArray jsonArray = new JSONArray();
		for(FencePoint fencePoint : fencePoints) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("idx", fencePoint.idx);
			jsonObject.put("count", fencePoint.count);
			jsonObject.put("lat", fencePoint.lat);
			jsonObject.put("lng", fencePoint.lng);
			jsonArray.put(jsonObject);
		}
		String strFenceArr = jsonArray.toString();
		Platform.runLater(() -> {
			jsproxy.call("setFence", strFenceArr);
		});	
	}
}
