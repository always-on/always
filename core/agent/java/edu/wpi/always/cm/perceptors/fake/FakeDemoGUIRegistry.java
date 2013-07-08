package edu.wpi.always.cm.perceptors.fake;

import edu.wpi.always.test.AquariumTripSchema;
import edu.wpi.disco.rt.Scheduler;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.*;
import edu.wpi.disco.rt.util.ComponentRegistry;
import org.picocontainer.*;

public class FakeDemoGUIRegistry implements ComponentRegistry, SchemaRegistry {

   private FakeDemoGUI gui = new FakeDemoGUI();

   @Override
   public void register (SchemaManager manager) {
      SchemaFactory factory = new SchemaFactory() {

         @Override
         public Class<? extends Schema> getSchemaType () {
            return AquariumTripSchema.class;
         }

         @Override
         public Schema create (PicoContainer container) {
            return new AquariumTripSchema(
                  container.getComponent(BehaviorProposalReceiver.class),
                  container.getComponent(BehaviorHistory.class),
                  gui.getTxtAquariumTrip());
         }

         @Override
         public long getUpdateDelay () {
            return Schema.DEFAULT_INTERVAL;
         }

         @Override
         public boolean getRunOnStartup () {
            return true;
         }
      };
      manager.registerSchema(factory);
   }

   @Override
   public void register (MutablePicoContainer container) {
      container.addComponent(gui.createMovementPerceptor());
      container.addComponent(gui.createFacePerceptor());
      java.awt.EventQueue.invokeLater(new Runnable() {

         @Override
         public void run () {
            try {
               gui.setVisible(true);
            } catch (Exception e) { e.printStackTrace(); }
         }
      });
   }
}
