package edu.wpi.always.weather;

import java.io.*;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.schemas.*;
import edu.wpi.always.user.people.*;
import edu.wpi.always.user.places.*;

public class WeatherSchema extends DiscoBasedSchema {

	public WeatherSchema(BehaviorProposalReceiver behaviorReceiver, BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor, MenuPerceptor menuPerceptor, PeopleManager peopleManager, PlaceManager placeManager) {
		super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor);
		try {
			loadModel("edu/wpi/always/weather/resources/Wea.xml");
			// generateNewReport();
			// loadRecentDataFromFile();
			setTaskId("Weather");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
