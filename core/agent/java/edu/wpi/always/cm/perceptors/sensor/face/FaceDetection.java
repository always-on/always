package edu.wpi.always.cm.perceptors.sensor.face;

import com.sun.jna.NativeLibrary;
import edu.wpi.always.*;

public class FaceDetection {

   private final CPPinterface lib;

   private Always.AgentType AgentOrReetiOrBoth()
   {
      return Always.getAgentType();
   }
   
   public FaceDetection (int intDebug) {
      lib = CPPinterface.INSTANCE;
      if ( lib != null )
      {
         if(AgentOrReetiOrBoth() == Always.AgentType.Unity || AgentOrReetiOrBoth() == Always.AgentType.Both)
            lib.initProcess(intDebug);
         if(AgentOrReetiOrBoth() == Always.AgentType.Reeti || AgentOrReetiOrBoth() == Always.AgentType.Both)
         {
            String[] ptr = new String[]{"130.215.28.4"};
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

   public void terminateFaceDetectionProcess (int intDebug) {
      if ( lib != null )
      {
         if(AgentOrReetiOrBoth() == Always.AgentType.Unity || AgentOrReetiOrBoth() == Always.AgentType.Both)
            lib.terminateProcess(intDebug);
         if(AgentOrReetiOrBoth() == Always.AgentType.Reeti || AgentOrReetiOrBoth() == Always.AgentType.Both)
         {
            String[] ptr = new String[]{"130.215.28.4"};
            lib.terminateProcessReeti(intDebug);
         }
      }
   }
}