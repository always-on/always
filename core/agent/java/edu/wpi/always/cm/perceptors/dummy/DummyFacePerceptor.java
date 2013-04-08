package edu.wpi.always.cm.perceptors.dummy;

import edu.wpi.always.cm.perceptors.*;

public class DummyFacePerceptor implements FacePerceptor {

   private volatile FacePerception latest;

   @Override
   public FacePerception getLatest () {
      return latest;
   }

   @Override
   public void run () {
      latest = new FacePerception(0, 0, 0, 0, -1);
   }
}
