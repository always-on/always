package edu.wpi.always.client.reeti;

//a Java socket client

import java.io.*;
import java.net.*;
import edu.wpi.cetask.Utils;

public class ReetiCommandSocketConnection {

   private /* final */ Socket socket;

   private /* final */ PrintWriter writer;

   public ReetiCommandSocketConnection (String host) {
      connect(host, 12045);
   }

   private void connect (String host, int port) {
      try {
         socket = new Socket(host, port);
         writer = new PrintWriter(socket.getOutputStream(), true);
      } catch (ConnectException e) {
         System.err.println(e+" to "+host+" "+port+" (retrying)"); 
         try { Thread.sleep(3000);  } catch (InterruptedException i) {}
         connect(host, port);
      } catch (Exception e) {
         Utils.rethrow("Error opening socket to Reeti", e);
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