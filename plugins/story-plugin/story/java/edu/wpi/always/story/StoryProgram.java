package edu.wpi.always.story;

import edu.wpi.always.*;
import edu.wpi.always.client.ClientRegistry;
import edu.wpi.always.cm.perceptors.dummy.DummyPerceptorsRegistry;
import edu.wpi.always.cm.perceptors.physical.PhysicalPerceptorsRegistry;
import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.owl.OntologyUserRegistry;
import org.picocontainer.*;

public class StoryProgram {

   public static void main (String[] args) {
      final ProgramBootstrapper program = new ProgramBootstrapper(false);
      program.addRegistry(new SimpleRegistry() {

         @Override
         public void register (MutablePicoContainer container) {
            container.as(Characteristics.CACHE).addComponent(
                  IRelationshipManager.class, FakeRelationshipManager.class);
            container.as(Characteristics.CACHE).addComponent(
                  ICollaborationManager.class,
                  edu.wpi.always.cm.CollaborationManager.class);
            container.as(Characteristics.CACHE).addComponent(
                  edu.wpi.always.story.StoryManager.class);
            // container.as(Characteristics.CACHE).addComponent(edu.wpi.always.test.user.people.PeopleManager.class);
         }
      });
      program.addRegistry(new OntologyUserRegistry("Test User"));
      program.addCMRegistry(new DummyPerceptorsRegistry());
      program.addCMRegistry(new PhysicalPerceptorsRegistry());
      program.addCMRegistry(new ClientRegistry());
      program.addCMRegistry(new StoryPluginRegistry());
      program.start();
      System.out.println("Loading user model...");
      program.getContainer().getComponent(UserModel.class).load();
   }
}
