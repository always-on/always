package edu.wpi.always.client;

public interface RemoteConnection {

   void connect ();

   boolean isConnected ();

   /**
    * non-blocking send
    */
   void beginSend (String message);

   void removeObserver (TcpConnectionObserver o);

   void addObserver (TcpConnectionObserver o);
}