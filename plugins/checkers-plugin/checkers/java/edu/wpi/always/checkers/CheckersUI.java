package edu.wpi.always.checkers;

import java.util.List;

public interface CheckersUI {

   public void resetGame();
   public void prepareAgentMove();
   public void makeBoardPlayable();
   public void prepareAgentCommentForAMoveBy(int player);
   public void makeBoardUnplayable();
   public String getCurrentAgentComment();
   public void triggerAgentPlayTimer();
   public void cancelHumanCommentingTimer();
   public void triggerHumanCommentingTimer();
   public void updatePlugin(CheckersUIListener listener);
   public void playAgentMove (CheckersUIListener listener);
   public List<String> getCurrentHumanCommentOptionsForAMoveBy(int player);
   public void startPluginForTheFirstTime(CheckersUIListener listener);
   public void triggerNextStateTimer(CheckersUIListener listener);

}
