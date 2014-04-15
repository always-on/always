package edu.wpi.always.cm;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.picocontainer.*;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import edu.wpi.always.*;
import edu.wpi.always.Always.AgentType;
import edu.wpi.always.client.ClientProxy;
import edu.wpi.always.client.reeti.*;
import edu.wpi.always.cm.perceptors.dummy.*;
import edu.wpi.always.cm.perceptors.sensor.face.ShoreFacePerceptor;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.schemas.*;
import edu.wpi.always.user.*;
import edu.wpi.always.user.owl.OntologyUserModel;
import edu.wpi.cetask.*;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.schema.Schema;
import edu.wpi.disco.rt.util.ThreadPools.ScheduledFutureTask;
import edu.wpi.disco.rt.util.*;
import edu.wpi.disco.rt.util.Utils;

public class CollaborationManager extends DiscoRT {

   private final MutablePicoContainer parent;
   private final TaskModel activities;
   
   public CollaborationManager (MutablePicoContainer parent) {
      super(parent);
      this.parent = parent;
      SCHEMA_INTERVAL = 500;
      container.removeComponent(Resources.class);
      container.as(Characteristics.CACHE).addComponent(AgentResources.class);
      container.addComponent(PluginSpecificActionRealizer.class);
      activities = interaction.load("Activities.xml");
      loadUserModel();
   }
 
   public void revertUserModel (InconsistentOntologyException e) {
      inconsistent(e);
      loadUserModel();
      loadPluginOntologies(null);
   }
   
   private void loadUserModel () {
      File last = UserUtils.USER_FILE == null ? UserUtils.lastModified() : 
         new File(UserUtils.USER_DIR, UserUtils.USER_FILE);
      UserUtils.USER_FILE = "User."+UserUtils.formatDate()+".owl";
      File file = new File(UserUtils.USER_DIR, UserUtils.USER_FILE);
      UserModel model = parent.getComponent(UserModel.class);
      ((OntologyUserModel) model).setUserDataFile(file);
      if ( last != null ) 
         try { 
            Utils.lnprint(System.out, "Last modified user file: "+last);
            Files.copy(last.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
         } catch (IOException e) { edu.wpi.cetask.Utils.rethrow(e); }
      try { model.load(); } 
      catch (InconsistentOntologyException e) { 
         inconsistent(e); 
         loadUserModel(); // try again
      }
   }
   
   private void inconsistent (InconsistentOntologyException e) {
      Utils.lnprint(System.out, "****************************************************************");
      Utils.lnprint(System.out, "    InconsistentOntologyException in "+UserUtils.USER_FILE);
      Throwable cause = e.getCause();
      if ( e != null ) {
         System.out.println();
         Utils.lnprint(System.out, cause.getMessage());
         System.out.println();
      }
      Utils.lnprint(System.out, "    Reverting to previous version of user model...");
      Utils.lnprint(System.out, "****************************************************************\n");
      File file = new File(UserUtils.USER_DIR, UserUtils.USER_FILE),
           bad = new File(UserUtils.USER_DIR, "BAD-"+UserUtils.USER_FILE);
      try { 
         if ( bad.exists() ) { // already crashed once this session, so try older file 
            if ( file.exists() )
               Files.move(file.toPath(), 
                     new File(UserUtils.USER_DIR, "BAD-"+UserUtils.USER_FILE+"-AGAIN.owl").toPath(), 
                     StandardCopyOption.ATOMIC_MOVE);
            File last = UserUtils.lastModified();
            if ( last != null ) 
               Files.move(last.toPath(), 
                     new File(UserUtils.USER_DIR, "BAD-"+last.toPath().getFileName()+"-SKIP.owl").toPath(), 
                     StandardCopyOption.ATOMIC_MOVE); 
         } else
            Files.move(file.toPath(), bad.toPath(), StandardCopyOption.ATOMIC_MOVE); 
      } catch (IOException i) { edu.wpi.cetask.Utils.rethrow(i); }
      UserUtils.USER_FILE = null;
      parent.getComponent(UserModel.class).reset();
   }

   private ReetiCommandSocketConnection reetiSocket;
   
   public ReetiCommandSocketConnection getReetiSocket () { return reetiSocket; }
   
   public void start (Class<? extends Plugin> plugin, String activity) {
      switch ( Always.getAgentType() ) {
         case Unity:
            container.as(Characteristics.CACHE).addComponent(ShoreFacePerceptor.Agent.class);
            break;
         case Reeti:
            container.as(Characteristics.CACHE).addComponent(ShoreFacePerceptor.Reeti.class);
            break;
         case Mirror:
            container.as(Characteristics.CACHE).addComponent(ShoreFacePerceptor.Mirror.class);
            break;
      }
      // FIXME Use real sensors
      container.as(Characteristics.CACHE).addComponent(DummyMovementPerceptor.class);
      container.as(Characteristics.CACHE).addComponent(DummyEngagementPerceptor.class);
      if ( plugin != null ) {
         parent.as(Characteristics.CACHE).addComponent(plugin);
         Plugin instance = parent.getComponent(plugin);
         for (Registry r : instance.getRegistries(new Activity(plugin, activity, 0, 0, 0, 0)))
            addRegistry(r);
         Utils.lnprint(System.out, "Loaded plugin: "+instance);
      } else 
         for (TaskClass top : activities.getTaskClasses()) {
            Plugin instance = Plugin.getPlugin(top, container);
            for (Activity a : instance.getActivities(0)) // not using closeness value
               for (Registry r : instance.getRegistries(a)) addRegistry(r);
            Utils.lnprint(System.out, "Loaded plugin: "+instance);
         } 
      loadPluginOntologies(plugin);
      start(plugin == null ? "Session" : null,
         new File(UserUtils.USER_DIR, "User."+UserUtils.formatDate()+".txt"));
      // after super.start() so ClientRegistry done
      ClientProxy proxy = container.getComponent(ClientProxy.class);
      if ( Always.getAgentType() != AgentType.Unity ) {
    	  //TODO: THIS WILL NEED TO BE UPDATED TO USE setVisible
//         if ( Always.getAgentType() == AgentType.Reeti ) proxy.toggleAgent();
         String host = container.getComponent(ReetiJsonConfiguration.class).getIP();
         proxy.reetiIP(host);
         reetiSocket = new ReetiCommandSocketConnection(host);
      }
      if ( Always.getAgentType() != AgentType.Reeti ) proxy.zoom(ClientProxy.ZOOM);
      if ( plugin == null ) setSchema(null, SessionSchema.class);
   }
   
   private void loadPluginOntologies (Class<? extends Plugin> plugin) {
      try { 
         if ( plugin != null ) 
            parent.getComponent(plugin).loadOntology();
         else
            for (TaskClass top : activities.getTaskClasses()) 
               Plugin.getPlugin(top, container).loadOntology();
         parent.getComponent(UserModel.class).ensureConsistency();
      } catch (InconsistentOntologyException e) { revertUserModel(e); }
   }
   
   @Override
   protected void configure (String title, File log) {
      // handle inconsistent user model in activity schemas
      Scheduler scheduler = new Scheduler(InconsistentOntologyException.class,
            new Scheduler.ExceptionHandler () {
              @Override
              public void handle (Runnable r, Throwable e) {
                 if ( r instanceof ScheduledFutureTask<?> ) {
                    r = ((ScheduledFutureTask<?>) r).getInner();
                    if ( r instanceof ActivitySchema ) 
                       ((ActivitySchema) r).setInconsistentOntologyException((InconsistentOntologyException) e); 
                 }}}); 
      container.as(Characteristics.CACHE).addComponent(scheduler);
      super.configure(title, log);
   }
   
   @Override
   public void stop () {
      super.stop();
      if (reetiSocket != null ) reetiSocket.close();
      Utils.lnprint(System.out, "Collaboration Manager stopped.");
   }

}