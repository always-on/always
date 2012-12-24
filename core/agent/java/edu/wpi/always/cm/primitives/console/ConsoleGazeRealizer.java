package edu.wpi.always.cm.primitives.console;

import edu.wpi.always.cm.primitives.GazeBehavior;
import edu.wpi.always.cm.realizer.PrimitiveRealizerImplBase;

public class ConsoleGazeRealizer extends
      PrimitiveRealizerImplBase<GazeBehavior> {

   public ConsoleGazeRealizer (GazeBehavior params) {
      super(params);
   }

   @Override
   public void run () {
      System.out.println("Gaze @ " + getParams().getPoint());
   }
}
