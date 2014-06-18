package edu.wpi.always.weather.wunderground;

import com.google.gson.*;
import edu.wpi.always.Always;
import edu.wpi.always.user.UserUtils;
import java.io.FileWriter;
import java.net.ConnectException;
import java.text.*;
import java.util.Date;

public class WundergroundParser {

   public static String ZIP = "02115";

   /**
    * @param args [zip] NB: Case-sensitive!
    *           <p>
    *           zip: 5-digits (default 02115)<br>
    */
   public static void main (String[] args) {
      String file =  UserUtils.USER_DIR+"/weatherData/"+UserUtils.formatDate()+".json";
      try (FileWriter writer = new FileWriter(file)) {
         if ( args.length > 0 ) {
            if ( !args[0].matches("\\d{5}((-)?\\d{4})?") )
               throw new IllegalArgumentException("Invalid zip code: "+args[0]);
            ZIP = args[0];
         }
         Always always = Always.make(null, null, null);
         WundergroundJSON weather = new WundergroundJSON(ZIP, always.getUserModel());
         // make easier to read for debugging
         Gson gson = new GsonBuilder().setPrettyPrinting().create();
         String json = gson.toJson(weather);
         writer.write(json);
         System.out.println("File written: "+file);
      } catch (Exception e) { 
         e.printStackTrace();
         Always.exit(-1); // so goes on
      }
   }
 }