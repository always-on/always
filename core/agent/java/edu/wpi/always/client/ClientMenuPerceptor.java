package edu.wpi.always.client;

import edu.wpi.always.cm.perceptors.*;
import edu.wpi.disco.rt.menu.*;

public class ClientMenuPerceptor implements MenuPerceptor, ClientProxyObserver {

   private volatile MenuPerception latest;

   public ClientMenuPerceptor (ClientProxy proxy) {
      proxy.addObserver(this);
   }

   @Override
   public MenuPerception getLatest () {
      return latest;
   }

   @Override
   public void run () {
   }

   @Override
   public void notifyDone (ClientProxy sender, String action, String data) {
   }

   @Override
   public void notifyMenuSelected (ClientProxy proxy, String text) {
      latest = new MenuPerception(text);
   }
}
