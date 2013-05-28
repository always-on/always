package edu.wpi.disco.rt.test;

import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.SchemaBase;

public class FakeSpeechSchema extends SchemaBase {

   final String text;

   public FakeSpeechSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, String text) {
      super(behaviorReceiver, behaviorHistory);
      this.text = text;
   }

   @Override
   public void run () {
      propose(new SpeechBehavior(text), 1);
   }
}
