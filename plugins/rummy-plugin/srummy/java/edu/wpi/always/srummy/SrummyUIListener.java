package edu.wpi.always.srummy;

public interface SrummyUIListener {

   void humanCommentTimeOut();
   void agentDrawDelayOver();
   void agentDiscardDelayOver();
   void agentPlayDelayOver();
   void nextState();
   void receivedHumanMove ();
   void receivedAgentMoveOptions (String chosenMoveType);
   void receivedNewState ();
   void agentPlayingGazeDelayOver ();
   void waitingForAgentDrawOptionsOver ();
}
