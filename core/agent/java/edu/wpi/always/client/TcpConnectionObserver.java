package edu.wpi.always.client;

public interface TcpConnectionObserver {

   void notifyMessageReceive (RemoteConnection sender, String text);
}
