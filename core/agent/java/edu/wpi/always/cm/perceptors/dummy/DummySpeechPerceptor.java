package edu.wpi.always.cm.perceptors.dummy;

import edu.wpi.always.cm.perceptors.*;
import edu.wpi.disco.rt.perceptor.*;

public class DummySpeechPerceptor extends PerceptorBase<SpeechPerception>
             implements SpeechPerceptor {

   private final PerceptorBufferManager<SpeechPerception> bufferManager 
        = new PerceptorBufferManager<SpeechPerception>();

   @Override
   public PerceptorBuffer<SpeechPerception> newBuffer () {
      return bufferManager.newBuffer();
   }
  
   @Override
   public void addPerceptorListener (
         AsyncPerceptorListener<SpeechPerception> listener) {}

   @Override
   public void removePerceptorListener (
         AsyncPerceptorListener<SpeechPerception> listener) {}
}
