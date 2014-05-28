package edu.wpi.always.user;

import edu.wpi.always.*;
import edu.wpi.disco.rt.util.Utils;

public abstract class UserModelBase implements UserModel {
   
   public static void saveIf () { 
      if ( !INHIBIT_SAVE ) {
         UserModel model = Always.THIS.getUserModel();
         if ( model.getUserName().isEmpty() )
            Utils.lnprint(System.out, "WARNING! Not saving user model because user name is empty.");
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
  
   @Override
   public void reset () {
      userName = userFirstName = "";
   }

   private static final String 
         SESSIONS = "UserSessions",
         START_TIME = "UserStartTime",
         CLOSENESS = "UserCloseness";
   
   @Override
   public int getSessions () { return getIntProperty(SESSIONS); }
      
   @Override
   public long getStartTime () { return getLongProperty(START_TIME); }   
   
   @Override
   public void load () {
      if ( getStartTime() == 0L ) 
         setProperty(START_TIME, Always.getSessionDate().getTime());
      setProperty(SESSIONS, getSessions()+1); 
   }

   @Override
   public Closeness getCloseness () {
      if ( userName.isEmpty() ) return Closeness.Stranger;  // for uninitialized model
      String closeness = getProperty(CLOSENESS);
      return closeness == null ? Closeness.Stranger : Closeness.valueOf(closeness);
   }

   @Override
   public void setCloseness (Closeness closeness) { 
      String name = closeness.name();
      setProperty(CLOSENESS, name);
      Utils.lnprint(System.out, "Setting closeness to "+name);
   }
  
}
