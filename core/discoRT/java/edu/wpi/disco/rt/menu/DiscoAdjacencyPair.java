package edu.wpi.disco.rt.menu;

import edu.wpi.disco.Agenda.Plugin;
import edu.wpi.disco.*;
import edu.wpi.disco.lang.*;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;
import java.util.*;

public class DiscoAdjacencyPair extends AdjacencyPairBase<AdjacencyPair.Context> {

   private final DiscoRT.Interaction interaction;
   
   public DiscoRT.Interaction getInteraction () { return interaction; }

   private Cache current;
   
   public DiscoAdjacencyPair (DiscoRT.Interaction interaction) {
      super(null, new AdjacencyPair.Context());
      this.interaction = interaction;
   }
   
   public static final String TOPLEVEL = "What would you like to do together?";

   public void update () {
      Agent agent = (Agent) interaction.getSystem();
      update(agent.respond(
            interaction, false, true) ? agent.getLastUtterance() : 
               interaction.getFocusExhausted(true) == null ? 
                  new Say(interaction.getDisco(), false, TOPLEVEL) : null,
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
      if ( i >= 0 && !REPEAT.equals(text) ) {
         interaction.doneUtterance((Utterance) current.items.get(i).task, 
               current.items.get(i).contributes, text);
         update(); 
      }
      // transition is always circular, so this is not a pure state!
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
   public boolean isTwoColumnMenu () { 
      return current != null && current.choices.size() > 7; 
   }

   @Override
   public boolean prematureEnd () { return false; }

   private class Cache {

      private final String message;
      private final List<Plugin.Item> items;
      private final List<String> choices;

      public Cache (Utterance utterance, List<Plugin.Item> items) {
         this.message = utterance == null ? null : interaction.format(utterance, true);
         this.items = items;
         choices = new ArrayList<String>(items.size()+1);
         for (Plugin.Item item : items) {
            choices.add(normalize(
              item.formatted != null ? item.formatted : interaction.format(item, false)));
         }
         if ( utterance != null ) choices.add(REPEAT);  
      }
   }

}
