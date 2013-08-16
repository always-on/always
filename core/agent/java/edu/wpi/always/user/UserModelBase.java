package edu.wpi.always.user;

import edu.wpi.always.Closeness;
import edu.wpi.always.user.calendar.Calendar;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;

public abstract class UserModelBase implements UserModel {

   protected String userName;
   
   @Override
   public String getUserName () {
      return userName;
   }
  
    private static final String 
         SESSIONS = "UserSessions",
         START_TIME = "UserStartTime",
         CLOSENESS = "UserCloseness";
   
   @Override
   public int getSessions () { return getIntProperty(SESSIONS); }
   
   public void nextSession () { setProperty(SESSIONS, getSessions()+1); }
   
   @Override
   public long getStartTime () { return getLongProperty(START_TIME); }
   
   public void start () { setProperty(START_TIME, System.currentTimeMillis()); }

   @Override
   public Closeness getCloseness () {
      if ( userName == null ) return Closeness.Stranger;  // for uninitialized model
      String closeness = getProperty(CLOSENESS);
      return closeness == null ? Closeness.Stranger : Closeness.valueOf(closeness);
   }

   @Override
   public void setCloseness (Closeness closeness) { 
      setProperty(CLOSENESS, closeness.name()); 
   }
  
}
