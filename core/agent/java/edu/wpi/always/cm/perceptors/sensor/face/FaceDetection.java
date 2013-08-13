package edu.wpi.always.cm.perceptors.sensor.face;

import com.sun.jna.NativeLibrary;

public class FaceDetection {

   private final CPPinterface lib;

   public FaceDetection (int intDebug) {
   	NativeLibrary.addSearchPath("FaceDetection","C:\\Users\\mel\\Documents\\GitHub\\always\\core\\agent\\lib\\native");
      lib = CPPinterface.INSTANCE;
      if ( lib != null )
         lib.initProcess(intDebug);
   }

   public CPPinterface.FaceInfo getFaceInfo (int intDebug) {
      if ( lib == null )
         return new CPPinterface.FaceInfo();
      CPPinterface.FaceInfo result = lib.getFaceInfo(intDebug);
      return result;
   }

   public void terminateFaceDetectionProcess (int intDebug) {
      if ( lib != null )
         lib.terminateProcess(intDebug);
   }
}