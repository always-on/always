package edu.wpi.always.cm.perceptors.physical.face;

import edu.wpi.always.SimpleRegistry;
import edu.wpi.always.cm.perceptors.EmotiveFacePerceptor;
import org.picocontainer.MutablePicoContainer;

public class OpenCVPerceptorsRegistry implements SimpleRegistry {

   @Override
   public void register (MutablePicoContainer container) {
      container.addComponent(EmotiveFacePerceptor.class,
            new OCVEmotiveFacePerceptor());
   }
}
