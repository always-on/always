package edu.wpi.always.cm.perceptors.sensor;

import org.picocontainer.Characteristics;
import org.picocontainer.MutablePicoContainer;

import edu.wpi.always.cm.perceptors.FacePerceptor;
import edu.wpi.always.cm.perceptors.MovementPerceptor;
import edu.wpi.always.cm.perceptors.SpeechPerceptor;
import edu.wpi.always.cm.perceptors.sensor.face.ShoreFacePerceptor;
import edu.wpi.always.cm.perceptors.sensor.pir.PIRMovementPerceptor;
import edu.wpi.always.cm.perceptors.sensor.speech.LaunSpeechPerceptor;
import edu.wpi.disco.rt.util.ComponentRegistry;

public class SensorsRegistry implements ComponentRegistry {

   @Override
   public void register (MutablePicoContainer container) {
      container.as(Characteristics.CACHE).addComponent(MovementPerceptor.class,
            PIRMovementPerceptor.class);
      container.as(Characteristics.CACHE).addComponent(SpeechPerceptor.class,
            LaunSpeechPerceptor.class);
      container.as(Characteristics.CACHE).addComponent(
            FacePerceptor.class, ShoreFacePerceptor.class);
   }
}
