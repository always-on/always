package edu.wpi.always.test.cm;

import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.SchemaBase;

public class DummySchema extends SchemaBase {

   public DummySchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory resourceMonitor) {
      super(behaviorReceiver, resourceMonitor);
   }

   @Override
   public void run () {
   }
}
