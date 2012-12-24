package edu.wpi.always.calendar.schema;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.schemas.registries.SchemaRegistry;
import edu.wpi.disco.rt.*;

public class CalendarViewerPluginRegistry implements SchemaRegistry {

   @Override
   public void register (SchemaManager manager) {
      manager.registerSchema(CalendarSchema.class, true);
   }
}
