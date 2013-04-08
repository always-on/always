package edu.wpi.always.user;

import org.joda.time.DateTimeZone;
import java.io.*;
import java.util.*;

public class ZipCodes {

   public static class ZipCodeEntry {

      private final String zip;
      private final String city;
      private final String state;
      private final DateTimeZone timezone;

      ZipCodeEntry (String rawData) {
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

   public ZipCodes () throws IOException {
      // data from http://www.boutell.com/zipcodes/
      try (Scanner s = new Scanner(new BufferedInputStream(getClass()
            .getResourceAsStream("/edu/wpi/always/user/ZipCodes.csv")))) {
         s.nextLine();// read first line
         while (s.hasNextLine()) {
            String line = s.nextLine();
            if ( !line.isEmpty() ) {
               ZipCodeEntry entry = new ZipCodeEntry(line);
               data.put(entry.getZip(), entry);
            }
         }
      }
   }

   public ZipCodeEntry getPlaceData (String zip) {
      return data.get(zip);
   }
}
