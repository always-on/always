package edu.wpi.always.ttt;

import java.awt.Point;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.primitives.GazeBehavior;
import edu.wpi.always.cm.schemas.ActivityStateMachineSchema;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;

public class TTTSchema extends ActivityStateMachineSchema {

   public TTTSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Keyboard keyboard, TTTUI tttUI,
         UIMessageDispatcher dispatcher, PlaceManager placeManager,
         PeopleManager peopleManager) {
      super(new WhoPlaysFirst(new TTTStateContext(keyboard, tttUI, dispatcher,
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

      if ( TTTClient.gazeAtBoard ) { 
         propose(new GazeBehavior(board));
         TTTClient.gazeAtBoard = false;
      }
      if ( TTTClient.gazeAtUser ) {
         propose(new GazeBehavior(user));
         TTTClient.gazeAtUser = false;
      }
      if ( TTTClient.gazeOnThinking ) {
         propose(new GazeBehavior(thinking));
         TTTClient.gazeOnThinking = false;
      }
      if ( TTTClient.nod ) {
         // TODO fill me in later (When human plays, nod?)
         TTTClient.nod = false;
      }

   }
}
