package edu.wpi.always.ttt;

import java.util.List;

public interface TTTUI {

   public void resetGame();
   public void prepareAgentMove();
   public void makeBoardPlayable();
   public void getCurrentAgentCommentForAMoveBy(int player);
   public void makeBoardUnplayable();
   public String getCurrentAgentComment();
   public void triggerAgentPlayTimer();
   public void cancelHumanCommentingTimer();
   public void triggerHumanCommentingTimer();
   public void updatePlugin(TTTUIListener listener);
   public void playAgentMove (TTTUIListener listener);
   public List<String> getCurrentHumanCommentOptionsForAMoveBy(int player);
   public void startPluginForTheFirstTime(TTTUIListener listener);

}
