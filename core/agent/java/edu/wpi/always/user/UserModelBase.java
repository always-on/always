package edu.wpi.always.user;

import edu.wpi.always.*;

public abstract class UserModelBase implements UserModel {
   
   public static void saveIf () { 
      if ( !INHIBIT_SAVE ) Always.THIS.getUserModel().save(); 
   }
   
   protected String userName, userFirstName;
   
   @Override
   public String getUserName () {
      return userName;
   }
  
   @Override
   public String getUserFirstName () { return userFirstName; }
   
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
      if ( userName != null ) setProperty(CLOSENESS, closeness.name()); 
   }
  
}
