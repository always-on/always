package edu.wpi.always.cm.schemas;

import edu.wpi.always.*;
import edu.wpi.always.Always.AgentType;
import edu.wpi.disco.rt.schema.SchemaManager;
import edu.wpi.disco.rt.schema.SchemaRegistry;

public class StartupSchemas implements SchemaRegistry {
   
   @Override
   public void register (SchemaManager manager) {
      // SessionSchema and CalendarInterruptSchema started by EngagementSchema
      manager.registerSchema(SessionSchema.class, false, true);
      // CalendarInterruptSchema runs once per minute
      manager.registerSchema(CalendarInterruptSchema.class, 60000 , false, true);
      // not using movement tracking, since field of view of camera is too narrow
      // manager.registerSchema(MovementTrackerSchema.class, true);
      manager.registerSchema(FaceTrackerSchema.class, true, true);
      // only EngagmentSchema is not daemon
      manager.registerSchema(EngagementSchema.class, true, false);
   }
}
