package wpi.edu.always.ttt;

import java.awt.Point;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.SyncSayBuilder;
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
         left = GazeRealizer.translateAgentTurn(-2, -1),
         upLeft = GazeRealizer.translateAgentTurn(-1, 1),
         back = GazeRealizer.translateAgentTurn(0, 0);
 
   @Override
   public void run () {

      super.run();

      // since MenuTurnStateMachine cannot accommodate
      // saying this inside any state (in a proper way)
      if ( TTTClient.sayAgentCommentOnHumanMove ) {
         propose(new SyncSayBuilder(
               "<GAZE horizontal=\"0\" vertical=\"0\"/>$ "
                  + WhoPlaysFirst.getCurrentAgentComment()
                  + " <GAZE horizontal=\"-1\" vertical=\"1\"/>$")
               .build());
         TTTClient.sayAgentCommentOnHumanMove = false;
      }
      if ( TTTClient.gazeLeft ) {
         propose(new GazeBehavior(left));
         TTTClient.gazeLeft = false;
      }
      if ( TTTClient.gazeBack ) {
         propose(new GazeBehavior(back));
         TTTClient.gazeBack = false;
      }
      if ( TTTClient.gazeUpLeft ) {
         propose(new GazeBehavior(upLeft));
         TTTClient.gazeUpLeft = false;
      }
      if ( TTTClient.nod ) {
         // TODO fill me in later
         TTTClient.nod = false;
      }

   }
}
