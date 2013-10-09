package edu.wpi.disco.rt.util;

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

   private Utils () {}
}
