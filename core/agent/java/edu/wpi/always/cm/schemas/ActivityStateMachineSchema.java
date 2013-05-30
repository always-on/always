package edu.wpi.always.cm.schemas;

import edu.wpi.always.client.*;
import edu.wpi.always.user.calendar.Calendar;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

public abstract class ActivityStateMachineSchema extends ActivitySchema {

   protected final MenuTurnStateMachine stateMachine;

   public ActivityStateMachineSchema (AdjacencyPair initial, 
         BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor) {
      super(behaviorReceiver, behaviorHistory);
      stateMachine = new MenuTurnStateMachine(behaviorHistory, resourceMonitor,
            menuPerceptor, new RepeatMenuTimeoutHandler());
      stateMachine.setSpecificityMetadata(SPECIFICITY);
      stateMachine.setAdjacencyPair(initial);
   }

   @Override
   public void run () {
      propose(stateMachine);
   }
}
