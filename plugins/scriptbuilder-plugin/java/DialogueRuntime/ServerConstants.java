package DialogueRuntime;

import java.util.regex.Pattern;

public  class ServerConstants {
    public static final int MAX_WEBAGENT_CHECKBOX_ITEMS = 20;
    public static final int UNKNOWN_INT = -1;
    public static final int PedoTimeOut = 65000;
    public static enum messagePlacement { OPENING, CLOSING, EITHER};
    public static final  Pattern OMRON_ERROR_P = Pattern.compile("<ERROR\\s+CODE=\"(-?[0-9]+)\"");
    public static final  Pattern OMRON_STEPS_MSG = Pattern.compile("<STEPS");
    public enum assignedTo {PATIENT, PROVIDER; };

     public static final Pattern OMRON_DAY_P = Pattern.compile("<DAY\\s+DATE=\"([0-9]+)/([0-9]+)/([0-9]+)\"\\s+STEPS=\"(-?[0-9]+)\"\n?");
     public static final String READ_PED_MSG = new String("<GET_STEPS/>");
     public static String DAYS_OF_WEEK[] = {
         "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"
     };
    
     public static String MONTHS[] = {
         "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER",
         "NOVEMBER", "DECEMBER"
     };
    
     public static enum Language { ENGLISH , SPANISH; };
    
     public static enum UsersStatus { ENROLLED , DROPPED , COMPLETED , INACTIVE, TABLETPHASE, KIOSK, POSTTABLET, UNKNOWN; };
     public static enum SubCondition { RELATIONAL, NONRELATIONAL};
     public static enum SubCondition2 { TRIVIA, NOTRIVIA};
             public static enum Media { ECA , IVR, WEBAGENT; };
     public static enum Condition { INTERVENTION,CONTROL, UNKNOWN };
     public static enum INTIMACYLEVEL { STRICTLYPROFESSIONAL, CASUALPROFESSIONAL, CASUALFRIEND, CLOSEFRIEND};
    
     // PRIVATE //

      /**
      *  The caller should be prevented from constructing objects of
      * this class, by declaring this private constructor.
      */
      protected ServerConstants(){
        //no public ctor - prevents construction by the caller
      }
}