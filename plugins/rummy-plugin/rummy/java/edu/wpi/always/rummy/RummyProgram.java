package edu.wpi.always.rummy;

import edu.wpi.always.*;
import edu.wpi.always.client.ClientRegistry;
import edu.wpi.always.cm.*;
import edu.wpi.always.cm.schemas.*;
import edu.wpi.always.cm.schemas.registries.SchemaRegistry;
import edu.wpi.disco.rt.*;
import org.picocontainer.*;

public class RummyProgram {

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
      program.addCMRegistry(new FakeGuiRegistry());
      program.addCMRegistry(new SchemaRegistry() {

         @Override
         public void register (SchemaManager manager) {
            manager.registerSchema(DiscoBasedSchema.class, false);
            manager.registerSchema(SimpleGreetingsSchema.class, true);
            manager.registerSchema(MovementTrackerSchema.class, true);
            // manager.registerSchema(FaceTrackerSchema.class, true);
            manager.registerSchema(ActivityStarterSchema.class, true);
            manager.registerSchema(RummySchema.class, false); // ///////
         }
      });
      program.addCMRegistry(new ClientRegistry() {

         @Override
         public void register (MutablePicoContainer container) {
            super.register(container);
            container.as(Characteristics.CACHE).addComponent(
                  RummyClientPlugin.class);
         }
      });
      program.start();
   }
}
