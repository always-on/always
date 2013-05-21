package edu.wpi.always.cm.primitives.console;

import edu.wpi.disco.rt.behavior.SpeechBehavior;
import edu.wpi.disco.rt.realizer.SingleRunPrimitiveRealizer;

public class ConsoleSpeechRealizer extends
      SingleRunPrimitiveRealizer<SpeechBehavior> {

   public ConsoleSpeechRealizer (SpeechBehavior params) {
      super(params);
   }

   @Override
   protected void singleRun () {
      System.out.println("Saying: " + getParams().getText());
      fireDoneMessage();
   }
}
