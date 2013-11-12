package edu.wpi.always.checkers;

import edu.wpi.always.client.*;
import edu.wpi.always.cm.primitives.GazeBehavior;
import edu.wpi.always.cm.schemas.ActivityStateMachineSchema;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;

public class CheckersSchema extends ActivityStateMachineSchema {

   public CheckersSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Keyboard keyboard, CheckersUI CheckersUI,
         UIMessageDispatcher dispatcher, PlaceManager placeManager,
         PeopleManager peopleManager) {
      super(new StartGamingSequence(new CheckersStateContext(keyboard, CheckersUI, dispatcher,
            placeManager, peopleManager)), behaviorReceiver, behaviorHistory,
            resourceMonitor, menuPerceptor);
   }

   @Override
   public void run () {

      super.run();

      if(CheckersClient.gazeDirection.equals("sayandgaze")){
         propose(new SyncSayBuilder(
               "$ "+StartGamingSequence.getCurrentAgentComment()+" $",
               GazeBehavior.USER)
         .build());
      }
      if(CheckersClient.gazeDirection.equals("sayandgazegameover")){
         propose(new SyncSayBuilder(
               "$ "+"Game over. "+
                     StartGamingSequence.getCurrentAgentComment()+" $",
                     GazeBehavior.USER)
         .build());
         CheckersClient.gazeDirection = "";
      }
      if(CheckersClient.gazeDirection.equals("replay")){
         propose(new SyncSayBuilder(
               "$ "+"Now do you want to play again?")
         .build());
         CheckersClient.gazeDirection = "";
      }
      if(CheckersClient.gazeDirection.equals("board")){
         propose(GazeBehavior.PLUGIN);
      }
      if(CheckersClient.gazeDirection.equals("thinking")){
         propose(GazeBehavior.THINKING);
      }

      if ( CheckersClient.nod ) {
         // TODO fill me in later (after human plays)
         CheckersClient.nod = false;
      }

   }
}
