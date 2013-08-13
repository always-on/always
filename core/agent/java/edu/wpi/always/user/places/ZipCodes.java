package edu.wpi.always.user.places;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.joda.time.DateTimeZone;

public class ZipCodes {

   public static class StateEntry {
      private final List<String> stateAbbrev = new ArrayList<String>();
      private final String state;
      private final String capital;
      private final String capitalZip;

      public StateEntry (String rawData) {
         String[] dataStrings = rawData.split(",");
         for(int i = 0; i <dataStrings.length; ++i) {
            dataStrings[i] = dataStrings[i].substring(1,
                  dataStrings[i].length() - 1);
         }
         state = dataStrings[0];
         capital = dataStrings[1];
         capitalZip = dataStrings[2];
         for(int i = 3; i < dataStrings.length; i++){
            stateAbbrev.add(dataStrings[i]);
         }
      }

      public String getState() {
         return state;
      }

      public List<String> getStateAbbrev() {
         return stateAbbrev;
      }

      public String getCapital() {
         return capital;
      }

      public String getCapitalZip() {
         return capitalZip;
      }
   }

   public static class ZipCodeEntry {

      private final String zip;
      private final String city;
      private final String state;
      private final DateTimeZone timezone;

      public ZipCodeEntry (String rawData) {
         String[] dataStrings = rawData.split(",");
         for (int i = 0; i < dataStrings.length; ++i) {
            dataStrings[i] = dataStrings[i].substring(1,
                  dataStrings[i].length() - 1);
         }
         zip = dataStrings[0];
         city = dataStrings[1];
         state = dataStrings[2];
         timezone = DateTimeZone.forOffsetHours(Integer
               .parseInt(dataStrings[5]));
      }

      public String getZip () {
         return zip;
      }

      public String getCity () {
         return city;
      }

      public String getState () {
         return state;
      }

      public DateTimeZone getTimezone () {
         return timezone;
      }

      @Override
      public String toString () {
         return "ZipCodeEntry [zip=" + zip + ", city=" + city + ", state="
               + state + ", timezone="
               + (timezone == null ? timezone : timezone.getID()) + "]";
      }
   }

   private Map<String, ZipCodeEntry> data = new HashMap<String, ZipCodes.ZipCodeEntry>(
         43200);

   private Map<String, StateEntry> stateAbbrevData = new HashMap<String, ZipCodes.StateEntry>();

   private Map<String, StateEntry> stateData = new HashMap<String, ZipCodes.StateEntry>();

   private List<ZipCodeEntry> cityData = new ArrayList<ZipCodes.ZipCodeEntry>();

   public ZipCodes () throws IOException {
      // data from http://www.boutell.com/zipcodes/
      try (Scanner s = new Scanner(new BufferedInputStream(getClass()
            .getResourceAsStream("/edu/wpi/always/user/places/ZipCodes.csv")))) {
         s.nextLine();// read first line
         while (s.hasNextLine()) {
            String line = s.nextLine();
            if ( !line.isEmpty() ) {
               ZipCodeEntry entry = new ZipCodeEntry(line);
               data.put(entry.getZip(), entry);
               cityData.add(entry);
            }
         }
      }
      try (Scanner s = new Scanner(new BufferedInputStream(getClass()
            .getResourceAsStream("/edu/wpi/always/user/places/States.csv")))) {
         s.nextLine();
         while(s.hasNext()) {
            String line = s.nextLine();
            if(!line.isEmpty()) {
               StateEntry stateEntry = new StateEntry(line);
               stateData.put(stateEntry.getState().toLowerCase(), stateEntry);
               stateAbbrevData.put(stateEntry.getStateAbbrev().get(0).toLowerCase(), stateEntry);
            }
         }
      }
   }

   public ZipCodeEntry getPlaceData (String zip) {
      return data.get(zip);
   }

   public StateEntry getState (String text) {
      StateEntry result;
      String stateName = text.toLowerCase();
      StateEntry stateAbbrev = stateAbbrevData.get(stateName);
      StateEntry state = stateData.get(stateName);
      if(state != null || stateAbbrev != null) {
         result = (state != null)? state :stateAbbrev;
      }
      else {
         StateEntry stateOther = null;
         for(StateEntry entry :stateData.values()){
            for(String name : entry.getStateAbbrev()){
               if(text.toLowerCase().equals(name.toLowerCase())){
                  stateOther = entry;
               }
            }
         }
         result = (stateOther != null)? stateOther : null;
      }
      return result;
   }

   public List<ZipCodeEntry> getCityData (String city) {
      ArrayList<ZipCodeEntry> cities = new ArrayList<ZipCodeEntry>();
      for(ZipCodeEntry entry : cityData) {
         if(entry.getCity().toLowerCase().equals(city.toLowerCase())){
            cities.add(entry);
         }
      }
      return cities;
   }
}
