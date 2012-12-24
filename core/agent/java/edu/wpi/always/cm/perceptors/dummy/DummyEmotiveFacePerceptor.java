package edu.wpi.always.cm.perceptors.dummy;

import edu.wpi.always.cm.perceptors.*;
import org.joda.time.DateTime;
import java.awt.Point;

public class DummyEmotiveFacePerceptor implements EmotiveFacePerceptor {

   private EmotiveFacePerception latest;

   @Override
   public EmotiveFacePerception getLatest () {
      return latest;
   }

   @Override
   public void run () {
      final DateTime timestamp = DateTime.now();
      latest = new EmotiveFacePerception() {

         @Override
         public DateTime getTimeStamp () {
            return timestamp;
         }

         @Override
         public boolean isNear () {
            return false;
         }

         @Override
         public boolean hasFace () {
            return false;
         }

         @Override
         public int getTop () {
            return 0;
         }

         @Override
         public int getRight () {
            return 0;
         }

         @Override
         public Point getLocation () {
            return null;
         }

         @Override
         public int getLeft () {
            return 0;
         }

         @Override
         public int getHappiness () {
            return 0;
         }

         @Override
         public int getBottom () {
            return 0;
         }
      };
   }
}
