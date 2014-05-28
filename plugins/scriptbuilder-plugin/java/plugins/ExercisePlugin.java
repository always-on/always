package plugins;

import edu.wpi.always.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;

public class ExercisePlugin extends Plugin {

   public ExercisePlugin (UserModel userModel, CollaborationManager cm) {
      super("Exercise", userModel, cm);
      addActivity("PlanExercise", 0, 0, 0, 0, ExerciseSchema.class);
   }
   
   public static final String PERFORMED = "ExercisePerformed";

   public static String[] getProperties () { return new String[] {PERFORMED}; }

   public static boolean isPerformed () {
      return Always.THIS.getUserModel().isProperty(PERFORMED);
   }
   
   /**
    * For testing plugin by itself
    */
   public static void main (String[] args) {
      Plugin.main(args, ExercisePlugin.class, "PlanExercise");
   }

}