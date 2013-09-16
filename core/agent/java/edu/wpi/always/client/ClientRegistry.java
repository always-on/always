package edu.wpi.always.client;

import edu.wpi.always.*;
import edu.wpi.always.Always.AgentType;
import edu.wpi.always.cm.primitives.AudioFileRealizer;
import edu.wpi.disco.rt.util.ComponentRegistry;
import org.picocontainer.*;

public class ClientRegistry implements ComponentRegistry {

   @Override
   public void register (MutablePicoContainer container) {
      container.addComponent(new UIMessageDispatcherImpl(new TcpConnection(
            "localhost", 11000)));
      container.addComponent(GazeRealizer.class);
      container.addComponent(FaceExpressionRealizer.class);
      container.addComponent(IdleBehaviorRealizer.class);
      
      Always.AgentType agentType = Always.getAgentType();
      if(agentType == Always.AgentType.Unity)
         container.addComponent(FaceTrackerRealizer.class);
      else if(agentType == Always.AgentType.Reeti)
         container.addComponent(ReetiFaceTrackerRealizer.class);
      else if(agentType == Always.AgentType.Both)
         container.addComponent(MirrorFaceTrackerRealizer.class);

      container.addComponent(SpeechRealizer.class);
      container.addComponent(AudioFileRealizer.class);
      container.addComponent(ClientMenuRealizer.class);
      container.addComponent(ClientMenuPerceptor.class);
      container.as(Characteristics.CACHE).addComponent(
            KeyboardMessageHandler.class);
      container.as(Characteristics.CACHE).addComponent(ClientProxy.class);
   }
}
