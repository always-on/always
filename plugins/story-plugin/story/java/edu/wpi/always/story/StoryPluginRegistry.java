package edu.wpi.always.story;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.schemas.registries.SchemaRegistry;
import edu.wpi.disco.rt.*;

public class StoryPluginRegistry implements SchemaRegistry {

   @Override
   public void register (SchemaManager manager) {
      manager.registerSchema(BackChannelSchema.class, 100, true);
      manager.registerSchema(StorySchema.class, 1000, true);
      // SchemaConfig a = new SchemaConfig(BackChannelSchema.class, 100);
      // manager.registerSchema(a, true);4aq2zV
      // manager.startUp();
   }
}
