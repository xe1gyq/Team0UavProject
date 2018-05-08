package gcs.hud;

import java.net.URL;
import java.util.ResourceBundle;

import gcs.appmain.AppMainController;
import gcs.network.Network;
import gcs.network.UAV;
import gcs.speech.SpeechRecognition;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class HudViewController implements Initializable {
	public static HudViewController instance;
	
	private double width;
	private double height;
	
	@FXML private Canvas canvas1;
	private GraphicsContext ctx1;
	private LinearGradient skyLinearGradient;
	private LinearGradient groundLinearGradient;
	private double pitchDistance;	
	private double roll;
	private double tempRoll;
	private double pitch;
	private double tempPitch;
	private double yaw;
	private double translateX;
	private double translateY;
	
	@FXML private Canvas canvas2;
	private GraphicsContext ctx2;	
	
	@FXML private Label lblSystemStatus;
	@FXML private Label lblArmed;
	@FXML private Label lblMode;
	@FXML private Label lblAltitude;
	@FXML private Label lblBattery;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
		
		width = canvas1.getWidth();
		height = canvas1.getHeight();
		
		initCanvasLayer1();
		initCanvasLayer2();
		
		ViewLoop viewLoop = new ViewLoop();
		viewLoop.start();
	}
	
	private void initCanvasLayer1() {
		this.ctx1 = canvas1.getGraphicsContext2D();
		//this.skyLinearGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop[] { new Stop(0, Color.rgb(0, 0, 0xff)), new Stop(1, Color.rgb(0x87, 0xce, 0xfa))});
		this.skyLinearGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop[] { new Stop(0.7, Color.rgb(0, 0, 0xff)), new Stop(1, Color.rgb(0x87, 0xce, 0xfa))});
		//this.groundLinearGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop[] { new Stop(0, Color.rgb(0x9b, 0xb8, 0x24)), new Stop(1, Color.rgb(0x41, 0x4f, 0x07))});
		this.groundLinearGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop[] { new Stop(0, Color.rgb(0xbc, 0xe5, 0x5c)), new Stop(0.3, Color.rgb(0x66, 0x4b, 0x00))});
		this.pitchDistance = height/2/45;

		//공통 스타일
		this.ctx1.setFont(Font.font("Arial", FontWeight.BOLD, 15));
		this.ctx1.setTextBaseline(VPos.CENTER);
		this.ctx1.setLineWidth(1.5);
	}
	
	private void initCanvasLayer2() {
		this.ctx2 = canvas2.getGraphicsContext2D();
		this.ctx2.translate(width/2, height/2);
	}
	
	private void layer1Draw() {
		//회전 복귀
		if(this.tempRoll != 0) {
			this.ctx1.rotate(this.tempRoll);
		}

		//원점 복귀
		if(this.translateX != 0) {
			this.ctx1.translate(-this.translateX, -this.translateY);
		}

		//화면 지우기
		this.ctx1.clearRect(0, 0, width, height);

		//원점 이동
		this.translateX = this.width/2;
		this.tempPitch = this.pitch;
		this.translateY = this.height/2 + (this.height/2/45)*this.tempPitch;
		this.ctx1.translate(this.translateX, this.translateY);

		//Roll 회전
		this.tempRoll = this.roll;
		this.ctx1.rotate(-this.tempRoll);

		//배경 그리기
		this.ctx1.setFill(this.skyLinearGradient);
		this.ctx1.fillRect(-(500+this.translateX), -(500+this.translateY), 2*(500+this.translateX), (500+this.translateY));
		this.ctx1.setFill(this.groundLinearGradient);
		this.ctx1.fillRect(-(500+this.translateX), 0, 2*(500+this.translateX), (500+this.translateY));

		//pitch 0점 선 그리기------------------------------
		this.ctx1.setStroke(Color.rgb(0x0, 0x66, 0x0));
		this.ctx1.strokeLine(-50, 0, 50, 0);
		this.ctx1.setFill(Color.WHITE);
		this.ctx1.fillText("0", -70, 0);

		//pitch 스카이 선 그리기
		this.ctx1.setStroke(Color.rgb(0xff, 0xff, 0xff));
		for(int i=5; i<=(25+tempPitch); i+=5) {
			this.ctx1.strokeLine(-((i%2==0)?50:25), -(this.pitchDistance*i), ((i%2==0)?50:25), -(this.pitchDistance*i));
			this.ctx1.setFill(Color.WHITE);
			if(i%2==0) {
				this.ctx1.fillText(String.valueOf(i), -80, -(this.pitchDistance*i));
			}
		}
		this.ctx1.setStroke(Color.rgb(0xff, 0xff, 0xff));
		//pitch 그라운드 선 그리기
		for(int i=5; i<=(25-tempPitch); i+=5) {
			this.ctx1.strokeLine(-((i%2==0)?50:25), (this.pitchDistance*i), ((i%2==0)?50:25), (this.pitchDistance*i));
			this.ctx1.setFill(Color.WHITE);
			if(i%2==0) {
				this.ctx1.fillText(String.valueOf(-i), -80, (this.pitchDistance*i));
			}
		}	
	}

	private void layer2Draw() {
		//UAV 선 그리기
		this.ctx2.setLineWidth(3);
		this.ctx2.setStroke(Color.rgb(0xff, 0x0, 0x0));
		this.ctx2.strokeLine(0, 0, 0 - 50, 20);	
		this.ctx2.strokeLine(0, 0, 50, 20);
		this.ctx2.strokeLine(-(this.height/2/2), 0, -(this.height/2/4*3), 0);	
		this.ctx2.strokeLine((this.height/2/2), 0, (this.height/2/4*3), 0);

		//UAV 원 그리기
		this.ctx2.setLineWidth(1);
		this.ctx2.setStroke(Color.rgb(0xff, 0xff, 0xff));
		this.ctx2.setLineDashes(1, 5);
		this.ctx2.strokeArc(-(this.height/2/4*3), -(this.height/2/4*3), 2*(this.height/2/4*3), 2*(this.height/2/4*3), 0, 180, ArcType.OPEN);
	}
	
    private class ViewLoop extends AnimationTimer {
        @Override
        public void handle(long now) {
        	layer1Draw();	
			layer2Draw();
        }
    }
    
	public void setStatus(UAV uav) {
		roll = uav.roll;
		pitch = uav.pitch;
		yaw = uav.yaw;
		Platform.runLater(()->{
			lblSystemStatus.setText(uav.systemStatus);
			if(uav.armed) {
				if(!lblArmed.getText().equals("ARMED")) {
					lblArmed.setText("ARMED");
					SpeechRecognition.instance.speech("시동되었습니다");
				}
			} else {
				if(!lblArmed.getText().equals("DISARMED")) {
					lblArmed.setText("DISARMED");
					SpeechRecognition.instance.speech("시동이 끄졌습니다");
				}
			}
			lblMode.setText(uav.mode);
			lblAltitude.setText(String.valueOf(uav.altitude));
			lblBattery.setText("BAT: " + uav.batteryVoltage + "V " + uav.batteryCurrent + "A " + uav.batteryLevel + "%");
		});
	}    
	
	public void setRollPitchYaw(double roll, double pitch, double yaw) {
		this.roll = roll;
		this.pitch = pitch;
		this.yaw = yaw;
	}
}













