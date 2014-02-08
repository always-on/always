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
import edu.wpi.always.cm.schemas.SessionSchema;
import edu.wpi.always.user.*;
import edu.wpi.cetask.*;
import edu.wpi.disco.rt.*;
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
      // load user model after Activities.xml for initialization of USER_DIR
      loadUserModel();
   }
 
   private void loadUserModel () {
       File last = UserUtils.USER_FILE == null ? UserUtils.lastModified() : 
         new File(UserUtils.USER_DIR, UserUtils.USER_FILE);
      UserUtils.USER_FILE = "User."+new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(new Date())+".owl";
      if ( last != null ) try { 
         Files.copy(last.toPath(), new File(UserUtils.USER_DIR, UserUtils.USER_FILE).toPath(),
               StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) { edu.wpi.cetask.Utils.rethrow(e); }
      parent.as(Characteristics.CACHE).addComponent(
            BindKey.bindKey(File.class, UserModel.UserOntologyLocation.class),
            new File(UserUtils.USER_DIR, UserUtils.USER_FILE));
      try { parent.getComponent(UserModel.class).load(); }
      catch (InconsistentOntologyException e) { revertUserModel(); }
   }
   
   private void revertUserModel () {
      Path bad = new File(UserUtils.USER_DIR, "BAD."+UserUtils.USER_FILE).toPath();
      Utils.lnprint(System.out, "****************************************************************");
      Utils.lnprint(System.out, "    InconsistentOntologyException in "+bad);
      Utils.lnprint(System.out, "    Reverting to previous version of user model...");
      Utils.lnprint(System.out, "****************************************************************");
      try { 
         Files.move(new File(UserUtils.USER_DIR, UserUtils.USER_FILE).toPath(), bad, StandardCopyOption.ATOMIC_MOVE); 
      } catch (IOException e) { edu.wpi.cetask.Utils.rethrow(e); } 
      UserUtils.USER_FILE = null;
      loadUserModel();
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
         System.out.println("Loaded plugin: "+instance);
      } else
         for (TaskClass top : activities.getTaskClasses()) {
            Plugin instance = Plugin.getPlugin(top, container);
            for (Activity a : instance.getActivities(0)) // not using closeness value
               for (Registry r : instance.getRegistries(a)) addRegistry(r);
            System.out.println("Loaded plugin: "+instance);
         }
      super.start(plugin == null ? "Session" : null);
      // after super.start() so ClientRegistry done
      ClientProxy proxy = container.getComponent(ClientProxy.class);
      if ( Always.getAgentType() != AgentType.Unity ) {
         if ( Always.getAgentType() == AgentType.Reeti ) proxy.toggleAgent();
         String host = container.getComponent(ReetiJsonConfiguration.class).getIP();
         proxy.reetiIP(host);
         reetiSocket = new ReetiCommandSocketConnection(host);
      }
      if ( Always.getAgentType() != AgentType.Reeti ) proxy.zoom(ClientProxy.ZOOM);
      if ( plugin == null ) setSchema(null, SessionSchema.class);
   }
   
   @Override
   protected void configure (String title) {
      // handle user model problems in schemas
      Scheduler scheduler = new Scheduler(InconsistentOntologyException.class,
            new Runnable () {
              @Override
              public void run () { revertUserModel(); }});
      container.as(Characteristics.CACHE).addComponent(scheduler);
      super.configure(title);
   }
   
   @Override
   public void stop () {
      super.stop();
      if (reetiSocket != null ) reetiSocket.close();
      Utils.lnprint(System.out, "Collaboration Manager stopped.");
   }

}