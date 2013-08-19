package edu.wpi.always.cm.schemas;

import edu.wpi.always.client.Keyboard;
import edu.wpi.always.cm.ProposalBuilder;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.Behavior;
import edu.wpi.disco.rt.behavior.BehaviorHistory;
import edu.wpi.disco.rt.behavior.BehaviorMetadata;
import edu.wpi.disco.rt.behavior.BehaviorMetadataBuilder;
import edu.wpi.disco.rt.behavior.BehaviorProposalReceiver;
import edu.wpi.disco.rt.menu.AdjacencyPair;
import edu.wpi.disco.rt.menu.MenuPerceptor;

public abstract class ActivityStateMachineKeyboardSchema extends ActivityStateMachineSchema {

   private final Keyboard keyboard;

   public ActivityStateMachineKeyboardSchema (AdjacencyPair initial, 
         BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Keyboard keyboard) {
      super(initial, behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor);
      this.keyboard = keyboard;
   }

   @Override
   public void run () {
      super.run();
      if ( keyboard.isOverflow() ) {
         keyboard.setOverflow(false);
         BehaviorMetadataBuilder metadata = new BehaviorMetadataBuilder();
         ProposalBuilder builder = new ProposalBuilder(); 
         metadata.specificity(0.9);
         builder.setMetadata(metadata);
         builder.say("Too many characters");
         Behavior b = builder.build();
         BehaviorMetadata m = builder.getMetadata();
         propose(b, m);
      }
   }

}
