package edu.wpi.always.user;

import edu.wpi.always.user.calendar.Calendar;
import edu.wpi.always.user.people.*;
import edu.wpi.always.user.places.PlaceManager;
import org.picocontainer.annotations.Bind;
import java.lang.annotation.*;

public interface UserModel {

   /**
    * Will throw an error if user model already has a name.
    */
   public void setUserName (String name);
   
   public String getUserName ();

   public void save ();

   public void load ();

   public Calendar getCalendar ();

   public PeopleManager getPeopleManager ();

   public PlaceManager getPlaceManager ();
   
   /**
    * Return named user property value extension.  Note that property must
    * be declared in a loaded ontology (.owl) file
    */
   public String getProperty (String property);
   
   /**
    * Set named user property value extension.  Note that property must
    * be declared in a loaded ontology (.owl) file
    */
   public void setProperty (String property, String value);
   
}
