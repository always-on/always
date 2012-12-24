package edu.wpi.always.client;

public interface ClientProxyObserver {

   void notifyDone (ClientProxy sender, String action, String additionalData);

   void notifyMenuSelected (ClientProxy ragClientProxy, String text);
}
