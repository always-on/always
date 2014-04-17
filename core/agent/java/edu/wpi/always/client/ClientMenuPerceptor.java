package edu.wpi.always.client;

import edu.wpi.always.cm.perceptors.*;
import edu.wpi.disco.rt.menu.*;
import edu.wpi.disco.rt.perceptor.PerceptorBase;

public class ClientMenuPerceptor extends PerceptorBase<MenuPerception>
             implements MenuPerceptor, ClientProxyObserver {

   public ClientMenuPerceptor (ClientProxy proxy) {
      proxy.addObserver(this);
   }

   @Override
   public void notifyDone (ClientProxy sender, String action, String data) {
   }

   @Override
   public void notifyMenuSelected (ClientProxy proxy, String text) {
      latest = new MenuPerception(text);
   }
}
