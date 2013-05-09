package edu.wpi.always.weather;

import edu.wpi.always.cm.perceptors.MenuPerceptor;
import edu.wpi.always.cm.schemas.DiscoActivitySchema;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;
import java.io.IOException;

public class WeatherSchema extends DiscoActivitySchema {

   public WeatherSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor,
         // these will be needed later
         PeopleManager peopleManager,
         PlaceManager placeManager) {
      super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor);
      interaction.eval("date = \"04_26_2012\";", "Cached weather data");
      interaction.load("edu/wpi/always/weather/resources/WeatherStranger.xml");        
      start("WeatherStranger");
   }
}
