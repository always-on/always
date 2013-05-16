package edu.wpi.disco.rt.behavior;

import edu.wpi.disco.rt.Resource;
import edu.wpi.disco.rt.realizer.*;
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
