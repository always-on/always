package edu.wpi.always.cm.perceptors;

import org.picocontainer.*;
import edu.wpi.always.Always;
import edu.wpi.always.cm.perceptors.dummy.*;
import edu.wpi.always.cm.perceptors.sensor.face.ShoreFacePerceptor;
import edu.wpi.always.cm.perceptors.sensor.pir.PIRMovementPerceptor;
import edu.wpi.disco.rt.util.ComponentRegistry;

public class EngagementRegistry implements ComponentRegistry {

   @Override
   public void register (MutablePicoContainer container) {
      switch ( Always.getAgentType() ) {
         case Unity:
            container.as(Characteristics.CACHE).addComponent(ShoreFacePerceptor.Agent.class);
            break;
         case Reeti:
         // we are not running both Reeti and virtual agent face perceptors in Mirror mode
         // since the face tracking for virtual agent would interfere with Reeti
         case Mirror:
            container.as(Characteristics.CACHE).addComponent(ShoreFacePerceptor.Reeti.class);
            break;
      }
      // TODO: Replace dummy with real PIR below
      //container.as(Characteristics.CACHE).addComponent(PIRMovementPerceptor.class);
      container.as(Characteristics.CACHE).addComponent(DummyMovementPerceptor.class);
      container.as(Characteristics.CACHE).addComponent(FaceMovementMenuEngagementPerceptor.class);
   }
}
