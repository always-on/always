package edu.wpi.always;

import java.util.List;

public class Utils {

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
