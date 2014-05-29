package edu.wpi.always.user;

import edu.wpi.always.*;
import edu.wpi.disco.rt.util.Utils;

public abstract class UserModelBase implements UserModel {
   
   public final static Object LOCK = new Object();
   
   public static void saveIf () { 
      if ( !INHIBIT_SAVE ) {
         UserModel model = Always.THIS.getUserModel();
         // never save when user name is empty
         if ( !model.getUserName().isEmpty() ) model.save();
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
   public void setUserName (String name) {
      // wait until user object created before setting property
      setProperty(START_TIME, Always.DATE.getTime());
   }

   @Override
   public void load () {
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
