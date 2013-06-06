package edu.wpi.always.weather;

import edu.wpi.always.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;
import edu.wpi.always.weather.wunderground.WundergroundWeatherProvider;

// TODO Make this work with live daily data

public class WeatherPlugin extends Plugin {
   
   public WeatherPlugin (UserModel userModel, CollaborationManager cm) {
      super("Weather", userModel, cm);
      addActivity("DiscussWeather", 0, 0, 0, 0, WeatherSchema.class, WundergroundWeatherProvider.class); 
   }
   
   // Property names must be constants and start with plugin name
   // This is just an example for testing
   public static final String FAVORITE = "WeatherFavorite"; 
         
   /**
    * For testing Weather by itself
    */
   public static void main (String[] args) {
      Always always = new Always(true, WeatherPlugin.class, "DiscussWeather");
      always.setCloseness(args);
      always.start();
      Plugin plugin = always.getContainer().getComponent(WeatherPlugin.class);
      // testing new user property extension (see Weather.owl)
      // see WeatherSchema console window
      plugin.setProperty(FAVORITE, "hot and humid");
      System.out.println("My favorite weather is "+plugin.getProperty(FAVORITE));
   }
  

  
}
