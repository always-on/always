package edu.wpi.always.weather;

import java.util.*;
import org.joda.time.DateTime;
import edu.wpi.always.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;
import edu.wpi.always.weather.wunderground.WundergroundWeatherProvider;
import edu.wpi.disco.*;
import edu.wpi.disco.rt.DiscoRT;

// TODO Make this work with live daily data

public class WeatherPlugin extends Plugin {
   
   public WeatherPlugin (UserModel userModel, CollaborationManager cm) {
      super("Weather", userModel, cm);
      addActivity("DiscussWeather", 0, 0, 0, 0, WeatherSchema.class); 
   }
   
   // Property names must be constants and start with plugin name
   // This is just an example for testing
   public static final String 
         FAVORITE = "WeatherFavorite",
         FLAG = "WeatherFlag",
         RANK = "WeatherRank",
         TIME = "WeatherTime";
      
   public static String[] getProperties () {
      return new String[] {FAVORITE, FLAG, RANK, TIME};
   }
   
   public static String date;  // for testing
   
   /**
    * For testing Weather by itself.
    * Optional Fourth arg is filename of weather json file to load, e.g. "testing" (default today's date).
    */
   public static void main (String[] args) {
      if ( args != null && args.length > 3 ) date = args[3];
      weatherInteraction.load("edu/wpi/always/weather/resources/Weather.xml"); 
      Always always = Plugin.main(args, WeatherPlugin.class, "DiscussWeather");
      // code below is temporary to demonstrate how to define and use a new user 
      // property extensions (see Weather.owl)
      // see WeatherSchema console window for printout
      Plugin plugin = always.getContainer().getComponent(WeatherPlugin.class);
      plugin.setProperty(FAVORITE, "hot and humid");
      plugin.setProperty(RANK, 2);
      plugin.setProperty(FLAG, true);
      plugin.setProperty(TIME, System.currentTimeMillis());
      if ( plugin.isProperty(FLAG) ) 
         System.out.println("At "+new DateTime(plugin.getLongProperty(TIME))
               +" my #"+plugin.getIntProperty(RANK)
               +" favorite weather is "+plugin.getProperty(FAVORITE));
   }
   
   // preload task model
   final static DiscoRT.Interaction weatherInteraction = 
         new DiscoRT.Interaction(new Agent("agent"), new User("user"));
   static { if ( Always.ALL_PLUGINS ) weatherInteraction.load("edu/wpi/always/weather/resources/Weather.xml"); }
 
}
