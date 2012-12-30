package edu.wpi.always.cm.schemas;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.schemas.*;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.schema.*;

public class StartupSchemas implements SchemaRegistry {

   @Override
   public void register (SchemaManager manager) {
      manager.registerSchema(DiscoBasedSchema.class, false);
      //manager.registerSchema(SimpleGreetingsSchema.class, true);
      manager.registerSchema(MovementTrackerSchema.class, true);
      manager.registerSchema(FaceTrackerSchema.class, true);
      manager.registerSchema(ActivityStarterSchema.class, true);
      manager.registerSchema(EngagementSchema.class, true);
   }
}
