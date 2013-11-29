package edu.wpi.always.explain;

import edu.wpi.always.Plugin;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;

public class ExplainPlugin extends Plugin {
   
   public ExplainPlugin (UserModel userModel, CollaborationManager cm) {
      super("Explain", userModel, cm);
      addActivity("ExplainSelf", 0, 0, 0, 0, ExplainSchema.class); 
   }

   /**
    * For testing Explain by itself
    */
   public static void main (String[] args) {
      Plugin.main(args, ExplainPlugin.class, "ExplainSelf");
   }

   // plugin-specific properties
   
   public static final String
      PREVIOUS_TALK = "ExplainPreviousTalk",
      PLAN_EXERCISE = "ExplainPlanExercise",
      TALK_ABOUT = "ExplainTalkAbout",
      USE_CALENDAR = "ExplainUseCalendar",
      PLAY_GAMES = "ExplainPlayGames",
      TELL_STORY = "ExplainTellStory",
      DISCUSS_WEATHER = "ExplainDiscussWeather",
      HEAR_ANECDOTES = "ExplainHearAnecdotes",
      ADVISE_NUTRITION = "ExplainAdviseNutrition",
      USE_SKYPE = "ExplainUseSkype";
            
   public static String[] getProperties () {
      return new String[] { PREVIOUS_TALK, PLAN_EXERCISE, TALK_ABOUT, USE_CALENDAR,
                            PLAY_GAMES, TELL_STORY, DISCUSS_WEATHER,
                            HEAR_ANECDOTES, ADVISE_NUTRITION, USE_SKYPE};
   }
  
}
