package edu.wpi.disco.rt;

import edu.wpi.cetask.Plan;
import edu.wpi.disco.*;
import edu.wpi.disco.Agenda.Plugin;
import edu.wpi.disco.plugin.ProposeShouldNotPlugin;
import java.util.*;

public class DiscoProposeShouldNotPlugin extends Plugin {

   private ProposeShouldNotPlugin innerPlugin;
   private final int priority2;
   private final Actor actor;

   public DiscoProposeShouldNotPlugin (Actor actor, Agenda agenda, int priority) {
      agenda.super(priority);
      this.actor = actor;
      priority2 = priority;
      tryCreateInnerPluginIfNotCreatedYet();
   }

   private void tryCreateInnerPluginIfNotCreatedYet () {
      if ( innerPlugin != null )
         return;
      Agenda agendaInstanceNeverToBeUsed = DiscoUtils
            .createEmptyAgendaFor(actor);
      if ( agendaInstanceNeverToBeUsed != null )
         innerPlugin = new ProposeShouldNotPlugin(agendaInstanceNeverToBeUsed,
               priority2);
   }

   @Override
   public List<Item> apply (Plan plan) {
      tryCreateInnerPluginIfNotCreatedYet();
      if ( innerPlugin != null && getAgenda().getDisco().getFocus() == plan ) {
         return innerPlugin.apply();
      }
      return new ArrayList<Item>();
   }
}
