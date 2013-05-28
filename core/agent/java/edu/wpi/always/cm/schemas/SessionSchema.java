package edu.wpi.always.cm.schemas;

import edu.wpi.always.*;
import edu.wpi.always.client.ClientProxy;
import edu.wpi.cetask.*;
import edu.wpi.disco.*;
import edu.wpi.disco.lang.Propose;
import edu.wpi.disco.plugin.TopsPlugin;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;
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
         RelationshipManager rm, ClientProxy proxy, Always always) {
      super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, interaction);
      container = always.getContainer();
      stop = new Stop(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, interaction,
                      proxy);
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
            } else yield(goal);
         } else {
            if ( focus.isLive() && Utils.isTrue(goal.getShould()) ) {
               if ( !focus.isStarted() ) {
                  TaskClass task = goal.getType();
                  started.put(goal,
                        Plugin.getPlugin(task, container).startActivity(Plugin.getActivity(task)));
                  focus.setStarted(true);
                  yield(goal);
               }
            }
         }
      }
      // fall through when session plan exhausted or focused activity schema done 
      // or focused task stopped 
      propose(stateMachine);
   }
   
   private void yield (Task goal) {
      stop.setGoal(goal);
      stop.update();
      stateMachine.setAdjacencyPair(stop);
      stateMachine.setExtension(true);
      stateMachine.setSpecificityMetadata(0.5);
      setNeedsFocusResource(false);
   }
   
   private class Stop extends DiscoAdjacencyPair {
      
      private Task goal;
      private final ClientProxy proxy;
      
      private void setGoal (Task goal) { this.goal = goal; }
      
      public Stop (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Interaction interaction, ClientProxy proxy) {
         super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, interaction);
         this.proxy = proxy;
      }
      
      @Override
      public void update () {
         update(null, Collections.singletonList(
               Agenda.newItem(new Propose.Stop(interaction.getDisco(), true, goal), null)));
      }
      
      @Override
      public AdjacencyPair nextState (String text) {
         super.nextState(text);
         proxy.showMenu(Collections.<String>emptyList(), false, true); // clear extension menu
         // TODO clear client plugin display
         discoAdjacencyPair.update();
         stateMachine.setExtension(false);
         stateMachine.setSpecificityMetadata(ActivitySchema.SPECIFICITY+0.2);
         setNeedsFocusResource(true);
         return discoAdjacencyPair; // one shot
      }
   }
 
}
