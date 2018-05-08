package gcs.network;

public class Network {
	//public static String mqttIp = "106.253.56.122";
	public static String mqttIp = "localhost";
	public static int mqttPort = 1883;
	public static String uavPubTopic = "/uav/pub";
	public static String uavSubTopic = "/uav/sub";
	public static String uavCameraTopic = "/uav/camera";
	
	private static UAV uav;
	
	public static void connect() {
		uav = new UAV();
		uav.connect();
	}
	
	public static void disconnect() {
		uav.disconnect();
	}
	
	public static UAV getUav() {
		return uav;
	}
}
