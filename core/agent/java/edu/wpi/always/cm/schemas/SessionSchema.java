package edu.wpi.always.cm.schemas;

import java.util.*;
import org.picocontainer.MutablePicoContainer;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import edu.wpi.always.*;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.*;
import edu.wpi.cetask.*;
import edu.wpi.disco.*;
import edu.wpi.disco.lang.*;
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
   private final DiscoRT.Interaction interaction;  
   
   private volatile ActivitySchema current; // currently running or null 

   // static so can be set before schema running
   // volatile because set by other threads  
   public static volatile String interrupt;

   private volatile ActivitySchema interrupted; // interrupted activity or null
   private volatile Plan interruptedPlan; // interrupted plan or null   
   private volatile String interruptedPlugin; // interrupted client plugin or null
   
   /**
    * Attempt to interrupt current session with given Disco task class name.  Returns
    * false iff interruption ignored.
    */
   public static boolean interrupt (String interrupt) {
      if ( THIS != null ) {
         if ( THIS.isInterruptible() ) 
            synchronized(THIS.interaction) { SessionSchema.interrupt = interrupt; }   
         else { 
            Utils.lnprint(System.out, "SessionSchema ignoring attempted interruption: "+interrupt);
            return false;
         }
      } else SessionSchema.interrupt = interrupt; // see EngagementPerception
      return true;
   }
 
   @Override
   public boolean isInterruptible () {
      synchronized (interaction) {
         return interruptible && (current == null || current.isInterruptible());
      }
   }
   
   public static ActivitySchema getInterruptedSchema () { 
      return THIS == null ? null : THIS.interrupted;
   }

   public static String getInterruptedPluginName () {
      return THIS == null ? null : THIS.interruptedPlugin;
   }
 
   /**
    * Date that session started (for time of day)
    * See {@link Always#DATE}.
    */
   public static Date DATE;
   
   private static SessionSchema THIS;
   
   public SessionSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, UIMessageDispatcher dispatcher, ClientProxy proxy,
         SchemaManager schemaManager, Always always, DiscoRT.Interaction interaction) {
      super(new Toplevel(interaction, dispatcher), behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor,
            always, interaction, Logger.Activity.SESSION);
      THIS = this;
      this.proxy = proxy;
      this.schemaManager = schemaManager;
      this.interaction = interaction;
      container = always.getContainer();
      cm = container.getComponent(CollaborationManager.class);
      stop = new Stop(interaction);
      // wait until THIS set so that another session schema not started!
      schemaManager.start(CalendarInterruptSchema.class);
      try {
         if ( DATE == null ) DATE = new Date();
         ((TopsPlugin) ((Agenda) interaction.getExternal().getAgenda()).getPlugin(TopsPlugin.class))
            .setInterrupt(false);
         // print out information here so it goes into log
         System.out.println();
         System.out.println("****************************************************************************");
         System.out.println("Writing log to: "+interaction.getLog());
         System.out.println("****************************************************************************");
         System.out.println("Agent type = "+Always.getAgentType());
         System.out.println("Time of day = "+UserUtils.getTimeOfDay());
         UserModel model = always.getUserModel();
         try { UserUtils.print(model, System.out);}
         catch (InconsistentOntologyException e) { cm.inconsistentUserModel(e); }  // try once
         TaskClass task = interaction.getDisco().getTaskClass("_Session") ; 
         if ( task != null ) { // restart
              if ( SessionSchema.getCurrentLoggerName() != Logger.Activity.SESSION )
                 Logger.logEvent(Logger.Event.END);
         } else {
            DiscoDocument session = always.getRM().getSession();
            if ( session != null ) {
               interaction.load("Relationship Manager", 
                     session.getDocument(), session.getProperties(), session.getTranslate());
               interaction.push(interaction.addTop("_Session"));
               always.getCM().setSchema(task, SessionSchema.class);
               if ( !model.getUserName().isEmpty() ) 
                  model.setSessions(model.getSessions()+1);
               Logger.logEvent(Logger.Event.START, model.getCloseness(), UserUtils.getTimeOfDay(), model.getSessions()); 
            }
         }
      } catch (Exception e) { 
         e.printStackTrace();
         Always.exit(3);  // restart Java
      } 
   }

   // activities for which startActivity has been called (not same as Plan.isStarted)
   private final Map<Plan,ActivitySchema> started = new HashMap<Plan,ActivitySchema>();  

   private static Logger.Activity LoggerName;
   
   public static void setCurrentLoggerName (Logger.Activity activity) { LoggerName = activity; }
   
   public static Logger.Activity getCurrentLoggerName () {
      return LoggerName != null ? LoggerName :
         (THIS == null || THIS.current == null) ? Logger.Activity.SESSION :
         THIS.current.getLoggerName();
   }

   // note this schema uses menu with focus and menu extension without focus
   
   @Override
   public void runActivity () {
      synchronized (interaction) {
         Plan plan = getFocus();
         if ( plan != null ) {
            // if actual focus is on session (or other internal) move it down to first live child
            Plan focus = interaction.getFocusExhausted(true); 
            if ( focus.getType().isInternal() ) {
               List<Plan> live = focus.getLive();
               if ( !live.isEmpty() ) {
                  plan = live.get(0);
                  interaction.push(plan);
               }
            }
            current = started.get(plan);
            interruptIf(); // may push interruption
            if ( current != null ) {
               if ( current.isDone() ) {
                  revertIfInconsistent(current);
                  stateMachine.setState(new ResumeAdjacencyPairWrapper(discoAdjacencyPair)); 
                  stop(plan); 
               } else yield(plan);
            } else if ( !(plan != getFocus() && plan == interruptedPlan) )  { 
               // don't start activity if about to interrupt it
               TaskClass task = plan.getType();
               if ( Plugin.isPlugin(task) &&
                     plan.isLive() && !plan.isOptional() && !plan.isStarted() ) {
                  try { // to properly log model updates in schema initializers
                     LoggerName = Plugin.getLoggerName(task);
                     current = Plugin.getPlugin(task, container).startActivity(Plugin.getActivity(task));
                  } finally { LoggerName = null; }
                  started.put(plan, current);
                  plan.setStarted(true);
                  Utils.lnprint(System.out, "Starting "+plan.getType()+"...");
                  Logger.logEvent(Logger.Event.START);
                  history();
                  yield(plan);
               }
            }
            Disco disco = interaction.getDisco();
            if ( !disco.getSegment().isInterruption() && 
                  disco.getProperty(disco.getTop(plan).getGoal().getType().getPropertyId()+"@interruption") == null ) 
               interruptible = true;
         } else { // plan == null, i.e., session plan exhausted (at toplevel)
            current = null;
            interruptible = true; // interruption done
         }
      }
      // above code does nothing when:
      //    -live plan is not a plugin
      //    -focused activity schema done
      //    -focused task stopped
      //    -session plan exhausted
      interruptIf();
      if ( current != null && current.isSelfStop() ) proposeNothing();
      else propose(stateMachine); 
      if ( EngagementSchema.EXIT ) {
         // for calendar interruption, hold focus so interrupted schema doesn't talk before exit 
         propose(Behavior.newInstance(new MenuBehavior(Collections.singletonList(" "))).addFocusResource(), 
                 SPECIFICITY+0.2);
      }
   }
   
   private Plan getFocus () {
       Plan plan = interaction.getFocusExhausted(true);
       return plan != null && plan.getGoal() instanceof Propose.Should ? plan.getParent() : plan;
   }
   
   private enum Interruption { CALENDAR, SKYPE }  // for logging
   
   private void interruptIf () {
      if ( interrupt != null ) {
         interruptible = false; // don't interrupt interruption
         Utils.lnprint(System.out, "Interrupting "+(current == null ? "session" : current)
               +" for "+interrupt);
         if ( interrupt.equals("_CalendarInterruption" ) )
            Logger.logEvent(Logger.Event.INTERRUPTION, Interruption.CALENDAR, CalendarInterruptSchema.ENTRY);
         else if ( interrupt.equals("_SkypeInterruption") ) 
            Logger.logEvent(Logger.Event.INTERRUPTION, Interruption.SKYPE, SkypeInterruptHandler.CALLER_ID);
         else Utils.lnprint(System.out, "Interruption unknown for logger: "+interrupt);
         interrupted = current;
         interruptedPlan = getFocus();
         interruptedPlugin = ClientPluginUtils.getPluginName(); // before unyield hides
         interaction.push(new Plan(interaction.getDisco().getTaskClass(interrupt).newInstance()));
         startInterruption();
         interrupt = null;
         stateMachine.setState(discoAdjacencyPair);
         if ( current == null ) discoAdjacencyPair.update();
         if ( interrupted != null ) unyield();
         else current = null;
      }
   }
   
   public static void startInterruption () { // to be called from schema
      if ( THIS != null && THIS.interrupted != null ) {
         THIS.interrupted.stop();  // don't go back afterwards
         try { // log stopped end now so time not double counted 
            LoggerName = THIS.interrupted.getLoggerName(); 
            Logger.logEvent(Logger.Event.END);
            THIS.interruptedPlan.setComplete(true); // mark for stop(Plan)
         } finally { LoggerName = null; }
      }
   }
   
   private void revertIfInconsistent (ActivitySchema schema) {
      InconsistentOntologyException e = schema.getInconsistentOntologyException();
      if ( e != null) cm.inconsistentUserModel(e);
   }
   
   @Override
   public void dispose () {
      super.dispose();
      ClientPluginUtils.hidePlugin(
            cm.getContainer().getComponent(UIMessageDispatcher.class));
      if ( !EngagementSchema.EXIT ) {
         // restart if fails for some reason
         Utils.lnprint(System.out, "Restarting SessionSchema...");
         interaction.clear();
         revertIfInconsistent(this);
         schemaManager.start(getClass());
      }
   }
   
   private void yield (Plan plan) {
      if ( !current.isSelfStop() ) {
         stop.setPlan(plan);
         stop.update();
         stateMachine.setState(stop);
         stateMachine.setExtension(true);
      }
      if ( interrupted != null ) {
         if ( interrupted instanceof ActivityStateMachineSchema )
            // prevent shortened timeout
            ((ActivityStateMachineSchema<AdjacencyPair.Context>) interrupted).resetTimeout();
         interruptible = true;
         interrupted = null;
         interruptedPlan = null;
         interruptedPlugin = null;
      }
      stateMachine.setSpecificityMetadata(SPECIFICITY-0.2);
      setNeedsFocusResource(false);
      Plugin.getPlugin(plan.getType(), container).show();
   }
   
   private void stop (Plan plan) {
      if ( current != null && !plan.isComplete() ) { // see startInterruption
         Utils.lnprint(System.out, "Returning to Session...");
         Logger.logEvent(Logger.Event.END);
         plan.setComplete(true);
      }
      started.remove(plan);
      history(); // before update
      unyield();
   }
   
   private void unyield () {
      current = null; // before menu for logging
      proxy.showMenu(null, false, true); // clear extension menu
      // force in case some other message caused plugin to become
      // visible, such as "accept" for Skype
      proxy.hidePlugin(true);
      discoAdjacencyPair.update();
      stateMachine.setExtension(false);
      stateMachine.setSpecificityMetadata(ActivitySchema.SPECIFICITY+0.2);
      setNeedsFocusResource(true);
   }
   
   public static final String TOPLEVEL = "What would you like to do together?";
   
   private static class Toplevel extends DiscoAdjacencyPair {
      
      private final UIMessageDispatcher dispatcher;

      private Toplevel (DiscoRT.Interaction interaction, UIMessageDispatcher dispatcher) {
         super(interaction);
         this.dispatcher = dispatcher;
      }
      
      @Override
      public void update () {
         Agent agent = (Agent) getInteraction().getSystem();
         update(agent.respond(getInteraction(), false, true) ? agent.getLastUtterance() : 
                  getInteraction().getFocusExhausted(true) == null ? 
                     new Say(getInteraction().getDisco(), false, TOPLEVEL) : null,
                getInteraction().getExternal().generate(getInteraction()));
      }
      
      // following two overrides to implement logging
      
      @Override
      protected void update (Utterance utterance, List<Agenda.Plugin.Item> menu) {
         if ( utterance instanceof Ask.Should ) {
            Task should = ((Ask.Should) utterance).getNestedGoal();
            if ( should != null ) {
               Logger.Activity loggerName = Plugin.getLoggerName(should.getType()); 
               if ( loggerName != null ) Logger.logActivity(loggerName, Logger.Event.PROPOSED);
            }
         }
         super.update(utterance, menu);
      }
      
      @Override
      public void nextState (Utterance utterance) {
         if ( utterance instanceof Respond ) {
            Propose propose = ((Respond) utterance).getProposal();
            if ( propose instanceof Propose.Should ) {
               Task should = ((Propose.Should) propose).getGoal();
               if ( should != null ) {
                  Logger.Activity loggerName = Plugin.getLoggerName(should.getType()); 
                  if ( loggerName != null ) { 
                     Logger.Event event = utterance instanceof Accept ? Logger.Event.ACCEPTED : Logger.Event.REJECTED;
                     Logger.logActivity(loggerName, event); 
                     if ( loggerName == Logger.Activity.SKYPE && event == Logger.Event.REJECTED ) {
                        dispatcher.send(Message.builder(SkypeInterruptHandler.SKYPE_REJECTED_MESSAGE).build());
                     }
                  }
               }
            }
         }
      }
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
                        Agenda.newItem(new Propose.Stop(getInteraction().getDisco(), true, plan.getGoal()), null)));
      }
      
      @Override
      public AdjacencyPair nextState (String text) {
         super.nextState(text);
         Logger.Activity loggerName = Plugin.getLoggerName(plan.getGoal().getType()); 
         if ( loggerName != null ) Logger.logActivity(loggerName, Logger.Event.STOPPED);
         Schema schema = started.get(plan);
         if ( schema != null ) schema.stop();
         stop(plan);
         return new ResumeAdjacencyPairWrapper(discoAdjacencyPair); // one shot
      }
   }

   private static class ResumeAdjacencyPairWrapper extends AdjacencyPairWrapper<AdjacencyPair.Context> {
      
      public ResumeAdjacencyPairWrapper (AdjacencyPair inner) {
         super(inner);
      }
   
      @Override
      public String getMessage () {
         String text = inner.getMessage();
         return text == null ? "Ok." : ("Ok. Now. " + text);
      }
   }
}
