package edu.wpi.always.enroll.schema;

import edu.wpi.always.client.Keyboard;
import edu.wpi.always.client.KeyboardMessageHandler;
import edu.wpi.always.client.UIMessageDispatcher;
import edu.wpi.disco.rt.menu.*;
import edu.wpi.always.cm.ProposalBuilder;
import edu.wpi.always.cm.schemas.ActivitySchema;
import edu.wpi.always.enroll.EnrollUI;
import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.Behavior;
import edu.wpi.disco.rt.behavior.BehaviorHistory;
import edu.wpi.disco.rt.behavior.BehaviorMetadata;
import edu.wpi.disco.rt.behavior.BehaviorMetadataBuilder;
import edu.wpi.disco.rt.behavior.BehaviorProposalReceiver;

public class EnrollSchema extends ActivitySchema{

   private final MenuTurnStateMachine stateMachine;

   public EnrollSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Keyboard keyboard, EnrollUI enrollUI, 
         UIMessageDispatcher dispatcher, UserModel model, PlaceManager placeManager, 
         PeopleManager peopleManager) {
      super(behaviorReceiver, behaviorHistory);
      stateMachine = new MenuTurnStateMachine(behaviorHistory, resourceMonitor,
            menuPerceptor, new RepeatMenuTimeoutHandler());
      stateMachine.setSpecificityMetadata(.9);
      EnrollStateContext enrollContext = new EnrollStateContext(
            keyboard, enrollUI, dispatcher, model, model.getPlaceManager(),
            model.getPeopleManager());
      AdjacencyPair initial;
      try {
         model.getUserName();
         initial = new InitialEnroll(enrollContext);
      } catch (IllegalStateException e) {
         initial = new UserModelAdjacencyPair(enrollContext);
      }
      stateMachine.setAdjacencyPair(initial);
   }

   @Override
   public void run () {
      propose(stateMachine);

      if(KeyboardMessageHandler.isOverflow){
         KeyboardMessageHandler.isOverflow = false;
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

}
