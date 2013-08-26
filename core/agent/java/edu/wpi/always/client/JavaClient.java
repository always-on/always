package edu.wpi.always.client;
//a Java socket client

import java.io.*;
import java.net.*;
public class JavaClient {

   private Socket echoSocket = null;
   private PrintWriter out = null;
   
   public JavaClient()
   {
      try {
            
         echoSocket = new Socket("130.215.28.4", 12045);
      }
      catch (UnknownHostException e) {
         System.err.println("Don't know about host: Reeti.");
         System.exit(1);
     } catch (IOException e) {
         System.err.println("Couldn't get I/O for "
                            + "the connection to: Reeti.");
         System.exit(1);
     }
   }
   public void send(String message)
   {
      try {
      
       out = new PrintWriter(echoSocket.getOutputStream(), true);
      } catch (UnknownHostException e) {
          System.err.println("Don't know about host: Reeti.");
          System.exit(1);
      } catch (IOException e) {
          System.err.println("Couldn't get I/O for "
                             + "the connection to: Reeti.");
          System.exit(1);
      }

      out.println(message);
 
   }
   public void EndTransmission() throws IOException
   {
      try {
      out.close();
      echoSocket.close();
      }
      catch (IOException e) {
         System.err.println("Couldn't get I/O for "
                            + "the connection to: Reeti.");
         System.exit(1);
     }
   }
}