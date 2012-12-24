package edu.wpi.always.test.cm;

import edu.wpi.always.cm.SchemaImplBase;
import edu.wpi.always.cm.primitives.GazeBehavior;
import edu.wpi.disco.rt.*;
import java.awt.Point;

public class FakeGazeSchema extends SchemaImplBase {

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
