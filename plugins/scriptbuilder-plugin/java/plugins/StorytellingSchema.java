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

public class StorytellingSchema extends ScriptbuilderSchema {

   public final static Logger.Activity LOGGER_NAME = Logger.Activity.STORY;
   
	public enum Saved { SAVED, NOT_SAVED }
   
	/* TODO for logging:
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
	public static void log (Saved saved, int duration, String title) {
	   Logger.logActivity(LOGGER_NAME, saved, duration, title);
	}
   
   private final ShoreFacePerceptor shore;
   
	public StorytellingSchema (BehaviorProposalReceiver behaviorReceiver,
			BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
			MenuPerceptor menuPerceptor, Keyboard keyboard,
			UIMessageDispatcher dispatcher, PlaceManager placeManager,
			PeopleManager peopleManager, Always always, ShoreFacePerceptor shore) {
		 super(new ScriptbuilderCoreScript(new RAGStateContext(
	               keyboard, dispatcher, placeManager, peopleManager, always, 
	               shore instanceof ShoreFacePerceptor.Reeti ? null : shore,
	               "Storytelling")),
		       behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, dispatcher,
		       LOGGER_NAME);
		 this.shore = shore instanceof ShoreFacePerceptor.Reeti ? null : shore;
		 always.getUserModel().setProperty(StorytellingPlugin.PERFORMED, true);
		 interruptible = false;
	}
	
	@Override
   public void dispose () { 
      super.dispose();
      // this is here so it is run even if schema throws an error
      if ( shore != null ) shore.start(); 
   }
	
}
