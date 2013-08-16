package edu.wpi.always;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.varia.NullAppender;
import org.picocontainer.Characteristics;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;
import org.picocontainer.behaviors.OptInCaching;

import edu.wpi.always.client.ClientRegistry;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.cm.schemas.StartupSchemas;
import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.UserUtils;
import edu.wpi.always.user.owl.OntologyRegistry;
import edu.wpi.always.user.owl.OntologyRuleHelper;
import edu.wpi.always.user.owl.OntologyUserRegistry;
import edu.wpi.disco.Agent;
import edu.wpi.disco.Interaction;
import edu.wpi.disco.User;
import edu.wpi.disco.rt.DiscoRT;
import edu.wpi.disco.rt.Registry;
import edu.wpi.disco.rt.util.ComponentRegistry;

public class Always {

   /**
    * Main method for starting complete Always-On system. 
    * 
    * @param args [closeness model agentType] NB: Case-sensitive!
    *  <p>
    *  closeness: Stranger (default), Acquaintance or Companion<br>
    *  model: file in always/user (default User.owl)<br>
    *  agentType: Unity (default), Reeti or Both 
    */
   public static void main (String[] args) {
      Always always = make(args, null, null);
      always.start();
   }

   public enum AgentType { Unity, Reeti, Both }
   
   private static AgentType agentType = AgentType.Unity;
   
   public static AgentType getAgentType () { return agentType; }
   
   /**
    * Factory method for Always.  
    * 
    * @param args see {@link Always#main(String[])} 
    * @param plugin Start just this plugin 
    * @param activity Start just this activity (required if plugin is non-null)
    * @return
    */
   public static Always make (String[] args, Class<? extends Plugin> plugin, String activity) {
      if ( args != null ) {
         if ( args.length > 1 ) UserUtils.USER_FILE = args[1];
         if ( args.length > 2 ) agentType = AgentType.valueOf(args[2]);
      }
      Always always = new Always(true, plugin == null);
      if ( args != null && args.length > 0 ) {
         Closeness closeness = Closeness.valueOf(args[0]);
         always.getUserModel().setCloseness(closeness);
      }
      System.out.println("Using closeness = "+always.getUserModel().getCloseness());
      always.plugin = plugin; 
      always.activity = activity;
      return always;
   }
   
   /**
    * Nested class with main method for testing Disco task models (and accessing
    * user model) without starting Always GUI.
    */
   public static class Disco {
      
      public static void main (String[] args) { 
         Interaction interaction = new Interaction(
            new Agent("agent"), 
            new User("user"),
            args.length > 0 && args[0].length() > 0 ? args[0] : null);
         UserUtils.USER_FILE = new java.io.File("../../user").exists() ? 
                                  "../../user" : "../../../user";  
         UserUtils.USER_FILE = "TestUser.owl";  // no way to change for now
         // initialize duplicate interaction created above
         new Always(true, false).init(interaction); 
         interaction.start(true);
      }
   }
   
   /**
    * Most recent instance of Always.  Useful for scripts.
    */
   public static Always THIS;
   
   public CollaborationManager getCM () {
      return container.getComponent(CollaborationManager.class);
   }
   
   public RelationshipManager getRM () {
      return container.getComponent(RelationshipManager.class);
   }
   
   public UserModel getUserModel () {
      return container.getComponent(UserModel.class);
   }
   
   /**
    * To enabled tracing of Always implementation.  Note this variable can be conveniently
    * set using eval command in Disco console or in init script of a task model, such 
    * as Activities.xml.
    * 
    * @see DiscoRT#TRACE
    */
   public static boolean TRACE = true;
   
   /**
    * The container for holding all the components of the system
    */
   private final MutablePicoContainer container =
         new PicoBuilder().withBehaviors(new OptInCaching())
            .withConstructorInjection().build();
   
   public MutablePicoContainer getContainer () {
      return container;
   }
   
   private Always (boolean logToConsole, boolean allPlugins) {
      THIS = this;
      if ( logToConsole )
         BasicConfigurator.configure();
      else
         BasicConfigurator.configure(new NullAppender());
      container.addComponent(container); 
      container.addComponent(this);
      container.as(Characteristics.CACHE).addComponent(RelationshipManager.class);  
      container.as(Characteristics.CACHE).addComponent(CollaborationManager.class);
      addRegistry(new OntologyUserRegistry()); 
      addCMRegistry(new ClientRegistry());
      addCMRegistry(new StartupSchemas(allPlugins));
      register();
      init(container.getComponent(CollaborationManager.class).getInteraction());
   }

   public void init (Interaction interaction) {
      edu.wpi.disco.Disco disco = interaction.getDisco();
      // for convenient use in Disco scripts
      disco.setGlobal("$always", this);
      disco.eval("edu.wpi.always = Packages.edu.wpi.always;", "Always.init");
   }

   private final List<OntologyRegistry> ontologyRegistries = new ArrayList<OntologyRegistry>();
   private final List<ComponentRegistry> registries = new ArrayList<ComponentRegistry>();
   private final List<Registry> cmRegistries = new ArrayList<Registry>();
   
   public void addRegistry (Registry registry) {
      if ( registry instanceof ComponentRegistry )
         registries.add((ComponentRegistry) registry);
      // NB could be instance of both
      if ( registry instanceof OntologyRegistry )
         ontologyRegistries.add((OntologyRegistry) registry);
      if ( !(registry instanceof ComponentRegistry || registry instanceof OntologyRegistry) )
            throw new IllegalArgumentException("Unknown registry type: "+registry);
   }

   public void addCMRegistry (Registry registry) {
      cmRegistries.add(registry);
   }

   private Class<? extends Plugin> plugin; 
   private String activity;
   
   public void start () {
      CollaborationManager cm = container.getComponent(CollaborationManager.class);
      for (Registry registry : cmRegistries) cm.addRegistry(registry);
      System.out.println("Starting Collaboration Manager");
      cm.start(plugin, activity);
      System.out.println("Always running...");
      if ( plugin != null ) container.getComponent(plugin).startActivity(activity);
   }

   private void register () {
      for (ComponentRegistry registry : registries)
         registry.register(container);
      OntologyRuleHelper helper = container.getComponent(OntologyRuleHelper.class);
      for (OntologyRegistry registry : ontologyRegistries)
         registry.register(helper);
   }
}

