package edu.wpi.always.cm.perceptors.sensor.face;

import java.util.*;
import com.sun.jna.*;

public interface CPPinterface extends Library {

   public class FaceInfo extends Structure implements Structure.ByValue {

      public int intLeft, intRight, intTop, intBottom, intHappiness;
      private final static List<String> fieldOrder = Arrays.asList(
         new String[]{"intLeft", "intRight", "intTop", "intBottom", "intHappiness"});
      
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

   FaceInfo getFaceInfo (int intDebug);

   void initProcess (int intDebug);

   void terminateProcess (int intDebug);
}