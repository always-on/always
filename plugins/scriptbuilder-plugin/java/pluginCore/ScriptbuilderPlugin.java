package pluginCore;

import edu.wpi.always.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;

public class ScriptbuilderPlugin extends Plugin {
   
 //  public TestPlugin(UserModel userModel) { 
 //     addActivity("UseTest", 0, 0, 0, 0, TestSchema.class, TestClient.class); 
 //  }
   
   public ScriptbuilderPlugin(UserModel userModel, CollaborationManager cm) {
		super("TestPlugin", userModel, cm);
		addActivity("UseCalendar", 0, 0, 0, 0, ScriptbuilderSchema.class, ScriptbuilderClient.class);
	}

/**
    * For testing TestPlugin by itself
    */
   public static void main (String[] args) {
	 //args = new String[]{"Stranger","TestPlugin"};
	 Plugin.main(args, ScriptbuilderPlugin.class, "UseCalendar");
   }	  

}