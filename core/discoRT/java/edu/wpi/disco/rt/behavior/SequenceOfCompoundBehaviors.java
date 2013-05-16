package edu.wpi.disco.rt.behavior;

import com.google.common.collect.Lists;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.realizer.*;
import edu.wpi.disco.rt.util.*;
import java.util.*;
import java.util.concurrent.ExecutorService;

public class SequenceOfCompoundBehaviors implements CompoundBehavior {

   private final List<CompoundBehavior> innerList;

   public SequenceOfCompoundBehaviors (Iterable<CompoundBehavior> behaviors) {
      innerList = Collections.unmodifiableList(Lists.newArrayList(behaviors));
   }

   public SequenceOfCompoundBehaviors (CompoundBehavior... behaviors) {
      innerList = Collections.unmodifiableList(Lists.newArrayList(behaviors));
   }

   @Override
   public Set<Resource> getResources () {
      Set<Resource> result = new HashSet<Resource>();
      for (CompoundBehavior cb : innerList) {
         result.addAll(cb.getResources());
      }
      return result;
   }

   @Override
   public CompoundRealizer createRealizer (
         PrimitiveBehaviorControl primitiveControl) {
      return new SequenceCompoundRealizer(primitiveControl, this);
   }

   @Override
   public boolean equals (Object o) {
      if ( o == this )
         return true;
      if ( !(o instanceof SequenceOfCompoundBehaviors) )
         return false;
      SequenceOfCompoundBehaviors theOther = (SequenceOfCompoundBehaviors) o;
      if ( theOther.innerList.size() != this.innerList.size() )
         return false;
      for (CompoundBehavior cb : this.innerList) {
         if ( !theOther.innerList.contains(cb) )
            return false;
      }
      return true;
   }

   @Override
   public int hashCode () {
      int h = 31;
      for (CompoundBehavior cb : this.innerList) {
         h = 31 * h + cb.hashCode();
      }
      return h;
   }

   @Override
   public String toString () {
      return "Sequence(" + Utils.listify(innerList) + ')';
   }

   private static class SequenceCompoundRealizer extends
         CompoundRealizerBase {

      private final SequenceOfCompoundBehaviors behavior;
      private final PrimitiveBehaviorControl pbc;
      private boolean done;
      private final ExecutorService executor = ThreadPools
            .newCachedThreadPool();

      public SequenceCompoundRealizer (PrimitiveBehaviorControl pbc,
            SequenceOfCompoundBehaviors behavior) {
         this.pbc = pbc;
         this.behavior = behavior;
      }

      @Override
      public void run () {
         if ( behavior.innerList.size() == 0 ) {
            setDone();
            return;
         }
         List<CompoundRealizer> realizers = Lists
               .newArrayListWithCapacity(behavior.innerList.size());
         for (CompoundBehavior cb : behavior.innerList) {
            realizers.add(cb.createRealizer(pbc));
         }
         for (int i = 0; i < realizers.size() - 1; i++) {
            final CompoundRealizer current = realizers.get(i);
            final CompoundRealizer next = realizers.get(i + 1);
            current.addObserver(new CompoundRealizerObserver() {

               @Override
               public void compoundRealizerDone (CompoundRealizer sender) {
                  sender.removeObserver(this);
                  executor.execute(next);
               }
            });
         }
         CompoundRealizer last = realizers.get(realizers.size() - 1);
         last.addObserver(new CompoundRealizerObserver() {

            @Override
            public void compoundRealizerDone (CompoundRealizer sender) {
               setDone();
            }
         });
         CompoundRealizer first = realizers.get(0);
         first.run();
      }

      private void setDone () {
         done = true;
         notifyDone();
      }

      @Override
      public boolean isDone () {
         return done;
      }

      @Override
      public String toString () {
         return behavior.toString();
      }
   }
}
