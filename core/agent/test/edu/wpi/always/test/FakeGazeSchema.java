package edu.wpi.always.test;

import edu.wpi.always.cm.primitives.GazeBehavior;
import edu.wpi.disco.rt.Scheduler;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.SchemaBase;
import java.awt.Point;

public class FakeGazeSchema extends SchemaBase {

   final Point point;

   public FakeGazeSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, Point fixedGazePoint) {
      super(behaviorReceiver, behaviorHistory);
      point = fixedGazePoint;
   }

   @Override
   public void run () {
      propose(new GazeBehavior(point), 1);
   }
}
