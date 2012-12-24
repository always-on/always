package edu.wpi.always.weather;

import com.google.gson.*;
import edu.wpi.always.user.people.Person;
import edu.wpi.always.user.places.Place;
import edu.wpi.always.weather.Almanac.RecordTemp;
import org.joda.time.LocalDate;
import org.joda.time.format.*;
import java.io.Writer;
import java.util.*;

public class WeatherJSONWriter {

   private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormat
         .forPattern("MM_dd_yyyy");

   public static String getFileName () {
      return FILE_DATE_FORMAT.print(new LocalDate()) + ".json";
   }

   public static void write (WeatherReport report, Writer writer) {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      String json = gson.toJson(toJsonStructure(report));
      write(json, writer);
   }

   private static void write (String json, Writer writer) {
      try {
         // Create file
         writer.write(json);
         writer.flush();
      } catch (Exception e) {// Catch exception if any
         System.err.println("Error writing weather to json: " + e.getMessage());
         e.printStackTrace();
      }
   }

   private static JsonElement toJsonStructure (WeatherReport report) {
      JsonObject root = new JsonObject();
      root.add("currentWeather", toJsonStructure(report.getCurrentWeather()));
      root.add("radar", toJsonStructure(report.getRadar()));
      root.add("alert", toJsonStructure(report.getAlert()));
      root.add("almanac", toJsonStructure(report.getAlmanac()));
      root.add("forecast", toJsonStructure(report.getForecast()));
      root.add("interestCities",
            toCityInterestJsonStructure(report.getInterestPlacesWeather()));
      root.add("friends",
            toPeopleInterestJsonStructure(report.getInterestPeopleWeather()));
      return root;
   }

   private static JsonElement toCityInterestJsonStructure (
         Map<Place, CurrentWeather> map) {
      JsonObject data = new JsonObject();
      for (Map.Entry<Place, CurrentWeather> f : map.entrySet())
         data.add(f.getKey().getCityName(), toJsonStructure(f.getValue()));
      return data;
   }

   private static JsonElement toPeopleInterestJsonStructure (
         Map<Person, CurrentWeather> map) {
      JsonObject data = new JsonObject();
      for (Map.Entry<Person, CurrentWeather> f : map.entrySet()) {
         data.add(f.getKey().getName(), toJsonStructure(f.getValue()));
      }
      return data;
   }

   private static JsonElement toJsonStructure (List<Forecast> forecast) {
      JsonArray data = new JsonArray();
      for (Forecast f : forecast)
         data.add(toJsonStructure(f));
      return data;
   }

   private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat
         .forPattern("MM/dd/yy");

   private static JsonElement toJsonStructure (Forecast forecast) {
      JsonObject forecastRoot = new JsonObject();
      JsonObject date = new JsonObject();
      date.addProperty("date", DATE_FORMAT.print(forecast.getDate()));
      date.addProperty("daysApartFromToday", forecast.getDaysApartFromToday());
      forecastRoot.add("date", date);
      forecastRoot.addProperty("summary", forecast.getSummary());
      return forecastRoot;
   }

   private static JsonElement toJsonStructure (Alert alert) {
      JsonObject alertRoot = new JsonObject();
      if ( alert == null )
         return null;
      alertRoot.addProperty("alertMessage", alert.getMessage());
      return alertRoot;
   }

   private static JsonElement toJsonStructure (Almanac almanac) {
      if ( almanac == null )
         return null;
      JsonObject almanacRoot = new JsonObject();
      almanacRoot.add("recordLow", toJsonStructure(almanac.getRecordLow()));
      almanacRoot.add("recordHigh", toJsonStructure(almanac.getRecordHigh()));
      return almanacRoot;
   }

   private static JsonElement toJsonStructure (RecordTemp temp) {
      JsonObject tempRoot = new JsonObject();
      tempRoot.addProperty("year", temp.getYear());
      tempRoot.addProperty("averageTemp", temp.getAverageTemp());
      tempRoot.addProperty("extremeTemp", temp.getExtremeTemp());
      return tempRoot;
   }

   private static JsonElement toJsonStructure (Radar radar) {
      JsonObject radarRoot = new JsonObject();
      radarRoot.addProperty("radarURL", radar.getImageURL());
      return radarRoot;
   }

   private static JsonElement toJsonStructure (CurrentWeather weather) {
      JsonObject currentRoot = new JsonObject();
      currentRoot.addProperty("locationName", weather.getLocationName());
      currentRoot.addProperty("temperature", weather.getTemperature());
      currentRoot
            .addProperty("weatherCondition", weather.getWeatherCondition());
      currentRoot.addProperty("humidity", weather.getHumidity());
      return currentRoot;
   }
}
