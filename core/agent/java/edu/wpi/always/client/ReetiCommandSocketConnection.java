package edu.wpi.always.client;

//a Java socket client

import java.io.*;
import java.net.*;


public class ReetiCommandSocketConnection {

   private final Socket socket;
   private final PrintWriter writer;

   public ReetiCommandSocketConnection (String host) {
      try {
         socket = new Socket(host, 12045);
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
}