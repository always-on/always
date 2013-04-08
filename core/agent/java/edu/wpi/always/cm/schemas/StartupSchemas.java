package edu.wpi.always.cm.schemas;

import edu.wpi.disco.rt.schema.*;

public class StartupSchemas implements SchemaRegistry {

   @Override
   public void register (SchemaManager manager) {
      manager.registerSchema(DiscoActivitySchema.class, false);
      manager.registerSchema(MovementTrackerSchema.class, true);
      manager.registerSchema(FaceTrackerSchema.class, true);
      manager.registerSchema(ActivityStarterSchema.class, true);
      manager.registerSchema(EngagementSchema.class, true);
   }
}
