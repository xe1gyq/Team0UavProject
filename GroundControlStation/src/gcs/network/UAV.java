package gcs.network;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import gcs.appmain.AppMainController;
import gcs.mission.FencePoint;
import gcs.mission.WayPoint;

public class UAV implements Cloneable {
	public String systemStatus;	
	public String statusText;
	public String mode;
	public double batteryVoltage;
	public double batteryCurrent;
	public double batteryLevel;
	public double roll;
	public double pitch;
	public double yaw;
	public boolean armed;
	public double latitude;
	public double longitude;
	public double altitude;
	public double heading;
	public double airSpeed;
	public double groundSpeed;	
	public double homeLat;
	public double homeLng;
	public double rangeFinderDistance;
	public double opticalFlowQuality;	
	
	public int nextWaypointNo;
	public List<WayPoint> wayPoints;
	
	public double fenceEnable;
	public double fenceType;
	public double fenceAction;
	public double fenceRadius;
	public double fenceAltMax;
	public double fenceMargin;
	public double fenceTotal;
	public List<FencePoint> fencePoints;
	
	public boolean connected;
	private MqttClient mqttClient;
	
	public UAV() {
	}
	
	public void connect() {
		Thread thread = new Thread() {
			@Override
			public void run() {			
				try {
					mqttClient = new MqttClient("tcp://" + Network.mqttIp + ":" + Network.mqttPort, MqttClient.generateClientId(), null);
					mqttClient.setCallback(new MqttCallback() {
						String strJson;
						@Override
						public void messageArrived(String topic, MqttMessage message) throws Exception {
							strJson = new String(message.getPayload());
							dataParsing(strJson);
							connected = true;
						}
						@Override
						public void deliveryComplete(IMqttDeliveryToken token) {
						}
						@Override
						public void connectionLost(Throwable e) {
							UAV.this.disconnect();
							e.printStackTrace();
						}
					});
					MqttConnectOptions mco = new MqttConnectOptions();
					mco.setConnectionTimeout(3);
					mqttClient.connect(mco);
					mqttClient.subscribe(Network.uavPubTopic);
				} catch (Exception e) {
					UAV.this.disconnect();
					//e.printStackTrace();
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
	
	public void disconnect() {
		try {
			AppMainController.instance.setStatus(new UAV());
			mqttClient.disconnect();
			mqttClient.close();
		} catch (Exception e) {
		}
		connected = false;
	}

	private void dataParsing(String strJson) {
		try {
			JSONObject jsonObject = new JSONObject(strJson);
			systemStatus = jsonObject.getString("system_status");
			statusText = jsonObject.getString("statustext");
			mode = jsonObject.getString("mode");
			batteryVoltage = jsonObject.getDouble("battery_voltage");
			batteryCurrent = jsonObject.getDouble("battery_current");
			try {
				batteryLevel = jsonObject.getDouble("battery_level");
			} catch(Exception e) {
				//SITL에서 가끔 battery_level 을 보내지 않아
				//시뮬레이션에서 가끔 NullPointerException 발생
			}
			roll = jsonObject.getDouble("roll");
			pitch = jsonObject.getDouble("pitch");
			yaw = jsonObject.getDouble("yaw");
			armed = jsonObject.getBoolean("armed");
			latitude = jsonObject.getDouble("latitude");
			longitude = jsonObject.getDouble("longitude");
			altitude = jsonObject.getDouble("altitude");
			heading = jsonObject.getDouble("heading");
			airSpeed = jsonObject.getDouble("airspeed");
			groundSpeed = jsonObject.getDouble("groundspeed");
			homeLat = jsonObject.getDouble("homeLat");
			homeLng = jsonObject.getDouble("homeLng");
			
			try {
				rangeFinderDistance = jsonObject.getDouble("rangefinder_distance");
				opticalFlowQuality = jsonObject.getDouble("optical_flow_quality");
			} catch(Exception e) {
				rangeFinderDistance = 0;
				opticalFlowQuality = 0;
			}
			
			nextWaypointNo = jsonObject.getInt("next_waypoint_no");
			
			JSONArray jsonArrayWayPoints = jsonObject.getJSONArray("waypoints");
			List<WayPoint> listWayPoint = new ArrayList<WayPoint>();
			for(int i=0; i<jsonArrayWayPoints.length(); i++) {
				JSONObject jo = jsonArrayWayPoints.getJSONObject(i);
				WayPoint wp = new WayPoint();
				wp.no = i+1;
				wp.kind = jo.getString("kind");
				if(wp.kind.equals("takeoff")) {
					wp.altitude = jo.getDouble("alt");
				} else if(wp.kind.equals("waypoint")) {
					wp.latitude = jo.getDouble("lat");
					wp.longitude = jo.getDouble("lng");
					wp.altitude = jo.getDouble("alt");
				} else if(wp.kind.equals("rtl")) {
				} else if(wp.kind.equals("jump")) {
					wp.latitude = jo.getDouble("lat");
					wp.longitude = jo.getDouble("lng");
				} else if(wp.kind.equals("roi")) {
					wp.latitude = jo.getDouble("lat");
					wp.longitude = jo.getDouble("lng");
				}
				listWayPoint.add(wp);
			}
			wayPoints = listWayPoint;
			
			JSONObject jsonObjectFenceInfo =  jsonObject.getJSONObject("fence_info");
			fenceEnable = jsonObjectFenceInfo.getDouble("fence_enable");
			try {
				fenceType = jsonObjectFenceInfo.getDouble("fence_type");
				fenceAction = jsonObjectFenceInfo.getDouble("fence_action");
				fenceRadius = jsonObjectFenceInfo.getDouble("fence_radius");
				fenceAltMax = jsonObjectFenceInfo.getDouble("fence_alt_max");
				fenceMargin = jsonObjectFenceInfo.getDouble("fence_margin");
				fenceTotal = jsonObjectFenceInfo.getDouble("fence_total");
				JSONArray jsonArrayFencePoints = jsonObjectFenceInfo.getJSONArray("fence_points");
				List<FencePoint> listFencePoint = new ArrayList<FencePoint>();
				for(int i=0; i<jsonArrayFencePoints.length(); i++) {
					JSONObject jo = jsonArrayFencePoints.getJSONObject(i);
					FencePoint fp = new FencePoint();
					fp.idx = jo.getInt("idx");
					fp.count = jo.getInt("count");
					fp.lat = jo.getDouble("lat");
					fp.lng = jo.getDouble("lng");
					listFencePoint.add(fp);
				}
				fencePoints = listFencePoint;
			} catch(Exception e) {
				fenceType = 0;
				fenceAction = 0;
				fenceRadius = 0;
				fenceAltMax = 0;
				fenceMargin = 0;
				fenceTotal = 0;
				fencePoints = new ArrayList<FencePoint>();
			}
			
			AppMainController.instance.viewStatus((UAV)this.clone());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}	
	
	public void send(String strJson) {
		if(connected) {
			Thread thread = new Thread() {
				@Override
				public void run() {
					try {
						MqttMessage message = new MqttMessage(strJson.getBytes());
						mqttClient.publish(Network.uavSubTopic, message);
					} catch(Exception e) {
						e.printStackTrace();
						UAV.this.disconnect();
					} 
				}
			};
			thread.setDaemon(true);
			thread.start();
		}
	}	
	
	public void arm() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "arm");
		String strJson = jsonObject.toString();
		send(strJson);
	}
	
	public void disarm() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "disarm");
		String strJson = jsonObject.toString();
		send(strJson);
	}
	
	public void takeoff(int height) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "takeoff");
		jsonObject.put("height", height);
		String strJson = jsonObject.toString();
		send(strJson);	
		
	}	
	
	public void rtl() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "rtl");
		String strJson = jsonObject.toString();
		send(strJson);	
	}	
	
	public void land() {	
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "land");
		String strJson = jsonObject.toString();
		send(strJson);
	}	
	
	public void gotoStart(double latitude, double longitude, double altitude) {	
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "goto");
		jsonObject.put("latitude", latitude);
		jsonObject.put("longitude", longitude);
		jsonObject.put("altitude", altitude);
		String strJson = jsonObject.toString();
		send(strJson);
	}
	
	public void missionUpload(List<WayPoint> list) {	
		JSONObject root = new JSONObject();
		root.put("command", "mission_upload");
		JSONArray jsonArray = new JSONArray();
		for(WayPoint wp : list) {
			JSONObject jo = new JSONObject();
			jo.put("kind", wp.kind);
			jo.put("lat", wp.latitude);
			jo.put("lng", wp.longitude);
			jo.put("alt", wp.altitude);
			jsonArray.put(jo);
		}
		root.put("waypoints", jsonArray);
		String strJson = root.toString();
		send(strJson);
	}
	
	public void missionDownload() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "mission_download");
		String strJson = jsonObject.toString();
		send(strJson);
	}
	
	public void missionStart() {	
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "mission_start");
		String strJson = jsonObject.toString();
		send(strJson);
	}
	
	public void missionStop() {	
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "mission_stop");
		String strJson = jsonObject.toString();
		send(strJson);
	}
	
	public void fenceEnable() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "fence_enable");
		String strJson = jsonObject.toString();
		send(strJson);
	}	
	
	public void fenceDisable() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "fence_disable");
		String strJson = jsonObject.toString();
		send(strJson);
	}	
	
	public void fenceUpload(String jsonFencePoints) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "fence_upload");
		jsonObject.put("fence_type", 4); //4:polygon, 7:All=polygon+radius+alt_max
		jsonObject.put("fence_action", 1); //RTL
		jsonObject.put("fence_radius", 500);
		jsonObject.put("fence_alt_max", 100);
		jsonObject.put("fence_margin", 5);
		JSONArray jsonArrayFencePoints = new JSONArray(jsonFencePoints);
		jsonObject.put("fence_total", jsonArrayFencePoints.length());
		jsonObject.put("points", jsonArrayFencePoints);
		String strJson = jsonObject.toString();
		send(strJson);
	}
	
	public void fenceDownload() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "fence_download");
		String strJson = jsonObject.toString();
		send(strJson);
	}
	
	public void fenceClear() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "fence_clear");
		String strJson = jsonObject.toString();
		send(strJson);
	}

	public void move(double velocityX, double velocityY, double velocityZ, double duration) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "move");
		jsonObject.put("velocity_x", velocityX);
		jsonObject.put("velocity_y", velocityY);
		jsonObject.put("velocity_z", velocityZ);
		jsonObject.put("duration", duration);
		String strJson = jsonObject.toString();
		send(strJson);
	}

	public void changeHeading(double heading) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "change_heading");
		jsonObject.put("heading", heading);
		String strJson = jsonObject.toString();
		send(strJson);
	}
}
