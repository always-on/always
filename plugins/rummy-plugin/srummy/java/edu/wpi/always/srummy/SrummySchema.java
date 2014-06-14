package edu.wpi.always.srummy;

import java.util.*;
import edu.wpi.always.*;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.primitives.GazeBehavior;
import edu.wpi.always.cm.schemas.ActivityStateMachineSchema;
import edu.wpi.always.srummy.game.SrummyGameState;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;
import edu.wpi.sgf.logic.GameLogicState.Won;

public class SrummySchema extends ActivityStateMachineSchema<SrummyStateContext> {

   private String randomStmnt = "";
   private boolean saidFirstYourTurn = false;
   private List<String> yourTurnStatements = 
         new ArrayList<String>();
   
   public final static Logger.Activity LOGGER_NAME = Logger.Activity.RUMMY;
     
   public static void log (Won won) {
      SrummyGameState state = client.getGameState();
      Logger.logActivity(LOGGER_NAME, won, SrummyClient.getFirst(), 
            state.getTurns(), state.getHumanMelds().size(), state.getAgentMelds().size());
   }
   
   private static SrummyClient client;
   
   public SrummySchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Keyboard keyboard, SrummyClient client,
         UIMessageDispatcher dispatcher, PlaceManager placeManager,
         PeopleManager peopleManager, Always always) {
      super(new SrummyInitial(new SrummyStateContext(keyboard, client, dispatcher,
            placeManager, peopleManager)), behaviorReceiver, behaviorHistory,
            resourceMonitor, menuPerceptor, LOGGER_NAME);
      SrummySchema.client = client;
      always.getUserModel().setProperty(SrummyPlugin.PERFORMED, true);
      yourTurnStatements.add("your turn");
      yourTurnStatements.add("go ahead");
   }

   @Override
   public void dispose () {
      if ( !client.getGameState().gameIsOver() ) log(Won.NEITHER);
      super.dispose();
   }
   
   @Override
   public void runActivity () {

      super.runActivity();
      
      if(SrummyClient.gazeDirection.equals("sayandgaze")){
         propose(new SyncSayBuilder(
               "$ "+StartGamingSequence.getCurrentAgentComment()+" $",
               GazeBehavior.USER)
         .build());  
      }
      if(SrummyClient.gazeDirection.equals("sayandgazeresp")){
         propose(new SyncSayBuilder(
               "$ "+StartGamingSequence.getCurrentAgentResponse()+" $",
               GazeBehavior.USER)
         .build());
      }
      if(SrummyClient.gazeDirection.equals("sayandgazegameover")){
         propose(new SyncSayBuilder(
               "$ "+"Game over. "+
                     StartGamingSequence.getCurrentAgentComment()+" $",
                     GazeBehavior.USER)
         .build());
         SrummyClient.gazeDirection = "";
      }
      if(SrummyClient.gazeDirection.equals("sayandgazelimbo")){
         if(!SrummyClient.limboEnteredOnce)
            randomStmnt = yourTurnStatements.get(
                  new Random().nextInt(yourTurnStatements.size()));
         else 
            randomStmnt = "";
         propose(new SyncSayBuilder("$ "+randomStmnt+" $",
               GazeBehavior.PLUGIN)
         .build());
         SrummyClient.limboEnteredOnce = true;
         if(!saidFirstYourTurn){
            yourTurnStatements.add("now you");
            saidFirstYourTurn = true;
         }
      }
      if(SrummyClient.gazeDirection.equals("replay")){
         propose(new SyncSayBuilder(
               "$ "+"Now do you want to play again?")
         .build());
         SrummyClient.gazeDirection = "";
      }
      if(SrummyClient.gazeDirection.equals("board")){
         propose(GazeBehavior.PLUGIN);
      }
      if(SrummyClient.gazeDirection.equals("thinking")){
         propose(GazeBehavior.THINKING);
      }

      if ( SrummyClient.nod ) {
         // TODO fill me in later (after human plays)
         SrummyClient.nod = false;
      }
   }
 
}
