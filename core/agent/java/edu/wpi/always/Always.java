package edu.wpi.always;

import edu.wpi.always.client.ClientRegistry;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.cm.schemas.StartupSchemas;
import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.owl.*;
import edu.wpi.disco.Disco;
import edu.wpi.disco.rt.*;
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
    * Most recent instance of Always.  Useful for scripts.
    */
   public static Always THIS;

   /**
    * For convenience in Disco task models.
    */
   public static UserModel getUserModel () {
      return THIS.container.getComponent(UserModel.class);
   }
   
    /**
    * For convenience in Disco console.
    */
   public static RelationshipManager getRM () {
      return THIS.container.getComponent(RelationshipManager.class);
   }
   
   /**
    * To enabled tracing of Always implementation.  Note this variable can be conveniently
    * set using eval command in Disco console or in init script of a task model, such 
    * as Activities.xml.
    * 
    * @see DiscoRT#TRACE
    */
   public static boolean TRACE;
   
   /**
    * The container for holding all the components of the system
    */
   private final MutablePicoContainer container =
         new PicoBuilder().withBehaviors(new OptInCaching())
            .withConstructorInjection().build();
   
   public MutablePicoContainer getContainer () {
      return container;
   }
   
   /**
    * Create new system instance.
    */
   public Always (boolean logToConsole) {
      init(logToConsole, true);
   }
 
   private void init (boolean logToConsole, boolean allPlugins) {
       if ( logToConsole )
         BasicConfigurator.configure();
      else
         BasicConfigurator.configure(new NullAppender());
      container.addComponent(container); 
      container.addComponent(this);
      container.as(Characteristics.CACHE).addComponent(CollaborationManager.class);
      container.as(Characteristics.CACHE).addComponent(RelationshipManager.class);
      // FIXME get real user info here
      addRegistry(new OntologyUserRegistry("Diane Ferguson")); 
      addCMRegistry(new ClientRegistry());
      addCMRegistry(new StartupSchemas(allPlugins));
      THIS = this;
   }
   // for constructor below--see start
   private Class<? extends Plugin> plugin; 
   private String activity;
   
   /**
    * Constructor for debugging given plugin activity.
    */
   public Always (boolean logToConsole, Class<? extends Plugin> plugin, String activity) {
      init(logToConsole, false);
      this.plugin = plugin; 
      this.activity = activity;
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
      if ( userModel != null ) userModel.load();
      CollaborationManager cm = container.getComponent(CollaborationManager.class);
      for (Registry registry : cmRegistries) cm.addRegistry(registry);
      System.out.println("Starting Collaboration Manager");
      cm.start(plugin, activity);
      System.out.println("Always running...");
      if ( plugin != null ) container.getComponent(plugin).startActivity(activity);
   }

}

