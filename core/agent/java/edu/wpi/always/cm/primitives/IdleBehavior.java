package edu.wpi.always.cm.primitives;

import edu.wpi.disco.rt.Resource;
import edu.wpi.disco.rt.realizer.PrimitiveBehavior;

public class IdleBehavior extends PrimitiveBehavior {

   private final boolean enabled;

   public IdleBehavior (boolean enabled) {
      this.enabled = enabled;
   }

   @Override
   public Resource getResource () {
      return AgentResources.IDLE;
   }

   public boolean isEnable () {
      return enabled;
   }

   @Override
   public boolean equals (Object o) {
      if ( this == o )
         return true;
      if ( !(o instanceof IdleBehavior) )
         return false;
      IdleBehavior theOther = (IdleBehavior) o;
      return theOther.enabled == this.enabled;
   }

   @Override
   public int hashCode () {
      return enabled ? 1 : 0;
   }

   @Override
   public String toString () {
      return "IDLE(" + enabled + ')';
   }
}
