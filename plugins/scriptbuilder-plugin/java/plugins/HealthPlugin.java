package plugins;

import edu.wpi.always.Plugin;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;

public class HealthPlugin extends Plugin {

   public HealthPlugin (UserModel userModel, CollaborationManager cm) {
      super("Health", userModel, cm);
      addActivity("GetHealthTips", 0, 0, 0, 0, HealthSchema.class);
   }

   /**
    * For testing plugin by itself
    */
   public static void main (String[] args) {
      Plugin.main(args, HealthPlugin.class, "GetHealthTips");
   }

}