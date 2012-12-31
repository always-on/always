package edu.wpi.always.cm.primitives.console;

import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.primitives.FaceTrackBehavior;
import edu.wpi.disco.rt.realizer.PrimitiveRealizerBase;

public class ConsoleFaceTrackerRealizer extends
      PrimitiveRealizerBase<FaceTrackBehavior> {

   private final FacePerceptor perceptor;

   public ConsoleFaceTrackerRealizer (FaceTrackBehavior params,
         FacePerceptor perceptor) {
      super(params);
      this.perceptor = perceptor;
   }

   @Override
   public void run () {
      FacePerception perception = perceptor.getLatest();
      if ( perception != null )
         System.out.println("Tracking face @ " + perception.getPoint());
      else
         System.out.println("WARNING: no face, but face tracker is active");
   }
}
