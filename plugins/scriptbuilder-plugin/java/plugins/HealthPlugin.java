package plugins;

import edu.wpi.always.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;

public class HealthPlugin extends Plugin {

   public HealthPlugin (UserModel userModel, CollaborationManager cm) {
      super("Health", userModel, cm);
      addActivity("GetHealthTips", 0, 0, 0, 0, HealthSchema.class);
   }
   
   public static final String PERFORMED = "HealthPerformed";

   public static String[] getProperties () { return new String[] {PERFORMED}; }

   public static boolean isPerformed () {
      return Always.THIS.getUserModel().isProperty(PERFORMED);
   }
   
   /**
    * For testing plugin by itself
    */
   public static void main (String[] args) {
      Plugin.main(args, HealthPlugin.class, "GetHealthTips");
   }

}