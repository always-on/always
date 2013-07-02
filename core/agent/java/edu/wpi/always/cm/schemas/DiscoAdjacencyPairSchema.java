package edu.wpi.always.cm.schemas;

import edu.wpi.always.Always;
import edu.wpi.disco.Interaction;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

public class DiscoAdjacencyPairSchema extends ActivitySchema {

   protected final Interaction interaction; 
   protected final DiscoAdjacencyPair discoAdjacencyPair;
   protected final MenuTurnStateMachine stateMachine;

   public DiscoAdjacencyPairSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Interaction interaction) {
      super(behaviorReceiver, behaviorHistory);
      this.interaction = interaction;
      discoAdjacencyPair = new DiscoAdjacencyPair(behaviorReceiver, behaviorHistory, 
                                                  resourceMonitor, menuPerceptor, interaction);
      stateMachine = new MenuTurnStateMachine(behaviorHistory, resourceMonitor,
            menuPerceptor, new RepeatMenuTimeoutHandler());
      stateMachine.setSpecificityMetadata(SPECIFICITY);
      stateMachine.setAdjacencyPair(discoAdjacencyPair);
   }

   @Override
   public void run () {
      if ( interaction.getFocus() != null ) {
         propose(stateMachine);
      } else {
         proposeNothing();
      }
   }
}
