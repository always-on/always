package edu.wpi.always.cm.schemas.registries;

import edu.wpi.always.Registry;
import edu.wpi.disco.rt.SchemaManager;

public interface SchemaRegistry extends Registry {

   void register (SchemaManager manager);
}
