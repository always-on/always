package edu.neu.always.skype;

import org.picocontainer.Characteristics;
import edu.wpi.always.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;

public class SkypePlugin extends Plugin {
   
   public SkypePlugin (UserModel userModel, CollaborationManager cm) {
      super("Skype", userModel, cm);
      container.as(Characteristics.CACHE).addComponent(SkypeClient.class);
      addActivity("UseSkypeIncoming", 0, 0, 0, 0, SkypeIncomingSchema.class); 
      addActivity("UseSkypeOutgoing", 0, 0, 0, 0, SkypeOutgoingSchema.class); 
   }
   
   public static final String PERFORMED = "SkypePerformed";

   public static String[] getProperties () { return new String[] {PERFORMED}; }

   public static boolean isPerformed () {
      return Always.THIS.getUserModel().isProperty(PERFORMED);
   }
 
   /**
    * For testing Skype by itself
    */
   public static void main (String[] args) {
      Plugin.main(args, SkypePlugin.class, "UseSkypeOutgoing");
   }
}
