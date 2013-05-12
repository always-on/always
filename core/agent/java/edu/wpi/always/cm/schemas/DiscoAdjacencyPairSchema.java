package edu.wpi.always.cm.schemas;

import edu.wpi.always.cm.dialog.*;
import edu.wpi.always.cm.perceptors.MenuPerceptor;
import edu.wpi.disco.Agenda.Plugin;
import edu.wpi.disco.*;
import edu.wpi.disco.lang.Utterance;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.SchemaBase;
import java.util.*;

public class DiscoAdjacencyPairSchema extends ActivitySchema {

   protected final Interaction interaction; 
   protected final MenuTurnStateMachine stateMachine;
   protected final DiscoAdjacencyPair discoAdjacencyPair;

   public DiscoAdjacencyPairSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Interaction interaction) {
      super(behaviorReceiver, behaviorHistory);
      this.interaction = interaction;
      discoAdjacencyPair = new DiscoAdjacencyPair(behaviorReceiver, behaviorHistory, 
                                                  resourceMonitor, menuPerceptor, interaction);
      stateMachine = new MenuTurnStateMachine(behaviorHistory, resourceMonitor,
            menuPerceptor, new RepeatMenuTimeoutHandler());
      stateMachine.setSpecificityMetadata(0.9);
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
