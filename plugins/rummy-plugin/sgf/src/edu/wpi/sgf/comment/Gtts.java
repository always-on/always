package edu.wpi.sgf.comment;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javazoom.jl.player.Player;

/**
 * Using Google Tts with online access.
 * @author Morteza Behrooz
 * @version 2.0
 */
public class Gtts {

	private final static String ttsUrl = 
			"http://translate.google.com/translate_tts?tl=en&q=";

	/** 
	 * Reads the a string with Google Tts via online access. 
	 * @param String someText
	 * @author Morteza Behrooz
	 * @version 2.0
	 */
	public void say(String someText) {

		try{

			URL url = new URL(ttsUrl + someText);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			urlConn.addRequestProperty("User-Agent", "Mozilla/4.76");
			InputStream audioSrc = urlConn.getInputStream();

			DataInputStream read = new DataInputStream(audioSrc);
			OutputStream outstream = new FileOutputStream(new File("ttstemp.mp3"));
			byte[] buffer = new byte[1024];
			int len;
			while ((len = read.read(buffer)) > 0) 
				outstream.write(buffer, 0, len);                    
			outstream.close();        

			FileInputStream fis     = new FileInputStream("ttstemp.mp3");
			BufferedInputStream bis = new BufferedInputStream(fis);
			final Player player = new Player(bis);

			//new Thread() {
			//	public void run() {
					try { player.play(); }
					catch (Exception e) { System.out.println(e); }
			//	}
		//	}.start();

			new File("ttstemp.mp3").delete();

		}catch(Exception e){
			System.out.println(e.getMessage());
		} 

	}
}
