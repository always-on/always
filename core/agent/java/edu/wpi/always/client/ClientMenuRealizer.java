package edu.wpi.always.client;

import edu.wpi.always.cm.primitives.MenuBehavior;
import edu.wpi.disco.rt.realizer.SingleRunPrimitiveRealizer;

public class ClientMenuRealizer extends
      SingleRunPrimitiveRealizer<MenuBehavior> implements ClientProxyObserver {

   protected final ClientProxy proxy;

   public ClientMenuRealizer (MenuBehavior params, ClientProxy proxy) {
      super(params);
      this.proxy = proxy;
   }

   @Override
   protected void singleRun () {
      proxy.addObserver(this);
      MenuBehavior params = getParams();
      proxy.showMenu(params.getItems(), params.isTwoColumn(), params.isExtension());
   }

   @Override
   public void notifyDone (ClientProxy sender, String action, String data) {
      if ( action.equals("show_menu") ) {
         fireDoneMessage();
         proxy.removeObserver(this);
      }
   }

   @Override
   public void notifyMenuSelected (ClientProxy proxy, String text) {
   }
}
