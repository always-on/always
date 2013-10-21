package edu.wpi.always.checkers;

public interface CheckersUIListener {

   public void humanPlayed ();
   public void humanCommentTimeOut();
   public void agentPlayDelayOver();
   public void nextState();
   public void agentPlayingGazeDelayOver ();

}
