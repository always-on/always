package edu.wpi.always.cm;

import edu.wpi.always.*;
import edu.wpi.always.cm.perceptors.dummy.*;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.cetask.TaskClass;
import edu.wpi.disco.rt.*;
import org.picocontainer.*;

public class CollaborationManager extends DiscoRT implements ICollaborationManager {

   public CollaborationManager (MutablePicoContainer parent) {
      super(parent); 
      container.removeComponent(Resources.class);
      container.as(Characteristics.CACHE).addComponent(AgentResources.class);
      container.addComponent(PluginSpecificActionRealizer.class);
   }
 
   @Override
   public void start (boolean allPlugins) {
      // FIXME Try to use real sensors
      container.as(Characteristics.CACHE).addComponent(DummyMovementPerceptor.class); 
      container.as(Characteristics.CACHE).addComponent(DummyFacePerceptor.class);
      container.as(Characteristics.CACHE).addComponent(DummyEngagementPerceptor.class);
      if ( allPlugins) 
         for (TaskClass top : interaction.load("Activities.xml").getTaskClasses()) {
            Plugin plugin = Plugin.getPlugin(top, container);
            for (Activity activity : plugin.getActivities(0)) // not using closeness value
               for (Registry r : plugin.getRegistries(activity))
                  addRegistry(r);
            System.out.println("Loaded plugin: "+plugin);
         }
      super.start(allPlugins ? "Session" : null);
   }
}