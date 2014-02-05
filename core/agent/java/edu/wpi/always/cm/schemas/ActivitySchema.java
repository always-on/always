package edu.wpi.always.cm.schemas;

import edu.wpi.always.Plugin;
import edu.wpi.always.client.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuTurnStateMachine;
import edu.wpi.disco.rt.schema.SchemaBase;

/**
 * Base schema for activities.  Each activity should have exactly one such schema. 
 */
public abstract class ActivitySchema extends SchemaBase {

   public static final double SPECIFICITY = 0.7;
   
   protected ActivitySchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory) {
      super(behaviorReceiver, behaviorHistory);
      setNeedsFocusResource(true); // default value
   }

   private Plugin plugin;
   
   public void setPlugin (Plugin plugin) { this.plugin = plugin; }
   
   @Override
   public void dispose () {
      super.dispose();
      plugin.hide();
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
   
   private boolean selfStop;
   
   public boolean isSelfStop () { return selfStop; }
   
   public void setSelfStop (boolean selfStop) {
      this.selfStop = selfStop;
   }
}
