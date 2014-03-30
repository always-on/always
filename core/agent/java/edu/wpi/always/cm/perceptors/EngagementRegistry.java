package edu.wpi.always.cm.perceptors;

import edu.wpi.always.*;
import edu.wpi.always.Always.AgentType;
import edu.wpi.always.client.reeti.*;
import edu.wpi.always.cm.perceptors.dummy.DummyMovementPerceptor;
import edu.wpi.always.cm.perceptors.sensor.face.ShoreFacePerceptor;
import edu.wpi.always.cm.perceptors.sensor.pir.PIRMovementPerceptor;
import edu.wpi.always.cm.primitives.AudioFileRealizer;
import edu.wpi.disco.rt.util.ComponentRegistry;
import org.picocontainer.*;

public class EngagementRegistry implements ComponentRegistry {

   @Override
   public void register (MutablePicoContainer container) {
      switch ( Always.getAgentType() ) {
         case Unity:
            container.as(Characteristics.CACHE).addComponent(ShoreFacePerceptor.Agent.class);
            break;
         case Reeti:
            container.as(Characteristics.CACHE).addComponent(ShoreFacePerceptor.Reeti.class);
            break;
         case Mirror:
            container.as(Characteristics.CACHE).addComponent(ShoreFacePerceptor.Mirror.class);
            break;
      }
      //container.as(Characteristics.CACHE).addComponent(PIRMovementPerceptor.class);
      container.as(Characteristics.CACHE).addComponent(DummyMovementPerceptor.class);
      container.as(Characteristics.CACHE).addComponent(FaceMovementMenuEngagementPerceptor.class);
   }
}
