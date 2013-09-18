package edu.wpi.always.cm.perceptors.sensor.face;

import com.sun.jna.NativeLibrary;
import edu.wpi.always.*;

public class FaceDetection {

   private final CPPinterface lib;
   
   public FaceDetection (int intDebug, Always.AgentType agentType) {
      
      lib = CPPinterface.INSTANCE;
      if ( lib != null )
      {
         if(agentType == Always.AgentType.Both)
         {
            lib.initAgentShoreEngine(intDebug);
            String[] ptr = new String[]{"130.215.28.4"}; //TODO: This should come by reading from Reeti's json profile.
            lib.initReetiShoreEngine(ptr, intDebug);
         }
         else if(agentType == Always.AgentType.Unity)
         {
            lib.initAgentShoreEngine(intDebug);
         }
         else if(agentType == Always.AgentType.Reeti)
         {
            String[] ptr = new String[]{"130.215.28.4"}; //TODO: This should come by reading from Reeti's json profile.
            lib.initReetiShoreEngine(ptr, intDebug);
         }
      }
   }

   public CPPinterface.FaceInfo getAgentFaceInfo (int intDebug) {
      if ( lib == null )
         return new CPPinterface.FaceInfo();
      CPPinterface.FaceInfo result = lib.getAgentFaceInfo(intDebug);
      return result;
   }
   
   public CPPinterface.FaceInfo getReetiFaceInfo (int intDebug)
   {
      if ( lib == null )
         return new CPPinterface.FaceInfo();
      CPPinterface.FaceInfo result = lib.getReetiFaceInfo(intDebug);
      return result;
   }

   public void terminateFaceDetectionProcess (int intDebug, Always.AgentType agentType) {
      if ( lib != null )
      {
         if(agentType == Always.AgentType.Both)
         {
            lib.terminateAgentShoreEngine(intDebug);
            String[] ptr = new String[]{"130.215.28.4"}; //TODO: This should come by reading from Reeti's json profile.
            lib.terminateReetiShoreEngine(intDebug);
         }
         else if(agentType == Always.AgentType.Unity)
            lib.terminateAgentShoreEngine(intDebug);
         else if(agentType == Always.AgentType.Reeti)
         {
            String[] ptr = new String[]{"130.215.28.4"}; //TODO: This should come by reading from Reeti's json profile.
            lib.terminateReetiShoreEngine(intDebug);
         }
      }
   }
}