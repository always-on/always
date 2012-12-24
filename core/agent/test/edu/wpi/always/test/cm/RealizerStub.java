package edu.wpi.always.test.cm;

import com.google.common.collect.Lists;
import edu.wpi.disco.rt.Resource;
import edu.wpi.disco.rt.realizer.*;
import java.util.List;

public class RealizerStub implements Realizer {

   public List<CompoundBehavior> realizedBehaviors = Lists.newArrayList();

   @Override
   public void realize (CompoundBehavior behavior) {
      realizedBehaviors.add(behavior);
   }

   @Override
   public void freeUpResource (Resource r) {
   }
}
