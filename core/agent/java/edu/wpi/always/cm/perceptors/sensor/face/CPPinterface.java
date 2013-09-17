package edu.wpi.always.cm.perceptors.sensor.face;

import java.util.*;
import com.sun.jna.*;
import com.sun.jna.ptr.PointerByReference;
import edu.wpi.always.cm.perceptors.sensor.face.CPPinterface.FaceInfo;

public interface CPPinterface extends Library {

   public class FaceInfo extends Structure implements Structure.ByValue {

      public int intLeft, intRight, intTop, intBottom, intHappiness, intArea, intCenter, intTiltCenter;
      private final static List<String> fieldOrder = Arrays.asList(
         new String[]{"intLeft", "intRight", "intTop", "intBottom", "intHappiness", "intArea", "intCenter", "intTiltCenter"});
      
      @Override
      protected List<String> getFieldOrder () { return fieldOrder; }
   }

   public class LoadHelper {

      public static CPPinterface loadLibrary () {
         try {
            return (CPPinterface) Native.loadLibrary("FaceDetection", CPPinterface.class);
         } catch (UnsatisfiedLinkError e) { throw new RuntimeException(e); }
      }
   }

   CPPinterface INSTANCE = LoadHelper.loadLibrary();

   FaceInfo AgentgetFaceInfo (int intDebug);

   void initAgentShoreEngine (int intDebug);

   void terminateAgentShoreEngine (int intDebug);

   FaceInfo ReetigetFaceInfo (int intDebug);

   void initReetiShoreEngine (String[] iP_ADDRESS, int intDebug);

   void terminateReetiShoreEngine (int intDebug);
}