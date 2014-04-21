package edu.wpi.always.cm.perceptors;

import org.joda.time.DateTime;

import edu.wpi.disco.rt.perceptor.Perception;

public class SpeechPerception extends Perception {
   
   public enum SpeechState { Silent, Normal, Loud }

   private final SpeechState state;

   public SpeechPerception (SpeechState state) {
      this.state = state;
   }

   public SpeechState getState () {
      return state;
   }

   @Override
   public String toString () {
      return "Speech[state=" + state + ", stamp=" + stamp + "]";
   }
   
}