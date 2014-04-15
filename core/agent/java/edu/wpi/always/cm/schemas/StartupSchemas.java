package edu.wpi.always.cm.schemas;

import edu.wpi.disco.rt.schema.SchemaManager;
import edu.wpi.disco.rt.schema.SchemaRegistry;

public class StartupSchemas implements SchemaRegistry {
   
   @Override
   public void register (SchemaManager manager) {
      // register session first so it gets started first
      // and user model printout not interrupted
      manager.registerSchema(SessionSchema.class, false); // started by EngagementSchema
      // not using movement tracking, since field of view of camera is too narrow
      // manager.registerSchema(MovementTrackerSchema.class, true);
      manager.registerSchema(FaceTrackerSchema.class, true);
      manager.registerSchema(EngagementSchema.class, true);
   }
}
