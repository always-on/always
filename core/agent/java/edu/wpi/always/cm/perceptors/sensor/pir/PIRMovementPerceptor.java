package edu.wpi.always.cm.perceptors.sensor.pir;

import edu.wpi.always.cm.perceptors.*;
import org.joda.time.DateTime;

public class PIRMovementPerceptor implements MovementPerceptor {

   private volatile MovementPerception latest;
   private final PIRSensor sensor = new PIRSensor();

   @Override
   public MovementPerception getLatest () {
      return latest;
   }

   @Override
   public void run () {
      // note location is unknown
      latest = new MovementPerception(DateTime.now(), sensor.getState(), null);
   }
}
