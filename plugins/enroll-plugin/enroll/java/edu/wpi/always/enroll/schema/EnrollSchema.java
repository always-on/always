package edu.wpi.always.enroll.schema;

import edu.wpi.always.Logger;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.schemas.ActivityStateMachineKeyboardSchema;
import edu.wpi.always.enroll.EnrollUI;
import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;

public class EnrollSchema extends ActivityStateMachineKeyboardSchema<EnrollStateContext> {

   public EnrollSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Keyboard keyboard, EnrollUI enrollUI, 
         UIMessageDispatcher dispatcher, UserModel model, PlaceManager placeManager, 
         PeopleManager peopleManager) {
      super(model.getUserName().isEmpty() ? 
         new UserModelAdjacencyPair(new EnrollStateContext(
            keyboard, enrollUI, dispatcher, model, placeManager, peopleManager)) :
         new InitialEnroll(new EnrollStateContext(
            keyboard, enrollUI, dispatcher, model, placeManager, peopleManager)),
         behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, keyboard);
      setSelfStop(true);
   }
   
   enum Change { NEW, EDIT };
   
   public static void log (Change change, String name) {
      Logger.logActivity(Logger.Activity.ENROLL, change, name);
   }
}
