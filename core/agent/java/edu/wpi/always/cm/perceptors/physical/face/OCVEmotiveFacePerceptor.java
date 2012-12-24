package edu.wpi.always.cm.perceptors.physical.face;

import edu.wpi.always.cm.perceptors.*;
import org.joda.time.DateTime;

public class OCVEmotiveFacePerceptor implements EmotiveFacePerceptor {

   volatile EmotiveFacePerception latest;
   private final FaceDetection face;

   @Override
   public EmotiveFacePerception getLatest () {
      return latest;
   }

   public OCVEmotiveFacePerceptor () {
      face = new FaceDetection(0);
   }

   @Override
   public void run () {
      latest = new EmotiveFacePerceptionImpl(DateTime.now(),
            face.getFaceInfo(0));
   }
}
