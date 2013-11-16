package plugins;

import edu.wpi.always.Plugin;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;

public class NutritionPlugin extends Plugin {

   public NutritionPlugin (UserModel userModel, CollaborationManager cm) {
      super("Nutrition", userModel, cm);
      addActivity("AdviseNutrition", 0, 0, 0, 0, NutritionSchema.class);
   }

   /**
    * For testing plugin by itself
    */
   public static void main (String[] args) {
      Plugin.main(args, NutritionPlugin.class, "AdviseNutrition");
   }

}