package edu.neu.always.skype;

import com.google.gson.JsonObject;
import edu.wpi.always.client.*;

public class SkypeClient {
   
   // this class is needed because UIMessage dispatcher is not created
   // until after plugins constructed

   public SkypeClient (UIMessageDispatcher dispatcher) {
      // this must be here so only registered once
      dispatcher.registerReceiveHandler("callEnded", new MessageHandler() {
         @Override
         public void handleMessage (JsonObject body) {
            SkypeIncomingSchema.EXIT = true;
         }
      });
   }
}
