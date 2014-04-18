package edu.wpi.always.client.reeti;

//a Java socket client

import java.io.*;
import java.net.*;
import edu.wpi.always.Always;
import edu.wpi.cetask.Utils;

public class ReetiCommandSocketConnection {

   private /* final */ Socket socket;
   private /* final */ String host;
   private /* final */ int port;
   private /* final */ PrintWriter writer;

   public ReetiCommandSocketConnection (String host) {
      connect(host, 12045);
   }

   private void connect (String host, int port) {
      this.port = port;
      try {
         socket = new Socket(host, port); // Was 130.215.28.4
         writer = new PrintWriter(socket.getOutputStream(), true);
      } catch (ConnectException e) {
         System.err.println(e+" to "+host+" "+port+" (retrying)"); 
         try { Thread.sleep(3000);  } catch (InterruptedException i) {}
         connect(host, port);
      } catch (Exception e) { Always.restart(e, " on "+host+" "+port); }
   }
   
   public void send (String message) {
      try { writer.println(message); }
      catch (Exception e) { Always.restart(e, " on "+host+" "+port); }
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