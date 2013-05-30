package edu.wpi.disco.rt.schema;

import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.behavior.Behavior;
import org.picocontainer.*;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

public class SchemaManager {

   private final MutablePicoContainer container;
   private final Scheduler scheduler;
   private final List<Class<? extends Schema>> toRunAtStartUp = new ArrayList<Class<? extends Schema>>();
   private final List<Class<? extends Schema>> alwaysAvailableOnes = new ArrayList<Class<? extends Schema>>();
   private final Map<Class<? extends Schema>, SchemaFactory> factories = new HashMap<Class<? extends Schema>, SchemaFactory>();
   private boolean startUpDone = false;

   public SchemaManager (MutablePicoContainer container) {
      this.container = container;
      this.scheduler = container.getComponent(Scheduler.class);
   }

   public void startUp () {
      assert !startUpDone : "SchemaManager.startUp() was already called";
      for (Class<? extends Schema> s : toRunAtStartUp) {
         start(s);
      }
      startUpDone = true;
   }

   public <T extends Schema> T start (Class<T> type) {
      if ( DiscoRT.TRACE ) System.out.println("Starting: "+type);
      T instance = null;
      if ( factories.containsKey(type) ) {
         SchemaFactory factory = factories.get(type);
         instance = (T) factory.create(container);
         ScheduledFuture<?> future = scheduler.schedule(instance, factory.getUpdateDelay());
         instance.setFuture(future);
         return instance;
      } else {
         instance = container.getComponent(type);
         scheduler.schedule(instance, Schema.DEFAULT_INTERVAL);
      }
      // initialize with empty behavior to avoid focus warning in Arbitrator
      container.getComponent(BehaviorProposalReceiver.class)
         .add(instance, Behavior.NULL, new BehaviorMetadataBuilder().specificity(0).build());
      return instance;
   }

   public void registerSchema (Class<? extends Schema> type, boolean runOnStartup) {
      registerSchema(type, Schema.DEFAULT_INTERVAL, runOnStartup);
   }

   public void registerSchema (Class<? extends Schema> type, long updateDelay,
         boolean runOnStartup) {
      registerSchema(new SchemaConfig(type, updateDelay, runOnStartup));
   }

   public void registerSchema (SchemaConfig config) {
      registerSchema(new ConfigSchemaFactory(config));
   }

   public void registerSchema (SchemaFactory factory) {
      Class<? extends Schema> type = factory.getSchemaType();
      factories.put(type, factory);
      if ( factory.getRunOnStartup() ) {
         assert !startUpDone : "SchemaManager.RegisterSchema() called with a startup schema, after startUp was called";
         toRunAtStartUp.add(type);
      }
   }

   private class ConfigSchemaFactory implements SchemaFactory {

      private SchemaConfig config;

      public ConfigSchemaFactory (SchemaConfig config) {
         container.addComponent(config.getType());
         this.config = config;
      }

      @Override
      public long getUpdateDelay () {
         return config.getUpdateDelay();
      }
      
      @Override
      public boolean getRunOnStartup () {      
         return config.getRunOnStartup();
      }

      @Override
      public Class<? extends Schema> getSchemaType () {
         return config.getType();
      }

      @Override
      public Schema create (PicoContainer container) {
         return container.getComponent(getSchemaType());
      }

   }

   public List<Class<? extends Schema>> getAlwaysAvailableSchemas () {
      return Collections.unmodifiableList(alwaysAvailableOnes);
   }

}
