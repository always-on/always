package edu.wpi.always.rummy;

import com.google.gson.JsonObject;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.*;
import edu.wpi.always.cm.ProposalBuilder.FocusRequirement;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.schemas.ActivitySchema;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuBehavior;
import edu.wpi.disco.rt.util.TimeStampedValue;
import org.joda.time.DateTime;
import java.awt.Point;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RummyClient implements ClientPlugin {

   private static final String MSG_AVAILABLE_ACTION = "rummy.available_action";
   private static final String MSG_MOVE_HAPPENED = "rummy.move_happened";
   private static final String MSG_STATE_CHANGED = "rummy.state_changed";
   private final UIMessageDispatcher dispatcher;
   private final ConcurrentLinkedQueue<Message> inbox = new ConcurrentLinkedQueue<Message>();
   private TimeStampedValue<String> availableMove = null;
   private TimeStampedValue<String> userMove = null;
   private BehaviorBuilder lastMoveProposal;
   private DateTime myLastMoveTime;
   Boolean userWon = null;
   private boolean reactedToFinishedGameAlready = false;
   private int agentCardsNum = 10;
   private int userCardsNum = 10;

   public RummyClient (UIMessageDispatcher dispatcher) {
      this.dispatcher = dispatcher;
      registerHandlerFor(MSG_AVAILABLE_ACTION);
      registerHandlerFor(MSG_MOVE_HAPPENED);
      registerHandlerFor(MSG_STATE_CHANGED);
   }

   private void registerHandlerFor (String messageType) {
      final String t = messageType;
      dispatcher.registerReceiveHandler(t, new MessageHandler() {

         @Override
         public void handleMessage (JsonObject body) {
            receivedMessage(new Message(t, body));
         }
      });
   }

   protected void receivedMessage (Message message) {
      inbox.add(message);
   }

   @Override
   public void initInteraction () {
      Message params = Message.builder("params").add("first_move", "agent")
            .build();
      Message m = Message.builder("start_plugin").add("name", "rummy")
            .add(params).build();
      sendToEngine(m);
   }

   boolean gameOver () {
      return userWon != null;
   }

   @Override
   public BehaviorBuilder updateInteraction (boolean lastProposalIsDone) {
      processInbox();
      ProposalBuilder builder = newProposal();
      BehaviorMetadataBuilder metadata = new BehaviorMetadataBuilder();
      metadata.timeRemaining(agentCardsNum + userCardsNum);
      metadata.specificity(ActivitySchema.SPECIFICITY);
      builder.setMetadata(metadata);
      // don't want to mistake lastPropsalIsDone with one about a *move*,
      // hence the check for lastMoveProposal not being null
      if ( gameOver() && lastMoveProposal == null ) {
         if ( !lastProposalIsDone && !reactedToFinishedGameAlready ) {
            if ( userWon ) {
               builder.say("You won. Oh, well.");
            } else {
               builder.say("Yay! I won!");
            }
            builder.metadataBuilder().timeRemaining(0);
         } else {
            reactedToFinishedGameAlready = true;
         }
         return builder;
      }
      if ( lastMoveProposal != null && lastProposalIsDone )
         myLastMoveTime = DateTime.now();
      if ( lastMoveProposal != null && !lastProposalIsDone ) {
         return lastMoveProposal;
      } else if ( availableMove != null
         && availableMove.getValue().length() > 0 ) {
         PluginSpecificBehavior move = new PluginSpecificBehavior(this,
               // for printing only, action name not used in doAction below
               availableMove.getValue() + "@" + availableMove.getTimeStamp(),
               AgentResources.HAND);
         GazeBehavior gazeAtCard = new GazeBehavior(new Point(-22, 0));
         FaceTrackBehavior lookBackAtFace = new FaceTrackBehavior();
         String toSay = "";
         if ( isMeld(availableMove.getValue()) ) {
            toSay = "Now, I am going to do this meld, $ and done!";         
         } else if ( isDraw(availableMove.getValue()) ) {
             toSay = "Okay, I have to draw a card. <GAZE horizontal=\"-2\" vertical=\"-1\"/> $ The Card is drawn, and let me see what I can do with it! <GAZE horizontal=\"0\" vertical=\"0\"/>";
//             toSay = "Okay, I have to draw a card. <GAZE dir=\"AWAY\"/> $ The Card is drawn, and let me see what I can do with it! <GAZE dir=\"TOWARD\"/>";
            //toSay = "Okay, I have to draw a card. $ The Card is drawn, and let me see what I can do with it!";
         } else if ( isDiscard(availableMove.getValue()) ) {
            toSay = "I am done, so I'll discard this one, $ and now it's your turn.";
         }
         SyncSayBuilder b = new SyncSayBuilder(toSay, move,
               // following behaviors are unconstrained (live at start)
               MenuBehavior.EMPTY, // for menu extension if any 
               new FocusRequestBehavior());  
         b.setMetaData(metadata);
         lastMoveProposal = b;
         availableMove = null;
         return b;
      } else {
         lastMoveProposal = null;
         if ( userMadeAMeldAfterMyLastMove() ) {
            builder = newProposal().say("Good one!");
         }
      }
      return builder;
   }

   private boolean userMadeAMeldAfterMyLastMove () {
      return userMove != null
         && userMove.getTimeStamp().isAfter(myLastMoveTime)
         && isMeld(userMove.getValue());
   }

   private boolean isDiscard (String value) {
      return value.equals("discard");
   }

   private boolean isDraw (String value) {
      return value.equals("draw");
   }

   private boolean isMeld (String value) {
      return value.equals("meld");
   }

   private ProposalBuilder newProposal () {
      return new ProposalBuilder(this, FocusRequirement.Required); 
   }

   private void processInbox () {
      while (!inbox.isEmpty()) {
         Message m = inbox.poll();
         if ( m.getType().equals(MSG_AVAILABLE_ACTION) ) {
            String action = m.getBody().get("action").getAsString();
            availableMove = new TimeStampedValue<String>(action);
         } else if ( m.getType().equals(MSG_MOVE_HAPPENED) ) {
            if ( m.getBody().get("player").getAsString().equals("user") ) {
               String move = m.getBody().get("move").getAsString();
               userMove = new TimeStampedValue<String>(move);
            }
         } else if ( m.getType().equals(MSG_STATE_CHANGED) ) {
            String newState = m.getBody().get("state").getAsString();
            if ( newState.equals("user_won") ) {
               userWon = true;
            } else if ( newState.equals("agent_won") ) {
               userWon = false;
            }
            agentCardsNum = m.getBody().get("agent_cards").getAsInt();
            userCardsNum = m.getBody().get("user_cards").getAsInt();
         }
      }
   }

   @Override
   public void endInteraction () {
      Message m = Message.builder("stop_plugin").add("name", "rummy").build();
      sendToEngine(m);
   }

   @Override
   public void doAction (String actionName) { // ignoring actionName
      Message m = Message.builder("rummy.best_move").build();
      sendToEngine(m);
   }

   private void sendToEngine (Message m) {
      dispatcher.send(m);
   }
}
