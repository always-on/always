package edu.wpi.always.weather.wunderground;

import edu.wpi.always.weather.*;
import edu.wpi.always.weather.provider.*;

public class WundergroundWeatherProvider implements WeatherProvider {

   @Override
   public CurrentWeather getCurrentWeather (String zip) {
      try {
         return new WundergroundCurrentWeather(zip);
      } catch (Exception e) {
         return null;
      }
   }

   @Override
   public Almanac getAlmanac (String zip) {
      try {
         return new WundergroundAlmanac(zip);
      } catch (Exception e) {
         return null;
      }
   }

   @Override
   public Alert getAlert (String zip) {
      try {
         return new WundergroundAlert(zip);
      } catch (Exception e) {
         return null;
      }
   }

   @Override
   public Radar getRadar (String zip) {
      try {
         return new WundergroundRadar(zip);
      } catch (Exception e) {
         return null;
      }
   }

   @Override
   public Forecast getForcast (String zip, int howManyDaysLater) {
      try {
         return new WundergroundForecast(zip, howManyDaysLater);
      } catch (Exception e) {
         return null;
      }
   }
}
