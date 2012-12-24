package edu.wpi.always.cm.perceptors.physical.pir;

import edu.wpi.always.cm.perceptors.*;
import org.joda.time.DateTime;

public class PIRMotionPerceptor implements MotionPerceptor {

   volatile MotionPerception latest;

   @Override
   public MotionPerception getLatest () {
      return latest;
   }

   private final PIRSensor sensor;

   public PIRMotionPerceptor () {
      sensor = new PIRSensor();
   }

   @Override
   public void run () {
      latest = new MotionPerceptionImpl(DateTime.now(), sensor.getState());
   }
}
