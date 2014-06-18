package edu.wpi.always.cm.schemas;

import edu.wpi.always.*;
import edu.wpi.disco.*;
import edu.wpi.disco.Interaction;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

public class DiscoAdjacencyPairSchema extends ActivityStateMachineSchema<AdjacencyPair.Context> {

   protected final DiscoAdjacencyPair discoAdjacencyPair;
   protected final DiscoRT.Interaction interaction;
   
   public DiscoRT.Interaction getInteraction () { return interaction; }

   public DiscoAdjacencyPairSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Always always, Logger.Activity loggerName) {
      this(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, always, 
            new Interaction(), loggerName);
   }
   
   public DiscoAdjacencyPairSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Always always, DiscoRT.Interaction interaction, 
         Logger.Activity loggerName) {
      this(new DiscoAdjacencyPair(interaction), behaviorReceiver, behaviorHistory, 
           resourceMonitor, menuPerceptor, always, interaction, loggerName);
   }
   
   public DiscoAdjacencyPairSchema (DiscoAdjacencyPair initial, BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Always always, DiscoRT.Interaction interaction,
         Logger.Activity loggerName) {
      super(initial, behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, loggerName);
      this.discoAdjacencyPair = (DiscoAdjacencyPair) stateMachine.getState();     
      this.interaction = interaction;
      interaction.setOk(false);
      interaction.setSchema(this);
      always.init(interaction);
   }
   
   public static class Interaction extends DiscoRT.Interaction {
      
      public Interaction () {
         super(new Agent("agent"), new User("user"), Always.getRelease() == null);
      }
   }
   
   @Override
   public void runActivity () {
      if ( interaction.getFocusExhausted(true) == null ) stop(); 
      else propose(stateMachine);
   }
   
   protected void history () {
      Console console = interaction.getConsole();
      if ( console != null ) console.history(null);
   }
 }
