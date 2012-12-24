package edu.wpi.disco.rt;

import java.io.*;
import java.util.List;

public class Utils {

   public static String readAllText (File file) {
      FileInputStream stream = null;
      try {
         stream = new FileInputStream(file);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
      if ( stream != null )
         return readAllText(stream);
      return null;
   }

   public static String readAllText (InputStream stream) {
      int len;
      char[] chr = new char[4096];
      final StringBuffer buffer = new StringBuffer();
      try {
         final InputStreamReader reader = new InputStreamReader(stream);
         try {
            while ((len = reader.read(chr)) > 0) {
               buffer.append(chr, 0, len);
            }
         } finally {
            reader.close();
         }
      } catch (IOException ex) {
         ex.printStackTrace();
         return "";
      }
      return buffer.toString();
   }
   /*
    * public static File getResourceAsFile(String resourcePath){ return
    * getResourceAsFile(FileUtils.class, resourcePath); } public static File
    * getResourceAsFile(Class<?> clazz, String resourcePath){ URL url =
    * clazz.getResource(resourcePath); try { return new File(url.toURI()); }
    * catch (URISyntaxException e) { return new File(url.getPath()); } }
    */

   public static String readAllText (String path) {
      return Utils.readAllText(new File(path));
   }

   public static String listify (List<?> list) {
      StringBuffer buffer = new StringBuffer();
      boolean first = true;
      for (Object object : list) {
         if ( first )
            first = false;
         else
            buffer.append(", ");
         buffer.append(object);
      }
      return buffer.toString();
   }

   private Utils () {
   }
}
