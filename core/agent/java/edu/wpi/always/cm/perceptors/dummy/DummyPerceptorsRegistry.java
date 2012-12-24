package edu.wpi.always.cm.perceptors.dummy;

import edu.wpi.always.SimpleRegistry;
import org.picocontainer.MutablePicoContainer;

public class DummyPerceptorsRegistry implements SimpleRegistry {

   @Override
   public void register (MutablePicoContainer container) {
      container.addComponent(DummyFacePerceptor.class);
      container.addComponent(DummyMovementPerceptor.class);
   }
}
