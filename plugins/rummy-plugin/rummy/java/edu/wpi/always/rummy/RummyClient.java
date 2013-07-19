package edu.wpi.always.rummy;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.*;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.schemas.ActivitySchema;
import edu.wpi.disco.rt.DiscoRT;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;
import edu.wpi.disco.rt.schema.Schema;
import edu.wpi.disco.rt.util.TimeStampedValue;
import org.joda.time.DateTime;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RummyClient implements ClientPlugin {

   private static final String MSG_AVAILABLE_ACTION = "rummy.available_action";
   private static final String MSG_ALL_AVAILABLE_MOVES = "rummy.available_moves";
   private static final String MSG_HUMAN_MOVE = "rummy.human_move";
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
   
   /**
    * For user turn reminder (in millis).
    */
   public static int TIMEOUT_DELAY = MenuTurnStateMachine.TIMEOUT_DELAY*3; 
   
   private long waitingForUserSince; // millis or zero if not waiting
   private boolean yourTurn;  // last proposal (not done)

   public RummyClient (UIMessageDispatcher dispatcher) {
      this.dispatcher = dispatcher;
      registerHandlerFor(MSG_AVAILABLE_ACTION);
      registerHandlerFor(MSG_MOVE_HAPPENED);
      registerHandlerFor(MSG_STATE_CHANGED);
      registerHandlerFor(MSG_ALL_AVAILABLE_MOVES);
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
   public BehaviorBuilder updateInteraction (boolean lastProposalIsDone, double focusMillis) {
      if ( lastProposalIsDone && yourTurn ) {
         yourTurn = false;
         waitingForUserSince = System.currentTimeMillis();
      }

      processInbox();
      
      // always propose at least an empty menu for extension
      ProposalBuilder builder = newProposal();
      builder.setNeedsFocusResource(true);
      BehaviorMetadataBuilder metadata = new BehaviorMetadataBuilder();
      metadata.timeRemaining(agentCardsNum + userCardsNum);
      metadata.specificity(ActivitySchema.SPECIFICITY);
      builder.setMetadata(metadata);
      // don't want to mistake lastProposalIsDone with one about a _move_,
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
         agentMove();
         return builder;
      }
      if ( lastMoveProposal != null && lastProposalIsDone )
         myLastMoveTime = DateTime.now();
      if ( lastMoveProposal != null && !lastProposalIsDone ) {
         agentMove();
         return lastMoveProposal;
      } else if ( availableMove != null && availableMove.getValue().length() > 0 ) {
         PluginSpecificBehavior move = new PluginSpecificBehavior(this,
               // for printing only, action name not used in doAction below
               availableMove.getValue() + "@" + availableMove.getTimeStamp(),
               AgentResources.HAND);
         String toSay = "";
         if ( isMeld(availableMove.getValue()) ) {
            toSay = "Now, I am going to do this meld, $ and done!";         
         } else if ( isDraw(availableMove.getValue()) ) {
             toSay = "Okay, I have to draw a card. <GAZE horizontal=\"-2\" vertical=\"-1\"/> $ The Card is drawn, and let me see what I can do with it! <GAZE horizontal=\"0\" vertical=\"0\"/>";
         } else if ( isDiscard(availableMove.getValue()) ) {
            toSay = "I am done, so I'll discard this one, $ and now it's your turn.";
         }
         SyncSayBuilder b = new SyncSayBuilder(toSay, move,
               // following behavior is unconstrained (live at start)
               // for menu extension if any 
               MenuBehavior.EMPTY);
         b.setMetaData(metadata);
         lastMoveProposal = b;
         availableMove = null;
         agentMove();
         return b;
      } else {
         lastMoveProposal = null;
         if ( userMadeAMeldAfterMyLastMove() ) {
            builder.say("Good one!");
            agentMove();
            return builder;
         }
      }

      if ( yourTurn  
           || (waitingForUserSince > 0 
               && ( (System.currentTimeMillis() - waitingForUserSince) > TIMEOUT_DELAY))
           || (focusMillis > DiscoRT.ARBITRATOR_INTERVAL*5) ) {
         builder.say("It's your turn");      
         yourTurn = true;
      } else {
         if ( waitingForUserSince == 0 ) waitingForUserSince = System.currentTimeMillis();
         builder.showMenu(Collections.<String>emptyList(), false);
      }
      //**** never returns just null anymore--always empty menu
      // see RummySchema for idle behavior (if builder returns Behavior.NULL)
      return builder;
   }

   private void agentMove () {
      yourTurn = false;
      waitingForUserSince = 0;
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
      return new ProposalBuilder(this); 
   }

   private void processInbox () {
      while (!inbox.isEmpty()) {
         Message m = inbox.poll();
         processInbox2(m);
         if ( m.getType().equals(MSG_AVAILABLE_ACTION) ) {
            String action = m.getBody().get("action").getAsString();
            availableMove = new TimeStampedValue<String>(action);
         } 
         if ( m.getType().equals(MSG_MOVE_HAPPENED) ) {
            if ( m.getBody().get("player").getAsString().equals("user") ) {
               String move = m.getBody().get("move").getAsString();
               userMove = new TimeStampedValue<String>(move);
            }
         }
         if ( m.getType().equals(MSG_STATE_CHANGED) ) {
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
   
   private void processInbox2(Message m2){
	   //while (!inbox.isEmpty()) {
		 //  Message m = inbox.poll();
		   if(m2.getType().equals(MSG_ALL_AVAILABLE_MOVES)){
			   
			   int discards = 0;
			   
			   
			   
			   JsonElement allMovesJson = m2.getBody().get("discard0");
			   //change to discardMove when moves code to srummy project
//			   List<Object> possibleDiscardMoves = new ArrayList<Object>(); 
			   
			   if(allMovesJson != null)
				   System.out.println(allMovesJson.getAsString());
			   
//			   while(allMovesJson.getAsJsonObject().has("discard"+(++discards))){
				   
//			   }
			   
		   }else if(m2.getType().equals(MSG_HUMAN_MOVE)){
			   
		   }
		   
		   
		   
	  // }
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
