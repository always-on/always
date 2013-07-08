package edu.wpi.always.cm;

import edu.wpi.always.*;
import edu.wpi.always.cm.perceptors.dummy.*;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.schemas.SessionSchema;
import edu.wpi.always.user.*;
import edu.wpi.always.user.owl.*;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.cetask.*;
import edu.wpi.disco.rt.*;
import org.picocontainer.*;
import java.io.File;

public class CollaborationManager extends DiscoRT {

   private final MutablePicoContainer parent;
   private final TaskModel activities;
   
   public CollaborationManager (MutablePicoContainer parent) {
      super(parent);
      this.parent = parent;
      container.removeComponent(Resources.class);
      container.as(Characteristics.CACHE).addComponent(AgentResources.class);
      container.addComponent(PluginSpecificActionRealizer.class);
      activities = interaction.load("Activities.xml");
      // load user model after Activities.xml for initialization of USER_FOLDER
      parent.as(Characteristics.CACHE).addComponent(
            BindKey.bindKey(File.class,
                  OntologyUserModel.UserOntologyLocation.class),
                  new File(UserUtils.USER_DIR, UserUtils.USER_FILE));
      parent.getComponent(UserModel.class).load();
   }
 
   public void start (Class<? extends Plugin> plugin, String activity) {
      // FIXME Try to use real sensors
      container.as(Characteristics.CACHE).addComponent(DummyMovementPerceptor.class); 
      container.as(Characteristics.CACHE).addComponent(DummyFacePerceptor.class);
      container.as(Characteristics.CACHE).addComponent(DummyEngagementPerceptor.class);
      if ( plugin != null ) {
         parent.addComponent(plugin);
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
      if ( plugin == null ) setSchema(null, SessionSchema.class);
   }
}