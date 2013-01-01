package edu.wpi.always;

import edu.wpi.always.client.ClientRegistry;
import edu.wpi.always.cm.*;
import edu.wpi.always.cm.schemas.StartupSchemas;
import edu.wpi.always.rm.*;
import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.owl.*;
import edu.wpi.disco.rt.Registry;
import edu.wpi.disco.rt.util.ComponentRegistry;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.varia.NullAppender;
import org.picocontainer.*;
import org.picocontainer.behaviors.OptInCaching;
import java.util.*;

public class Always {

   /**
    * Main method for starting complete Always-On system
    */
   public static void main (String[] args) {
      new Always(true).start();
   }

    /**
    * Most recent instance of Always.  Useful for scripts in user/Init.xml
    */
   public static Always THIS;

   /**
    * The container for holding all the components of the system
    */
   private final MutablePicoContainer container =
         new PicoBuilder().withBehaviors(new OptInCaching())
            .withConstructorInjection().build();
   
   /**
    * Create new system instance.
    */
   public Always (boolean logToConsole) {
      if ( logToConsole )
         BasicConfigurator.configure();
      else
         BasicConfigurator.configure(new NullAppender());
      container.addComponent(container); 
      container.as(Characteristics.CACHE).addComponent(
            ICollaborationManager.class, CollaborationManager.class);
      container.as(Characteristics.CACHE).addComponent(
            // note using DUMMY relationship manager!
            IRelationshipManager.class, DummyRelationshipManager.class);
      // TODO get real user info here
      addRegistry(new OntologyUserRegistry("Diane Ferguson")); 
      addCMRegistry(new ClientRegistry());
      addCMRegistry(new StartupSchemas());
      THIS = this;
   }

   /**
    * Constructor for debugging given plugin activity.
    */
   public Always (boolean logToConsole, Class<? extends Plugin> plugin, String name) {
      this(logToConsole);
      container.addComponent(plugin);
      Plugin p = container.getComponent(plugin);
      for (Registry r : p.getRegistries(new Activity(plugin, name, 0, 0, 0, 0)))
            addCMRegistry(r);
   }
                                                                         
   private final List<OntologyRegistry> ontologyRegistries = new ArrayList<OntologyRegistry>();
   private final List<ComponentRegistry> registries = new ArrayList<ComponentRegistry>();
   private final List<Registry> cmRegistries = new ArrayList<Registry>();
   
   public void addRegistry (Registry registry) {
      if ( registry instanceof ComponentRegistry )
         registries.add((ComponentRegistry) registry);
      else if ( registry instanceof OntologyRegistry )
         ontologyRegistries.add((OntologyRegistry) registry);
      else throw new IllegalArgumentException("Unknown registry type: "+registry);
   }

   public void addCMRegistry (Registry registry) {
      cmRegistries.add(registry);
   }

   public void start () {
      for (ComponentRegistry registry : registries)
         registry.register(container);
      OntologyRuleHelper helper = container.getComponent(OntologyRuleHelper.class);
      for (OntologyRegistry registry : ontologyRegistries)
         registry.register(helper);
      UserModel userModel = container.getComponent(UserModel.class);
      if ( userModel != null ) {
         userModel.load();
         System.out.println("Loaded user model");
      }
      ICollaborationManager cm = container.getComponent(ICollaborationManager.class);
      for (Registry registry : cmRegistries)
         cm.addRegistry(registry);
      System.out.println("Starting Collaboration Manager");
      cm.start();
      System.out.println("Always running...");
   }

   public MutablePicoContainer getContainer () {
      return container;
   }
}

