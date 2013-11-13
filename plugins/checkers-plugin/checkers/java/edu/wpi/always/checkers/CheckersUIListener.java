package edu.wpi.always.checkers;

public interface CheckersUIListener {

   void humanCommentTimeOut();
   void agentPlayDelayOver();
   void nextState();
   void receivedHumanMove();
   void shouldHaveJumped();
   void agentPlayingGazeDelayOver();
   void humanTouchedAgentStuff (int howManyTimes);
   //   void receivedNewState();

}
