package edu.wpi.always.client;

//a Java socket client

import java.io.*;
import java.net.*;


public class ReetiCommandSocketConnection {

   private final Socket socket;
   private final PrintWriter writer;
   
   public final ReetiJsonConfiguration reetiJsonConfig;

   public ReetiCommandSocketConnection () {
      
      reetiJsonConfig = new ReetiJsonConfiguration();
      
      try {;
      String test = reetiJsonConfig.getIP();
         socket = new Socket(reetiJsonConfig.getIP(), 12045); // Was 130.215.28.4
         writer = new PrintWriter(socket.getOutputStream(), true);
      } catch (IOException e) {
         throw new RuntimeException("Error opening socket to Reeti", e);
      }
   }

   public void send (String message) {
      writer.println(message);
   }

   public void close () {
      writer.close();
      try {
         socket.close();
      } catch (IOException e) {
         throw new RuntimeException("Error closing socket to Reeti", e);
      }
   }
   
   public ReetiJsonConfiguration getConfigInfo () {
      return this.reetiJsonConfig;
   }
}