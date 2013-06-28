package edu.wpi.always.weather;

import edu.wpi.always.Always;
import edu.wpi.always.cm.schemas.DiscoActivitySchema;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;

public class WeatherSchema extends DiscoActivitySchema {

   public WeatherSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor,
         // these will be needed later
         PeopleManager peopleManager,
         PlaceManager placeManager) {
      super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor);
      interaction.eval("date = \"testing\";", "Cached weather data");
      interaction.load("edu/wpi/always/weather/resources/Weather.xml"); 
      switch (Always.THIS.getRM().getCloseness()) {
         case Stranger: start("_WeatherStranger"); break;
         case Acquaintance: start("_WeatherAcquaintance"); break;
         case Companion: start("_WeatherCompanion"); break;
      }
   }
}
