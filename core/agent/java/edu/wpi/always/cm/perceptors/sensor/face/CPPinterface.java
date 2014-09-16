package edu.wpi.always.cm.perceptors.sensor.face;

import java.util.*;
import com.sun.jna.*;
import com.sun.jna.ptr.PointerByReference;
import edu.wpi.always.cm.perceptors.sensor.face.CPPinterface.FaceInfo;
import edu.wpi.disco.rt.util.Utils;

public interface CPPinterface extends Library {

   public class FaceInfo extends Structure implements Structure.ByValue {

      public int intLeft, intRight, intTop, intBottom, intHappiness, intArea, intCenter, intTiltCenter;
      private final static List<String> fieldOrder = Arrays.asList(
         new String[]{"intLeft", "intRight", "intTop", "intBottom", "intHappiness", "intArea", "intCenter", "intTiltCenter"});
      
      @Override
      protected List<String> getFieldOrder () { return fieldOrder; }
      
      boolean isFace () { return intLeft >= 0; }
      
      //-2 (or less) is used as a signal to exit and restart as a results of a socket failure
      boolean isRestart () { return intHappiness <= -2; }
   }

   public class LoadHelper {

      public static CPPinterface loadLibrary () {
         try {
            System.setProperty("jna.debug_load", "true");
            return (CPPinterface) Native.loadLibrary("FaceDetection", CPPinterface.class);
         } catch (UnsatisfiedLinkError e) { throw new RuntimeException(e); }
      }
   }

   CPPinterface INSTANCE = LoadHelper.loadLibrary();

   /**
    * @return This method returns all required face information for the agent calculated based on Shore.
    * 
    * In case of error, FaceInfo values will be as the following:
    * 
    * Image content error code     --> -1,
    * Frame capture error code     --> -2,
    * Camera connection error code --> -3.
    */
   FaceInfo getAgentFaceInfo (int intDebug);

   /**
    * @return This method returns -1 in case of Shore engine setup failure.
    * It returns 0, if it successfully creates the face engine.
    */
   int initAgentShoreEngine (int intDebug);

   void terminateAgentShoreEngine (int intDebug);

   /**
    * @return This method returns all required face information for the robot calculated based on Shore.
    * 
    * In case of error, FaceInfo values will be as the following:
    * 
    * Image content error code           --> -1,
    * Sending capture command error code --> -4.
    */
   FaceInfo getReetiFaceInfo (int intDebug);

   /**
    * @return This method returns -1 in case of Shore engine setup failure.
    * It returns 0, if it successfully creates the face engine.
    */
   int initReetiShoreEngine (String[] iP_ADDRESS, int intDebug);

   void terminateReetiShoreEngine (int intDebug);
}
