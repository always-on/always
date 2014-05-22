package edu.wpi.always.client;

import java.net.*;
import edu.wpi.always.Always;
import edu.wpi.disco.rt.util.Utils;

public abstract class RemoteConnection {

   protected final String host;
   protected final int port;
   protected Socket socket;
   
   protected RemoteConnection (String host, int port) {
      this.host = host;
      this.port = port;
   }
   
   protected void connect (String host, int port) {
      // loop to avoid stack overflow
      while ( socket == null ) { 
         try { socket = new Socket(host, port); } 
         catch (ConnectException e) { 
            Utils.lnprint(System.out, e+" to "+host+" "+port+" (retrying)"); 
            try { Thread.sleep(10000);  } catch (InterruptedException i) {} }
         catch (Exception e) { restart(e); }
      }
      Utils.lnprint(System.out, "Successfully connected to "+host+" "+port);
   }
   
   protected void restart (Exception e) { Always.restart(e, " on "+host+" "+port); }
}