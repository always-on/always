package edu.wpi.always.srummy;

public interface SrummyUIListener {

   void humanCommentTimeOut();
   void agentPlayDelayOver();
   void nextState();
   void receivedHumanMove ();
   void receivedAgentMoveOptions ();
   void receivedNewState ();
   void agentPlayingGazeDelayOver ();

}
