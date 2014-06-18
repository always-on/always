package edu.wpi.disco.rt.schema;

public class SchemaConfig {

   public final Class<? extends Schema> type;
   public final long updateDelay;
   public final boolean runOnStartup, daemon;

   public SchemaConfig (Class<? extends Schema> type, long updateDelay, boolean runOnStartup, boolean daemon) {
      this.type = type;
      this.updateDelay = updateDelay;
      this.runOnStartup = runOnStartup;
      this.daemon = daemon;
   }
}
