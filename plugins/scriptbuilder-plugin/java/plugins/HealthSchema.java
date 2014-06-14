package plugins;

import edu.wpi.always.*;
import edu.wpi.always.client.*;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;
import pluginCore.*;

public class HealthSchema extends ScriptbuilderSchema {

   public final static Logger.Activity LOGGER_NAME = Logger.Activity.HEALTH;
   
   /* TODO for logging:
    * 
    * Note: If you are satisfied with the log messages that are already
    * automatically generated for start/end of activity and for all
    * user model updates, then you can delete the log method below
    * (and already defined enums above, if any) and go directly to (4) below.
    * 
    * (1) Add arguments to log method below as needed (use enums instead of
    *     string constants to avoid typos and ordering errors!)
    *     
    * (2) Update always/docs/log-format.txt with any new logging fields
    * 
    * (3) Call log method at appropriate places in code
    * 
    * (4) Remove this comment!
    *
    */
	public static void log (Object... args) {
      Logger.logActivity(LOGGER_NAME, args);
   }

   public HealthSchema (BehaviorProposalReceiver behaviorReceiver,
			BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
			MenuPerceptor menuPerceptor, Keyboard keyboard,
			UIMessageDispatcher dispatcher, PlaceManager placeManager,
			PeopleManager peopleManager, Always always) {
		 super(new ScriptbuilderCoreScript(new RAGStateContext(
	               keyboard, dispatcher, placeManager, peopleManager, always, "Education")),
		       behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, dispatcher,
		       LOGGER_NAME);
		 always.getUserModel().setProperty(HealthPlugin.PERFORMED, true);
	}
}