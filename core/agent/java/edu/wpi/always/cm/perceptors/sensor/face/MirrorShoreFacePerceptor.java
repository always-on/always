package edu.wpi.always.cm.perceptors.sensor.face;

import org.joda.time.DateTime;
import java.io.*;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.*;

public class MirrorShoreFacePerceptor implements FacePerceptor {

   private volatile FacePerception latest, reetiLatest;
   
   private final FaceDetection shore = new FaceDetection(false, Always.AgentType.Unity),
         reetiShore = new FaceDetection(false, Always.AgentType.Reeti);
   
   @Override
   public FacePerception getLatest () {
      return latest;
   }
   
   public FacePerception getReetiLatest () {
      return reetiLatest;
   }
   
   @Override
   public void run () {
      CPPinterface.FaceInfo info = shore.getAgentFaceInfo(false);
      latest = new FacePerception(DateTime.now(), 
            info.intTop, info.intBottom, info.intLeft, info.intRight, info.intArea, info.intCenter, info.intTiltCenter);
      info = reetiShore.getAgentFaceInfo(false);
      reetiLatest = new FacePerception(DateTime.now(), 
            info.intTop, info.intBottom, info.intLeft, info.intRight, info.intArea, info.intCenter, info.intTiltCenter);
   }
}