###########################################
# pip install paho-mqtt
# 'import cv2' need when it run in x-window
###########################################

#import cv2
import urllib 
import numpy as np
import paho.mqtt.client as mqtt
import time

mqttClient = mqtt.Client()
stream = None

while True:
    try:
        stream=urllib.urlopen('http://localhost:50001/?action=stream')        
        mqttClient.connect('106.253.56.122', 1883);
        
        bytes=''
        while True:
            temp =stream.read(1024)
            if temp=='':
                raise Exception('mjpeg streamer stop') 
            bytes+=temp
            a = bytes.find('\xff\xd8')
            b = bytes.find('\xff\xd9')
            if a!=-1 and b!=-1:
                jpg = bytes[a:b+2]
                bytes= bytes[b+2:]
                
                #MQTT Publishing-------------------------------
                mqttClient.publish('/mingyu-uav/camera', jpg)
                
                #X Window--------------------------------------
                #i = cv2.imdecode(np.fromstring(jpg, dtype=np.uint8),cv2.CV_LOAD_IMAGE_COLOR)
                #cv2.imshow('i',i)
                #if cv2.waitKey(1) ==27:
                    #exit(0)   
    except:
        if stream is not None: stream.close()
        mqttClient.disconnect()
        time.sleep(1)
