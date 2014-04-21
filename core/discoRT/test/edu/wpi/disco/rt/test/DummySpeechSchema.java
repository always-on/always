package edu.wpi.disco.rt.test;

import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.SchemaBase;

public class DummySpeechSchema extends DummySchema {

   final String text;

   public DummySpeechSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, String text) {
      super(behaviorReceiver, behaviorHistory);
      this.text = text;
   }

   @Override
   public void run () {
      propose(new SpeechBehavior(text), 1);
   }
}
