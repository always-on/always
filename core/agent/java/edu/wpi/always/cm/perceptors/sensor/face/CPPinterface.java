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
            // System.setProperty("jna.debug_load", "true");
            return (CPPinterface) Native.loadLibrary("FaceDetection", CPPinterface.class);
         } catch (UnsatisfiedLinkError e) { throw new RuntimeException(e); }
      }
   }

   CPPinterface INSTANCE = LoadHelper.loadLibrary();

   FaceInfo getAgentFaceInfo (int intDebug);

   void initAgentShoreEngine (int intDebug);

   void terminateAgentShoreEngine (int intDebug);

   FaceInfo getReetiFaceInfo (int intDebug);

   void initReetiShoreEngine (String[] iP_ADDRESS, int intDebug);

   void terminateReetiShoreEngine (int intDebug);
}