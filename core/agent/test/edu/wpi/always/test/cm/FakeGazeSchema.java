package edu.wpi.always.test.cm;

import edu.wpi.always.cm.primitives.GazeBehavior;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.SchemaBase;
import java.awt.Point;

public class FakeGazeSchema extends SchemaBase {

   final Point point;

   public FakeGazeSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory resourceMonitor, Point fixedGazePoint) {
      super(behaviorReceiver, resourceMonitor);
      point = fixedGazePoint;
   }

   @Override
   public void run () {
      propose(new GazeBehavior(point), 1);
   }
}
