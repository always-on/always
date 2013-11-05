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
      DISCUSS_FAMILY = "ExplainDiscussFamily",
      USE_CALENDAR = "ExplainUseCalendar",
      PLAY_RUMMY = "ExplainPlayRummy",
      TELL_STORY = "ExplainTellStory",
      DISCUSS_WEATHER = "ExplainDiscussWeather";
            
   public static String[] getProperties () {
      return new String[] { PREVIOUS_TALK, PLAN_EXERCISE, DISCUSS_FAMILY, USE_CALENDAR,
                            PLAY_RUMMY, TELL_STORY, DISCUSS_WEATHER };
   }
  
}
