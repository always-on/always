package edu.wpi.always.weather.wunderground;

import com.google.gson.*;
import edu.wpi.always.Always;
import edu.wpi.always.user.UserUtils;
import edu.wpi.always.weather.WeatherPlugin;
import java.io.FileWriter;
import java.net.ConnectException;
import java.text.*;
import java.util.Date;

public class WundergroundParser {

   // Note: Some Boston-area zip codes (e.g., Brookline 02146) do not
   // seem to appear in Wunderground, so always use 02115
   public final static String ZIP = "02115";

   /**
    * @param args [zip] NB: Case-sensitive!
    *           <p> **not supported** (see above)
    *           zip: 5-digits (default 02115)<br>
    */
   public static void main (String[] args) {
      try {
         Always always = Always.make(null, WeatherPlugin.class, null);
         WundergroundJSON weather = new WundergroundJSON(ZIP, always.getUserModel());
         // make easier to read for debugging
         Gson gson = new GsonBuilder().setPrettyPrinting().create();
         String json = gson.toJson(weather);
         String file =  UserUtils.USER_DIR+"/weatherData/"+UserUtils.formatDate()+".json";
         try (FileWriter writer = new FileWriter(file)) {
            /*
            if ( args.length > 0 ) {
               if ( !args[0].matches("\\d{5}((-)?\\d{4})?") )
                  throw new IllegalArgumentException("Invalid zip code: "+args[0]);
               ZIP = args[0];
            }
            */
            writer.write(json);
            System.out.println("File written: "+file);
         } catch (Exception e) { 
            e.printStackTrace();
            Always.exit(4); // so goes on
         }
      } catch (Exception e) {
         e.printStackTrace();
         Always.exit(5); // so goes on
      }
   }
 }