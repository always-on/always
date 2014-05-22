package edu.wpi.always.client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.*;
import edu.wpi.always.Always;
import edu.wpi.disco.rt.util.Utils;

// non-blocking read and write

public class TcpConnection extends RemoteConnection {

   private PrintWriter out;
   private BufferedReader in;
   private final ExecutorService sendThreadService;
   private final ExecutorService recvThreadService;
   private final ArrayList<TcpConnectionObserver> observers = new ArrayList<TcpConnectionObserver>();

   public TcpConnection (String host, int port) {
      super(host, port);
      sendThreadService = Executors.newSingleThreadExecutor();
      recvThreadService = Executors.newSingleThreadExecutor();
      connect(host, port);
   }
   
   @Override
   protected void connect (String host, int port) {
      super.connect(host, port);
      try {
         out = new PrintWriter(socket.getOutputStream(), true);
         in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         recvThreadService.execute(new Runnable() {

            @Override
            public void run () {
               char[] buf = new char[8192];
               int n;
               try {
                  while ((n = in.read(buf)) != -1) {
                     String s = new String(buf, 0, n);
                     // note can receive more than one message in buffer
                     if ( Always.TRACE ) Utils.lnprint(System.out, "Received "+ s);
                     fireMessageReceived(s);
                  }
               } catch (Exception e) { restart(e); }
            }
         });
      } catch (Exception e) { restart(e); }
   }

   protected void fireMessageReceived (String s) {
      for (TcpConnectionObserver o : observers) {
         o.notifyMessageReceive(this, s);
      }
   }

   public void beginSend (String message) {
      enqueue(message);
   }

   private void enqueue (String message) {
      final String msg = message;
      sendThreadService.execute(new Runnable() {

         @Override
         public void run () {
            try { send(msg); }
            catch (Exception e) { restart(e); }
         }
      });
   }

   private String lastMessage = "";
   private int count;
   
   public static int STARTS_WITH = 18; // change to 0 to disable
  
   private void send (String message) throws IOException {
      out.println(message);
      if ( out.checkError() ) 
         throw new IOException("TcpConnection println failed");
      // optimization to help debugging
      if ( lastMessage.equals(message) ||
            (STARTS_WITH > 0 && lastMessage.length() >= STARTS_WITH &&
            message.startsWith(lastMessage.substring(0, STARTS_WITH))) ) 
         count++;
      else {
         if ( count > 0 ) 
            Utils.lnprint(System.out, "Sent "+lastMessage.substring(0, STARTS_WITH)+"-- "+count+" more"); 
         Utils.lnprint(System.out, "Sent "+message);
         count = 0;
      }
      lastMessage = message;
   }

   public void addObserver (TcpConnectionObserver o) {
      observers.add(o);
   }

   public void removeObserver (TcpConnectionObserver o) {
      observers.remove(o);
   }
   

}
