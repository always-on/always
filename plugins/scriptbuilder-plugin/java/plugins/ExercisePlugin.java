package plugins;

import edu.wpi.always.Plugin;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;

public class ExercisePlugin extends Plugin {

   public ExercisePlugin (UserModel userModel, CollaborationManager cm) {
      super("Exercise", userModel, cm);
      addActivity("PlanExercise", 0, 0, 0, 0, ExerciseSchema.class);
   }

   /**
    * For testing plugin by itself
    */
   public static void main (String[] args) {
      Plugin.main(args, ExercisePlugin.class, "PlanExercise");
   }

}