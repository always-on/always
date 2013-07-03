package edu.wpi.always.cm.schemas;

import edu.wpi.always.Always;
import edu.wpi.disco.Interaction;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

public class DiscoAdjacencyPairSchema extends ActivityStateMachineSchema {

   protected final Interaction interaction; 
   protected final DiscoAdjacencyPair discoAdjacencyPair;

   public DiscoAdjacencyPairSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Interaction interaction) {
      super(new DiscoAdjacencyPair(behaviorReceiver, behaviorHistory, 
                    resourceMonitor, menuPerceptor, interaction), 
            behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor);
      this.interaction = interaction;
      discoAdjacencyPair = (DiscoAdjacencyPair) stateMachine.getState();
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
