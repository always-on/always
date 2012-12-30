package edu.wpi.always.story;

import edu.wpi.always.client.*;
import edu.wpi.always.cm.*;
import edu.wpi.always.cm.dialog.*;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.schema.SchemaBase;

public class StorySchema extends SchemaBase {

   private final MenuTurnStateMachine stateMachine;

   public StorySchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Keyboard keyboard,
         SpeechPerceptor speechPerceptor, UIMessageDispatcher dispatcher,
         StoryManager storyManager, PeopleManager peopleManager) {
      super(behaviorReceiver, behaviorHistory);
      stateMachine = new MenuTurnStateMachine(behaviorHistory, resourceMonitor,
            menuPerceptor, new RepeatMenuTimeoutHandler());
      stateMachine.setSpecificityMetadata(.1);
      stateMachine
            .setAdjacencyPair(new StoryAdjacencyPairs.StoryStartAdjacencyPair(
                  new StoryStateContext(dispatcher, storyManager, keyboard,
                        peopleManager)));
      setNeedsFocusResouce();
   }

   boolean hasDisplayed = false;

   @Override
   public void run () {
      if ( stateMachine.equals(Behavior.NULL) )
         proposeNothing();
      propose(stateMachine);
      // else
      // propose(stateMachine.menuBehavior, stateMachine.getMetadata());
      // proposeNothing();
   }
}