package edu.wpi.disco.rt;

import org.picocontainer.*;
import java.util.*;

public class SchemaManager {

   private static final long DEFAULT_SCHEMAS_INTERVAL = 500;
   private final Scheduler scheduler;
   private final MutablePicoContainer container;
   private ArrayList<Class<? extends Schema>> toRunAtStartUp = new ArrayList<Class<? extends Schema>>();
   private ArrayList<Class<? extends Schema>> alwaysAvailableOnes = new ArrayList<Class<? extends Schema>>();
   private HashMap<Class<? extends Schema>, SchemaFactory> factories = new HashMap<Class<? extends Schema>, SchemaFactory>();
   private boolean startUpDone = false;

   public SchemaManager (MutablePicoContainer container, Scheduler scheduler) {
      this.container = container;
      this.scheduler = scheduler;
   }

   public void startUp () {
      assert !startUpDone : "SchemaManager.startUp() was already called";
      for (Class<? extends Schema> s : toRunAtStartUp) {
         schedule(s);
      }
      startUpDone = true;
   }

   private <T extends Schema> T schedule (Class<T> type) {
      if ( factories.containsKey(type) ) {
         SchemaFactory factory = factories.get(type);
         @SuppressWarnings("unchecked")
         T instance = (T) factory.create(container);
         schedule(instance, factory.getUpdateDelay());
         return instance;
      }
      T instance = container.getComponent(type);
      scheduler.schedule(instance, DEFAULT_SCHEMAS_INTERVAL);
      return instance;
   }

   private void schedule (Schema s, long updateDelay) {
      scheduler.schedule(s, updateDelay);
   }

   public void registerSchema (Class<? extends Schema> type,
         boolean runOnStartup) {
      registerSchema(type, DEFAULT_SCHEMAS_INTERVAL, runOnStartup);
   }

   public void registerSchema (Class<? extends Schema> type, long updateDelay,
         boolean runOnStartup) {
      registerSchema(new SchemaConfig(type, updateDelay), runOnStartup);
   }

   public void registerSchema (SchemaConfig config, boolean runOnStartup) {
      registerSchema(new ConfigSchemaFactory(config), runOnStartup);
   }

   public void registerSchema (SchemaFactory factory, boolean runOnStartup) {
      Class<? extends Schema> type = factory.getSchemaType();
      factories.put(type, factory);
      if ( runOnStartup ) {
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

   public <T extends Schema> T start (Class<T> type) {
      return schedule(type);
   }
}
