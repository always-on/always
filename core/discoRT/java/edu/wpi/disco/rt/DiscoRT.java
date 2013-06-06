package edu.wpi.disco.rt;

import edu.wpi.cetask.*;
import edu.wpi.disco.*;
import edu.wpi.disco.rt.perceptor.Perceptor;
import edu.wpi.disco.rt.realizer.*;
import edu.wpi.disco.rt.schema.*;
import edu.wpi.disco.rt.util.ComponentRegistry;
import org.picocontainer.*;
import org.picocontainer.behaviors.OptInCaching;
import java.awt.Frame;
import java.util.*;

public class DiscoRT {
   
   public static final long ARBITRATOR_INTERVAL = 50;
   public static final long PERCEPTORS_INTERVAL = 200;
      
   /**
    * To enabled tracing of DiscoRT implementation.  Note this variable can be conveniently
    * set using eval command in Disco console or in init script of a task model.
    * 
    * @see Disco#TRACE
    */
   public static boolean TRACE;
   
   
   private final Scheduler scheduler = new Scheduler();
   protected final Interaction interaction =  new Interaction(new Agent("agent"), new User("user"));
   protected final MutablePicoContainer container;
   protected final List<SchemaRegistry> schemaRegistries = new ArrayList<SchemaRegistry>();
   protected final List<ComponentRegistry> registries = new ArrayList<ComponentRegistry>();
  
   public Interaction getInteraction () { return interaction; }
   
   public DiscoRT () {
      container = new PicoBuilder().withBehaviors(new OptInCaching()).withConstructorInjection().build();
      container.as(Characteristics.CACHE).addComponent(Resources.class);
      container.addComponent(interaction);
   }

   public DiscoRT (MutablePicoContainer parent) {
      container = new DefaultPicoContainer(new OptInCaching(), parent);
      container.as(Characteristics.CACHE).addComponent(Resources.class);
      container.addComponent(interaction);
   }
   
   private void configure (String title) {
      container.addComponent(container);
      container.as(Characteristics.CACHE).addComponent(PrimitiveBehaviorManager.class);
      container.as(Characteristics.CACHE).addComponent(Realizer.class);
      container.addComponent(FocusRequestRealizer.class);
      container.addComponent(FuzzyArbitrationStrategy.class);
      container.as(Characteristics.CACHE).addComponent(CandidateBehaviorsContainer.class);
      container.as(Characteristics.CACHE).addComponent(Arbitrator.class);
      container.as(Characteristics.CACHE).addComponent(ResourceMonitor.class);
      container.as(Characteristics.CACHE).addComponent(SchemaManager.class);
      container.addComponent(scheduler);
      if ( title != null ) new DiscoRT.ConsoleWindow(interaction, title);
   }

   public static class ConsoleWindow extends edu.wpi.disco.ConsoleWindow {
      
      public ConsoleWindow (Interaction interaction, String title) {
         super(interaction, 600, 500, 14);
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
      for (ComponentRegistry registry : registries) {
         registry.register(container);
      }
      SchemaManager schemaManager = getContainer().getComponent(SchemaManager.class);
      for (SchemaRegistry registry : schemaRegistries) {
         registry.register(schemaManager);
      }
      PrimitiveRealizerFactory realizerFactory = new PrimitiveRealizerFactory(container);
      container.addComponent(realizerFactory);
      realizerFactory.registerAllRealizerInContainer();
      Arbitrator arbitrator = getContainer().getComponent(Arbitrator.class);
      @SuppressWarnings("rawtypes")
      List<Perceptor> perceptors = container.getComponents(Perceptor.class);
      scheduler.schedule(arbitrator, ARBITRATOR_INTERVAL);
      for (Perceptor<?> p : perceptors) {
         scheduler.schedule(p, PERCEPTORS_INTERVAL);
      }
      schemaManager.startUp();
   }

   public MutablePicoContainer getContainer () {
      return container;
   }
   
   // mapping from tasks to schemas for arbitrator to assign focus
   private final Map<TaskClass,Class<? extends Schema>> tasks 
                      = new HashMap<TaskClass,Class<? extends Schema>>();
   
   /**
    * Return the schema, if any, associated with the given task class or null.  If argument is null,
    * then return the toplevel schema, if any.
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
         }
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
