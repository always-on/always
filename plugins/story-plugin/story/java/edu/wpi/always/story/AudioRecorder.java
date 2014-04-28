package edu.wpi.always.story;

import java.io.*;
import javax.sound.sampled.*;

public class AudioRecorder extends Thread {

   private static TargetDataLine m_line;
   private AudioFileFormat.Type m_targetType;
   private AudioInputStream m_audioInputStream;
   private File m_outputFile;

   public AudioRecorder (TargetDataLine line, AudioFileFormat.Type targetType,
         File file) {
      m_line = line;
      m_audioInputStream = new AudioInputStream(line);
      m_targetType = targetType;
      m_outputFile = file;
   }

   public AudioRecorder () {
   }

   @Override
   public void start () {
      m_line.start();
      super.start();
   }

   public void stopRecording () {
      m_line.stop();
      m_line.close();
   }

   @Override
   public void run () {
      try {
         AudioSystem.write(m_audioInputStream, m_targetType, m_outputFile);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void record (String strFilename) {
      /*
       * out("enter the file name without path without format.");
       * InputStreamReader converter = new InputStreamReader(System.in);
       * BufferedReader in = new BufferedReader(converter); String strFilename =
       * null; try { strFilename = in.readLine(); } catch (IOException e1) {
       * e1.printStackTrace(); }
       */
      File outputFile = new File("C:\\Users\\mel\\Desktop\\" + strFilename
         + ".wav");
      AudioFormat audioFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F,
            false);
      DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
      TargetDataLine targetDataLine = null;
      try {
         targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
         targetDataLine.open(audioFormat);
      } catch (LineUnavailableException e) {
         out("unable to get a recording line");
         e.printStackTrace();
         System.exit(1);
      }
      AudioFileFormat.Type targetType = AudioFileFormat.Type.WAVE;
      AudioRecorder recorder = new AudioRecorder(targetDataLine, targetType,
            outputFile);
      // out("Press ENTER to start the recording.");
      // try{
      // System.in.read();
      // }
      // catch (IOException e){
      // e.printStackTrace();
      // }
      recorder.start();
      out("Recording...");
      // out("Press ENTER to stop the recording.");
      // try{
      // System.in.read();System.in.read();
      // }
      // catch (IOException e){
      // e.printStackTrace();
      // }
      // out("Recording stopped.");
   }

   private static void out (String strMessage) {
      System.out.println(strMessage);
   }
}
