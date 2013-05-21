package edu.wpi.always.test;

import static com.google.common.collect.Lists.newArrayList;
import edu.wpi.disco.rt.Resource;
import edu.wpi.disco.rt.behavior.PrimitiveBehavior;
import edu.wpi.disco.rt.realizer.*;
import java.util.List;

class PrimitiveBehaviorControlStub implements PrimitiveBehaviorControl {

   public PrimitiveBehaviorControlObserver observer;
   public List<PrimitiveBehavior> realizedBehaviors = newArrayList();

   @Override
   public PrimitiveRealizerHandle realize (PrimitiveBehavior behavior) {
      realizedBehaviors.add(behavior);
      return null;
   }

   @Override
   public void addObserver (PrimitiveBehaviorControlObserver observer) {
      this.observer = observer;
   }

   @Override
   public void removeObserver (PrimitiveBehaviorControlObserver observer) {
      this.observer = null;
   }

   @Override
   public void stop (Resource gaze) {
   }
}