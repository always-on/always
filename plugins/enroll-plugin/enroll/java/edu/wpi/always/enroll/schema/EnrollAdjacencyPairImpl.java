package edu.wpi.always.enroll.schema;

import org.joda.time.LocalDate;

import edu.wpi.disco.rt.menu.*;
import edu.wpi.always.user.calendar.CalendarEntry;

public class EnrollAdjacencyPairImpl 
extends MultithreadAdjacencyPair<EnrollStateContext>{

   public EnrollAdjacencyPairImpl (String message,
         EnrollStateContext context) {
      super(message, context);
   }

   public EnrollAdjacencyPairImpl (String message,
         EnrollStateContext context, boolean twoColumn) {
      super(message, context, twoColumn);
   }

   public AdjacencyPair selected (CalendarEntry entry) {
      return null;
   }

   public AdjacencyPair selected (LocalDate date) {
      return null;
   }

   public void skipTo (AdjacencyPair nextAdjacencyPair) {
      setNextState(nextAdjacencyPair);
   }

}
