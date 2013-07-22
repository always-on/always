package wpi.edu.always.ttt;

import edu.wpi.always.client.*;
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
         UIMessageDispatcher dispatcher,PlaceManager placeManager, PeopleManager peopleManager) {
      super(new WannaPlay(new TTTStateContext(
            keyboard, tttUI, dispatcher, placeManager, peopleManager)),
            behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor);
   }

   // always adds focus
   @Override
   public void setNeedsFocusResource (boolean focus) {} 

}

