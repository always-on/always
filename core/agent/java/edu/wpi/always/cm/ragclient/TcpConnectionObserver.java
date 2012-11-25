package edu.wpi.always.cm.ragclient;

public interface TcpConnectionObserver {
	void notifyMessageReceive(RemoteConnection sender, String text);
}
