package edu.wpi.always.weather;

import edu.wpi.always.*;
import edu.wpi.always.cm.ICollaborationManager;
import edu.wpi.always.user.*;
import edu.wpi.always.weather.wunderground.WundergroundWeatherProvider;
import edu.wpi.disco.rt.schema.SchemaManager;

// TODO Make this work with live data
//      using generateNewReport(), loadRecentDataFromFile();

public class WeatherPlugin extends Plugin {
   
   public WeatherPlugin (UserModel userModel, ICollaborationManager cm) {
      super("Weather", userModel, cm);
      addActivity("DiscussWeather", 0, 0, 0, 0, WeatherSchema.class, WundergroundWeatherProvider.class); 
   }
   
   // Property names must be constants and start with plugin name
   public static final String FAVORITE = "WeatherFavorite"; 
         
   /**
    * For testing Weather by itself
    */
   public static void main (String[] args) {
      Always always = new Always(true, WeatherPlugin.class, "DiscussWeather");
      always.start();
      Plugin plugin = always.getContainer().getComponent(WeatherPlugin.class);
      // testing new user property extension (see Weather.owl)
      // see WeatherSchema console window
      plugin.setProperty(FAVORITE, "hot and humid");
      System.out.println("My favorite weather is "+plugin.getProperty(FAVORITE));
   }
  

  
}
