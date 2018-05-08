package gcs.mjpgstream;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class MjpgStream {

    private String serverURI;
    private String mqttBrokerURI;
    private String topic;
    private boolean debug;

    private URL url;
    private HttpURLConnection httpURLConnection;
    private DataInputStream dataInputStream;
    private StreamSplit streamSplit;
    private String boundary;

    private Canvas canvas;
    private GraphicsContext gc;

    private MqttClient mqttClient;

    public Thread thread;
    
    //CamPublisher("http://192.168.3.xxx:50005/?action=stream", "tcp://192.168.3.xxx:1883", "/uav/camera");
    public MjpgStream(String serverURI, String mqttBrokerURI, String topic) throws Exception {
        this.serverURI = serverURI;
        this.mqttBrokerURI = mqttBrokerURI;
        this.topic = topic;
    }    

    //CamPublisher("http://192.168.3.xxx:50005/?action=stream", "tcp://192.168.3.xxx:1883", "/uav/camera");
    public MjpgStream(String mqttBrokerURI, String topic, Canvas canvas) throws Exception {
        this.mqttBrokerURI = mqttBrokerURI;
        this.topic = topic;
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }
    
    //CamPublisher("http://192.168.3.xxx:50005/?action=stream", canvas);
    public MjpgStream(String serverURI, Canvas canvas) {
        this.serverURI = serverURI;
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }    
    
    public void stop() {
    	try {
    		mqttClient.disconnect();
    		mqttClient.close();
    	} catch(Exception e) {}
    	
		try {
			httpURLConnection.disconnect();
		} catch(Exception e) {}
    }
    
    public void start() {
        if(mqttBrokerURI != null && canvas == null) {
            thread = new Thread() {
                boolean connected;
                @Override
                public void run() {
                    while(!connected) {
                        try {
                        	mqttClient = new MqttClient(mqttBrokerURI, MqttClient.generateClientId(), null);
                            MqttConnectOptions options = new MqttConnectOptions();
                            options.setConnectionTimeout(1000);
                            mqttClient.connect(options);
                            
                            url = new URL(serverURI);
                            URLConnection conn = url.openConnection();
                            httpURLConnection = (HttpURLConnection) url.openConnection();
                            httpURLConnection.setConnectTimeout(1000);
                            httpURLConnection.connect();
                            connected = true;
                            
                            Hashtable httpHeaders = StreamSplit.readHeaders(httpURLConnection);
                            String contentType = (String) httpHeaders.get("content-type");
                            int bidx = contentType.indexOf("boundary=");
                            boundary = "--" + contentType.substring(bidx + 9);
                            dataInputStream = new DataInputStream(new BufferedInputStream(httpURLConnection.getInputStream()));
                            streamSplit = new StreamSplit(dataInputStream);
                            streamSplit.skipToBoundary(boundary);
                            
                            while (true) {
                                Hashtable partHeaders = streamSplit.readHeaders();
                                byte[] imageBytes = streamSplit.readToBoundary(boundary);
                                MqttMessage message = new MqttMessage(imageBytes);
                                mqttClient.publish(topic, message);
                            }
                        } catch (Exception e) {
                            try {
                                MjpgStream.this.stop();
                            } catch(Exception e2) {}
                        }
                    }
                }
            };
            thread.setDaemon(true);
            thread.start();
        } else if(mqttBrokerURI != null && canvas != null) {
            thread = new Thread() {
                boolean connected;
                @Override
                public void run() {
                    while(!connected) {
                        try {
                        	mqttClient = new MqttClient(mqttBrokerURI, MqttClient.generateClientId(), null);
                            mqttClient.setCallback(new MqttCallback() {
                                @Override
                                public void messageArrived(String topic, MqttMessage message) throws Exception {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                            	byte[] imageBytes = message.getPayload();
                                            	BufferedImage bufferdImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
                                                Image imgFx = SwingFXUtils.toFXImage(bufferdImage, null);
                                                gc.drawImage(imgFx, 0, 0, imgFx.getWidth(), imgFx.getHeight(), 0, 0, canvas.getWidth(), canvas.getHeight());
                                            } catch (IOException ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    });
                                }
                                @Override
                                public void deliveryComplete(IMqttDeliveryToken token) {
                                }
                                @Override
                                public void connectionLost(Throwable cause) {
                                }
                            });
                            MqttConnectOptions options = new MqttConnectOptions();
                            options.setConnectionTimeout(1000);
                            mqttClient.connect(options);
                            mqttClient.subscribe(topic);
                            connected = true;
                        } catch(Exception e) {
                            try {
                                MjpgStream.this.stop();
                            } catch(Exception e2) {}
                        }
                    }
                }
            };
            thread.setDaemon(true);
            thread.start();
        } else {
            thread = new Thread() {
                boolean connected;
                @Override
                public void run() {
                    while(!connected) {
                        try {
                            url = new URL(serverURI);
                            httpURLConnection = (HttpURLConnection) url.openConnection();
                            httpURLConnection.setConnectTimeout(1000);
                            httpURLConnection.connect();
                            connected = true;
                            
                            Hashtable httpHeaders = StreamSplit.readHeaders(httpURLConnection);
                            String contentType = (String) httpHeaders.get("content-type");
                            int bidx = contentType.indexOf("boundary=");
                            boundary = "--" + contentType.substring(bidx + 9);
                            dataInputStream = new DataInputStream(new BufferedInputStream(httpURLConnection.getInputStream()));
                            streamSplit = new StreamSplit(dataInputStream);
                            streamSplit.skipToBoundary(boundary);
                            
                            while (true) {
                                Hashtable partHeaders = streamSplit.readHeaders();
                                byte[] imageBytes = streamSplit.readToBoundary(boundary);
                                BufferedImage bufferdImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
                                Image imgFx = SwingFXUtils.toFXImage(bufferdImage, null);
                                gc.drawImage(imgFx, 0, 0, imgFx.getWidth(), imgFx.getHeight(), 0, 0, canvas.getWidth(), canvas.getHeight());
                            }
                        } catch (Exception e) {
                            try {
                                MjpgStream.this.stop();
                            } catch(Exception e2) {}
                        }
                    }
                }
            };
            thread.setDaemon(true);
            thread.start();
        }
    }

    static class StreamSplit {
        public static final String BOUNDARY_MARKER_PREFIX = "--";
        public static final String BOUNDARY_MARKER_TERM = BOUNDARY_MARKER_PREFIX;

        protected DataInputStream m_dis;
        private boolean m_streamEnd;

        public StreamSplit(DataInputStream dis) {
            m_dis = dis;
            m_streamEnd = false;
        }

        public Hashtable readHeaders() throws IOException {
            Hashtable ht = new Hashtable();
            String response;
            boolean satisfied = false;

            do {
                //noinspection deprecation
                response = m_dis.readLine();
                if (response == null) {
                    m_streamEnd = true;
                    break;
                } else if (response.equals("")) {
                    if (satisfied) {
                        break;
                    } else {
                        // Carry on...
                    }
                } else {
                    satisfied = true;
                }
                addPropValue(response, ht);
            } while (true);
            return ht;
        }

        protected static void addPropValue(String response, Hashtable ht) {
            int idx = response.indexOf(":");
            if (idx == -1) {
                return;
            }
            String tag = response.substring(0, idx);
            String val = response.substring(idx + 1).trim();
            ht.put(tag.toLowerCase(), val);
        }

        public static Hashtable readHeaders(URLConnection conn) {
            Hashtable ht = new Hashtable();
            int i = 0;
            do {
                String key = conn.getHeaderFieldKey(i);
                if (key == null) {
                    if (i == 0) {
                        i++;
                        continue;
                    } else {
                        break;
                    }
                }
                String val = conn.getHeaderField(i);
                ht.put(key.toLowerCase(), val);
                i++;
            } while (true);
            return ht;
        }

        public void skipToBoundary(String boundary) throws IOException {
            readToBoundary(boundary);
        }

        public byte[] readToBoundary(String boundary) throws IOException {
            ResizableByteArrayOutputStream baos = new ResizableByteArrayOutputStream();
            StringBuffer lastLine = new StringBuffer();
            int lineidx = 0;
            int chidx = 0;
            byte ch;
            do {
                try {
                    ch = m_dis.readByte();
                } catch (EOFException e) {
                    m_streamEnd = true;
                    break;
                }
                if (ch == '\n' || ch == '\r') {
                    //
                    // End of line... Note, this will now look for the boundary
                    // within the line - more flexible as it can habdle
                    // arfle--boundary\n  as well as
                    // arfle\n--boundary\n
                    //
                    String lls = lastLine.toString();
                    int idx = lls.indexOf(BOUNDARY_MARKER_PREFIX);
                    if (idx != -1) {
                        lls = lastLine.substring(idx);
                        if (lls.startsWith(boundary)) {
                            //
                            // Boundary found - check for termination
                            //
                            String btest = lls.substring(boundary.length());
                            if (btest.equals(BOUNDARY_MARKER_TERM)) {
                                m_streamEnd = true;
                            }
                            chidx = lineidx + idx;
                            break;
                        }
                    }
                    lastLine = new StringBuffer();
                    lineidx = chidx + 1;
                } else {
                    lastLine.append((char) ch);
                }
                chidx++;
                baos.write(ch);
            } while (true);
            //
            baos.close();
            baos.resize(chidx);
            return baos.toByteArray();
        }

        public boolean isAtStreamEnd() {
            return m_streamEnd;
        }
    }

    static class ResizableByteArrayOutputStream extends ByteArrayOutputStream {

        public ResizableByteArrayOutputStream() {
            super();
        }

        public void resize(int size) {
            count = size;
        }
    }
}
