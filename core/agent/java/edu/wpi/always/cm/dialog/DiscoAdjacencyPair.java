package edu.wpi.always.cm.dialog;

import edu.wpi.always.cm.perceptors.MenuPerceptor;
import edu.wpi.disco.Agenda.Plugin;
import edu.wpi.disco.*;
import edu.wpi.disco.lang.Utterance;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import java.util.*;

public class DiscoAdjacencyPair implements AdjacencyPair {

   protected final Interaction interaction; 
   protected final MenuTurnStateMachine stateMachine;
   private Cache current;

   public DiscoAdjacencyPair (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Interaction interaction) {
      this.interaction = interaction;
      stateMachine = new MenuTurnStateMachine(behaviorHistory, resourceMonitor,
            menuPerceptor, new RepeatMenuTimeoutHandler());
      stateMachine.setSpecificityMetadata(0.9);
      stateMachine.setAdjacencyPair(this);
   }

   protected void update () {
      Agent agent = (Agent) interaction.getSystem();
      agent.respond(interaction, false, true);
      update(agent.getLastUtterance(),
            interaction.getExternal().generate(interaction));
   }

   protected void update (Utterance utterance, List<Plugin.Item> menu) {
      current = new Cache(utterance, menu);
   }

   @Override
   public double timeRemaining () { return 5; }

   @Override
   public void enter () {}

   @Override
   public AdjacencyPair nextState (String text) {
      int i = current.choices.indexOf(text);
      if ( i >= 0 ) {
         interaction.doneUtterance((Utterance) current.items.get(i).task, 
               current.items.get(i).contributes, text);
         update();
      }
      return this;
   }

   @Override
   public String getMessage () {
      if ( current == null ) update();
      return current.message;
   }

   @Override
   public List<String> getChoices () {
      if ( current == null ) update();
      return current.choices;
   }

   @Override
   public boolean isTwoColumnMenu () { return false; }

   @Override
   public boolean prematureEnd () { return false; }

   private class Cache {

      private final String message;
      private final List<Plugin.Item> items;
      private final List<String> choices;

      public Cache (Utterance utterance, List<Plugin.Item> items) {
         this.message = utterance == null ? null : interaction.formatUtterance(utterance);
         this.items = items;
         choices = new ArrayList<String>(items.size());
         for (Plugin.Item item : items) 
            choices.add(interaction.formatUtterance((Utterance) item.task));
      }
   }
}
