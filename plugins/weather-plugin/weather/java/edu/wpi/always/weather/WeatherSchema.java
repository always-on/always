package edu.wpi.always.weather;

import java.text.*;
import java.util.Date;
import edu.wpi.always.*;
import edu.wpi.always.cm.schemas.DiscoActivitySchema;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;

public class WeatherSchema extends DiscoActivitySchema {
   
   private static boolean running;

   @Override
   public void dispose () { 
      super.dispose();
      running = false; 
   } 
   
   public WeatherSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Always always,
         // these will be needed later
         PeopleManager peopleManager,
         PlaceManager placeManager) {
      super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, always,
            WeatherPlugin.weatherInteraction);
      if ( running ) throw new IllegalStateException("WeatherSchema already running!");
      running = true;
      interaction.eval("date = "+ "\"" +
           (WeatherPlugin.date == null ? getTodayDate(): WeatherPlugin.date)+ "\"" + ";",
          "Weather data");
      interaction.clear();
      switch (Always.THIS.getUserModel().getCloseness()) {
         case STRANGER: start("_WeatherStranger"); break;
         case ACQUAINTANCE: start("_WeatherAcquaintance"); break;
         case COMPANION: start("_WeatherCompanion"); break;
      }
   }
   
   /*
    * Get today's date, use that as the file name
    */
   private static String getTodayDate(){
      DateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy");
      Date date = new Date();
      return dateFormat.format(date);      
   }
   
   enum Option { CITIES, FRIENDS }
   public static void log (Option option) {
      Logger.logActivity(Logger.Activity.WEATHER, option);
   }
}
