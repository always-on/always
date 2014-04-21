package edu.wpi.always.cm.perceptors.fake;

import org.picocontainer.*;
import edu.wpi.always.cm.perceptors.FaceMovementMenuEngagementPerceptor;
import edu.wpi.disco.rt.schema.*;
import edu.wpi.disco.rt.util.ComponentRegistry;

public class FakeEngagementRegistry implements ComponentRegistry, SchemaRegistry {

   private final FakeEngagementGUI gui = new FakeEngagementGUI();
   
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

   @Override
   public void register (SchemaManager manager) {}
}
