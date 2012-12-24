package edu.wpi.always.client;

import com.google.common.collect.Lists;
import java.util.*;

public class JsonBreakDown {

   public static List<String> stringsOfIndividualClsses (String source) {
      ArrayList<String> result = Lists.newArrayList();
      boolean insideOne = false, foundAtLeastOne = false;
      int notMatched = 0;
      StringBuilder currentOne = null;
      for (int i = 0; i < source.length(); i++) {
         if ( !insideOne ) {
            if ( source.charAt(i) == '{' ) {
               foundAtLeastOne = insideOne = true;
               notMatched = 1;
               currentOne = new StringBuilder();
               currentOne.append(source.charAt(i));
            }
         } else {
            if ( source.charAt(i) == '{' )
               notMatched++;
            if ( source.charAt(i) == '}' )
               notMatched--;
            currentOne.append(source.charAt(i));
            if ( notMatched == 0 ) {
               insideOne = false;
               result.add(currentOne.toString());
            }
         }
      }
      if ( insideOne )
         result.add(currentOne.toString());
      if ( !foundAtLeastOne )
         result.add(source);
      return result;
   }
}
