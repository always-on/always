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

public class DiscoAdjacencyPairSchema extends ActivitySchema implements AdjacencyPair {

   protected final Interaction interaction; 
   protected final MenuTurnStateMachine stateMachine;
   private APCache currentAP;

   public DiscoAdjacencyPairSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Interaction interaction) {
      super(behaviorReceiver, behaviorHistory);
      this.interaction = interaction;
      stateMachine = new MenuTurnStateMachine(behaviorHistory, resourceMonitor,
            menuPerceptor, new RepeatMenuTimeoutHandler());
      stateMachine.setSpecificityMetadata(0.9);
      stateMachine.setAdjacencyPair(this);
   }

   @Override
   public void run () {
      if ( interaction.getFocus() != null ) {
         propose(stateMachine);
      } else {
         proposeNothing();
      }
   }

   @Override
   public double timeRemaining () { return 5; }

   @Override
   public void enter () {}

   @Override
   public AdjacencyPair nextState (String text) {
      int i = currentAP.choices.indexOf(text);
      if ( i < 0 ) throw new IllegalArgumentException("Unexpected menu selection: "+text);
      interaction.doneUtterance((Utterance) currentAP.items.get(i).task, 
            currentAP.items.get(i).contributes, text);
      updateCurrentAP();
      return this;
   }

   private void updateCurrentAP () {
      Agent agent = (Agent) interaction.getSystem();
      agent.respond(interaction, false, true);
      currentAP = new APCache(agent.getLastUtterance(),
            interaction.getExternal().generate(interaction));
   }

   @Override
   public String getMessage () {
      if ( currentAP == null ) updateCurrentAP();
      return currentAP.message;
   }

   @Override
   public List<String> getChoices () {
      if ( currentAP == null ) updateCurrentAP();
      return currentAP.choices;
   }

   @Override
   public boolean isTwoColumnMenu () { return false; }

   @Override
   public boolean prematureEnd () { return false; }

   private class APCache {

      private final String message;
      private final List<Plugin.Item> items;
      private final List<String> choices;

      public APCache (Utterance utterance, List<Plugin.Item> items) {
         this.message = utterance == null ? null : interaction.formatUtterance(utterance);
         this.items = items;
         choices = new ArrayList<String>(items.size());
         for (Plugin.Item item : items) 
            choices.add(interaction.formatUtterance((Utterance) item.task));
      }
   }
}
