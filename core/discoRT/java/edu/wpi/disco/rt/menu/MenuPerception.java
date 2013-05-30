package edu.wpi.disco.rt.menu;

import edu.wpi.disco.rt.perceptor.Perception;
import org.joda.time.DateTime;

public class MenuPerception extends Perception {

   private final String selected;

   public MenuPerception (String selected, DateTime stamp) {
      super(stamp);
      this.selected = selected;
   }

   public MenuPerception (String selected) {
      this(selected, DateTime.now());
   }
   public String getSelected () { return selected; }

   @Override
   public String toString () {
      return "Menu[selected=" + selected + ", stamp=" + stamp
         + "]";
   }

   
}
