package edu.wpi.always.cm.perceptors.dummy;

import edu.wpi.always.cm.perceptors.*;
import org.joda.time.DateTime;
import java.awt.Point;

public class DummyFacePerceptor implements FacePerceptor {

   FacePerception latest;

   @Override
   public FacePerception getLatest () {
      return latest;
   }

   @Override
   public void run () {
      latest = new FacePerception() {

         DateTime d = DateTime.now();

         @Override
         public DateTime getTimeStamp () {
            return d;
         }

         @Override
         public Point faceLocation () {
            return new Point(10, 20);
         }
      };
   }
}
