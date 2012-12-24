package edu.wpi.always.cm.schemas.registries;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.schemas.*;

public class StandardRegistry implements SchemaRegistry {

   @Override
   public void register (SchemaManager manager) {
      manager.registerSchema(DiscoBasedSchema.class, false);
      manager.registerSchema(SimpleGreetingsSchema.class, true);
      manager.registerSchema(MovementTrackerSchema.class, true);
      manager.registerSchema(FaceTrackerSchema.class, true);
      manager.registerSchema(ActivityStarterSchema.class, true);
   }
}
