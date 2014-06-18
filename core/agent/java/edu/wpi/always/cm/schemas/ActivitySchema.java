package edu.wpi.always.cm.schemas;

import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import edu.wpi.always.*;
import edu.wpi.always.client.*;
import edu.wpi.cetask.Task;
import edu.wpi.disco.*;
import edu.wpi.disco.rt.DiscoRT;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuTurnStateMachine;
import edu.wpi.disco.rt.schema.*;

/**
 * Base schema for activities.  Each activity should have exactly one such schema. 
 */
public abstract class ActivitySchema extends SchemaBase {

   public static final double SPECIFICITY = 0.5;
   
   private final Logger.Activity loggerName;
   
   protected ActivitySchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, Logger.Activity loggerName) {
      super(behaviorReceiver, behaviorHistory);
      this.loggerName = loggerName;
      setNeedsFocusResource(true); // default value
   }

   public Logger.Activity getLoggerName () { return loggerName; }
   
   private Plugin plugin;
   
   public void setPlugin (Plugin plugin) { this.plugin = plugin; }
   
   @Override
   public final void run () {
      if ( EngagementSchema.EXIT ) {
         stop();
         proposeNothing();
      } else runActivity();
   }

   protected abstract void runActivity ();
   
   @Override
   public void dispose () {
      super.dispose();
      if ( plugin != null ) plugin.hide();
   }
   
   /**
    * Implementation must provide support for automatically adding focus resource.
    * 
    * @see ActivityStateMachineSchema#setNeedsFocusResource(boolean)
    */
   public abstract void setNeedsFocusResource (boolean focus);
   
   protected void propose (PrimitiveBehavior behavior) {
      propose(behavior, SPECIFICITY);
   }
   
   protected void propose (Behavior behavior) {
      propose(behavior, SPECIFICITY);
   }

   private InconsistentOntologyException e;
   
   public InconsistentOntologyException getInconsistentOntologyException () { return e; } 
    
   public void setInconsistentOntologyException (InconsistentOntologyException e) { this.e = e; } 
   
   private boolean selfStop;
   
   public boolean isSelfStop () { return selfStop; }
   
   public void setSelfStop (boolean selfStop) {
      this.selfStop = selfStop;
   }
   
   protected boolean interruptible = true; 
   
   public boolean isInterruptible () { return interruptible; }
}
