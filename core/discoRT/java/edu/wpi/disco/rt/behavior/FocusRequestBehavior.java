package edu.wpi.disco.rt.behavior;

import edu.wpi.disco.rt.*;

public class FocusRequestBehavior extends PrimitiveBehavior {

   @Override
   public Resource getResource () {
      return Resources.FOCUS;
   }

   @Override
   public boolean equals (Object o) {
      if ( o == null )
         return false;
      return o.getClass().equals(FocusRequestBehavior.class);
   }

   @Override
   public int hashCode () {
      return 0;
   }

   @Override
   public String toString () {
      return "FocusRequest";
   }
}
