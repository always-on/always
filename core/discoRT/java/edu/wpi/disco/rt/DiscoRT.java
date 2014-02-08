package edu.wpi.disco.rt;

import java.awt.Frame;
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
   
   protected final DiscoRT.Interaction interaction =  new DiscoRT.Interaction(new Agent("agent"), new User("user"));
   protected final MutablePicoContainer container;
   protected final List<SchemaRegistry> schemaRegistries = new ArrayList<SchemaRegistry>();
   protected final List<ComponentRegistry> registries = new ArrayList<ComponentRegistry>();
  
   public Interaction getInteraction () { return interaction; }
   
   public static class Interaction extends edu.wpi.disco.Interaction {
      
      public Interaction (Actor system, Actor external) {
         super(system, external);
         setName("edu.wpi.disco.rt.DiscoRT.Interaction");
         getConsole().THROW = true; // so exceptions thrown
      }
      
      public void setSchema (Schema schema) { setGlobal("$schema", schema); } 
   }
   
   public DiscoRT () {
      container = new PicoBuilder().withBehaviors(new OptInCaching())
            .withLifecycle(new StartableLifecycleStrategy(new LifecycleComponentMonitor()))
            .withConstructorInjection().build();
      container.as(Characteristics.CACHE).addComponent(Resources.class);
      container.as(Characteristics.CACHE).addComponent(interaction);
   }

   public DiscoRT (MutablePicoContainer parent) {
      container = new DefaultPicoContainer(new OptInCaching(), 
            new StartableLifecycleStrategy(new LifecycleComponentMonitor()), 
            parent);
      container.as(Characteristics.CACHE).addComponent(Resources.class);
      container.as(Characteristics.CACHE).addComponent(interaction);
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
      if ( title != null ) new DiscoRT.ConsoleWindow(interaction, title, false);
   }

   public static class ConsoleWindow extends edu.wpi.disco.ConsoleWindow {
      
      public ConsoleWindow (Interaction interaction, String title, boolean append) {
         super(interaction, 600, 500, 14, append);
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
      for (ComponentRegistry registry : registries) {
         registry.register(container);
      }
      for (SchemaRegistry registry : schemaRegistries) {
         registry.register(schemaManager);
      }
      PrimitiveRealizerFactory realizerFactory = new PrimitiveRealizerFactory(container);
      container.as(Characteristics.CACHE).addComponent(realizerFactory);
      realizerFactory.registerAllRealizerInContainer();
      Arbitrator arbitrator = container.getComponent(Arbitrator.class);
      @SuppressWarnings("rawtypes")
      List<Perceptor> perceptors = container.getComponents(Perceptor.class);
      Scheduler scheduler = container.getComponent(Scheduler.class);
      scheduler.schedule(arbitrator, ARBITRATOR_INTERVAL);
      for (Perceptor<?> p : perceptors) {
         scheduler.schedule(p, PERCEPTOR_INTERVAL);
      }
      schemaManager.startUp();
   }
   
   @Override
   public void start () {
      System.out.println("Starting DiscoRT...");
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
