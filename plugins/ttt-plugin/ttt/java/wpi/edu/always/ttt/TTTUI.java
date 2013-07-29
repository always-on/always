package wpi.edu.always.ttt;

import java.util.List;

public interface TTTUI {

   public void gazeLeft();
   public void prepareAgentMove();
   public void makeBoardPlayable();
   public void prepareAgentComment();
   public void makeBoardUnplayable();
   public String getCurrentAgentComment();
   public void triggerAgentPlayTimer();
   public void cancelHumanCommentingTimer();
   public void triggerHumanCommentingTimer();
   public void updatePlugin(TTTUIListener listener);
   public void playAgentMove (TTTUIListener listener);
   public List<String> getCurrentHumanCommentOptions();
   public void startPluginForTheFirstTime(TTTUIListener listener);

}
