package edu.wpi.always.checkers;

import java.util.List;
import edu.wpi.always.checkers.CheckersUIListener;

public interface CheckersUI {

   public void prepareAgentMove();
   public void triggerAgentPlayTimer();
   public void playAgentMove(CheckersUIListener listener);
   public String getCurrentAgentComment();
   public void triggerHumanCommentingTimer();
   public void cancelHumanCommentingTimer();
   public void triggerNextStateTimer();
   public void startPluginForTheFirstTime(CheckersUIListener listener);
   public void updatePlugin(CheckersUIListener listener);
   public void resetGame();
   public void prepareAgentCommentUserResponseForAMoveBy(int player);
   List<String> getCurrentHumanCommentOptionsAgentResponseForAMoveBy (int player);
   List<String> getCurrentHumanResponseOptions ();
   String getCurrentAgentResponse (String humanChoosenComment);
   public void makeBoardPlayable();
   public void makeBoardUnplayable();

}
