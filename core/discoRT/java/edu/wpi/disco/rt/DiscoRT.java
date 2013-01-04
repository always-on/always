package edu.wpi.disco.rt;

import edu.wpi.disco.Agent;
import edu.wpi.disco.rt.perceptor.Perceptor;
import edu.wpi.disco.rt.realizer.*;
import edu.wpi.disco.rt.schema.*;
import edu.wpi.disco.rt.util.ComponentRegistry;
import org.picocontainer.*;
import org.picocontainer.behaviors.OptInCaching;
import java.util.*;

public class DiscoRT {
   
   private static final long ARBITRATOR_INTERVAL = 50;
   private static final long PERCEPTORS_INTERVAL = 200;
   
   private final Scheduler scheduler = new Scheduler();
   protected final MutablePicoContainer container;
   protected final List<SchemaRegistry> schemaRegistries = new ArrayList<SchemaRegistry>();
   protected final List<ComponentRegistry> registries = new ArrayList<ComponentRegistry>();

   public DiscoRT () {
      container = new PicoBuilder().withBehaviors(new OptInCaching()).withConstructorInjection().build(); 
      configure();
   }

   public DiscoRT (MutablePicoContainer parent) {
      container = new DefaultPicoContainer(new OptInCaching(), parent);
      configure();
      // give parent system access to  
      parent.addComponent(container.getComponent(DiscoSynchronizedWrapper.class));
   }
   
   private void configure () {
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
      container.addComponent(new DiscoSynchronizedWrapper(
            new Agent("agent"), "DiscoRT Session"));
   }

   public void addRegistry (Registry registry) {
      if ( registry instanceof ComponentRegistry )
         registries.add((ComponentRegistry) registry);
      else if ( registry instanceof SchemaRegistry )
         schemaRegistries.add((SchemaRegistry) registry);
      else throw new IllegalArgumentException("Unknown registry type: "+registry);
   }

   public void start () {
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
}
