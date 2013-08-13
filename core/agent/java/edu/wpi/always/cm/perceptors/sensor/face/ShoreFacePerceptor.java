package edu.wpi.always.cm.perceptors.sensor.face;

import org.joda.time.DateTime;

import edu.wpi.always.cm.perceptors.FacePerception;
import edu.wpi.always.cm.perceptors.FacePerceptor;

public class ShoreFacePerceptor implements FacePerceptor {

   private volatile FacePerception latest;
   private final FaceDetection shore = new FaceDetection(0);

   @Override
   public FacePerception getLatest () {
      return latest;
   }

   @Override
   public void run () {
      CPPinterface.FaceInfo info = shore.getFaceInfo(0);
      latest = new FacePerception(DateTime.now(), 
            info.intTop, info.intBottom, info.intLeft, info.intRight, info.intHappiness);
   }
}
