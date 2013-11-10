package edu.wpi.always.checkers;

import java.awt.Point;
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

   private final static Point 
   board = GazeRealizer.translateAgentTurn(-2, -1),
   thinking = GazeRealizer.translateAgentTurn(-1, 1),
   user = GazeRealizer.translateAgentTurn(0, 0);

   @Override
   public void run () {

      super.run();

      if(CheckersClient.gazeDirection.equals("sayandgaze")){
         propose(new SyncSayBuilder(
               "$ "+StartGamingSequence.getCurrentAgentComment()+" $",
               new GazeBehavior(user))
         .build());
      }
      if(CheckersClient.gazeDirection.equals("sayandgazegameover")){
         propose(new SyncSayBuilder(
               "$ "+"Game over. "+
                     StartGamingSequence.getCurrentAgentComment()+" $",
                     new GazeBehavior(user))
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
         propose(new GazeBehavior(board));
      }
      //>> these to be commented out for face track test
      if(CheckersClient.gazeDirection.equals("user")){
         propose(new GazeBehavior(user));
      }
      if(CheckersClient.gazeDirection.equals("useronce")){
         propose(new GazeBehavior(user));
         CheckersClient.gazeDirection = "";
      }
      //to here <<
      if(CheckersClient.gazeDirection.equals("thinking")){
         propose(new GazeBehavior(thinking));
      }

      if ( CheckersClient.nod ) {
         // TODO fill me in later (after human plays)
         CheckersClient.nod = false;
      }

   }
}
