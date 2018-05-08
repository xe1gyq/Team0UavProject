#-*- coding:utf-8 -*-
#######################################################
# pip install paho-mqtt
# pip install simplejson
# pip install dronekit
#######################################################
from __future__ import print_function
from dronekit import connect, VehicleMode, LocationGlobalRelative, Command
from pymavlink import mavutil
import paho.mqtt.client as mqtt
import time
import threading
import simplejson

#예외 발생시 예외 내용 출력을 위해 True로 설정----------------------
debug = True

#Autopilot과 연결-----------------------------------------
vehicle = connect("udp:172.30.1.23:14560", wait_ready=True)
#vehicle = connect("udp:192.168.3.18:14560", wait_ready=True)
#vehicle = connect("/dev/ttyS0", wait_ready=True, baud=57600)

#MQTT Broker와 연결하기 위한 정보-----------------------------
#mqtt_ip = "106.253.56.122"
mqtt_ip = "localhost"
mqtt_port = 1883
uav_pub_topic = "/uav/pub"
uav_sub_topic = "/uav/sub"

#MQTT Broker와 연결---------------------------------------    
mqtt_client = None
def connect_mqtt():
    try:
        global mqtt_client
        global mqtt_ip
        global mqtt_port
        
        if mqtt_client != None: 
            mqtt_client.disconnect()
        
        mqtt_client = mqtt.Client()   
        mqtt_client.on_connect = on_connect
        mqtt_client.on_disconnect = on_disconnect
        mqtt_client.connect(mqtt_ip, mqtt_port)
        mqtt_client.loop_forever()
    except Exception as e:
        if debug: print(">>>", type(e), "connect_mqtt():", e)
        time.sleep(1)
        fail_safe("connect_mqtt_fail")
#------------------------------------------------------
mqtt_client_connected = False
pub_thread = None
def on_connect(client, userdata, flags, rc):
    global mqtt_client_connected
    global pub_thread
    try:
        print(">>> UavMqttClient: MQTT Broker Connected")
        mqtt_client_connected = True
        
        if pub_thread == None or not pub_thread.is_alive():
            pub_thread = threading.Thread(target=send_data)
            pub_thread.setDaemon(True)
            pub_thread.start()
            
        mqtt_client.on_message = on_message
        mqtt_client.subscribe(uav_sub_topic)    
    except Exception as e:
        if debug: print(">>>", type(e), "on_connect():", e)
        fail_safe("connect_mqtt_fail")
#------------------------------------------------------
def on_disconnect(client, userdata, rc):
    global mqtt_client_connected
    print(">>> UavMqttClient: MQTT Broker Disconnected")
    mqtt_client_connected = False
    fail_safe("connect_mqtt_fail")
#------------------------------------------------------
def fail_safe(message):
    if message == "connect_mqtt_fail":
        print(">>> fail_safe: connect_mqtt()")
        connect_mqtt() 
#------------------------------------------------------
def send_data():
    data = {}
    while True:
        try:
            if mqtt_client_connected == True:
                data.clear()
    
                send_autopilot_version_info(data)
                send_system_status_info(data)
                send_battery_info(data)
                send_gps_info(data)
                send_rangefinder_info(data)
                send_mode_info(data)
                send_armed_info(data)
                send_global_position_int_info(data)
                send_vfr_hud_info(data)
                send_attitude_info(data)
                send_home_position_info(data)
                send_statustext_info(data)            
                send_mission_info(data)
                send_fence_info(data)
                
                json = simplejson.JSONEncoder().encode(data)
                mqtt_client.publish(uav_pub_topic, json)
                #print(json)
                
            time.sleep(0.1)
        except Exception as e:
            if debug: print(">>>", type(e), "send_data():", e)
#------------------------------------------------------
'''
[Resonse Message]
https://github.com/mavlink/mavlink/blob/master/message_definitions/v1.0/common.xml
    <message id="0" name="HEARTBEAT">
        <field type="uint8_t" name="type" enum="MAV_TYPE"> -> 2(MAV_TYPE_QUADROTOR)
        <field type="uint8_t" name="autopilot" enum="MAV_AUTOPILOT"> -> 3(MAV_AUTOPILOT_ARDUPILOTMEGA)   
    <message id="148" name="AUTOPILOT_VERSION"> 
        <field type="uint32_t" name="flight_sw_version"> -> 50724864
            major   = flight_sw_version >> 24 & 0xFF -> 3
            minor   = flight_sw_version >> 16 & 0xFF -> 6
            patch   = flight_sw_version >> 8  & 0xFF -> 0
            release = flight_sw_version & 0xFF -> 0(FIRMWARE_VERSION_TYPE_DEV)

[Request Message]
https://github.com/mavlink/mavlink/blob/master/message_definitions/v1.0/ardupilotmega.xml
    <message id="183" name="AUTOPILOT_VERSION_REQUEST">
      <description>Request the autopilot version from the system/component.</description>
      <field type="uint8_t" name="target_system">System ID</field>
      <field type="uint8_t" name="target_component">Component ID</field>
    </message>
    
[Request Code] 
vehicle.message_factory.autopilot_version_request_send(0,0)

[Response Message]
https://github.com/mavlink/mavlink/blob/master/message_definitions/v1.0/common.xml
    <message id="148" name="AUTOPILOT_VERSION"> 
        <field type="uint32_t" name="flight_sw_version"> -> 50724864
            major   = flight_sw_version >> 24 & 0xFF -> 3
            minor   = flight_sw_version >> 16 & 0xFF -> 6
            patch   = flight_sw_version >> 8  & 0xFF -> 0
            release = flight_sw_version & 0xFF -> 0(FIRMWARE_VERSION_TYPE_DEV)    

[Dronkit]
dronekit-python\dronekit\__init__.py\class Version\def __str__()
    str(vehicle.version) -> "APM:Copter-3.6.0-dev0"
'''
'''
@vehicle.on_message('HEARTBEAT')
def heartbeat_receive_callback(self, name, message):
    print(message)
'''
'''
@vehicle.on_message('AUTOPILOT_VERSION')
def autopilot_version_receive_callback(self, name, message):
    print(message)
'''    
def send_autopilot_version_info(data):
    data["autopilot_version"] = str(vehicle.version)
#------------------------------------------------------
'''
[Response]
https://github.com/mavlink/mavlink/blob/master/message_definitions/v1.0/common.xml
    <message id="0" name="HEARTBEAT">
        <field type="uint8_t" name="system_status" enum="MAV_STATE"> -> 3(MAV_STATE_STANDBY)
        
[Dronkit]
dronekit-python\dronekit\__init__.py\class Vehicle\def system_status()
    vehicle.system_status.state -> "STANDBY" 
'''
'''
@vehicle.on_message('HEARTBEAT')
def heartbeat_receive_callback(self, name, message):
    print(message)
'''    
def send_system_status_info(data):
    data["system_status"] = vehicle.system_status.state
#------------------------------------------------------
'''
[Response]
https://github.com/mavlink/mavlink/blob/master/message_definitions/v1.0/common.xml
    <message id="1" name="SYS_STATUS">
      <field type="uint16_t" name="voltage_battery" units="mV">
      <field type="int16_t" name="current_battery" units="cA">
      <field type="int8_t" name="battery_remaining" units="%"> -> level
      
[Dronkit]
dronekit-python\dronekit\__init__.py\class Vehicle\def __init__()\@self.on_message('SYS_STATUS')
    vehicle.battery.voltage
    vehicle.battery.current
    vehicle.battery.level     
'''
def send_battery_info(data):
    data["battery_voltage"] = vehicle.battery.voltage
    data["battery_current"] = vehicle.battery.current
    data["battery_level"] = vehicle.battery.level     
#------------------------------------------------------
'''
[Response]
https://github.com/mavlink/mavlink/blob/master/message_definitions/v1.0/common.xml
    <message id="24" name="GPS_RAW_INT">
      <field type="uint8_t" name="fix_type" enum="GPS_FIX_TYPE">
      <field type="uint8_t" name="satellites_visible">

[Dronkit]
dronekit-python\dronekit\__init__.py\class Vehicle\def __init__()\@self.on_message('GPS_RAW_INT')
    vehicle.gps_0.fix_type
    vehicle.gps_0.satellites_visible 
'''
    
def send_gps_info(data):
    data["gps_fix_type"] = vehicle.gps_0.fix_type
    data["gps_satellites_visible"] = vehicle.gps_0.satellites_visible    
#------------------------------------------------------
'''
[Response]
https://github.com/mavlink/mavlink/blob/master/message_definitions/v1.0/ardupilotmega.xml
    <message id="173" name="RANGEFINDER">
      <field type="float" name="distance" units="m">    
      <field type="float" name="voltage" units="V"> 0v:0m ~ 5v:MAXm

[Dronkit]
dronekit-python\dronekit\__init__.py\class Vehicle\def __init__()\@self.on_message('RANGEFINDER')
'''
    
def send_rangefinder_info(data):
    data["rangefinder_distance"] = vehicle.rangefinder.distance
    data["rangefinder_voltage"] = vehicle.rangefinder.voltage
    data["optical_flow_quality"] = optical_flow_quality

optical_flow_quality = 0
@vehicle.on_message("OPTICAL_FLOW")
def optical_flow_message(self, name, message):
    global optical_flow_quality
    optical_flow_quality = message.quality
    
#------------------------------------------------------
'''
[Response]
https://github.com/mavlink/mavlink/blob/master/message_definitions/v1.0/common.xml
    <message id="0" name="HEARTBEAT">
      <field type="uint32_t" name="custom_mode"> 
            https://github.com/mavlink/mavlink/blob/master/message_definitions/v1.0/ardupilotmega.xml         
                <enum name="COPTER_MODE">
                  <entry value="0" name="COPTER_MODE_STABILIZE"/>
                  <entry value="1" name="COPTER_MODE_ACRO"/>
                  <entry value="2" name="COPTER_MODE_ALT_HOLD"/>
                  <entry value="3" name="COPTER_MODE_AUTO"/>
                  <entry value="4" name="COPTER_MODE_GUIDED"/>
                  <entry value="5" name="COPTER_MODE_LOITER"/>
                  <entry value="6" name="COPTER_MODE_RTL"/>
                  <entry value="7" name="COPTER_MODE_CIRCLE"/>
                  <entry value="9" name="COPTER_MODE_LAND"/>
                  <entry value="11" name="COPTER_MODE_DRIFT"/>
                  <entry value="13" name="COPTER_MODE_SPORT"/>
                  <entry value="14" name="COPTER_MODE_FLIP"/>
                  <entry value="15" name="COPTER_MODE_AUTOTUNE"/>
                  <entry value="16" name="COPTER_MODE_POSHOLD"/>
                  <entry value="17" name="COPTER_MODE_BRAKE"/>
                  <entry value="18" name="COPTER_MODE_THROW"/>
                  <entry value="19" name="COPTER_MODE_AVOID_ADSB"/>
                  <entry value="20" name="COPTER_MODE_GUIDED_NOGPS"/>
                  <entry value="21" name="COPTER_MODE_SMART_RTL"/>
                </enum>      
                
[Dronkit]
dronekit-python\dronekit\__init__.py\class Vehicle\def __init__()\@self.on_message('HEARTBEAT')
    vehicle.mode.name               
'''
'''
@vehicle.on_message('HEARTBEAT')
def heartbeat_receive_callback(self, name, message):
    print(message)
'''
    
def send_mode_info(data):
    data["mode"] = vehicle.mode.name
#------------------------------------------------------
'''
[Response]
https://github.com/mavlink/mavlink/blob/master/message_definitions/v1.0/common.xml
    <message id="0" name="HEARTBEAT">
      <field type="uint8_t" name="base_mode" enum="MAV_MODE_FLAG" display="bitmask"> -> base_mode & mavutil.mavlink.MAV_MODE_FLAG_SAFETY_ARMED(128)
          
[Dronkit]
dronekit-python\dronekit\__init__.py\class Vehicle\def __init__()\@self.on_message('HEARTBEAT')
    vehicle.armed  
'''
    
def send_armed_info(data):
    data["armed"] = vehicle.armed
#------------------------------------------------------
'''
[Response]
https://github.com/mavlink/mavlink/blob/master/message_definitions/v1.0/common.xml
    <message id="33" name="GLOBAL_POSITION_INT">
      <field type="int32_t" name="lat" units="degE7"> lat/1.0e7
      <field type="int32_t" name="lon" units="degE7"> lon/1.0e7
      <field type="int32_t" name="relative_alt" units="mm"> relative_alt / 1000.0 (millimeters)
    </message>
    
[Dronkit]
dronekit-python\dronekit\__init__.py\class Vehicle\def __init__()\@vehicle.on_message('GLOBAL_POSITION_INT')    
    vehicle.location.global_relative_frame.lat
    vehicle.location.global_relative_frame.lon
    vehicle.location.global_relative_frame.alt
'''

def send_global_position_int_info(data):
    data["latitude"] = vehicle.location.global_relative_frame.lat
    data["longitude"] = vehicle.location.global_relative_frame.lon
    data["altitude"] = vehicle.location.global_relative_frame.alt
#------------------------------------------------------
'''
[Response]
https://github.com/mavlink/mavlink/blob/master/message_definitions/v1.0/common.xml
    <message id="74" name="VFR_HUD">
      <field type="float" name="airspeed" units="m/s">
      <field type="float" name="groundspeed" units="m/s">
      <field type="int16_t" name="heading" units="deg"> (0..360, 0=north)

[Dronkit]
dronekit-python\dronekit\__init__.py\class Vehicle\def __init__()\@vehicle.on_message('GLOBAL_POSITION_INT')    
    vehicle.heading
    vehicle.airspeed
    vehicle.groundspeed     
'''
    
def send_vfr_hud_info(data):
    data["heading"] = vehicle.heading
    data["airspeed"] = vehicle.airspeed
    data["groundspeed"] = vehicle.groundspeed
#------------------------------------------------------
'''
[Response]
https://github.com/mavlink/mavlink/blob/master/message_definitions/v1.0/common.xml
    <message id="30" name="ATTITUDE">
      <field type="float" name="roll" units="rad"> (-pi..+pi)  degree = roll * 57.2958
      <field type="float" name="pitch" units="rad"> (-pi..+pi) degree = pitch * 57.2958
      <field type="float" name="yaw" units="rad"> (-pi..+pi) degree = yaw * 57.2958

[Dronkit]
dronekit-python\dronekit\__init__.py\class Vehicle\def __init__()\@self.on_message('ATTITUDE')   
    vehicle.attitude.roll
    vehicle.attitude.pitch
    vehicle.attitude.yaw        
'''
    
def send_attitude_info(data):
    data["roll"] = vehicle.attitude.roll * 57.2958
    data["pitch"] = vehicle.attitude.pitch * 57.2958
    data["yaw"] = vehicle.attitude.yaw * 57.2958
#------------------------------------------------------ 
'''
[Response]
https://github.com/mavlink/mavlink/blob/master/message_definitions/v1.0/common.xml
    <message id="242" name="HOME_POSITION">
      <field type="int32_t" name="latitude" units="degE7"> latitude/1.0e7
      <field type="int32_t" name="longitude" units="degE7"> longitude/1.0e7
      <field type="int32_t" name="altitude" units="mm"> altitude/1000.0

[Dronkit]
dronekit-python\dronekit\__init__.py\class Vehicle\def __init__()\@self.on_message(['HOME_POSITION']) 
    vehicle.home_location.lat
    vehicle.home_location.lon
    vehicle.home_location.alt
    
    when disarmed, home_location is None
    when armed, 'HOME_POSITION' message received and home_location asigned    
'''
    
def send_home_position_info(data):
    try:
        data["homeLat"] = vehicle.home_location.lat
        data["homeLng"] = vehicle.home_location.lon
        data["homealt"] = vehicle.home_location.alt
    except Exception as e: 
        data["homeLat"] = 0
        data["homeLng"] = 0
        data["homealt"] = 0
#------------------------------------------------------     
'''
[Response]
https://github.com/mavlink/mavlink/blob/master/message_definitions/v1.0/common.xml
    <message id="253" name="STATUSTEXT">
      <field type="char[50]" name="text">
      
[Dronkit]
http://python.dronekit.io/automodule.html#dronekit.Vehicle.on_message      
'''
statustext = ""

@vehicle.on_message('STATUSTEXT')
def statustext_receive_callback(self, name, message):
    global statustext
    statustext = message.text.decode('utf-8').rstrip()
    
def send_statustext_info(data):
    global statustext
    data["statustext"] = statustext
    statustext = ""    
#------------------------------------------------------
'''
[Response]
https://github.com/mavlink/mavlink/blob/master/message_definitions/v1.0/common.xml
    <message id="42" name="MISSION_CURRENT">
      <field type="uint16_t" name="seq">   
      
dronekit-python\dronekit\__init__.py\class Vehicle\def __init__()
    @self.on_message(['WAYPOINT_CURRENT', 'MISSION_CURRENT'])  
      
vehicle.commands.next    
    dronekit-python\dronekit\__init__.py\class CommandSequence\def __init__()
        @property def next(self): 
-------------------------------------------------------- 
[Request]
https://github.com/mavlink/mavlink/blob/master/message_definitions/v1.0/common.xml
    <message id="43" name="MISSION_REQUEST_LIST">
      <field type="uint8_t" name="target_system">System ID</field>
      <field type="uint8_t" name="target_component">Component ID</field>

vehicle.commands.download()
    C:\Python27\Lib\site-packages\pymavlink\mavutil.py
        def waypoint_request_list_send(self):
            if self.mavlink10():
                self.mav.mission_request_list_send(self.target_system, self.target_component)

[Response]
https://github.com/mavlink/mavlink/blob/master/message_definitions/v1.0/common.xml
    <message id="39" name="MISSION_ITEM">
      <field type="uint16_t" name="seq"> 0 is home
      <field type="uint16_t" name="command" enum="MAV_CMD">
      <field type="float" name="param1">PARAM1, see MAV_CMD enum
      <field type="float" name="param2">PARAM2, see MAV_CMD enum
      <field type="float" name="param3">PARAM3, see MAV_CMD enum
      <field type="float" name="param4">PARAM4, see MAV_CMD enum
      <field type="float" name="x"> latitude
      <field type="float" name="y"> longitude
      <field type="float" name="z"> altitude 
      
dronekit-python\dronekit\__init__.py\class Vehicle\@self.on_message(['WAYPOINT', 'MISSION_ITEM'])  
'''  
mission_download_request = False
def send_mission_info(data):
    global mission_download_request
    data["next_waypoint_no"] = vehicle.commands.next  #first seq is 1, not 0 (0 is home)
    if mission_download_request == True:
        vehicle.commands.download()
        vehicle.commands.wait_ready()
        waypoints = []
        for cmd in vehicle.commands:
            waypoint = {}
            if cmd.command==22:
                waypoint["kind"] = "takeoff"
                waypoint["alt"] = cmd.z
            elif cmd.command==16:
                waypoint["kind"] = "waypoint"
                waypoint["lat"] = cmd.x
                waypoint["lng"] = cmd.y
                waypoint["alt"] = cmd.z
            elif cmd.command==20:
                waypoint["kind"] = "rtl"   
            elif cmd.command==177:
                waypoint["kind"] = "jump"   
                waypoint["lat"] = cmd.param1
                waypoint["lng"] = cmd.param2
            elif cmd.command==201:
                waypoint["kind"] = "roi"
                waypoint["lat"] = cmd.x
                waypoint["lng"] = cmd.y
            waypoints.append(waypoint)  
        data["waypoints"] = waypoints    
        mission_download_request = False
    else:
        data["waypoints"] = []   
#------------------------------------------------------ 
'''
[Request]
https://github.com/mavlink/mavlink/blob/master/message_definitions/v1.0/ardupilotmega.xml
    <message id="161" name="FENCE_FETCH_POINT">
      <field type="uint8_t" name="target_system">
      <field type="uint8_t" name="target_component">
      <field type="uint8_t" name="idx"> first point is 1, 0 is for return point
      
[Dronekit]
http://python.dronekit.io/automodule.html#dronekit.Vehicle.message_factory
    fence_total = int(vehicle.parameters["FENCE_TOTAL"])
    for idx in range(fence_total):
        msg = vehicle.message_factory.fence_fetch_point_encode(0,0,idx)
        vehicle.send_mavlink(msg)

[Response]
https://github.com/mavlink/mavlink/blob/master/message_definitions/v1.0/ardupilotmega.xml
    <message id="160" name="FENCE_POINT">
      <field type="uint8_t" name="idx"> first point is 1, 0 is for return point
      <field type="uint8_t" name="count">total number of points
      <field type="float" name="lat" units="deg">
      <field type="float" name="lng" units="deg">

[Dronkit]
http://python.dronekit.io/automodule.html#dronekit.Vehicle.on_message    
'''

fence_points = []
  
@vehicle.on_message('FENCE_POINT')
def fence_point_receive_callback(self, name, message):
    global fence_points
    try:
        fence_point = {}
        fence_point["idx"] = message.idx
        fence_point["count"] = message.count
        fence_point["lat"] = message.lat
        fence_point["lng"] = message.lng
        fence_points.append(fence_point)
    except Exception as e:
        if debug: print(">>>", type(e), "fence_point_receive_callback():", e)

fence_download_request = False    
def send_fence_info(data):
    global fence_points
    global fence_download_request    
    try:
        if fence_download_request == True:
            fence_total = int(vehicle.parameters["FENCE_TOTAL"])
            for idx in range(fence_total):
                msg = vehicle.message_factory.fence_fetch_point_encode(0,0,idx)
                vehicle.send_mavlink(msg)
            while len(fence_points) != fence_total: pass
            
            fence_info = {}
            fence_info["fence_enable"] = vehicle.parameters["FENCE_ENABLE"]
            fence_info["fence_type"] = vehicle.parameters["FENCE_TYPE"]
            fence_info["fence_action"] = vehicle.parameters["FENCE_ACTION"]
            fence_info["fence_radius"] = vehicle.parameters["FENCE_RADIUS"]
            fence_info["fence_alt_max"] = vehicle.parameters["FENCE_ALT_MAX"]
            fence_info["fence_margin"] = vehicle.parameters["FENCE_MARGIN"]
            fence_info["fence_total"] = vehicle.parameters["FENCE_TOTAL"]
            fence_info["fence_points"] = fence_points
            
            data["fence_info"] = fence_info
            fence_download_request = False
        else:
            fence_info = {}
            fence_info["fence_enable"] = vehicle.parameters["FENCE_ENABLE"]
            data["fence_info"] = fence_info
    except Exception as e:
        if debug: print(">>>", type(e), "send_fence_info():", e)
        fence_info = {}
        fence_info["fence_enable"] = vehicle.parameters["FENCE_ENABLE"]
        data["fence_info"] = fence_info
    fence_points = []
#------------------------------------------------------
def on_message(client, userdata, msg):
    try:
        json = msg.payload
        json_dict = simplejson.loads(json)
        command = json_dict["command"]
        if command == "arm": arm(json_dict)
        elif command == "disarm": disarm(json_dict)
        elif command == "takeoff": takeoff(json_dict)
        elif command == "rtl": rtl(json_dict)
        elif command == "land": land(json_dict)
        elif command == "goto": goto(json_dict)
        elif command == "mission_upload": mission_upload(json_dict)
        elif command == "mission_download": mission_download(json_dict)
        elif command == "mission_start": mission_start(json_dict)
        elif command == "mission_stop": mission_stop(json_dict)
        elif command == "mission_clear": mission_clear(json_dict)
        elif command == "fence_enable": fence_enable(json_dict)
        elif command == "fence_disable": fence_disable(json_dict)
        elif command == "fence_upload": fence_upload(json_dict)
        elif command == "fence_download": fence_download(json_dict)
        elif command == "fence_clear": fence_clear(json_dict)
        elif command == "move": move(json_dict)
        elif command == "change_heading": change_heading(json_dict)
    except Exception as e:
        if debug: print(">>>", type(e), "on_message():", e)    
#------------------------------------------------------  
def arm(json_dict):
    if vehicle.armed: return
    vehicle.mode = VehicleMode("GUIDED")
    vehicle.armed = True
#------------------------------------------------------ 
def disarm(json_dict):
    if not vehicle.armed: return
    vehicle.mode = VehicleMode("GUIDED")
    vehicle.armed = False
#------------------------------------------------------
def takeoff(json_dict):
    if not vehicle.armed: return
    vehicle.mode = VehicleMode("GUIDED")
    height = json_dict["height"]
    vehicle.simple_takeoff(height) 
#------------------------------------------------------ 
def rtl(json_dict):
    if not vehicle.armed: return
    vehicle.mode = VehicleMode("RTL")
#------------------------------------------------------
def land(json_dict):
    if not vehicle.armed: return
    vehicle.mode = VehicleMode("LAND") 
#------------------------------------------------------ 
def goto(json_dict):
    if not vehicle.armed: return
    if vehicle.location.global_relative_frame.alt < 3: return
    vehicle.mode = VehicleMode("GUIDED")       
    latitude = json_dict["latitude"]
    longitude = json_dict["longitude"]
    altitude = json_dict["altitude"]
    targetLocation = LocationGlobalRelative(latitude, longitude, altitude)
    vehicle.simple_goto(targetLocation)
#------------------------------------------------------ 
def mission_upload(json_dict):
    waypoints = json_dict["waypoints"]   
    while True:
        vehicle.commands.clear()
        for waypoint in waypoints:
            kind = waypoint["kind"]
            if kind=="takeoff":
                altitude = waypoint["alt"]
                #add MAV_CMD_NAV_TAKEOFF command. This is ignored if the vehicle is already in the air.
                vehicle.commands.add(Command( 0, 0, 0, mavutil.mavlink.MAV_FRAME_GLOBAL_RELATIVE_ALT, mavutil.mavlink.MAV_CMD_NAV_TAKEOFF, 0, 0, 0, 0, 0, 0, 0, 0, altitude))
            elif kind=="waypoint":
                latitude = waypoint["lat"]
                longitude = waypoint["lng"]
                altitude = waypoint["alt"]
                cmd = Command(0, 0, 0, mavutil.mavlink.MAV_FRAME_GLOBAL_RELATIVE_ALT, mavutil.mavlink.MAV_CMD_NAV_WAYPOINT, 0, 0, 0, 0, 0, 0, latitude, longitude, altitude)
                vehicle.commands.add(cmd)
            elif kind=="rtl":
                vehicle.commands.add(Command( 0, 0, 0, mavutil.mavlink.MAV_FRAME_GLOBAL_RELATIVE_ALT, mavutil.mavlink.MAV_CMD_NAV_RETURN_TO_LAUNCH, 0, 0, 0, 0, 0, 0, 0, 0, 0))
            elif kind=="jump":
                jumpNo = waypoint["lat"]
                repeatCount = waypoint["lng"]
                vehicle.commands.add(Command( 0, 0, 0, mavutil.mavlink.MAV_FRAME_GLOBAL_RELATIVE_ALT, 177, 0, 0, jumpNo, repeatCount, 0, 0, 0, 0, 0))
            elif kind=="roi":
                latitude = waypoint["lat"]
                longitude = waypoint["lng"]
                vehicle.commands.add(Command( 0, 0, 0, mavutil.mavlink.MAV_FRAME_GLOBAL_RELATIVE_ALT, 201, 0, 0, 0, 0, 0, 0, latitude, longitude, 0))
        #처음 미션을 추가할 경우  한 개가 빠지는 오류 때문
        if len(waypoints) == vehicle.commands.count:  
            break;
    vehicle.commands.upload()
#------------------------------------------------------ 
def mission_download(json_dict):
    global mission_download_request
    mission_download_request = True 
#------------------------------------------------------ 
def mission_start(json_dict):
    if not vehicle.armed: return
    if vehicle.location.global_relative_frame.alt < 3: return    
    vehicle.mode = VehicleMode("AUTO")
#------------------------------------------------------ 
def mission_stop(json_dict):
    if not vehicle.armed: return
    vehicle.mode = VehicleMode("GUIDED")
#------------------------------------------------------ 
def mission_clear(json_dict):
    pass
#------------------------------------------------------ 
def fence_enable(json_dict):
    vehicle.parameters["FENCE_ENABLE"] = 1
#------------------------------------------------------ 
def fence_disable(json_dict):
    vehicle.parameters["FENCE_ENABLE"] = 0
#------------------------------------------------------ 
def fence_upload(json_dict):
    global statustext
    fence_points = json_dict["points"]
    vehicle.parameters["FENCE_TYPE"] = json_dict["fence_type"]
    vehicle.parameters["FENCE_ACTION"] = json_dict["fence_action"]
    vehicle.parameters["FENCE_RADIUS"] = json_dict["fence_radius"]
    vehicle.parameters["FENCE_ALT_MAX"] = json_dict["fence_alt_max"]
    vehicle.parameters["FENCE_MARGIN"] = json_dict["fence_margin"]
    vehicle.parameters["FENCE_TOTAL"] = json_dict["fence_total"]
    for idx, fence_point in enumerate(fence_points):
        #[Reference]
        #http://python.dronekit.io/automodule.html#dronekit.Vehicle.message_factory
        #https://github.com/mavlink/mavlink/blob/master/message_definitions/v1.0/ardupilotmega.xml
        msg = vehicle.message_factory.fence_point_encode(0,0,idx,len(fence_points),fence_point["lat"],fence_point["lng"])
        vehicle.send_mavlink(msg)
    statustext = "Fence received"
#------------------------------------------------------ 
def fence_download(json_dict):
    global fence_download_request
    fence_download_request = True   
#------------------------------------------------------
def fence_clear(json_dict):
    vehicle.parameters["FENCE_TOTAL"] = 0
    vehicle.parameters["FENCE_ENABLE"] = 0
#------------------------------------------------------
def move(json_dict):
    vehicle.mode = VehicleMode("GUIDED")

    velocity_x = json_dict["velocity_x"]
    velocity_y = json_dict["velocity_y"]
    velocity_z = json_dict["velocity_z"]
    duration = json_dict["duration"]

    vehicle.parameters["WP_YAW_BEHAVIOR"] = 0
    msg = vehicle.message_factory.set_position_target_local_ned_encode(
        0, 0, 0, mavutil.mavlink.MAV_FRAME_LOCAL_NED,
        0b0000111111000111, 0, 0, 0,
        velocity_x, velocity_y, velocity_z,
        0, 0, 0, 0, 0)
    vehicle.send_mavlink(msg)

    def return_original_velocity():
        time.sleep(duration) #max 3(sec)
        msg = vehicle.message_factory.set_position_target_local_ned_encode(
            0, 0, 0, mavutil.mavlink.MAV_FRAME_LOCAL_NED,
            0b0000111111000111, 0, 0, 0,
            0, 0, 0,
            0, 0, 0, 0, 0)
        vehicle.send_mavlink(msg)
        vehicle.parameters["WP_YAW_BEHAVIOR"] = 3

    thread = threading.Thread(target=return_original_velocity)
    thread.start()
#------------------------------------------------------
def change_heading(json_dict):
    heading = json_dict["heading"]
    relative = False
    if relative: is_relative=1 #yaw relative to direction of travel
    else: is_relative=0 #yaw is an absolute angle
    msg = vehicle.message_factory.command_long_encode(
        0, 0,    # target system, target component
        mavutil.mavlink.MAV_CMD_CONDITION_YAW, #command
        0, #confirmation
        heading,    # param 1, yaw in degrees
        0,          # param 2, yaw speed deg/s
        1,          # param 3, direction -1 ccw, 1 cw
        is_relative, # param 4, relative offset 1, absolute angle 0
        0, 0, 0)    # param 5 ~ 7 not used
    vehicle.send_mavlink(msg)
#------------------------------------------------------

if __name__ == "__main__":
    connect_mqtt()