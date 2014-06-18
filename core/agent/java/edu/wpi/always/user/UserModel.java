package edu.wpi.always.user;

import java.lang.annotation.*;
import org.picocontainer.annotations.Bind;
import edu.wpi.always.Closeness;
import edu.wpi.always.user.calendar.Calendar;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;

/**
 * Note user model is automatically saved to file after every update command
 * unless prevented with {@link UserModelBase#INHIBIT_SAVE}.
 */
public interface UserModel {
      
   @Retention(RetentionPolicy.RUNTIME)
   @Target({ ElementType.FIELD, ElementType.PARAMETER })
   @Bind
   public @interface UserOntologyLocation {}
   
   /**
    * Will throw an error if user model already has a name.
    */
   void setUserName (String name);

   /**
    * The user's name (unique)
    */
   String getUserName ();
   
   /**
    * The user's first name (computed from full name).  Useful
    * for addressing the user.
    */
   String getUserFirstName ();
   
   /**
    * Number of sessions user has completed.
    */
   int getSessions ();
   
   void setSessions (int sessions);
   
   /**
    * Time when system first run for this user.
    */
   long getStartTime ();
   
   /**
    * Current closeness level
    */
   Closeness getCloseness ();
   
   void setCloseness (Closeness closeness);

   void save ();

   void load ();
   
   void ensureConsistency ();
   
   void reset ();

   Calendar getCalendar ();

   PeopleManager getPeopleManager ();

   PlaceManager getPlaceManager ();

   /**
    * Return named user property value extension. Note that property must be
    * declared in a loaded ontology (.owl) file
    */
   public String getProperty (String property);

   /**
    * Set named user property value extension. Note that property must be
    * declared in a loaded ontology (.owl) file
    */
   public void setProperty (String property, String value);

   /**
    * Return named user property value extension. Note that property must be
    * declared in a loaded ontology (.owl) file
    */
   public int getIntProperty (String property);

   /**
    * Set named user property value extension. Note that property must be
    * declared in a loaded ontology (.owl) file
    */
   public void setProperty (String property, int value);

   /**
    * Return named user property value extension. Note that property must be
    * declared in a loaded ontology (.owl) file
    */
   public long getLongProperty (String property);

   /**
    * Set named user property value extension. Note that property must be
    * declared in a loaded ontology (.owl) file
    */
   public void setProperty (String property, long value);

   /**
    * Return named user property value extension. Note that property must be
    * declared in a loaded ontology (.owl) file
    */
   public double getDoubleProperty (String property);

   /**
    * Set named user property value extension. Note that property must be
    * declared in a loaded ontology (.owl) file
    */
   public void setProperty (String property, double value);

   /**
    * Return boolean value for named user property value extension. Note that
    * property must be declared in a loaded ontology (.owl) file. If property
    * does not exist, then return false.
    */
   public boolean isProperty (String property);

   /**
    * Set boolean value for named user property value extension. Note that
    * property must be declared in a loaded ontology (.owl) file
    */
   public void setProperty (String property, boolean value);

}
