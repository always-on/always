package edu.wpi.always.story.schema;

import edu.wpi.always.client.*;
import edu.wpi.always.cm.perceptors.SpeechPerceptor;
import edu.wpi.always.cm.schemas.ActivityStateMachineSchema;
import edu.wpi.always.story.StoryManager;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

public class StorySchema extends ActivityStateMachineSchema<StoryStateContext> {

   public StorySchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Keyboard keyboard,
         SpeechPerceptor speechPerceptor, UIMessageDispatcher dispatcher,
         StoryManager storyManager, PeopleManager peopleManager) {
      super(new StoryAdjacencyPairs.StoryStartAdjacencyPair(
                  new StoryStateContext(dispatcher, storyManager, keyboard, peopleManager)),
            behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor);
   }

   @Override
   public void run () {
      if ( stateMachine.equals(Behavior.NULL) )
         proposeNothing();
      propose(stateMachine);
   }
}