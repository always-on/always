package edu.wpi.always.enroll.schema;

import edu.wpi.always.client.*;
import edu.wpi.always.cm.ProposalBuilder;
import edu.wpi.always.cm.schemas.ActivityStateMachineSchema;
import edu.wpi.always.enroll.EnrollUI;
import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;

public class EnrollSchema extends ActivityStateMachineSchema {

   private final Keyboard keyboard;

   public EnrollSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Keyboard keyboard, EnrollUI enrollUI, 
         UIMessageDispatcher dispatcher, UserModel model, PlaceManager placeManager, 
         PeopleManager peopleManager) {
      super(model.getUserName() == null ? 
         new UserModelAdjacencyPair(new EnrollStateContext(
            keyboard, enrollUI, dispatcher, model, placeManager, peopleManager)) :
         new InitialEnroll(new EnrollStateContext(
            keyboard, enrollUI, dispatcher, model, placeManager, peopleManager)),
            behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor);
      this.keyboard = keyboard;
   }

   @Override
   public void run () {
      propose(stateMachine);
      if ( keyboard.isOverflow() ) {
         keyboard.setOverflow(false);
         BehaviorMetadataBuilder metadata = new BehaviorMetadataBuilder();
         ProposalBuilder builder = new ProposalBuilder(); 
         metadata.specificity(0.9);
         builder.setMetadata(metadata);
         builder.say("Too many characters");
         Behavior b = builder.build();
         BehaviorMetadata m = builder.getMetadata();
         propose(b, m);
      }
   }

   @Override
   public void setNeedsFocusResource(boolean focus) {
      // TODO Auto-generated method stub
   }

}
