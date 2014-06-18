package edu.wpi.disco.rt;

import java.awt.Frame;
import java.io.File;
import java.util.*;
import org.picocontainer.*;
import org.picocontainer.behaviors.OptInCaching;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.monitors.LifecycleComponentMonitor;
import edu.wpi.cetask.*;
import edu.wpi.disco.*;
import edu.wpi.disco.rt.perceptor.Perceptor;
import edu.wpi.disco.rt.realizer.*;
import edu.wpi.disco.rt.schema.*;
import edu.wpi.disco.rt.util.Utils;
import edu.wpi.disco.rt.util.ComponentRegistry;

public class DiscoRT implements Startable {
   
   // default intervals in msec
   public static int  
         SCHEMA_INTERVAL = 1000,
         ARBITRATOR_INTERVAL = 300,
         PERCEPTOR_INTERVAL = 200,
         REALIZER_INTERVAL = 100;
      
   /**
    * To enabled tracing of DiscoRT implementation.  Note this variable can be conveniently
    * set using eval command in Disco console or in init script of a task model.
    * 
    * @see Disco#TRACE
    */
   public static boolean TRACE;
   
   protected final DiscoRT.Interaction interaction;
   protected final MutablePicoContainer container;
   protected final Set<SchemaRegistry> schemaRegistries = new HashSet<SchemaRegistry>();
   protected final Set<ComponentRegistry> registries = new HashSet<ComponentRegistry>();
  
   public Interaction getInteraction () { return interaction; }
   
   public static class Interaction extends edu.wpi.disco.Interaction {
      
      private final ConsoleWindow console;
      
      public ConsoleWindow getConsoleWindow () { return console; }
      
      public Interaction (Actor system, Actor external, String from) {
         // sic do not use this()
         super(system, external, from, true, null, "edu.wpi.disco.rt.DiscoRT.Interaction");
         console = null;
      }
      
      public Interaction (Actor system, Actor external) {
         this(system, external, null, true, null);
      }
      
      public Interaction (Actor system, Actor external, String from, boolean console, File log) {
         super(system, external, from, console && isHeadless(), null, "edu.wpi.disco.rt.DiscoRT.Interaction");
         this.console = (console && !isHeadless()) ? new ConsoleWindow(this, "DiscoRT", log) : null;
      }
      
      private static boolean isHeadless () {
         return Boolean.parseBoolean(System.getProperty("java.awt.headless"));
      }
      
      public void setSchema (Schema schema) { 
         setGlobal("$schema", schema);
         if ( console != null ) console.setTitle(schema.getClass().getSimpleName());
      } 
   }
   
   private DiscoRT (File log, MutablePicoContainer container) {
      interaction = new DiscoRT.Interaction(new Agent("agent"), new User("user"), null, true, log);
      this.container = container;
      container.as(Characteristics.CACHE).addComponent(Resources.class);
      container.as(Characteristics.CACHE).addComponent(interaction);
   }

   public DiscoRT () { 
      this(null,
           new PicoBuilder().withBehaviors(
            new OptInCaching()).withLifecycle(
                  new StartableLifecycleStrategy(new LifecycleComponentMonitor()))
                    .withConstructorInjection().build()); 
   }
   
   public DiscoRT (MutablePicoContainer parent, File log) {
      this(log,
           new DefaultPicoContainer(
              new OptInCaching(), 
              new StartableLifecycleStrategy(new LifecycleComponentMonitor()), parent)); 
   }
   
   protected void configure (String title) {
      container.as(Characteristics.CACHE).addComponent(PrimitiveBehaviorManager.class);
      container.as(Characteristics.CACHE).addComponent(Realizer.class);
      container.addComponent(FocusRequestRealizer.class);
      container.as(Characteristics.CACHE).addComponent(FuzzyArbitrationStrategy.class);
      container.as(Characteristics.CACHE).addComponent(CandidateBehaviorsContainer.class);
      container.as(Characteristics.CACHE).addComponent(Arbitrator.class);
      container.as(Characteristics.CACHE).addComponent(ResourceMonitor.class);
      // allow easy overriding of this
      if ( container.getComponent(Scheduler.class) == null )
         container.as(Characteristics.CACHE).addComponent(Scheduler.class);
      if ( interaction.console != null ) {
         interaction.console.setTitle(title);
         interaction.console.setVisible(true);
      }
   }

   public static class ConsoleWindow extends edu.wpi.disco.ConsoleWindow {
      
      public ConsoleWindow (Interaction interaction, String title, File log) {
         super(interaction, 600, 500, 14, log);
         setExtendedState(Frame.ICONIFIED);
         setTitle(title);
      }
   }

   public void addRegistry (Registry registry) {
      if ( registry instanceof ComponentRegistry )
         registries.add((ComponentRegistry) registry);
      else if ( registry instanceof SchemaRegistry )
         schemaRegistries.add((SchemaRegistry) registry);
      else throw new IllegalArgumentException("Unknown registry type: "+registry);
   }

   public void start (String title) {
      configure(title);
      SchemaManager schemaManager = new SchemaManager(container); 
      container.as(Characteristics.CACHE).addComponent(schemaManager);
      for (ComponentRegistry registry : registries) 
         registry.register(container);
      for (SchemaRegistry registry : schemaRegistries)
         registry.register(schemaManager);
      PrimitiveRealizerFactory realizerFactory = new PrimitiveRealizerFactory(container);
      container.as(Characteristics.CACHE).addComponent(realizerFactory);
      realizerFactory.registerAllRealizerInContainer();
      Arbitrator arbitrator = container.getComponent(Arbitrator.class);
      @SuppressWarnings("rawtypes")
      List<Perceptor> perceptors = container.getComponents(Perceptor.class);
      Scheduler scheduler = container.getComponent(Scheduler.class);
      // arbitrator and perceptors are daemon threads
      scheduler.schedule(arbitrator, ARBITRATOR_INTERVAL, true);
      for (Perceptor<?> p : perceptors) 
         scheduler.schedule(p, PERCEPTOR_INTERVAL, true);
      schemaManager.startUp();
   }
   
   @Override
   public void start () {
      Utils.lnprint(System.out, "Starting DiscoRT...");
      container.start();
   }
   
   @Override
   public void stop () {
      container.stop();
      Utils.lnprint(System.out, "DiscoRT stopped.");
   }

   public MutablePicoContainer getContainer () {
      return container;
   }
   
   // mapping from tasks to schemas for arbitrator to assign focus
   private final Map<TaskClass,Class<? extends Schema>> tasks 
                      = new HashMap<TaskClass,Class<? extends Schema>>();
   
   /**
    * Return the schema associated with the given task class or if there
    * isn't one, then the toplevel schema, if any.
    * 
    * @see #setSchema(TaskClass,Class)
    */
   public Class<? extends Schema> getSchema (TaskClass task) {
      Class<? extends Schema> schema = (Class<? extends Schema>) tasks.get(task); 
      if ( task != null && schema == null ) {  // cache from properties file if found
         String name = task.getProperty("@schema");
         if ( name != null ) 
            try {
               schema = (Class<? extends Schema>) Class.forName(name);
               tasks.put(task, schema);
            } catch (ClassNotFoundException e) { 
               System.err.println("Ignoring unknown @schema property: "+name);
         } else return getSchema(null);
      }
      return schema;
   }
   
   /**
    * Set the schema associated with given task class (or null for toplevel schema).
    * This schema will be given the {@link Resources#FOCUS} when the corresponding
    * task has the Disco focus (regardless of whether it requests it or not).
    */
   public void setSchema (TaskClass task, Class<? extends Schema> schema) {
      tasks.put(task, schema);
   }
}
