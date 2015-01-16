package edu.neu.always.skype;

import com.google.gson.JsonObject;
import edu.wpi.always.*;
import edu.wpi.always.Always.AgentType;
import edu.wpi.always.client.*;

public class SkypeClient {
   
   // this class is needed because UIMessage dispatcher is not created
   // until after plugins constructed

   public SkypeClient (UIMessageDispatcher dispatcher, final ClientProxy proxy) {
      // this must be here so only registered once
      dispatcher.registerReceiveHandler("callEnded", new MessageHandler() {
         @Override
         public void handleMessage (JsonObject body) {
            // see SkypeIncomingSchema.IncomingSkype.enter()
            if ( Always.getAgentType() == AgentType.REETI )
               proxy.setAgentVisible(false);
            SkypeIncomingSchema.EXIT = true;
         }
      });
   }
}
