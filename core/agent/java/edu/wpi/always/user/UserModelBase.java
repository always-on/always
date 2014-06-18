package edu.wpi.always.user;

import edu.wpi.always.*;
import edu.wpi.disco.rt.util.Utils;

public abstract class UserModelBase implements UserModel {
   
   public final static Object LOCK = new Object();
   
    /**
    * <code>
    * try { 
    *    UserModelBase.INHIBIT_SAVE = true;
    *    ...updates to user model...
    * } finally { 
    *    UserModelBase.INHIBIT_SAVE = false;
    *    UserModelBase.saveIf()
    * </code>
    */
   public static boolean INHIBIT_SAVE;

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
   public void setSessions (int sessions) { setProperty(SESSIONS, sessions); } 

   @Override
   public long getStartTime () { return getLongProperty(START_TIME); }   
   
   @Override
   public void setUserName (String name) {
      // need to wait until user object created before setting properties
      if ( getStartTime() == 0 ) setProperty(START_TIME, Always.DATE.getTime());
      if ( getSessions() == 0 ) setSessions(1);
   }

   @Override
   public Closeness getCloseness () {
      if ( userName.isEmpty() ) return Closeness.STRANGER;  // for uninitialized model
      String closeness = getProperty(CLOSENESS);
      return closeness == null ? Closeness.STRANGER : Closeness.valueOf(closeness);
   }

   @Override
   public void setCloseness (Closeness closeness) { 
      String name = closeness.name();
      setProperty(CLOSENESS, name);
      Utils.lnprint(System.out, "Setting closeness to "+name);
   }
  
}
