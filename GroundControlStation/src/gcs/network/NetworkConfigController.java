package gcs.network;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NetworkConfigController implements Initializable {
	@FXML private TextField txtMqttIp;
	@FXML private TextField txtMqttPort;
	@FXML private TextField txtUavPubTopic;
	@FXML private TextField txtUavSubTopic;
	@FXML private TextField txtUavCameraTopic;
	
	@FXML private Button btnApply;
	@FXML private Button btnCancel;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		getConfig();		
		
		btnApply.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {
				setConfig();
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
	
	private void getConfig() {
		txtMqttIp.setText(Network.mqttIp);
		txtMqttPort.setText(String.valueOf(Network.mqttPort));
		txtUavPubTopic.setText(Network.uavPubTopic);
		txtUavSubTopic.setText(Network.uavSubTopic);
		txtUavCameraTopic.setText(Network.uavCameraTopic);
	}
	
	private void setConfig() {
		Network.mqttIp = txtMqttIp.getText();
		Network.mqttPort = Integer.parseInt(txtMqttPort.getText());
		Network.uavPubTopic = txtUavPubTopic.getText();
		Network.uavSubTopic = txtUavSubTopic.getText();
		Network.uavCameraTopic = txtUavCameraTopic.getText();
	}
}
