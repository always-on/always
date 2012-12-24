package edu.wpi.always.calendar;

import edu.wpi.always.*;
import edu.wpi.always.calendar.schema.CalendarViewerPluginRegistry;
import edu.wpi.always.client.ClientRegistry;
import edu.wpi.always.cm.perceptors.dummy.DummyPerceptorsRegistry;
import edu.wpi.always.user.owl.OntologyUserRegistry;
import org.picocontainer.*;

public class CalendarProgram {

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
         }
      });
      
      program.addRegistry(new OntologyUserRegistry("Test User"));
      program.addCMRegistry(new DummyPerceptorsRegistry());
      program.addCMRegistry(new ClientRegistry() {

         @Override
         public void register (MutablePicoContainer container) {
            super.register(container);
            container.as(Characteristics.CACHE).addComponent(
                  CalendarClient.class);
         }
      });
      program.addCMRegistry(new CalendarViewerPluginRegistry());
      program.start();
   }
}
