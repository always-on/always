package edu.wpi.always.cm.realizer;

import edu.wpi.always.cm.Resource;
import java.util.Set;

public interface CompoundBehavior {

   Set<Resource> getResources ();

   /**
    * Creates a realizer for a compound behavior. Make sure the realizer and all
    * its minions request PrimitiveBehaviors through the instance passed in here
    * 
    * @param primitiveControl
    * @return
    */
   CompoundRealizer createRealizer (PrimitiveBehaviorControl primitiveControl);
}
