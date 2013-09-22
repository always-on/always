package edu.wpi.always.cm.perceptors.sensor.face;

import org.joda.time.DateTime;
import java.io.*;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.*;

public class ShoreFacePerceptor implements FacePerceptor {

   private volatile FacePerception latest;
   private final FaceDetection shore = new FaceDetection(0, Always.getAgentType());
   
   @Override
   public FacePerception getLatest () {
      return latest;
   }
   
   @Override
   public void run () {
  
      CPPinterface.FaceInfo info = shore.getAgentFaceInfo(0);
      latest = new FacePerception(DateTime.now(), 
            info.intTop, info.intBottom, info.intLeft, info.intRight, info.intArea, info.intCenter, info.intTiltCenter);
   }
}