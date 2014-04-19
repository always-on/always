package edu.wpi.always;

import java.io.File;
import java.util.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.varia.NullAppender;
import org.picocontainer.*;
import org.picocontainer.behaviors.OptInCaching;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.monitors.LifecycleComponentMonitor;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.cm.perceptors.EngagementRegistry;
import edu.wpi.always.cm.schemas.StartupSchemas;
import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.UserUtils;
import edu.wpi.always.user.owl.OntologyRegistry;
import edu.wpi.always.user.owl.OntologyRuleHelper;
import edu.wpi.always.user.owl.OntologyUserRegistry;
import edu.wpi.cetask.*;
import edu.wpi.disco.Agent;
import edu.wpi.disco.Interaction;
import edu.wpi.disco.User;
import edu.wpi.disco.rt.DiscoRT;
import edu.wpi.disco.rt.Registry;
import edu.wpi.disco.rt.behavior.SpeechMarkupBehavior;
import edu.wpi.disco.rt.schema.Schema;
import edu.wpi.disco.rt.util.*;
import edu.wpi.disco.rt.util.Utils;

public class Always {

   /**
    * Main method for starting complete Always-On system. 
    * 
    * @param args [closeness model agentType login] NB: Case-sensitive!
    *  <p>
    *  closeness: Stranger, Acquaintance, Companion or null (default, use value in user model)<br>
    *  model: file in always/user (default most recent User.*.owl)<br>
    *  agentType: Unity (default), Reeti or Both 
    *  login: true or false (default)
    */
   public static void main (String[] args) {
      Always always = make(args, null, null);
      always.start();
   }

   public enum AgentType { Unity, Reeti, Mirror }
   
   private static AgentType agentType = AgentType.Unity;
   
   public static AgentType getAgentType () { return agentType; }
   
   private static Date sessionDate;
   
   public static Date getSessionDate () { return sessionDate; }
   
   private static boolean login;
   
   public static boolean isLogin () { return login; }
   
   /**
    * Factory method for Always.  
    * 
    * @param args see {@link Always#main(String[])} 
    * @param plugin Start just this plugin 
    * @param activity Start just this activity (required if plugin is non-null)
    */
   public static Always make (String[] args, Class<? extends Plugin> plugin, String activity) {
      if ( args != null ) {
         if ( args.length > 1 ) UserUtils.USER_FILE = args[1];
         if ( args.length > 2 ) agentType = AgentType.valueOf(args[2]);
         if ( args.length > 3 ) login = Boolean.parseBoolean(args[3]);
      }
      if ( login ) Utils.lnprint(System.out, "Login condition!");
      Utils.lnprint(System.out, "Agent type = "+agentType);
      Always always = new Always(true, plugin == null);
      if ( args != null && args.length > 0 && !"null".equals(args[0]) ) {
         Closeness closeness = Closeness.valueOf(args[0]);
         always.getUserModel().setCloseness(closeness);
      }
      Utils.lnprint(System.out, "Using closeness = "+always.getUserModel().getCloseness());
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
         Interaction interaction = new DiscoRT.Interaction(
            new Agent("agent"), 
            new User("user"),
            args.length > 0 && args[0].length() > 0 ? args[0] : null);
         UserUtils.USER_FILE = "User.Diane.owl";  // no way to change for now
         // to get plugin classes 
         for (TaskClass task : new TaskEngine().load("/edu/wpi/always/resources/Activities.xml").getTaskClasses())
            Plugin.getPlugin(task);
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
   
   public void printUserModel () {
      UserUtils.print(getUserModel(), System.out);
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
            .withLifecycle(new StartableLifecycleStrategy(new LifecycleComponentMonitor())) 
            .withConstructorInjection().build();
   
   public MutablePicoContainer getContainer () {
      return container;
   }
   
   public static boolean ALL_PLUGINS;
   
   public Always (boolean logToConsole, boolean allPlugins) {
      ALL_PLUGINS = allPlugins;
      THIS = this;
      sessionDate = new Date();
      if ( logToConsole )
         BasicConfigurator.configure();
      else
         BasicConfigurator.configure(new NullAppender());
      container.as(Characteristics.CACHE).addComponent(this);
      container.as(Characteristics.CACHE).addComponent(RelationshipManager.class);  
      addRegistry(new OntologyUserRegistry()); 
      register();
      addCMRegistry(new ClientRegistry());
      addCMRegistry(new StartupSchemas());
      addCMRegistry(new EngagementRegistry());
      SpeechMarkupBehavior.ANALYZER = new AgentSpeechMarkupAnalyzer();
      CollaborationManager cm = new CollaborationManager(container);
      container.as(Characteristics.CACHE).addComponent(cm);
      init(cm.getInteraction());
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
   
   public List<Registry> getCMRegistries () { return cmRegistries; }
   
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
      // start container first, since cm has own start method
      container.start(); 
      CollaborationManager cm = container.getComponent(CollaborationManager.class);
      for (Registry registry : cmRegistries) cm.addRegistry(registry);      
      Utils.lnprint(System.out, "Starting Collaboration Manager...");
      cm.start(plugin, activity); 
      Utils.lnprint(System.out, "Always running...");
      if ( plugin != null ) {
         Schema schema =  container.getComponent(plugin).startActivity(activity);
         cm.setSchema(null, schema.getClass());
         cm.getInteraction().setSchema(schema);
      }
   }

   public void stop () { 
      container.stop();
      Utils.lnprint(System.out, "Always stopped.");
   }
   
   private void register () {
      for (ComponentRegistry registry : registries)
         registry.register(container);
      OntologyRuleHelper helper = container.getComponent(OntologyRuleHelper.class);
      for (OntologyRegistry registry : ontologyRegistries)
         registry.register(helper);
   }
   
   public static final boolean EXIT = true;
   
   // Assumes java being called inside a restart loop
   public static void restart (Exception e, String message) {
      Utils.lnprint(System.out, e+message);
      Utils.lnprint(System.out, "EXITING (FOR RESTART)...");
      if ( EXIT ) System.exit(1); 
      else edu.wpi.cetask.Utils.rethrow(e);  
   }
}

