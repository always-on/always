package edu.wpi.always.weather;

import edu.wpi.always.*;
import edu.wpi.always.weather.wunderground.WundergroundWeatherProvider;

// TODO Make this work with live data
//      using generateNewReport(), loadRecentDataFromFile();

public class WeatherPlugin extends Plugin {
   
   public WeatherPlugin () { 
      addActivity("DiscussWeather", 0, 0, 0, 0, WeatherSchema.class, WundergroundWeatherProvider.class); 
   }
   
   /**
    * For testing Weather by itself
    */
   public static void main (String[] args) {
      new Always(true, WeatherPlugin.class, "DiscussWeather").start();
   }
  

  
}
