package edu.wpi.always.weather.wunderground;

import com.google.gson.*;
import edu.wpi.always.Always;
import edu.wpi.always.user.UserUtils;
import java.io.FileWriter;
import java.net.ConnectException;
import java.text.*;
import java.util.Date;

public class WundergroundParser {

   // default
   private static String zip = "02115";

   /**
    * @param args [zip model] NB: Case-sensitive!
    *           <p>
    *           zip: 5-digits (default 02115)<br>
    *           model: file in always/user (default TestUser.owl)
    */
   public static void main (String[] args) {
      checkArgs(args);
      Always always = Always.make(new String[] { "Stranger",
         args.length > 1 ? args[1] : "TestUser.owl" }, null, null);
      String file =  UserUtils.USER_DIR+"/weatherData/"+UserUtils.formatDate()+".json";
      try (FileWriter writer = new FileWriter(file)) {
         WundergroundJSON weather = new WundergroundJSON(zip,
               always.getUserModel());
         // make easier to read for debugging
         Gson gson = new GsonBuilder().setPrettyPrinting().create();
         String json = gson.toJson(weather);
         writer.write(json);
         System.out.println("File written: "+file);
      } catch (Exception e) { throw new RuntimeException(e); }
   }
      
   /*
    * if argument 1 is a valid zip code, use that otherwise, use the default
    */
   private static void checkArgs (String[] args) {
      if ( args.length > 0 ) {
         if ( validateZip(args[0]) )
            zip = args[0];
      }
   }

   /*
    * validate zip code
    */
   private static boolean validateZip (String zip) {
      return zip.matches("\\d{5}((-)?\\d{4})?");
   }
}