package edu.wpi.always.cm.schemas;

import edu.wpi.always.Always;
import edu.wpi.disco.*;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

public class DiscoAdjacencyPairSchema extends ActivityStateMachineSchema<AdjacencyPair.Context> {

   protected final DiscoAdjacencyPair discoAdjacencyPair;
   protected final DiscoRT.Interaction interaction;
   
   public Interaction getInteraction () { return interaction; }

   public DiscoAdjacencyPairSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Always always) {
      this(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, always, 
            new DiscoRT.Interaction(new Agent("agent"), new User("user")));
   }
   
   public DiscoAdjacencyPairSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Always always, DiscoRT.Interaction interaction) {
      this(new DiscoAdjacencyPair(interaction), behaviorReceiver, behaviorHistory, 
           resourceMonitor, menuPerceptor, always, interaction);
   }
   
   public DiscoAdjacencyPairSchema (DiscoAdjacencyPair initial, BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Always always, DiscoRT.Interaction interaction) {
      super(initial, behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor);
      this.discoAdjacencyPair = (DiscoAdjacencyPair) stateMachine.getState();     
      this.interaction = interaction;
      interaction.setOk(false);
      interaction.setSchema(this);
      always.init(interaction);
   }
   
   @Override
   public void run () {
      if ( interaction.getFocusExhausted(true) == null ) stop(); 
      else propose(stateMachine);
   }
   
   protected void history () {
      Console console = interaction.getConsole();
      if ( console != null ) console.history(null);
   }
}
