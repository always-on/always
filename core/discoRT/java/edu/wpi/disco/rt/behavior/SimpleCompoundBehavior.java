package edu.wpi.disco.rt.behavior;

import com.google.common.collect.Lists;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.realizer.*;
import edu.wpi.disco.rt.util.Utils;
import java.util.*;

public class SimpleCompoundBehavior implements CompoundBehavior {

   private final List<PrimitiveBehavior> primitives;

   public SimpleCompoundBehavior (Iterable<PrimitiveBehavior> primitives) {
      this.primitives = Collections.unmodifiableList(Lists
            .newArrayList(primitives));
   }

   public SimpleCompoundBehavior (PrimitiveBehavior... primitives) {
      this.primitives = Collections.unmodifiableList(Lists
            .newArrayList(primitives));
   }

   @Override
   public Set<Resource> getResources () {
      Set<Resource> result = new HashSet<Resource>();
      for (PrimitiveBehavior pb : primitives)
         result.add(pb.getResource());
      return result;
   }

   @Override
   public CompoundRealizer createRealizer (PrimitiveBehaviorControl primitiveControl) {
      return new Realizer(primitiveControl);
   }

   @Override
   public boolean equals (Object o) {
      if ( o == this )
         return true;
      if ( !(o instanceof SimpleCompoundBehavior) )
         return false;
      SimpleCompoundBehavior theOther = (SimpleCompoundBehavior) o;
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

   private class Realizer extends CompoundRealizerBase implements
         PrimitiveBehaviorControlObserver {

      private final PrimitiveBehaviorControl primitiveControl;
      private final Map<PrimitiveBehavior, Boolean> primitivesStatus = new HashMap<PrimitiveBehavior, Boolean>();

      public Realizer (PrimitiveBehaviorControl primitiveControl) {
         this.primitiveControl = primitiveControl;
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
}
