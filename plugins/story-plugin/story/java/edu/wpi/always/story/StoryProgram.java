package edu.wpi.always.story;

import edu.wpi.always.Bootstrapper;
import edu.wpi.always.client.ClientRegistry;
import edu.wpi.always.cm.*;
import edu.wpi.always.cm.perceptors.sensor.SensorsRegistry;
import edu.wpi.always.rm.*;
import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.owl.OntologyUserRegistry;
import edu.wpi.disco.rt.ComponentRegistry;
import org.picocontainer.*;

public class StoryProgram {

   public static void main (String[] args) {
      final Bootstrapper program = new Bootstrapper(false);
      program.addRegistry(new ComponentRegistry() {

         @Override
         public void register (MutablePicoContainer container) {
            container.as(Characteristics.CACHE).addComponent(
                  IRelationshipManager.class, DummyRelationshipManager.class);
            container.as(Characteristics.CACHE).addComponent(
                  ICollaborationManager.class,
                  edu.wpi.always.cm.CollaborationManager.class);
            container.as(Characteristics.CACHE).addComponent(
                  edu.wpi.always.story.StoryManager.class);
            // container.as(Characteristics.CACHE).addComponent(edu.wpi.always.test.user.people.PeopleManager.class);
         }
      });
      program.addRegistry(new OntologyUserRegistry("Test User"));
      program.addCMRegistry(new SensorsRegistry());
      program.addCMRegistry(new ClientRegistry());
      program.addCMRegistry(new StoryPluginRegistry());
      program.start();
      System.out.println("Loading user model...");
      program.getContainer().getComponent(UserModel.class).load();
   }
}
