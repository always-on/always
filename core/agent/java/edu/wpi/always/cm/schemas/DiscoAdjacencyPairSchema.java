package edu.wpi.always.cm.schemas;

import edu.wpi.disco.Interaction;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.BehaviorHistory;
import edu.wpi.disco.rt.behavior.BehaviorProposalReceiver;
import edu.wpi.disco.rt.menu.DiscoAdjacencyPair;
import edu.wpi.disco.rt.menu.MenuPerceptor;

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
      interaction.setOk(false);
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
