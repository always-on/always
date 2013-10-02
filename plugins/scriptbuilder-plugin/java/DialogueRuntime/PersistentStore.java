package DialogueRuntime;

import edu.wpi.always.user.UserModel;

import java.util.*;

/* Abstraction for access to file system or DB for loading/saving
   properties. */
public abstract class PersistentStore {
    /* Must be a well-defined mapping between these and DB int values,
       so assume 1st value is zero, 2nd is 1, etc. */
    //public static enum LogEventType { STATE_CHANGE, USER_INPUT, INTERNAL_ERROR}; //To do - make consistent with prior enums
	   protected int userID = -1;
	    protected int sessionID = -1;
	    protected int studyDay;
	    protected String start_date;
    public abstract void open() throws Exception; //call first
    public abstract void loadProperties(Properties p) throws Exception;
    public abstract void saveProperties(Properties p) throws Exception;
    public abstract void addLog(LogEventType eventType,String eventData) throws Exception; //enum eventType
    public abstract void close(DialogueListener.TerminationReason reason) throws Exception; //call last
    public abstract boolean isValidID(int userID);
    public abstract void setUserID(int userID);
    public abstract void setSessionID(int sessionID);
	public abstract int getUsersLoginPIN();
//	public abstract int getUsersStatus();
	public abstract ServerConstants.UsersStatus getStatus();
	 	public abstract int usersComputeStudyDay() throws Exception;
	public abstract int usersComputeStudyDay(Calendar c) throws Exception;
	public abstract void setUsersStudyDay(int studyDay);
	public abstract int addSession(ServerConstants.Media media);
	public abstract boolean alreadyLoggedInToday();
	public abstract int getUsersStudyDay();
	public abstract int saveSteps(int day, int steps);
	//public abstract int getUsersLang();
	public abstract String getUserName();
	public abstract ServerConstants.Condition getUserCondition();
	public abstract void recordGoal(int goal);
	public abstract boolean tryLoadProperties(Properties p) throws Exception;
	public void finalUpdate(Properties p) {};
	public static UserModel store;
}

