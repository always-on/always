package edu.wpi.disco.rt.schema;

public class SchemaConfig {

   private final Class<? extends Schema> type;
   private final long updateDelay;
   private final boolean runOnStartup;

   public SchemaConfig (Class<? extends Schema> type, long updateDelay, boolean runOnStartup) {
      this.type = type;
      this.updateDelay = updateDelay;
      this.runOnStartup = runOnStartup;
   }

   public Class<? extends Schema> getType () {
      return type;
   }

   public long getUpdateDelay () {
      return updateDelay;
   }
   
   public boolean getRunOnStartup () { 
      return runOnStartup;
   }
}
