package edu.wpi.always.cm.perceptors.physical;

import edu.wpi.always.SimpleRegistry;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.physical.face.OCVEmotiveFacePerceptor;
import edu.wpi.always.cm.perceptors.physical.pir.PIRMotionPerceptor;
import edu.wpi.always.cm.perceptors.physical.speech.LaunSpeechPerceptor;
import org.picocontainer.*;

public class PhysicalPerceptorsRegistry implements SimpleRegistry {

   @Override
   public void register (MutablePicoContainer container) {
      container.as(Characteristics.CACHE).addComponent(MotionPerceptor.class,
            PIRMotionPerceptor.class);
      container.as(Characteristics.CACHE).addComponent(SpeechPerceptor.class,
            LaunSpeechPerceptor.class);
      container.as(Characteristics.CACHE).addComponent(
            EmotiveFacePerceptor.class, OCVEmotiveFacePerceptor.class);
   }
}
