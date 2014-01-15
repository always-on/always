package edu.wpi.always.srummy;

import java.util.List;
import edu.wpi.always.srummy.SrummyUI;

public interface SrummyUI {

   public String getCurrentAgentComment();
   public void triggerAgentPlayTimers();
   public void triggerAgentDiscardDelay ();
   public void cancelHumanCommentingTimer();
   public void triggerHumanCommentingTimer();
   public void triggerNextStateTimer();
   public void startPluginForTheFirstTime(SrummyUIListener listener);
   public void updatePlugin(SrummyUIListener listener);
   public void sendBackAgentMove ();
   public void resetGame();
   String getCurrentAgentResponse (String humanChoosenComment);
   List<String> getCurrentHumanCommentOptionsAgentResponseForAMoveBy (int player);
   void prepareAgentCommentUserResponseForAMoveBy (int player);
   List<String> getCurrentHumanResponseOptions ();
   //   public void makeBoardPlayable();
   //   public void makeBoardUnplayable();

}
