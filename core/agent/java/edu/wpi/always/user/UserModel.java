package edu.wpi.always.user;

import edu.wpi.always.Closeness;
import edu.wpi.always.user.calendar.Calendar;
import edu.wpi.always.user.people.*;
import edu.wpi.always.user.places.PlaceManager;
import org.picocontainer.annotations.Bind;
import java.lang.annotation.*;

/**
 * Note user model is automatically saved to file after every update command
 * unless prevented with {@link UserModel#INHIBIT_SAVE}.
 */
public interface UserModel {
   
   /**
    * <code>
    * try { 
    *    UserModel.INHIBIT_SAVE = true;
    *    ...updates to userModel...
    * } finally { 
    *    UserModel.INHIBIT_SAVE = false;
    *    userModel.save()
    * </code>
    */
   static boolean INHIBIT_SAVE = false;

   /**
    * Will throw an error if user model already has a name.
    */
   void setUserName (String name);

   /**
    * The user's name (unique)
    */
   String getUserName ();
   
   /**
    * Number of sessions user has completed.
    */
   int getSessions ();
   
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
