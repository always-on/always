package edu.wpi.always.client;

public interface TcpConnectionObserver {

   void notifyMessageReceive (TcpConnection sender, String text);
}
