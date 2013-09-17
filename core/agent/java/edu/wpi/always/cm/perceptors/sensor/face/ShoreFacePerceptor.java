package edu.wpi.always.cm.perceptors.sensor.face;

import org.joda.time.DateTime;
import java.io.*;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.*;

public class ShoreFacePerceptor implements FacePerceptor {

   private volatile FacePerception latest;
   private volatile FacePerception ReetiLatest;
   private final FaceDetection shore = new FaceDetection(0, Always.getAgentType());
   
   @Override
   public FacePerception getLatest () {
      return latest;
   }
   
   @Override
   public FacePerception getReetiLatest() {
      return ReetiLatest;
   }

   @Override
   public void run () {
  
      Always.AgentType agentType = Always.getAgentType();
      if(agentType == Always.AgentType.Both)
      {
         CPPinterface.FaceInfo info = shore.ReetigetFaceInfo(0);
         ReetiLatest = new FacePerception(DateTime.now(), 
               info.intTop, info.intBottom, info.intLeft, info.intRight, info.intHappiness, info.intArea, info.intCenter, info.intTiltCenter); 
      }
      else if(agentType == Always.AgentType.Reeti)
      {    
         CPPinterface.FaceInfo info = shore.ReetigetFaceInfo(0);
         ReetiLatest = new FacePerception(DateTime.now(), 
               info.intTop, info.intBottom, info.intLeft, info.intRight, info.intHappiness, info.intArea, info.intCenter, info.intTiltCenter); 
      }
      else if(agentType == Always.AgentType.Unity)
      {
         CPPinterface.FaceInfo info = shore.AgentgetFaceInfo(0);
         latest = new FacePerception(DateTime.now(), 
               info.intTop, info.intBottom, info.intLeft, info.intRight, info.intHappiness, info.intArea, info.intCenter, info.intTiltCenter);
      }  
   }
}