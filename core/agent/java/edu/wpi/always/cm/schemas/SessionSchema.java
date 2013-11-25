package edu.wpi.always.cm.schemas;

import java.util.*;
import org.picocontainer.MutablePicoContainer;
import edu.wpi.always.*;
import edu.wpi.always.client.ClientProxy;
import edu.wpi.cetask.*;
import edu.wpi.disco.*;
import edu.wpi.disco.lang.Propose;
import edu.wpi.disco.plugin.TopsPlugin;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;
import edu.wpi.disco.rt.schema.*;
import edu.wpi.disco.rt.util.*;
import edu.wpi.disco.rt.util.Utils;

public class SessionSchema extends DiscoAdjacencyPairSchema {
   
   private final MutablePicoContainer container; // for plugins
   private final Stop stop;
   private final ClientProxy proxy;
   private final SchemaManager schemaManager;
   private final Interaction interaction;
   
   public SessionSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, ClientProxy proxy,
         SchemaManager schemaManager, Always always, 
         DiscoRT.Interaction interaction) {
      super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, always, interaction);
      this.proxy = proxy;
      this.schemaManager = schemaManager;
      this.interaction = interaction;
      container = always.getContainer();
      stop = new Stop(interaction);
      DiscoDocument session = always.getRM().getSession();
      Disco disco = interaction.getDisco();
      if ( disco.getTaskClass("_Session") == null && session != null ) { // could be restart
         interaction.load("Relationship Manager", 
               session.getDocument(), session.getProperties(), session.getTranslate());
         interaction.push(interaction.addTop("_Session"));
         always.getCM().setSchema(disco.getTaskClass("_Session"), SessionSchema.class);
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
         if ( plan.getType().getId().equals("_Session") ) {
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
            } else yield(plan);
         } else {
            TaskClass task = plan.getType();
            if ( Plugin.isPlugin(task) &&
                 plan.isLive() && !plan.isOptional() && !plan.isStarted() ) {
               started.put(plan,
                  Plugin.getPlugin(task, container).startActivity(Plugin.getActivity(task)));
               plan.setStarted(true);
               Utils.lnprint(System.out, "Starting "+plan.getType()+"...");
               history();
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
   
   @Override
   public void dispose () {
      super.dispose();
      // restart if fails for some reason
      Utils.lnprint(System.out, "Restarting SessionSchema...");
      interaction.clear();
      schemaManager.start(getClass());
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
      Utils.lnprint(System.out, "Returning to Session...");
      history(); // before update
      discoAdjacencyPair.update();
      stateMachine.setExtension(false);
      stateMachine.setSpecificityMetadata(ActivitySchema.SPECIFICITY+0.2);
      setNeedsFocusResource(true);
   }
   
   private class Stop extends DiscoAdjacencyPair {
      
      private Plan plan;
      
      private void setPlan (Plan plan) { this.plan = plan; }
      
      public Stop (DiscoRT.Interaction interaction) {
         super(interaction);
      }
      
      @Override
      public void update () {
         update(null, Collections.singletonList(
               Agenda.newItem(new Propose.Stop(interaction.getDisco(), true, plan.getGoal()), null)));
      }
      
      @Override
      public AdjacencyPair nextState (String text) {
         super.nextState(text);
         Schema schema = started.get(plan);
         if ( schema != null ) schema.cancel();
         stop(plan);
         return new StopAdjacencyPairWrapper(discoAdjacencyPair); // one shot
      }
   }

   private static class StopAdjacencyPairWrapper extends AdjacencyPairWrapper {
      
      public StopAdjacencyPairWrapper (AdjacencyPair inner) {
         super(inner);
      }
   
      @Override
      public String getMessage () {
         String text = inner.getMessage();
         return text == null ? "Ok." : ("Ok. Now. " + text);
      }
   }
}
