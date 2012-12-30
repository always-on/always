package edu.wpi.always.rummy;

import edu.wpi.always.*;

public class RummyPlugin extends PluginBase {
   
   public RummyPlugin () { 
      addActivity("PlayRummy", 0, 0, 0, 0, RummySchema.class, RummyClientPlugin.class); 
   }
   
   /**
    * For testing Rummy by itself
    */
   public static void main (String[] args) {
      new Always(true, RummyPlugin.class, "PlayRummy").start();
   }
  

  
}
