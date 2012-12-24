package edu.wpi.always.weather;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.perceptors.MenuPerceptor;
import edu.wpi.always.cm.schemas.DiscoBasedSchema;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.*;
import java.io.IOException;

public class WeatherSchema extends DiscoBasedSchema {

   public WeatherSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, PeopleManager peopleManager,
         PlaceManager placeManager) {
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
