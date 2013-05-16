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
   private Cache current;

   public DiscoAdjacencyPair (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Interaction interaction) {
      this.interaction = interaction;
   }
   
   public void update () {
      Agent agent = (Agent) interaction.getSystem();
      update(agent.respond(interaction, false, true) ? agent.getLastUtterance() : null,
             interaction.getExternal().generate(interaction));
   }

   protected void update (Utterance utterance, List<Plugin.Item> menu) {
      current = new Cache(utterance, menu);
   }

   @Override
   public double timeRemaining () { return 0; }

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
         this.message = utterance == null ? null : interaction.formatUtterance(utterance, true);
         this.items = items;
         choices = new ArrayList<String>(items.size());
         for (Plugin.Item item : items) 
            choices.add(interaction.formatUtterance((Utterance) item.task, false));
      }
   }

}
