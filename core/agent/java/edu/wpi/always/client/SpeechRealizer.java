package edu.wpi.always.client;

import edu.wpi.always.cm.primitives.SpeechBehavior;
import edu.wpi.disco.rt.realizer.SingleRunPrimitiveRealizer;

public class SpeechRealizer extends SingleRunPrimitiveRealizer<SpeechBehavior>
      implements ClientProxyObserver {

   private final ClientProxy proxy;
   private boolean done;

   public SpeechRealizer (SpeechBehavior params, ClientProxy proxy) {
      super(params);
      this.proxy = proxy;
      proxy.addObserver(this);
   }

   @Override
   protected void singleRun () {
      proxy.say(getParams().getText());
   }

   @Override
   public void notifyDone (ClientProxy sender, String action, String data) {
      if ( action.equals("speech")
         && data.trim().toLowerCase()
               .equals(getParams().getText().trim().toLowerCase()) ) {
         done = true;
         fireDoneMessage();
         proxy.removeObserver(this);
      }
   }

   @Override
   public void notifyMenuSelected (ClientProxy ragClientProxy, String text) {
   }

   @Override
   public void shutdown () {
      if ( !done )
         proxy.stopSpeech();
   }
}
