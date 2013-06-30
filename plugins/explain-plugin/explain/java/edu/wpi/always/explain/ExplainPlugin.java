package edu.wpi.always.explain;


import edu.wpi.always.*;
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
      Always always = new Always(true, ExplainPlugin.class, "ExplainSelf");
      always.processArgs(args);
      always.start();
   }
  

  
}
