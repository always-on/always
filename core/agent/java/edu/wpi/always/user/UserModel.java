package edu.wpi.always.user;

import edu.wpi.always.user.calendar.Calendar;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import org.picocontainer.annotations.Bind;
import java.lang.annotation.*;

public interface UserModel {

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ ElementType.FIELD, ElementType.PARAMETER })
   @Bind
   public @interface UserName {
   }

   public String getUserName ();

   public void save ();

   public void load ();

   public Calendar getCalendar ();

   public PeopleManager getPeopleManager ();

   public PlaceManager getPlaceManager ();
}
