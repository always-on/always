package edu.wpi.disco.rt.behavior;

import com.google.common.collect.Lists;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.realizer.*;
import edu.wpi.disco.rt.realizer.petri.PetriRealizer;
import edu.wpi.disco.rt.util.Utils;
import java.util.*;

public class CompoundBehaviorWithConstraints implements CompoundBehavior {

   private final List<PrimitiveBehavior> primitives;
   private final List<Constraint> constraints;

   public CompoundBehaviorWithConstraints (List<PrimitiveBehavior> primitives,
         List<Constraint> constraints) {
      this.primitives = Collections.unmodifiableList(Lists
            .newArrayList(primitives));
      this.constraints = Collections.unmodifiableList(Lists
            .newArrayList(constraints));
   }

   @Override
   public Set<Resource> getResources () {
      Set<Resource> result = new HashSet<Resource>();
      for (PrimitiveBehavior pb : primitives)
         result.add(pb.getResource());
      return result;
   }

   @Override
   public CompoundRealizer createRealizer (
         PrimitiveBehaviorControl primitiveControl) {
      return new PetriRealizer(this, primitiveControl);
   }

   public List<PrimitiveBehavior> getPrimitives () {
      return primitives;
   }

   public List<Constraint> getConstraints () {
      return constraints;
   }

   @Override
   public boolean equals (Object obj) {
      if ( obj == this )
         return true;
      if ( !(obj instanceof CompoundBehaviorWithConstraints) )
         return false;
      CompoundBehaviorWithConstraints theOther = (CompoundBehaviorWithConstraints) obj;
      if ( this.primitives.size() != theOther.primitives.size() )
         return false;
      if ( this.constraints.size() != theOther.constraints.size() )
         return false;
      for (PrimitiveBehavior p : primitives)
         if ( !theOther.primitives.contains(p) )
            return false;
      for (Constraint c : constraints)
         if ( !theOther.constraints.contains(c) )
            return false;
      return true;
   }

   @Override
   public int hashCode () {
      return primitives.hashCode() * 31 + constraints.hashCode();
   }

   @Override
   public String toString () {
      return "Constraints(" + Utils.listify(primitives) + ')';
   }
}
