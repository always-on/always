package edu.wpi.always.story;

import java.io.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;


public class AudioPlayer
{
	private static final int EXTERNAL_BUFFER_SIZE = 128000;

	public void play(String strFilename){
		
		File soundFile = new File("C:\\Users\\mel\\Desktop\\" + strFilename +".wav");
		AudioInputStream audioInputStream = null;

		try{
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		}catch (Exception e){
			e.printStackTrace();
			System.exit(1);
		}

		AudioFormat	audioFormat = audioInputStream.getFormat();
		SourceDataLine	line = null;
		DataLine.Info	info = new DataLine.Info(SourceDataLine.class, audioFormat);

		try{
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(audioFormat);
		}catch (LineUnavailableException e){
			e.printStackTrace();
			System.exit(1);
		}
		catch (Exception e){
			e.printStackTrace();
			System.exit(1);
		}

		line.start();
		
		int	nBytesWritten=0;
		int	nBytesRead = 0;
		byte[]	abData = new byte[EXTERNAL_BUFFER_SIZE];
		while (nBytesRead != -1){
			try{
				nBytesRead = audioInputStream.read(abData, 0, abData.length);
			}catch (IOException e){
				e.printStackTrace();
			}
			if (nBytesRead >= 0){
				nBytesWritten = line.write(abData, 0, nBytesRead);
			}
		}
		line.drain();
		line.close();
	}

}
