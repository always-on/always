package edu.wpi.always.checkers;

public interface CheckersUIListener {

   void humanCommentTimeOut();
   void agentPlayDelayOver();
   void nextState();
   void receivedHumanMove();
   void receivedAgentMoveOptions(String chosenMoveType);
   void agentPlayingGazeDelayOver();
   //   void receivedNewState();

}
