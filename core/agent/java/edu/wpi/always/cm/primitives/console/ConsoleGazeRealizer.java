package edu.wpi.always.cm.primitives.console;

import edu.wpi.always.cm.primitives.GazeBehavior;
import edu.wpi.disco.rt.realizer.PrimitiveRealizerImplBase;

public class ConsoleGazeRealizer extends
      PrimitiveRealizerImplBase<GazeBehavior> {

   public ConsoleGazeRealizer (GazeBehavior params) {
      super(params);
   }

   @Override
   public void run () {
      System.out.println("GAZE @ " + getParams().getPoint());
   }
}
