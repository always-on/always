package edu.wpi.always.cm.schemas;

import edu.wpi.always.*;
import edu.wpi.always.Always.AgentType;
import edu.wpi.disco.rt.schema.SchemaManager;
import edu.wpi.disco.rt.schema.SchemaRegistry;

public class StartupSchemas implements SchemaRegistry {
   
   @Override
   public void register (SchemaManager manager) {
      // register session first so it gets started first
      // and user model printout not interrupted
       // following two schemas started by EngagementSchema
      manager.registerSchema(SessionSchema.class, false);
      manager.registerSchema(CalendarInterruptSchema.class, 60000 , false); // once per minute
      // not using movement tracking, since field of view of camera is too narrow
      // manager.registerSchema(MovementTrackerSchema.class, true);
      manager.registerSchema(FaceTrackerSchema.class, true);
      manager.registerSchema(EngagementSchema.class, true);
   }
}
