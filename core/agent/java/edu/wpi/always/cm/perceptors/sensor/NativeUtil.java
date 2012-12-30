package edu.wpi.always.cm.perceptors.sensor;

import com.sun.jna.Native;
import java.io.*;
import java.util.Map;

public class NativeUtil {

   public static void copyLibraryFromResource (String sourceResource,
         String targetName) {
      File targetFile = new File(System.mapLibraryName(targetName));
      InputStream sourceFileStream = NativeUtil.class
            .getResourceAsStream(System.mapLibraryName(sourceResource));
      FileOutputStream targetFileStream = null;
      try {
         byte[] buffer = new byte[1024];
         int numRead;
         targetFileStream = new FileOutputStream(targetFile);
         while ((numRead = sourceFileStream.read(buffer)) > 0) {
            targetFileStream.write(buffer, 0, numRead);
         }
      } catch (IOException e) {
         System.err.println("Could not copy native library from resource: "
            + sourceResource + " to " + targetFile.getAbsolutePath());
         e.printStackTrace();
      } finally {
         try {
            sourceFileStream.close();
         } catch (IOException e) {
         }
         try {
            if ( targetFileStream != null )
               targetFileStream.close();
         } catch (IOException e) {
         }
      }
   }

   public static Object loadLibraryFromResource (String sourceResource,
         String name, Class<?> interfaceClass, Map<String, Object> options) {
      copyLibraryFromResource(sourceResource, name);
      return Native.loadLibrary(name, interfaceClass, options);
   }

   public static Object loadLibraryFromResource (String sourceResource,
         String name, Class<?> interfaceClass) {
      copyLibraryFromResource(sourceResource, name);
      return Native.loadLibrary(name, interfaceClass);
   }
}
