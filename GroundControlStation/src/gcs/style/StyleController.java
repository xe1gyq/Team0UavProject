package gcs.style;

import java.net.URL;
import java.util.ResourceBundle;

import gcs.appmain.AppMain;
import gcs.network.Network;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

public class StyleController implements Initializable {
	@FXML private RadioButton rbDarkTheme;
	@FXML private RadioButton rbWhiteTheme;
	@FXML private Button btnApply;
	@FXML private Button btnCancel;
	
	private String theme;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if(AppMain.instance.theme.equals("dark")) {
			rbDarkTheme.setSelected(true);
		} else if(AppMain.instance.theme.equals("white")) {
			rbWhiteTheme.setSelected(true);
		}
		btnApply.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {
				AppMain.instance.scene.getStylesheets().clear();
				if(rbDarkTheme.isSelected()) {
					AppMain.instance.scene.getStylesheets().add(getClass().getResource("../style/style_dark.css").toExternalForm());
					AppMain.instance.theme = "dark";
				} else if(rbWhiteTheme.isSelected()) {
					AppMain.instance.scene.getStylesheets().add(getClass().getResource("../style/style_white.css").toExternalForm());
					AppMain.instance.theme = "white";
				}
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
