package edu.wpi.always.srummy;

import java.util.*;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.primitives.GazeBehavior;
import edu.wpi.always.cm.schemas.ActivityStateMachineSchema;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;

public class SrummySchema extends ActivityStateMachineSchema {

   private String randomStmnt = "";
   private boolean saidFirstYourTurn = false;
   private List<String> yourTurnStatements = 
         new ArrayList<String>();
   
   public SrummySchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Keyboard keyboard, SrummyUI SrummyUI,
         UIMessageDispatcher dispatcher, PlaceManager placeManager,
         PeopleManager peopleManager) {
      super(new StartGamingSequence(new SrummyStateContext(keyboard, SrummyUI, dispatcher,
            placeManager, peopleManager)), behaviorReceiver, behaviorHistory,
            resourceMonitor, menuPerceptor);
      
    yourTurnStatements.add("your turn");
    yourTurnStatements.add("go ahead");

   }
  
   @Override
   public void run () {

      super.run();

      if(SrummyClient.gazeDirection.equals("sayandgaze")){
         propose(new SyncSayBuilder(
               "$ "+StartGamingSequence.getCurrentAgentComment()+" $",
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
