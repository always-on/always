package edu.wpi.always.checkers;

import edu.wpi.always.*;
import edu.wpi.always.checkers.logic.CheckersGameState;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.primitives.GazeBehavior;
import edu.wpi.always.cm.schemas.ActivityStateMachineSchema;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;
import edu.wpi.sgf.logic.GameLogicState.Won;

public class CheckersSchema extends ActivityStateMachineSchema<CheckersStateContext> {

   public final static Logger.Activity LOGGER_NAME = Logger.Activity.CHECKERS;
   
   public static void log (Won won) {
      Logger.logActivity(LOGGER_NAME, Logger.Event.WON, won, CheckersGameState.turns, CheckersGameState.jumps);
   }
   
   public CheckersSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Keyboard keyboard, CheckersClient client,
         UIMessageDispatcher dispatcher, PlaceManager placeManager,
         PeopleManager peopleManager, Always always) {
      super(new StartGamingSequence(new CheckersStateContext(keyboard, client, dispatcher,
            placeManager, peopleManager)), behaviorReceiver, behaviorHistory,
            resourceMonitor, menuPerceptor, LOGGER_NAME);
      always.getUserModel().setProperty(CheckersPlugin.PERFORMED, true);
   }

   @Override
   public void dispose () {
      if ( !CheckersClient.gameOver ) log(Won.NEITHER);
      super.dispose();
   }
   
   @Override
   public void runActivity () {

      super.runActivity();
      
      if(CheckersClient.gazeDirection.equals("sayandgaze")){
         propose(new SyncSayBuilder(
               "$ "+StartGamingSequence.getCurrentAgentComment()+" $",
               GazeBehavior.USER)
         .build());
      }
      if(CheckersClient.gazeDirection.equals("sayandgazeresp")){
         propose(new SyncSayBuilder(
               "$ "+StartGamingSequence.getCurrentAgentResponse()+" $",
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
