package edu.wpi.disco.rt.behavior;

import com.google.common.collect.Lists;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.realizer.*;
import edu.wpi.disco.rt.util.Utils;
import java.util.*;

/**
 * For speech behaviors with nested markup that uses additional resources.
 */
public class CompoundSpeechBehavior {
   
   /* implements CompoundBehavior {
   
   private final SpeechBehavior speech;
   private final Set<Resources> resources;

   public CompoundSpeechBehavior (SpeechBehavior speech, Resources... resources) {
      this.speech = speech;
      this.resources = new HashSet(Arrays.asList(resources));
   }

   @Override
   public Set<Resource> getResources () { return resources; }

   @Override
   public CompoundRealizer createRealizer (
         final PrimitiveBehaviorControl primitiveControl) {
      return new Realizer(primitiveControl, primitives);
   }

   @Override
   public boolean equals (Object o) {
      if ( o == this )
         return true;
      if ( !(o instanceof CompoundSpeechBehavior) )
         return false;
      CompoundSpeechBehavior theOther = (CompoundSpeechBehavior) o;
      if ( primitives.size() != theOther.primitives.size() )
         return false;
      for (PrimitiveBehavior p : primitives)
         if ( !theOther.primitives.contains(p) )
            return false;
      return true;
   }

   @Override
   public int hashCode () {
      return primitives.hashCode();
   }

   public List<PrimitiveBehavior> getPrimitives () {
      return primitives;
   }

   @Override
   public String toString () {
      return "Simple(" + Utils.listify(primitives) + ')';
   }

   public static class Realizer extends CompoundRealizerBase implements
         PrimitiveBehaviorControlObserver {

      private final PrimitiveBehaviorControl primitiveControl;
      private final Map<PrimitiveBehavior, Boolean> primitivesStatus = new HashMap<PrimitiveBehavior, Boolean>();
      private final List<PrimitiveBehavior> primitives;

      public Realizer (PrimitiveBehaviorControl primitiveControl,
            List<PrimitiveBehavior> primitives) {
         this.primitiveControl = primitiveControl;
         this.primitives = primitives;
         for (PrimitiveBehavior pb : primitives)
            primitivesStatus.put(pb, false);
      }

      @Override
      public void run () {
         primitiveControl.addObserver(this);
         for (PrimitiveBehavior pb : primitivesStatus.keySet())
            primitiveControl.realize(pb);
      }

      @Override
      public boolean isDone () {
         for (Boolean b : primitivesStatus.values())
            if ( !b )
               return false;
         return true;
      }

      @Override
      public void primitiveDone (PrimitiveBehaviorControl sender,
            PrimitiveBehavior pb) {
         if ( primitivesStatus.containsKey(pb) ) {
            primitivesStatus.put(pb, true);
            if ( isDone() ) {
               notifyDone();
            }
         }
      }

      @Override
      public void primitiveStopped (PrimitiveBehaviorControl sender,
            PrimitiveBehavior pb) {
      }

      @Override
      public String toString () {
         return "Simple(" + Utils.listify(primitives) + ')';
      }
   }
   */
}
