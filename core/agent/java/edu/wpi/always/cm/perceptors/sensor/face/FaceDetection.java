package edu.wpi.always.cm.perceptors.sensor.face;

import com.sun.jna.NativeLibrary;
import edu.wpi.always.*;

public class FaceDetection {

   public FaceDetection (boolean debug, Always.AgentType agentType) {

      int intDebug = debug ? 1 : 0;
      if ( CPPinterface.INSTANCE != null ) {
         switch (agentType) {
            case Mirror:
               CPPinterface.INSTANCE.initAgentShoreEngine(intDebug);
               // fall through
            case Reeti:
               // TODO: This should come by reading user/Reeti.json
               String[] ptr = new String[] { "130.215.28.4" };
               CPPinterface.INSTANCE.initReetiShoreEngine(ptr, intDebug);
               break;
            case Unity:
               CPPinterface.INSTANCE.initAgentShoreEngine(intDebug);
               break;
         }
      }
   }

   public CPPinterface.FaceInfo getAgentFaceInfo (boolean debug) {
      return CPPinterface.INSTANCE == null ? new CPPinterface.FaceInfo() : 
         CPPinterface.INSTANCE.getAgentFaceInfo(debug ? 1 : 0);
   }

   public CPPinterface.FaceInfo getReetiFaceInfo (boolean debug) {
      return CPPinterface.INSTANCE == null ? new CPPinterface.FaceInfo() : 
         CPPinterface.INSTANCE.getReetiFaceInfo(debug ? 1 : 0);
   }

   public void terminateFaceDetectionProcess (boolean debug,
         Always.AgentType agentType) {
      int intDebug = debug ? 1 : 0;
      if ( CPPinterface.INSTANCE != null )
         switch (agentType) {
            case Mirror:
               CPPinterface.INSTANCE.terminateAgentShoreEngine(intDebug);
               // fall through
            case Reeti:
               CPPinterface.INSTANCE.terminateReetiShoreEngine(intDebug);
               break;
            case Unity:
               CPPinterface.INSTANCE.terminateAgentShoreEngine(intDebug);
               break;
         }
   }
}