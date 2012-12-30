package edu.wpi.always.cm;

import edu.wpi.always.*;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.dummy.*;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.disco.*;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.perceptor.*;
import edu.wpi.disco.rt.realizer.*;
import edu.wpi.disco.rt.schema.*;
import org.picocontainer.*;
import org.picocontainer.behaviors.OptInCaching;
import java.util.*;

public class CollaborationManager extends DiscoRT implements ICollaborationManager {

   public CollaborationManager (PicoContainer parent) {
      super(parent);
      container.as(Characteristics.CACHE).addComponent(CollaborationIdleBehaviors.class);
      container.addComponent(PluginSpecificActionRealizer.class);
   }
 
   @Override
   public void start () {
      container.as(Characteristics.CACHE).addComponent(DummyMovementPerceptor.class); /////////////
      container.as(Characteristics.CACHE).addComponent(DummyFacePerceptor.class);
      container.as(Characteristics.CACHE).addComponent(DummyEngagementPerceptor.class);
      super.start();
   }
}
