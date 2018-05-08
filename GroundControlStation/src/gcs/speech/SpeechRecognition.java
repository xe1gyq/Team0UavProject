package gcs.speech;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import gcs.appmain.AppMainController;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SpeechRecognition {
	public static SpeechRecognition instance;
	public boolean speech = false;
	
    public SpeechRecognition() {
    	instance = this;
    }	
    
    public void speech(String text) {
    	if(speech) {
	    	 
    	} else {
    		System.out.println(">> " + text);
    	}
    }	
}
