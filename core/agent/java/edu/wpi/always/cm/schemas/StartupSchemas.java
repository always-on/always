package edu.wpi.always.cm.schemas;

import edu.wpi.disco.rt.schema.SchemaManager;
import edu.wpi.disco.rt.schema.SchemaRegistry;

public class StartupSchemas implements SchemaRegistry {

   private final boolean allPlugins;
   
   public StartupSchemas (boolean allPlugins) { this.allPlugins = allPlugins; }
   
   @Override
   public void register (SchemaManager manager) {
      // register session first so it gets started first
      // and user model printout not interrupted
      if ( allPlugins) manager.registerSchema(SessionSchema.class, true);
      manager.registerSchema(MovementTrackerSchema.class, true);
      manager.registerSchema(FaceTrackerSchema.class, true);
      manager.registerSchema(EngagementSchema.class, true);
   }
}
