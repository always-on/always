package edu.wpi.always.cm.perceptors.sensor.face;

import com.sun.jna.NativeLibrary;
import edu.wpi.always.*;

public class FaceDetection {

   private final CPPinterface lib;

   public FaceDetection (boolean debug, Always.AgentType agentType) {

      lib = CPPinterface.INSTANCE;
      int intDebug = debug ? 1 : 0;
      if ( lib != null ) {
         switch (agentType) {
            case Mirror:
               lib.initAgentShoreEngine(intDebug);
               // fall through
            case Reeti:
               // TODO: This should come by reading user/Reeti.json
               String[] ptr = new String[] { "130.215.28.4" };
               lib.initReetiShoreEngine(ptr, intDebug);
               break;
            case Unity:
               lib.initAgentShoreEngine(intDebug);
               break;
         }
      }
   }

   public CPPinterface.FaceInfo getAgentFaceInfo (boolean debug) {
      return lib == null ? new CPPinterface.FaceInfo() : 
         lib.getAgentFaceInfo(debug ? 1 : 0);
   }

   public CPPinterface.FaceInfo getReetiFaceInfo (boolean debug) {
      return lib == null ? new CPPinterface.FaceInfo() : 
         lib.getReetiFaceInfo(debug ? 1 : 0);
   }

   public void terminateFaceDetectionProcess (boolean debug,
         Always.AgentType agentType) {
      int intDebug = debug ? 1 : 0;
      if ( lib != null )
         switch (agentType) {
            case Mirror:
               lib.terminateAgentShoreEngine(intDebug);
               // fall through
            case Reeti:
               lib.terminateReetiShoreEngine(intDebug);
               break;
            case Unity:
               lib.terminateAgentShoreEngine(intDebug);
               break;
         }
   }
}