package edu.wpi.disco.rt.behavior;

import edu.wpi.disco.rt.Resource;
import edu.wpi.disco.rt.realizer.*;
import java.util.*;

// Needed this for use with SequenceofCompoundBehaviors, since
// CompoundBehaviorWithConstraints designed only for PrimitiveBehavior's

public class WaitBehavior implements CompoundBehavior {

   private final long millis;
   
   public WaitBehavior (long millis) {
      this.millis = millis;
   }
   
   @Override
   public Set<Resource> getResources () { return Collections.emptySet(); }

   @Override
   public CompoundRealizer createRealizer (PrimitiveBehaviorControl primitiveControl) {
      return new Realizer(primitiveControl);
   }

   @Override
   public int hashCode () {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (millis ^ (millis >>> 32));
      return result;
   }

   @Override
   public boolean equals (Object obj) {
      if ( this == obj )
         return true;
      if ( obj == null )
         return false;
      if ( getClass() != obj.getClass() )
         return false;
      WaitBehavior other = (WaitBehavior) obj;
      if ( millis != other.millis )
         return false;
      return true;
   }

   @Override
   public String toString () { return "Wait("+millis+")"; }

   private class Realizer extends CompoundRealizerBase implements
         PrimitiveBehaviorControlObserver {

      private boolean done;

      public Realizer (PrimitiveBehaviorControl primitiveControl) {}

      @Override
      public void run () { 
         try { Thread.sleep(millis); }
         catch (InterruptedException e) {}
         done = true;
         notifyDone();
      }

      @Override
      public boolean isDone () { return done; }

      @Override
      public void primitiveDone (PrimitiveBehaviorControl sender,
            PrimitiveBehavior primitive) {}

      @Override
      public void primitiveStopped (PrimitiveBehaviorControl sender,
            PrimitiveBehavior primitive) {}

      @Override
      public String toString () { 
         return WaitBehavior.this.toString();
      }
   }
}

