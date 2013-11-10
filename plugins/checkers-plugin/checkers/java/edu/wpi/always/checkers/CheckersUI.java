package edu.wpi.always.checkers;

import java.util.List;
import edu.wpi.always.checkers.CheckersUIListener;

public interface CheckersUI {

   public void prepareAgentMove();
   public void triggerAgentPlayTimer();
   public void playAgentMove(CheckersUIListener listener);
   public void prepareAgentCommentForAMoveBy(int player);
   public String getCurrentAgentComment();
   public List<String> getCurrentHumanCommentOptionsForAMoveBy(int player);
   public void triggerHumanCommentingTimer();
   public void cancelHumanCommentingTimer();
   public void triggerNextStateTimer();
   public void startPluginForTheFirstTime(CheckersUIListener listener);
   public void updatePlugin(CheckersUIListener listener);
   public void resetGame();
   public void makeBoardPlayable();
   public void makeBoardUnplayable();

}
