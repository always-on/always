package edu.wpi.always.calendar.schema;

import edu.wpi.always.calendar.CalendarUIListener;
import edu.wpi.always.cm.dialog.*;
import edu.wpi.always.user.calendar.CalendarEntry;
import org.joda.time.LocalDate;

public class CalendarAdjacencyPairImpl extends
      MultithreadAdjacencyPair<CalendarStateContext> implements
      CalendarUIListener {

   public CalendarAdjacencyPairImpl (String message,
         CalendarStateContext context) {
      super(message, context);
   }

   public CalendarAdjacencyPairImpl (String message,
         CalendarStateContext context, boolean twoColumn) {
      super(message, context, twoColumn);
   }

   @Override
   public void entrySelected (CalendarEntry entry) {
      AdjacencyPair state = selected(entry);
      if ( state != null )
         setNextState(state);
   }

   public AdjacencyPair selected (CalendarEntry entry) {
      return null;
   }

   @Override
   public void daySelected (LocalDate date) {
      AdjacencyPair state = selected(date);
      if ( state != null )
         setNextState(state);
   }

   public AdjacencyPair selected (LocalDate date) {
      return null;
   }

   public void skipTo (AdjacencyPair nextAdjacencyPair) {
      setNextState(nextAdjacencyPair);
   }
}
