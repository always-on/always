package edu.wpi.always.cm.perceptors;

import edu.wpi.disco.rt.perceptor.Perception;

public interface SpeechPerception extends Perception {

   public enum SpeechState {
      Silent, Normal, Loud
   }

   public SpeechState speakingState ();
}
