package edu.wpi.always.cm.schemas;

import edu.wpi.always.*;
import edu.wpi.always.cm.dialog.DiscoAdjacencyPair;
import edu.wpi.always.cm.perceptors.MenuPerceptor;
import edu.wpi.always.rm.IRelationshipManager;
import edu.wpi.cetask.*;
import edu.wpi.disco.*;
import edu.wpi.disco.lang.Propose;
import edu.wpi.disco.plugin.TopsPlugin;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.Schema;
import edu.wpi.disco.rt.util.DiscoDocument;
import org.picocontainer.MutablePicoContainer;
import java.util.*;

public class SessionSchema extends DiscoAdjacencyPairSchema {
   
   private final MutablePicoContainer container; // for plugins
   private final Stop stop;
   
   public SessionSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Interaction interaction,
         IRelationshipManager rm, Always always) {
      super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, interaction);
      container = always.getContainer();
      stop = new Stop(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, interaction);
      DiscoDocument session = rm.getSession();
      if ( session != null ) {
         interaction.load("Relationship Manager", 
               session.getDocument(), session.getProperties(), session.getTranslate());
         interaction.push(interaction.addTop("_Session"));
      }
      interaction.setOk(false);
      ((TopsPlugin) ((Agenda) interaction.getExternal().getAgenda()).getPlugin(TopsPlugin.class))
                      .setInterrupt(false);
   }

   // activities for which startActivity has been called (not same as Plan.isStarted)
   private final Map<Task,ActivitySchema> started = new HashMap<Task,ActivitySchema>();
   
   // note this schema uses menu with focus and menu extension without focus
   
   @Override
   public void run () {
      Plan focus = interaction.getFocusExhausted(true);
      if ( focus != null ) {
         Task goal = focus.getGoal();
         if ( goal instanceof Propose.Should ) {
            goal = ((Propose.Should) goal).getGoal();
            focus = focus.getParent();
         }
         Schema schema = started.get(goal);
         if ( schema != null ) {
            if ( schema.isDone() ) {
               focus.setComplete(true);
               started.remove(goal);
            } else {
               propose(stateMachine);
               return;
            }
         } else {
            if ( focus.isLive() && Utils.isTrue(goal.getShould()) ) {
               if ( !focus.isStarted() ) {
                  TaskClass task = goal.getType();
                  started.put(goal,
                        Plugin.getPlugin(task, container).startActivity(Plugin.getActivity(task)));
                  focus.setStarted(true);
                  stop.setGoal(goal);
                  stateMachine.setAdjacencyPair(stop);
                  stateMachine.setExtension(true);
                  stateMachine.setSpecificityMetadata(0.5);
                  setNeedsFocusResource(false);
               }
               propose(stateMachine);
               return;
            }
         }
      }
      // fall through when session plan exhausted or focused activity schema done 
      // or focused task stopped 
      stateMachine.setAdjacencyPair(discoAdjacencyPair);
      stateMachine.setExtension(false);
      stateMachine.setSpecificityMetadata(0.9);
      setNeedsFocusResource(true);
      propose(stateMachine);
   }
   
   private class Stop extends DiscoAdjacencyPair {
      
      private Task goal;
      
      private void setGoal (Task goal) { this.goal = goal; }
      
      public Stop (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Interaction interaction) {
         super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, interaction);
         this.goal = goal;
      }
      
      @Override
      protected void update () {
          update(null, Collections.singletonList(
                Agenda.newItem(new Propose.Stop(interaction.getDisco(), true, goal), null)));
      }
   }
 
}
