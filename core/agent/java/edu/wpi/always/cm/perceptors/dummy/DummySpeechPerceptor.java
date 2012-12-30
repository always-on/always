package edu.wpi.always.cm.perceptors.dummy;

import edu.wpi.always.cm.perceptors.*;
import edu.wpi.disco.rt.perceptor.*;
import org.joda.time.DateTime;
import java.awt.Point;

public class DummySpeechPerceptor implements SpeechPerceptor {

   private volatile SpeechPerception latest;

   @Override
   public SpeechPerception getLatest () {
      return latest;
   }

   @Override
   public void run () {}
   
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
