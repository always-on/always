package edu.wpi.always.cm;

import edu.wpi.always.cm.perceptors.dummy.*;
import edu.wpi.always.cm.primitives.PluginSpecificActionRealizer;
import edu.wpi.disco.rt.DiscoRT;
import org.picocontainer.*;

public class CollaborationManager extends DiscoRT implements ICollaborationManager {

   public CollaborationManager (MutablePicoContainer parent) {
      super(parent);
      container.as(Characteristics.CACHE).addComponent(CollaborationIdleBehaviors.class);
      container.addComponent(PluginSpecificActionRealizer.class);
   }
 
   @Override
   public void start () {
      // FIXME Try to use real sensors
      container.as(Characteristics.CACHE).addComponent(DummyMovementPerceptor.class); 
      container.as(Characteristics.CACHE).addComponent(DummyFacePerceptor.class);
      container.as(Characteristics.CACHE).addComponent(DummyEngagementPerceptor.class);
      super.start();
   }
}
