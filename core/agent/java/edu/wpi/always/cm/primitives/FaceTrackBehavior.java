package edu.wpi.always.cm.primitives;

import edu.wpi.disco.rt.Resource;
import edu.wpi.disco.rt.realizer.PrimitiveBehavior;

public class FaceTrackBehavior extends PrimitiveBehavior {

   @Override
   public Resource getResource () {
      return AgentResources.GAZE;
   }

   @Override
   public boolean equals (Object o) {
      if ( o == this )
         return true;
      if ( !(o instanceof FaceTrackBehavior) ) {
         return false;
      }
      return true;
   }

   @Override
   public int hashCode () {
      return 0;
   }

   @Override
   public String toString () {
      return "FaceTrack";
   }
}
