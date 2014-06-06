package edu.wpi.disco.rt.behavior;

import edu.wpi.disco.rt.Resource;
import edu.wpi.disco.rt.realizer.*;
import java.util.*;

public class Behavior {

   public static Behavior NULL = new Behavior(Collections.<PrimitiveBehavior> emptyList());
   
   private final CompoundBehavior inner;

   private Behavior (List<PrimitiveBehavior> required) {
      this.inner = new SimpleCompoundBehavior(required);
   }

   public Behavior (CompoundBehavior behavior) {
      this.inner = behavior;
   }

   public static Behavior newInstance (List<PrimitiveBehavior> required) {
      return new Behavior(required);
   }

   public static Behavior newInstance (PrimitiveBehavior required) {
      return newInstance(Collections.singletonList(required));
   }

   public static Behavior newInstance (PrimitiveBehavior... required) {
      return newInstance(Arrays.asList(required));
   }

   public CompoundBehavior getInner () {
      return inner;
   }

   @Override
   public boolean equals (Object o) {
      if ( o == this )
         return true;
      if ( !(o instanceof Behavior) )
         return false;
      Behavior theOther = (Behavior) o;
      return this.inner.equals(theOther.inner);
   }

   public boolean isEmpty () {
      return getInner().getResources().isEmpty();
   }

   public Behavior addFocusResource () {
       return this.equals(NULL) ? this :
          new Behavior(new SequenceOfCompoundBehaviors(getInner(),
             new SimpleCompoundBehavior(new FocusRequestBehavior())));
   }

   /**
    * Useful to make behavior that does the same thing, but is not equal
    */
   public Behavior addNull () {
      CompoundBehavior inner = getInner();
      return new Behavior(new SequenceOfCompoundBehaviors(inner,
            // make null behavior that uses same resource as inner
            new SimpleCompoundBehavior(PrimitiveBehavior.nullBehavior(inner.getResources().iterator().next()))));
   }
   
   /**
    * Useful to make behavior that does the same thing, but is not equal
    */
   public Behavior addNull () {
      CompoundBehavior inner = getInner();
      return new Behavior(new SequenceOfCompoundBehaviors(inner,
            // make null behavior that uses same resource as inner
            new SimpleCompoundBehavior(PrimitiveBehavior.nullBehavior(inner.getResources().iterator().next()))));
   }
   
   @Override
   public String toString () {
      return getInner().toString();
   }

   public Set<Resource> getResources () {
      return inner.getResources();
   }
}
