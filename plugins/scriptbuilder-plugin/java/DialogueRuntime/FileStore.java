package DialogueRuntime;

import java.util.*;
import java.io.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;

/* Handles local file-based persistent stores as alternative to DB.
   Should work exactly as in GeriTrack. */
public class FileStore extends PersistentStore {
	protected File sessionPath;
	protected File logPath;
	protected PrintStream logfile;
	protected PrintStream sessionfile;
	private ServerConstants.UsersStatus status;
	private File propsFile;
	private File defaultpropsFile;
	private String username = new String();
	public ServerConstants.Media media;
	public String filePrefix;
	public  final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	// open the log and sessions file with the APPEND flag set to true
	@Override
	public void open() throws Exception {
		if(logPath!=null) {
			FileOutputStream logFOS=new FileOutputStream(logPath,true);
			logfile=new PrintStream(logFOS);
		} 
		
		if(sessionPath!=null) {
			FileOutputStream sessionFOS=new FileOutputStream(sessionPath,true);
			sessionfile =new PrintStream(sessionFOS);
		}
	}

	@Override
	public boolean tryLoadProperties(Properties userProperties) throws Exception {
		loadProperties(userProperties);
		return true;
	}

	// closing the open files
	@Override
	public void close(DialogueListener.TerminationReason reason) {
		logfile.close();
		closeSession(reason);
		sessionfile.close();
	}

	/* Either arg can be null. */
	public FileStore(String Prefix, String sessionPath,String logPath, String propsPath, String defaultProps) throws Exception {
		this.sessionPath= new File(Prefix + sessionPath); 
		this.logPath=new File(Prefix + logPath);

		if (propsPath == null){
			System.out.println("Creating current.dat");
			propsPath = "current.dat";
		}
		this.propsFile = new File(Prefix + propsPath);
		//create new file iff it doesn't already exist at the location specified
		this.propsFile.createNewFile();
		if (defaultProps == null){
			System.out.println("Creating DEFAULT.DAT");
			defaultProps = "DEFAULT.DAT";
		}
		this.defaultpropsFile = new File(Prefix + defaultProps);
		this.defaultpropsFile.createNewFile();
		filePrefix = Prefix;
	}

	@Override
	public ServerConstants.UsersStatus getStatus() {
		return(status);
	}

	@Override
	public void loadProperties(Properties p) throws Exception {
		p.clear();
		try {
			FileInputStream FIS=new FileInputStream(propsFile);
			p.load(FIS);
			FIS.close();
		}catch(FileNotFoundException e1) {
			// if there's no current.dat, it means this is the first session. Load DEFAULT.DAT
			FileInputStream FIS=new FileInputStream(defaultpropsFile);
			p.load(FIS);
			if(p.isEmpty()){
				System.err.println("Attempted to Load Empty DEFAULT.DAT File");
				throw new FileNotFoundException();
			}
			FIS.close();
		}

		p.list(System.out);

		try {
			userID = Integer.parseInt(p.getProperty("USERID"));
		} catch (NumberFormatException e) {
			System.out.println("Userid read from properties file is not numeric" + p.getProperty("USERID") + "Using user 0 right now.");
			userID = 0;
			//throw new NumberFormatException("Userid read from properties file is not numeric " + p.getProperty("USERID") + "Using user 0 right now.");
		}
		start_date = p.getProperty("START_DATE");
		if (p.containsKey("NAME")) {
			username = p.getProperty("NAME");
		}
		
		if (p.containsKey("NUM_SESSIONS")) {
			sessionID = Integer.parseInt(p.getProperty("NUM_SESSIONS")) + 1;
		} else {
			sessionID = 1;
		}
		p.setProperty("NUM_SESSIONS",""+sessionID);
		System.out.println("this session num set to  " + sessionID);
	}


    // save the properties to both the current file (i.e. current.dat or whatever) and the
    // date-stamped file, because current-dat is overwritten each session
    @Override
	public void saveProperties(Properties p) throws Exception {
    	FileOutputStream FOS=new FileOutputStream(propsFile);
    	p.store(FOS,"RAG FileStore");
    	FOS.close();
    	
    	TDate today = new TDate();
    	String filename = filePrefix + "PROPS\\backup_" + today.toString2();
    	FileOutputStream FOs=new FileOutputStream(filename);
    	p.store(FOs,"RAG FileStore");
    	FOs.close();
    	
    	String ord =  p.getProperty("THIS_SESSION_INT");
    	String replay = filePrefix + "PROPS\\RESTART_" + ord;
    	FOs=new FileOutputStream(replay);
    	p.store(FOs,"RAG FileStore");
    	FOs.close();
    }


	@Override
	public void addLog(LogEventType eventType,String eventData) throws Exception {
		if(logfile!=null)
			logfile.println(""+sessionID + "\t" + ""+new Date()+"\t"+eventType+"\t"+eventData);
	}

	public int getUserID() { 
		return userID;
	}

	@Override
	public int usersComputeStudyDay() { 
		TDate tdateNow=new TDate();
		System.out.println("start date is " + start_date);
		TDate studyStart=new TDate(start_date);
		int studyDay=TDate.daysBetween(studyStart,tdateNow);

		this.studyDay = studyDay; 
		return studyDay;
	}


	@Override
	public int usersComputeStudyDay(Calendar c) { 
		TDate tdatestart = new TDate(start_date);

		TDate check =new TDate(c);
		int diff = TDate.daysBetween(tdatestart,check);
		System.out.println("start date is " + start_date + " check date is " + check.toString() + " diff is " + diff);

		return diff;
	}

	// In filestore, this method doesn't really make sense. What to do?
	@Override
	public boolean  isValidID(int userIN) {

		return (userIN == userID);
	}


	@Override
	public int getUsersStudyDay() { 
		return studyDay;
	}

	@Override
	public String getUserName() { 
		return username;	
	}

	public String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}

	// Create an entry for the sessions file, with just an open time
	//   SESSIONs file entry:  SESSION_ID, USER_ID, enum MEDIA, START_TIME, END_TIME,    enum END_REASON
	// This assumes it is being run after the properties have been loaded, to get the value of lastSession 
	// DKB 0209: it was using the interaction number as the session number, but that caused duplicate session 
	// entries when the interaction number did not advance (like in tryAgainTomorrow). Changed to use the property 
	// NUM_SESSIONS, which is incremented every time
	@Override
	public int addSession(ServerConstants.Media media) {        	
		this.media = media;
		int returnval = 1;

		try {
			sessionfile.println("");
			sessionfile.print(sessionID + "\t" + userID + "\t" + media.name() + "\t" + now() + "\t");
		} catch (Exception e) {
			returnval = 0;
		}

    	return returnval;
    }
    
 //writes to the session record with end time set to now
    //TODO: make the end reason be in synch with 
    public void closeSession(DialogueListener.TerminationReason endReason) {
       	try {
    		sessionfile.println(now() + "\t" + endReason);
    	} catch (Exception e) {
    		System.out.println("FileStore.addSession could not write to sessions file " + e.toString());
    	}
    }
    


	public void closeLog() throws Exception {
		if(logPath!=null)
			logfile.close();
	}

	// Print error message: the userID is set from properties
	@Override
	public void setUserID(int userIDIn) {
		System.err.println("ERROR: the server tried to reset the userid to " +  userIDIn);
	}

	@Override
	public  int getUsersLoginPIN() {
		return 0;
	}

	/**
	 * @deprecated The status is an enum, we should not revert it to an int
	 */
	public int getUsersStatus() {
		return 0;
	}


	@Override
	public  void setUsersStudyDay(int studyDayIn) {
		studyDay = studyDayIn;
	}

	@Override
	public  boolean alreadyLoggedInToday() {
		
		return false;
	}

	// if desired, save the steps out to persistentstore as soon as they are known. 
	@Override
	public int saveSteps(int day, int steps) {
		return 1;
	}

	//incomplete function
	// save the goal when the user adopts a goal
	// goal should be associated with current studyDay
	@Override
	public void recordGoal(int goal) {

	}

	// incomplete function
	@Override
	public ServerConstants.Condition getUserCondition() {
		return (ServerConstants.Condition.INTERVENTION);
	}

	@Override
	public void setSessionID(int sessionID) {
		// TODO Auto-generated method stub
		
	}

}