package plugins;

import edu.wpi.always.*;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.perceptors.sensor.face.ShoreFacePerceptor;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;
import pluginCore.*;

public class NutritionSchema extends ScriptbuilderSchema {

	public NutritionSchema (BehaviorProposalReceiver behaviorReceiver,
			BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
			MenuPerceptor menuPerceptor, Keyboard keyboard,
			UIMessageDispatcher dispatcher, PlaceManager placeManager,
			PeopleManager peopleManager, Always always) {
		 super(new ScriptbuilderCoreScript(new RAGStateContext(
	               keyboard, dispatcher, placeManager, peopleManager, always, "Nutrition")),
		       behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, dispatcher,
		       Logger.Activity.NUTRITION);
		 always.getUserModel().setProperty(NutritionPlugin.PERFORMED, true);
	}
	
   //	TODO define enums as appropriate
   
   public static void log (Object... args) {
      Logger.logActivity(Logger.Activity.NUTRITION, args);
   }
}