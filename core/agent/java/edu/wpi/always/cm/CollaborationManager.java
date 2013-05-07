package edu.wpi.always.cm;

import edu.wpi.always.*;
import edu.wpi.always.cm.perceptors.dummy.*;
import edu.wpi.always.cm.primitives.PluginSpecificActionRealizer;
import edu.wpi.cetask.*;
import edu.wpi.disco.Disco;
import edu.wpi.disco.rt.*;
import org.picocontainer.*;

public class CollaborationManager extends DiscoRT implements ICollaborationManager {

   public CollaborationManager (MutablePicoContainer parent) {
      super(null, parent); // Disco instance only used once below to load plugins
      container.as(Characteristics.CACHE).addComponent(CollaborationIdleBehaviors.class);
      container.addComponent(PluginSpecificActionRealizer.class);
   }
 
   @Override
   public void start (boolean allPlugins) {
      // FIXME Try to use real sensors
      container.as(Characteristics.CACHE).addComponent(DummyMovementPerceptor.class); 
      container.as(Characteristics.CACHE).addComponent(DummyFacePerceptor.class);
      container.as(Characteristics.CACHE).addComponent(DummyEngagementPerceptor.class);
      Disco disco = container.getComponent(DiscoSynchronizedWrapper.class).getDisco();
      if ( allPlugins) 
         for (TaskClass top : disco.load("Activities.xml").getTaskClasses()) {
            Plugin plugin = Plugin.getPlugin(top, container);
            for (Activity activity : plugin.getActivities(0)) // not using closeness value
               for (Registry r : plugin.getRegistries(activity))
                  addRegistry(r);
            System.out.println("Loaded plugin: "+plugin);
         }
      super.start();
   }
}