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
            lib.initProcess(intDebug);
            String[] ptr = new String[]{"130.215.28.4"}; //TODO: This should come by reading from Reeti's json profile.
            lib.initProcessReeti(ptr, intDebug);
         }
         else if(agentType == Always.AgentType.Unity)
         {
            lib.initProcess(intDebug);
         }
         else if(agentType == Always.AgentType.Reeti)
         {
            String[] ptr = new String[]{"130.215.28.4"}; //TODO: This should come by reading from Reeti's json profile.
            lib.initProcessReeti(ptr, intDebug);
         }
      }
   }

   public CPPinterface.FaceInfo getFaceInfo (int intDebug) {
      if ( lib == null )
         return new CPPinterface.FaceInfo();
      CPPinterface.FaceInfo result = lib.getFaceInfo(intDebug);
      return result;
   }
   
   public CPPinterface.FaceInfo getFaceInfoReeti (int intDebug)
   {
      if ( lib == null )
         return new CPPinterface.FaceInfo();
      CPPinterface.FaceInfo result = lib.getFaceInfoReeti(intDebug);
      return result;
   }

   public void terminateFaceDetectionProcess (int intDebug, Always.AgentType agentType) {
      if ( lib != null )
      {
         if(agentType == Always.AgentType.Unity || agentType == Always.AgentType.Both)
            lib.terminateProcess(intDebug);
         if(agentType == Always.AgentType.Reeti || agentType == Always.AgentType.Both)
         {
            String[] ptr = new String[]{"130.215.28.4"}; //TODO: This should come by reading from Reeti's json profile.
            lib.terminateProcessReeti(intDebug);
         }
      }
   }
}