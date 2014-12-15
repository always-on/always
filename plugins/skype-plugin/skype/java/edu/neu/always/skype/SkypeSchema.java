package edu.neu.always.skype;

import edu.wpi.always.*;
import edu.wpi.always.cm.schemas.ActivityStateMachineSchema;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

public abstract class SkypeSchema extends ActivityStateMachineSchema<AdjacencyPair.Context> {
   
   public final static Logger.Activity LOGGER_NAME = Logger.Activity.SKYPE;
   public enum Direction { INCOMING, OUTGOING }
   
   protected static void log (Direction direction, String name) {
      Logger.logActivity(LOGGER_NAME, direction, name);
   }
   
   protected SkypeSchema (AdjacencyPair initial, BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Always always) {
      super(initial,
            behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor,
            LOGGER_NAME);
      always.getUserModel().setProperty(SkypePlugin.PERFORMED, true);
      setSelfStop(true);
   }
}
