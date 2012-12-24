package edu.wpi.always.cm.perceptors.dummy;

import edu.wpi.always.cm.perceptors.*;
import org.joda.time.DateTime;

public class DummyMotionPerceptor implements MotionPerceptor {

   private MotionPerceptionImpl latest;

   @Override
   public MotionPerception getLatest () {
      return latest;
   }

   @Override
   public void run () {
      latest = new MotionPerceptionImpl(DateTime.now(), false);
   }
}
