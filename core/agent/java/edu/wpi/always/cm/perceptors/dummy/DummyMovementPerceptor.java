package edu.wpi.always.cm.perceptors.dummy;

import edu.wpi.always.cm.perceptors.*;
import org.joda.time.DateTime;
import java.awt.Point;

public class DummyMovementPerceptor implements MovementPerceptor {

   private volatile MovementPerception latest;

   @Override
   public MovementPerception getLatest () {
      return latest;
   }

   @Override
   public void run () {
      latest = new MovementPerception(false, null);
   }

}
