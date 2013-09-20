package edu.wpi.always.cm.perceptors.sensor.face;

import org.joda.time.DateTime;
import java.io.*;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.*;

public class ReetiShoreFacePerceptor implements FacePerceptor {

   private volatile FacePerception ReetiLatest;
   private final FaceDetection shore = new FaceDetection(0, Always.getAgentType());
   
   @Override
   public FacePerception getLatest () {
      return ReetiLatest;
   }
   
   @Override
   public void run () {
  
      CPPinterface.FaceInfo info = shore.getReetiFaceInfo(0);
      ReetiLatest = new FacePerception(DateTime.now(), 
            info.intTop, info.intBottom, info.intLeft, info.intRight, info.intArea, info.intCenter, info.intTiltCenter); 
   }
}