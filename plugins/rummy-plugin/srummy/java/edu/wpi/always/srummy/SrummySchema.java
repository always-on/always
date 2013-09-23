package edu.wpi.always.srummy;

import java.awt.Point;
import java.util.*;
import com.google.common.collect.Lists;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.primitives.GazeBehavior;
import edu.wpi.always.cm.schemas.ActivityStateMachineSchema;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;

public class SrummySchema extends ActivityStateMachineSchema {

   public SrummySchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Keyboard keyboard, SrummyUI SrummyUI,
         UIMessageDispatcher dispatcher, PlaceManager placeManager,
         PeopleManager peopleManager) {
      super(new StartGamingSequence(new SrummyStateContext(keyboard, SrummyUI, dispatcher,
            placeManager, peopleManager)), behaviorReceiver, behaviorHistory,
            resourceMonitor, menuPerceptor);
   }

   private final static Point 
   board = GazeRealizer.translateAgentTurn(-2, -1),
   thinking = GazeRealizer.translateAgentTurn(-1, 1),
   user = GazeRealizer.translateAgentTurn(0, 0);

   private String randomStmnt = "";
   private final List<String> yourTurnStatements = 
         Lists.newArrayList("your turn", "go ahead", "now you");

   @Override
   public void run () {

      super.run();

      if(SrummyClient.gazeDirection.equals("sayandgaze")){
         propose(new SyncSayBuilder(
               "$ "+StartGamingSequence.getCurrentAgentComment()+" $",
               new GazeBehavior(user))
         .build());  
      }
      if(SrummyClient.gazeDirection.equals("sayandgazegameover")){
         propose(new SyncSayBuilder(
               "$ "+"Game over. "+
                     StartGamingSequence.getCurrentAgentComment()+" $",
                     new GazeBehavior(user))
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
               new GazeBehavior(board))
         .build());
         SrummyClient.limboEnteredOnce = true;
      }
      if(SrummyClient.gazeDirection.equals("replay")){
         propose(new SyncSayBuilder(
               "$ "+"Now do you want to play again?")
         .build());
         SrummyClient.gazeDirection = "";
      }
      if(SrummyClient.gazeDirection.equals("board")){
         propose(new GazeBehavior(board));
      }
      //>> these to be commented out for face track test
      if(SrummyClient.gazeDirection.equals("user")){
         propose(new GazeBehavior(user));
      }
      if(SrummyClient.gazeDirection.equals("useronce")){
         propose(new GazeBehavior(user));
         SrummyClient.gazeDirection = "";
      }
      //to here <<
      if(SrummyClient.gazeDirection.equals("thinking")){
         propose(new GazeBehavior(thinking));
      }

      if ( SrummyClient.nod ) {
         // TODO fill me in later (after human plays)
         SrummyClient.nod = false;
      }

   }
}
