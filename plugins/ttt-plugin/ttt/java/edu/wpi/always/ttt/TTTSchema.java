package edu.wpi.always.ttt;

import edu.wpi.always.*;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.primitives.GazeBehavior;
import edu.wpi.always.cm.schemas.ActivityStateMachineSchema;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;
import edu.wpi.sgf.logic.GameLogicState.Won;
import edu.wpi.sgf.logic.LegalMove.First;

public class TTTSchema extends ActivityStateMachineSchema<TTTStateContext> {
   
   public final static Logger.Activity LOGGER_NAME = Logger.Activity.TTT;
   
   public static void log (Won won) {
      Logger.logActivity(LOGGER_NAME, Logger.Event.WON, won, WhoPlaysFirst.USER ? First.USER : First.AGENT);
   }
   
   public TTTSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Keyboard keyboard, TTTClient client,
         UIMessageDispatcher dispatcher, PlaceManager placeManager,
         PeopleManager peopleManager, Always always) {
      super(new WhoPlaysFirst(new TTTStateContext(keyboard, client, dispatcher,
            placeManager, peopleManager)), behaviorReceiver, behaviorHistory,
            resourceMonitor, menuPerceptor, LOGGER_NAME);
      always.getUserModel().setProperty(TTTPlugin.PERFORMED, true);
   }
   
   @Override
   public void dispose () {
      if ( !TTTClient.gameOver ) log(Won.NEITHER);
      super.dispose();
   }
   
   @Override
   public void runActivity () {
      
      super.runActivity();

      if(TTTClient.gazeDirection.equals("sayandgaze")){
         propose(new SyncSayBuilder(
               "$ "+WhoPlaysFirst.getCurrentAgentComment()+" $",
               GazeBehavior.USER)
         .build());
      }
      if(TTTClient.gazeDirection.equals("sayandgazeresp")){
         propose(new SyncSayBuilder(
               "$ "+WhoPlaysFirst.getCurrentAgentResponse()+" $",
               GazeBehavior.USER)
         .build());
      }
      if(TTTClient.gazeDirection.equals("sayandgazegameover")){
         propose(new SyncSayBuilder(
               "$ "+"Game over. "+
                     WhoPlaysFirst.getCurrentAgentComment()+" $",
                     GazeBehavior.USER)
         .build());
         TTTClient.gazeDirection = "";
      }
      if(TTTClient.gazeDirection.equals("replay")){
         propose(new SyncSayBuilder(
               "$ "+"Now do you want to play again?")
         .build());
         TTTClient.gazeDirection = "";
      }
      if(TTTClient.gazeDirection.equals("board")){
         propose(GazeBehavior.PLUGIN);
      }
      if(TTTClient.gazeDirection.equals("thinking")){
         propose(GazeBehavior.THINKING);
      }

      if ( TTTClient.nod ) {
         // TODO fill me in later (after human plays)
         TTTClient.nod = false;
      }
   }
 
}
