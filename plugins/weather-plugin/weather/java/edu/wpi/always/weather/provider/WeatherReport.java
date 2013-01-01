package edu.wpi.always.weather.provider;

import edu.wpi.always.user.people.Person;
import edu.wpi.always.user.places.Place;
import java.util.*;

public class WeatherReport {

   private static final int FORECAST_SIZE = 3;
   private CurrentWeather currentWeather;
   private Radar radar;
   private Alert alert;
   private Almanac almanac;
   private List<Forecast> forecastList;
   private Map<Place, CurrentWeather> interestPlacesWeather;
   private Map<Person, CurrentWeather> interestPeopleWeather;

   public WeatherReport (WeatherProvider provider, String mainZip,
         Person[] interestWeatherPeople, Place[] interestPlaces) {
      currentWeather = provider.getCurrentWeather(mainZip);
      radar = provider.getRadar(mainZip);
      almanac = provider.getAlmanac(mainZip);
      alert = provider.getAlert(mainZip);
      // forecast
      forecastList = new ArrayList<Forecast>();
      for (int i = 0; i < FORECAST_SIZE; i++) {
         Forecast forecast = provider.getForcast(mainZip, i);
         if ( forecast != null )
            forecastList.add(forecast);
      }
      // interest cities
      interestPlacesWeather = new HashMap<Place, CurrentWeather>();
      for (Place place : interestPlaces) {
         String zip = place.getZip();
         if ( zip != null ) {
            CurrentWeather weather = provider.getCurrentWeather(zip);
            if ( weather != null )
               interestPlacesWeather.put(place, weather);
         }
      }
      // interest people
      interestPeopleWeather = new HashMap<Person, CurrentWeather>();
      for (Person person : interestWeatherPeople) {
         Place place = person.getLocation();
         if ( place != null ) {
            String zip = place.getZip();
            if ( zip != null ) {
               CurrentWeather weather = provider.getCurrentWeather(zip);
               if ( weather != null )
                  interestPeopleWeather.put(person, weather);
            }
         }
      }
   }

   public CurrentWeather getCurrentWeather () {
      return currentWeather;
   }

   public Radar getRadar () {
      return radar;
   }

   public Alert getAlert () {
      return alert;
   }

   public Almanac getAlmanac () {
      return almanac;
   }

   public List<Forecast> getForecast () {
      return forecastList;
   }

   public Map<Place, CurrentWeather> getInterestPlacesWeather () {
      return interestPlacesWeather;
   }

   public Map<Person, CurrentWeather> getInterestPeopleWeather () {
      return interestPeopleWeather;
   }
}
