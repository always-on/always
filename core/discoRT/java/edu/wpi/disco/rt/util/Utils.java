package edu.wpi.disco.rt.util;

import java.io.PrintStream;
import java.util.*;

public abstract class Utils {
 
   public static String listify (List<?> list) {
      if ( list == null ) return "()";
      StringBuffer buffer = new StringBuffer();
      boolean first = true;
      for (Object object : list) {
         if ( first ) first = false;
         else buffer.append(", ");
         buffer.append(object);
      }
      return buffer.toString();
   }
   
   public static String listify (Set<?> set) {
      return listify(new ArrayList<Object>(set));
   }

   /**
    * Reverse of println, i.e. print platform independent newline
    * first, then string.  Works better in multi-threaded log.
    */
   public static void lnprint (PrintStream stream, String string) {
      StringBuffer buffer = new StringBuffer(string.length()+2);
      buffer.append(System.lineSeparator());
      buffer.append(string);
      stream.print(buffer);
   }

   private Utils () {}
}
