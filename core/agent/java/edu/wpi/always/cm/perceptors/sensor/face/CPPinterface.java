package edu.wpi.always.cm.perceptors.sensor.face;

import com.sun.jna.*;
import edu.wpi.always.cm.perceptors.sensor.NativeUtil;

public interface CPPinterface extends Library {

   public class FaceInfo extends Structure {

      public static class ByValue extends FaceInfo implements Structure.ByValue {
      }

      // public static class ByReference extends Point implements
      // Structure.ByReference {}
      public int intLeft;
      public int intRight;
      public int intTop;
      public int intBottom;
      public int intHappiness;
   }

   public class LoadHelper {

      public static CPPinterface loadLibrary () {
         NativeUtil
               .copyLibraryFromResource(
                     "/edu/wpi/always/collabman/perceptors/physical/face/opencv_core242",
                     "opencv_core242");
         NativeUtil
               .copyLibraryFromResource(
                     "/edu/wpi/always/collabman/perceptors/physical/face/opencv_features2d242",
                     "opencv_features2d242");
         NativeUtil
               .copyLibraryFromResource(
                     "/edu/wpi/always/collabman/perceptors/physical/face/opencv_highgui242",
                     "opencv_highgui242");
         NativeUtil
               .copyLibraryFromResource(
                     "/edu/wpi/always/collabman/perceptors/physical/face/opencv_imgproc242",
                     "opencv_imgproc242");
         NativeUtil.copyLibraryFromResource(
               "/edu/wpi/always/collabman/perceptors/physical/face/Shore140",
               "Shore140");
         NativeUtil.copyLibraryFromResource(
               "/edu/wpi/always/collabman/perceptors/physical/face/tbb", "tbb");
         try {
            return (CPPinterface) NativeUtil
                  .loadLibraryFromResource(
                        "/edu/wpi/always/collabman/perceptors/physical/face/FaceDetection",
                        "FaceDetection", CPPinterface.class);
         } catch (UnsatisfiedLinkError e) {
            return null;
         }
      }
   }

   CPPinterface INSTANCE = LoadHelper.loadLibrary();

   FaceInfo.ByValue getFaceInfo (int intDebug);

   void initProcess (int intDebug);

   void terminateProcess (int intDebug);
}