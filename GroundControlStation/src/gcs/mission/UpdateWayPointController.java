package gcs.mission;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateWayPointController implements Initializable {
	public static UpdateWayPointController instance;
	
	@FXML private TextField txtNo;
	@FXML private TextField txtKind;
	@FXML private TextField txtLat;
	@FXML private TextField txtLng;
	@FXML private TextField txtAlt;
	
	@FXML private Button btnUpdate;
	@FXML private Button btnCancel;
	
	private WayPoint wayPoint;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
		
		wayPoint = MissionController.instance.tableView.getSelectionModel().getSelectedItem();
		txtNo.setText(String.valueOf(wayPoint.no));
		txtKind.setText(wayPoint.kind);
		txtLat.setText(String.valueOf(wayPoint.latitude));
		txtLng.setText(String.valueOf(wayPoint.longitude));
		txtAlt.setText(String.valueOf(wayPoint.altitude));
		
		btnUpdate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				wayPoint.latitude = Double.parseDouble(txtLat.getText());
				wayPoint.longitude = Double.parseDouble(txtLng.getText());
				wayPoint.altitude = Double.parseDouble(txtAlt.getText());
				MissionController.instance.tableView.refresh();
				Stage dialog = (Stage)btnCancel.getScene().getWindow();
				dialog.close();
			}
		});
		
		btnCancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Stage dialog = (Stage)btnCancel.getScene().getWindow();
				dialog.close();
			}
		});
	}	
}
