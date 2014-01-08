package edu.wpi.always.cm;

import java.io.File;
import org.picocontainer.*;
import edu.wpi.always.*;
<<<<<<< HEAD
import edu.wpi.always.Always.AgentType;
import edu.wpi.always.client.*;
import edu.wpi.always.client.reeti.ReetiJsonConfiguration;
=======
import edu.wpi.always.client.reeti.ReetiCommandSocketConnection;
>>>>>>> d66048f927dcc5743bf465359f60991ccd83ec2b
import edu.wpi.always.cm.perceptors.dummy.*;
import edu.wpi.always.cm.perceptors.sensor.face.*;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.schemas.SessionSchema;
import edu.wpi.always.user.*;
import edu.wpi.always.user.owl.OntologyUserModel;
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
      // load user model after Activities.xml for initialization of USER_FOLDER
      parent.as(Characteristics.CACHE).addComponent(
            BindKey.bindKey(File.class, UserModel.UserOntologyLocation.class),
                  new File(UserUtils.USER_DIR, UserUtils.USER_FILE));
      parent.getComponent(UserModel.class).load();
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
      if ( plugin == null ) setSchema(null, SessionSchema.class);
   }
   
   @Override
   public void stop () {
      super.stop();
      if (reetiSocket != null ) reetiSocket.close();
      Utils.lnprint(System.out, "Collaboration Manager stopped.");
   }

}