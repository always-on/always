package edu.wpi.always.cm.schemas;

import java.util.*;
import org.picocontainer.MutablePicoContainer;
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

public class SessionSchema extends DiscoAdjacencyPairSchema {
   
   private final MutablePicoContainer container; // for plugins
   private final Stop stop;
   private final ClientProxy proxy;
   
   public SessionSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Interaction interaction,
         RelationshipManager rm, ClientProxy proxy, Always always) {
      super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, interaction);
      this.proxy = proxy;
      container = always.getContainer();
      stop = new Stop(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, interaction);
      DiscoDocument session = rm.getSession();
      if ( session != null ) {
         interaction.load("Relationship Manager", 
               session.getDocument(), session.getProperties(), session.getTranslate());
         interaction.push(interaction.addTop("_Session"));
      }
      ((TopsPlugin) ((Agenda) interaction.getExternal().getAgenda()).getPlugin(TopsPlugin.class))
                      .setInterrupt(false);
   }

   // activities for which startActivity has been called (not same as Plan.isStarted)
   private final Map<Plan,ActivitySchema> started = new HashMap<Plan,ActivitySchema>();
   
   // note this schema uses menu with focus and menu extension without focus
   
   @Override
   public void run () {
      Plan plan = interaction.getFocusExhausted(true);
      if ( plan != null ) {
         if ( interaction.isTop(plan) ) {
            // focus is on session, move it down to first live child
            // (must have some or would be popped as exhausted)
            interaction.push(plan.getLive().get(0));
            plan = interaction.getFocusExhausted(true);
         }
         if ( plan.getGoal() instanceof Propose.Should ) 
            plan = plan.getParent();
         Schema schema = started.get(plan);
         if ( schema != null ) {
            if ( schema.isDone() ) {
               stop(plan);
               stateMachine.setState(discoAdjacencyPair);
            }
            else yield(plan);
         } else {
            TaskClass task = plan.getType();
            if ( Plugin.isPlugin(task) &&
                 plan.isLive() && !plan.isOptional() && !plan.isStarted() ) {
               started.put(plan,
                  Plugin.getPlugin(task, container).startActivity(Plugin.getActivity(task)));
               plan.setStarted(true);
               yield(plan);
            }
         }
      }
      // fall through when:
      //    -live plan is not a plugin
      //    -focused activity schema done
      //    -focused task stopped
      //    -session plan exhausted 
      propose(stateMachine);
   }
   
   private void yield (Plan plan) {
      stop.setPlan(plan);
      stop.update();
      stateMachine.setState(stop);
      stateMachine.setExtension(true);
      stateMachine.setSpecificityMetadata(0.5);
      setNeedsFocusResource(false);
      Plugin.getPlugin(plan.getType(), container).show();
   }
   
   private void stop (Plan plan) {
      plan.setComplete(true);
      started.remove(plan.getGoal());
      proxy.showMenu(Collections.<String>emptyList(), false, true); // clear extension menu
      proxy.hidePlugin();
      discoAdjacencyPair.update();
      stateMachine.setExtension(false);
      stateMachine.setSpecificityMetadata(ActivitySchema.SPECIFICITY+0.2);
      setNeedsFocusResource(true);
   }
   
   private class Stop extends DiscoAdjacencyPair {
      
      private Plan plan;
      
      private void setPlan (Plan plan) { this.plan = plan; }
      
      public Stop (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Interaction interaction) {
         super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, interaction);
      }
      
      @Override
      public void update () {
         update(null, Collections.singletonList(
               Agenda.newItem(new Propose.Stop(interaction.getDisco(), true, plan.getGoal()), null)));
      }
      
      @Override
      public AdjacencyPair nextState (String text) {
         super.nextState(text);
         stop(plan);
         return discoAdjacencyPair; // one shot
      }
   }
 
}
