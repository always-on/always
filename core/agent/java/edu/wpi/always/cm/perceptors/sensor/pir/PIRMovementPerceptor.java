package edu.wpi.always.cm.perceptors.sensor.pir;

import org.joda.time.DateTime;
import edu.wpi.always.cm.perceptors.MovementPerception;
import edu.wpi.always.cm.perceptors.MovementPerceptor;
import edu.wpi.disco.rt.perceptor.PerceptorBase;

public class PIRMovementPerceptor extends PerceptorBase<MovementPerception>
             implements MovementPerceptor {

   private final PIRSensor sensor = new PIRSensor();

   @Override
   public void run () {
      // note location is unknown
      latest = new MovementPerception(sensor.getState(), null);
   }

   // This main method tests the PIR sensor functionality
   public static void main(String[] args) {

      PIRMovementPerceptor movementPerceptor = new PIRMovementPerceptor();

      while(true){
         System.out.println(movementPerceptor.sensor.getState());
         try {
            Thread.sleep(200);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }

   }

}

