package edu.wpi.always.user;

import edu.wpi.always.*;

public abstract class UserModelBase implements UserModel {
   
   public static void saveIf () { 
      if ( !INHIBIT_SAVE ) {
         UserModel model = Always.THIS.getUserModel();
         if ( model.getUserName().isEmpty() )
            System.err.println("WARNING! Not saving user model because user name is empty.");
         else model.save();
      }
   }
   
   protected String userName = "", userFirstName = "";
   
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
      if ( userName.isEmpty() ) return Closeness.Stranger;  // for uninitialized model
      String closeness = getProperty(CLOSENESS);
      return closeness == null ? Closeness.Stranger : Closeness.valueOf(closeness);
   }

   @Override
   public void setCloseness (Closeness closeness) { 
<<<<<<< HEAD
      if ( !userName.isEmpty() ) setProperty(CLOSENESS, closeness.name()); 
=======
      setProperty(CLOSENESS, closeness.name()); 
>>>>>>> develop
   }
  
}
