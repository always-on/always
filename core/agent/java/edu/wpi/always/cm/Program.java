package edu.wpi.always.cm;

import edu.wpi.always.*;
import edu.wpi.always.client.ClientRegistry;
import edu.wpi.always.cm.schemas.registries.StandardRegistry;
import org.picocontainer.*;

public class Program {

   public static void main (String[] args) {
      ProgramBootstrapper program = new ProgramBootstrapper(false);
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
      program.addCMRegistry(new ClientRegistry());
      program.addCMRegistry(new StandardRegistry());
      // program.addCMRegistry(new FunPackRegistry());
      program.addCMRegistry(new FakeGuiRegistry());
      program.start();
   }
}
