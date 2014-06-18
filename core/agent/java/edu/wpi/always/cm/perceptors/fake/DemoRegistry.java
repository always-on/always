package edu.wpi.always.cm.perceptors.fake;

import org.picocontainer.*;
import edu.wpi.always.cm.perceptors.FaceMovementMenuEngagementPerceptor;
import edu.wpi.always.test.AquariumTripSchema;
import edu.wpi.disco.rt.DiscoRT;
import edu.wpi.disco.rt.behavior.BehaviorHistory;
import edu.wpi.disco.rt.behavior.BehaviorProposalReceiver;
import edu.wpi.disco.rt.schema.Schema;
import edu.wpi.disco.rt.schema.SchemaFactory;
import edu.wpi.disco.rt.schema.SchemaManager;
import edu.wpi.disco.rt.schema.SchemaRegistry;
import edu.wpi.disco.rt.util.ComponentRegistry;

public class DemoRegistry implements ComponentRegistry, SchemaRegistry {

   private DemoGUI gui = new DemoGUI();

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
            return DiscoRT.SCHEMA_INTERVAL;
         }

         @Override
         public boolean isRunOnStartup () {
            return true;
         }
         
         @Override
         public boolean isDaemon () {
            return true;
         }
      };
      manager.registerSchema(factory);
   }

   @Override
   public void register (MutablePicoContainer container) {
      container.addComponent(gui.createMovementPerceptor());
      container.addComponent(gui.createFacePerceptor());
      container.as(Characteristics.CACHE).addComponent(FaceMovementMenuEngagementPerceptor.class);
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
