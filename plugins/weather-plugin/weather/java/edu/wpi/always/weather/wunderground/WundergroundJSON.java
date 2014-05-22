package edu.wpi.always.weather.wunderground;

import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.people.Person;
import edu.wpi.always.user.places.Place;
import edu.wpi.always.weather.provider.*;
import edu.wpi.disco.rt.util.Utils;
import java.util.*;

public class WundergroundJSON {
   CurrentWeather currentWeather;

   Radar radar;

   Alert alert;

   Almanac almanac;

   Forecast[] forecast;

   transient UserModel model; // transient so not included in JSON

   Map<String, CurrentWeather> interestCities;

   Map<String, CurrentWeather> friendsCities;

   static final int forecastSize = 2;

   private transient final Map<String,String> comments = new HashMap<String,String>();

   private transient final Map<String, String> CITY_ZIP_MAP = createCityMap();

   private transient final Map<String, String> FRIENDS_ZIP_MAP;

   public WundergroundJSON (String s, UserModel model) throws Exception {
      this.model = model;
      FRIENDS_ZIP_MAP = createFriendMap();
      Utils.lnprint(System.out, "Getting weather for ZIP " + s);
      fillData(s);
   }

   private void fillData (String zip) throws Exception {
      currentWeather = new WundergroundCurrentWeather(zip);
      radar = new WundergroundRadar(zip);
      almanac = new WundergroundAlmanac(zip);
      alert = new WundergroundAlert(zip);

      // forecast
      forecast = new Forecast[forecastSize];
      for (int i = 0; i < forecastSize; i++)
         forecast[i] = new WundergroundForecast(zip, i);

      // interest cities
      interestCities = new HashMap<String, CurrentWeather>();
      for (Map.Entry<String, String> entry : CITY_ZIP_MAP.entrySet()) {
         CurrentWeather weather = new WundergroundCurrentWeather(entry.getValue());
         weather.setComment(comments.get(entry.getKey()));
         interestCities.put(entry.getKey(), weather);
      }

      // friends
      friendsCities = new HashMap<String, CurrentWeather>();
      for (Map.Entry<String, String> entry : FRIENDS_ZIP_MAP.entrySet()) {
         friendsCities.put(entry.getKey(),
               new WundergroundCurrentWeather(entry.getValue()));
      }
   }

   private Map<String, String> createCityMap () {
      Map<String, String> result = new HashMap<String, String>();
      result.put("Seattle", "98106");
      comments.put("Seattle", "Seattle is the emerald city,and the home of Mount Rainier");
      result.put("Miami", "33125");
      comments.put("Miami", "Miami is the known as the city of beaches.");
      result.put("San Francisco", "94105");
      comments.put("San Francisco", "San Francisco is the Golden Gate city.");
      result.put("Chicago", "60601");
      comments.put("Chicago", "Chicago is known as the Windy City");
      result.put("New York", "10001");
      comments.put("New York", "New York is the big apple.");
      result.put("New Orleans", "70112");
      comments.put("New Orleans", "They call New Orleans the big easy.");
      return Collections.unmodifiableMap(result);
   }

   private Map<String, String> createFriendMap () {
      Map<String, String> result = new HashMap<String, String>();
      if ( model != null ) {
         Person[] people = model.getPeopleManager().getPeople(false);
         if ( people != null ) {
            for (Person person : people) {
               String name = person.getName();
               if ( name.equals(model.getUserName()) ) continue;
               Place place = person.getLocation();
               if ( place != null )
                  result.put(name, place.getZip());
            }
         }
      }
      return Collections.unmodifiableMap(result);
   }
}
