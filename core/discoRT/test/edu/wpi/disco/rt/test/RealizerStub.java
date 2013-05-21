package edu.wpi.disco.rt.test;

import com.google.common.collect.Lists;
import edu.wpi.disco.rt.Resource;
import edu.wpi.disco.rt.behavior.CompoundBehavior;
import edu.wpi.disco.rt.realizer.*;
import java.util.List;

public class RealizerStub implements IRealizer {

   public List<CompoundBehavior> realizedBehaviors = Lists.newArrayList();

   @Override
   public void realize (CompoundBehavior behavior) {
      realizedBehaviors.add(behavior);
   }

   @Override
   public void freeUpResource (Resource r) {
   }
}
