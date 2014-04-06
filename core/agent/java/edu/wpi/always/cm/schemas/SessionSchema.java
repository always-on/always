package edu.wpi.always.cm.schemas;

import java.util.*;
import org.joda.time.LocalTime;
import org.picocontainer.MutablePicoContainer;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import edu.wpi.always.*;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.*;
import edu.wpi.always.user.owl.OntologyUserModel;
import edu.wpi.cetask.*;
import edu.wpi.disco.*;
import edu.wpi.disco.Agenda.Plugin.Item;
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
   private final CollaborationManager cm;
   private final Interaction interaction;
   
   public static int HOUR = -1;  // for testing
   
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
      cm = container.getComponent(CollaborationManager.class);
      if ( HOUR < 0 ) HOUR = LocalTime.now().getHourOfDay();
      stop = new Stop(interaction);
      ((TopsPlugin) ((Agenda) interaction.getExternal().getAgenda()).getPlugin(TopsPlugin.class))
           .setInterrupt(false);
      // print out information here so it goes into log
      System.out.println();
      System.out.println("****************************************************************************");
      System.out.println("Writing log to: "+interaction.getConsole().log);
      System.out.println("****************************************************************************");
      System.out.println("Agent type = "+Always.getAgentType());
      try { UserUtils.print(always.getUserModel(), System.out);}
      catch (InconsistentOntologyException e) { cm.revertUserModel(e); }  // try once
      DiscoDocument session = always.getRM().getSession();
      Disco disco = interaction.getDisco();
      if ( disco.getTaskClass("_Session") == null && session != null ) { // could be restart
         interaction.load("Relationship Manager", 
               session.getDocument(), session.getProperties(), session.getTranslate());
         interaction.push(interaction.addTop("_Session"));
         always.getCM().setSchema(disco.getTaskClass("_Session"), SessionSchema.class);
      }

   }

   // activities for which startActivity has been called (not same as Plan.isStarted)
   private final Map<Plan,ActivitySchema> started = new HashMap<Plan,ActivitySchema>();
   
   // note this schema uses menu with focus and menu extension without focus
   
   @Override
   public void run () {
      Plan plan = interaction.getFocusExhausted(true);
      ActivitySchema schema = null;
      if ( plan != null ) {
         if ( plan.getType().getId().equals("_Session") ) {
            // focus is on session, move it down to first live child
            List<Plan> live = plan.getLive();
            if ( !live.isEmpty() ) {
               plan = live.get(0);
               interaction.push(plan);
            }
         }
         if ( plan.getGoal() instanceof Propose.Should ) 
            plan = plan.getParent();
         schema = started.get(plan);
         if ( schema != null ) {
            if ( schema.isDone() ) {
               revertIfInconsistent(schema);
               stop(plan);
               stateMachine.setState(schema.isSelfStop() ? 
                  new StopAdjacencyPairWrapper(discoAdjacencyPair) : 
                  discoAdjacencyPair);
            } else yield(plan, schema);
         } else {
            TaskClass task = plan.getType();
            if ( Plugin.isPlugin(task) &&
                 plan.isLive() && !plan.isOptional() && !plan.isStarted() ) {
               schema = Plugin.getPlugin(task, container).startActivity(Plugin.getActivity(task)); 
               started.put(plan, schema);
               plan.setStarted(true);
               Utils.lnprint(System.out, "Starting "+plan.getType()+"...");
               history();
               yield(plan, schema);
            }
         }
      }
      // above code does nothing when:
      //    -live plan is not a plugin
      //    -focused activity schema done
      //    -focused task stopped
      //    -session plan exhausted 
      if ( schema != null && schema.isSelfStop() ) proposeNothing();
      else propose(stateMachine);
   }
   
   private void revertIfInconsistent (ActivitySchema schema) {
      InconsistentOntologyException e = schema.getInconsistentOntologyException();
      if ( e != null) cm.revertUserModel(e);
   }
   
   @Override
   public void dispose () {
      super.dispose();
      ClientPluginUtils.hidePlugin(
            cm.getContainer().getComponent(UIMessageDispatcher.class));
      // restart if fails for some reason
      Utils.lnprint(System.out, "Restarting SessionSchema...");
      interaction.clear();
      revertIfInconsistent(this);
      schemaManager.start(getClass());
   }
   
   private void yield (Plan plan, ActivitySchema schema) {
      if ( !schema.isSelfStop() ) {
         stop.setPlan(plan);
         stop.update();
         stateMachine.setState(stop);
         stateMachine.setExtension(true);
      }
      stateMachine.setSpecificityMetadata(SPECIFICITY-0.2);
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
         if ( schema != null ) schema.stop();
         stop(plan);
         return new StopAdjacencyPairWrapper(discoAdjacencyPair); // one shot
      }
   }

   private static class StopAdjacencyPairWrapper extends AdjacencyPairWrapper<AdjacencyPair.Context> {
      
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
