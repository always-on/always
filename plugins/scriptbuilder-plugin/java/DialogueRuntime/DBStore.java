package DialogueRuntime;

import edu.wpi.cetask.Utils;
import java.util.*;
import java.io.*;
import java.sql.*;
import java.sql.Date;

/* Access to standard DB tables. Subclasses will extend for specialized
   apps. */

public class DBStore extends PersistentStore {
	protected static  String connectionPropertiesFile;
	protected Properties connectionProperties = new Properties();
	protected InputStream inStream;
	protected Connection DBCon;
	protected Statement stmt;
	protected Statement stmt2;
	private boolean open = false;
	private boolean testing = false;

	//FOR TESTING ONLY
    public DBStore() throws IOException {
    	testing = true;
    }
	
    public DBStore(InputStream in) throws IOException {
    	connectionProperties.load(in);
    }
	
	// Initializes connection with the configuration properties file
	public DBStore(String connectionPropertiesFilename) throws Exception {
		connectionPropertiesFile = connectionPropertiesFilename;
		File propsFile = new File(connectionPropertiesFile);
		FileInputStream FIS;

		FIS = new FileInputStream(propsFile);
		connectionProperties.load(FIS);
		}

	// Establishes connection to DB
	@Override
	public void open() throws Exception {
		if(testing)
			return;
		if (!this.isOpen()) {
			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
			} catch (Exception e) {
				System.err.println("Could not load SQL drivers:");
				Utils.rethrow(e);
			}
			String serverAddress = "jdbc:mysql://"
				+ connectionProperties.getProperty("serverIP") 
				+ connectionProperties.getProperty("port") 
				+ connectionProperties.getProperty("dbName") + "?user="
				+ connectionProperties.getProperty("userName")
				+ "&password="
				+ connectionProperties.getProperty("sPassword");
		DBCon = DriverManager.getConnection(serverAddress);
			stmt = DBCon.createStatement();
			stmt2 = DBCon.createStatement();
			this.setOpen(true);
		}
	}
	
@Override
public boolean tryLoadProperties(Properties userProperties) throws Exception {
	System.out.println("Inside Try Load Properties ...");
    	if (userID > -1 && isValidID(userID)){
    		loadProperties(userProperties);
    		return true;
    	}
    	return false;
    }
    
	// Closes the DB connection after checking if the Current Session is closed
	// DB 8/08: there won't be a session unless the user successfully logged in, so 
	//                    check that there is a sessionID before closing it
	@Override
	public void close(DialogueListener.TerminationReason reason) throws Exception {
		if (sessionID > 0) {
			ResultSet rs = stmt.executeQuery("SELECT end_time FROM sessions WHERE session_id="
						+ sessionID);
			if (rs.next()) {
				if (rs.getInt(1) == 0) {
					this.closeSession(reason);
				}
			}
			stmt.close();
			DBCon.close();
		}
		
	}
		
	public String pickProperties(String userID, String prefix) throws Exception {
		//returns the suffix of a random property with the given prefix
		ResultSet rs = stmt.executeQuery("SELECT PROPERTY FROM properties WHERE USER_ID='" + userID + "' AND PROPERTY LIKE '" + prefix + "%'");
		ArrayList<String> array = new ArrayList<String>();
		while (rs.next()) {
			array.add(rs.getString(1));
		}
		if (array.size()==1 && array.get(0).toUpperCase().endsWith("_CUR")){
			return null;
		}
		while (array.size()>0){
			int num = (int)(Math.random()*array.size());
			if (!array.get(num).toUpperCase().endsWith("_CUR")){
				return array.get(num);
			}
		}
		return null;
	}
	
	public String pickProperties(String userID, String prefix, String suffix) throws Exception {
		//returns full prefix of a random property with the given prefix and suffix
		ResultSet rs = stmt.executeQuery("SELECT PROPERTY FROM properties WHERE USER_ID='" + userID + "' AND PROPERTY LIKE '" + prefix + "%" + suffix + "'");
		ArrayList<String> array = new ArrayList<String>();
		while (rs.next()) {
			array.add(rs.getString(1));
		}
		if (array.size()>0){
			int num = (int)(Math.random()*array.size());
			return array.get(num);
		}
		return null;
	}
	
	public String pickPropertiesValue(String userID, String prefix, String suffix) throws Exception {
		ResultSet rs = stmt.executeQuery("SELECT Value FROM properties WHERE USER_ID='" + userID + "' AND PROPERTY LIKE '" + prefix + "%" + suffix + "'");
		ArrayList<String> array = new ArrayList<String>();
		while (rs.next()) {
			array.add(rs.getString(1));
		}
		if (array.size()>0){
			int num = (int)(Math.random()*array.size());
			return array.get(num);
		}
		return null;
	}
	
	public String pickPropertyValue(String userID, String prefix, String suffix) throws Exception {
		ResultSet rs = stmt.executeQuery("SELECT Value FROM properties WHERE USER_ID='" + userID + "' AND PROPERTY='" + prefix + "_" + suffix + "'");
		ArrayList<String> array = new ArrayList<String>();
		if (rs.next()){
			return rs.getString(1);
		}
		return null;
	}
	
	@Override
	public void setUserID(int ID) {
		userID = ID;
	}

	public int getUserID() {
		return userID;
	}

	@Override
	public String getUserName() {
		String name = "";
		String query = "SELECT name FROM users WHERE user_id=" + userID;
		try {
			ResultSet rs = stmt.executeQuery(query);
			if (!rs.next())
				throw new SQLException("SQLException: UserID:" + userID
						+ " not set?");
			name = rs.getString("name");
		} catch (SQLException e) {
			System.out.println(e);
			System.out.println("Exception thrown while attempting this query: "
					+ query);
			System.out
					.println("Unable to find the Name for the User " + userID);
		}
		return name;
	}

	@Override
	public ServerConstants.Condition getUserCondition() {
		ServerConstants.Condition cond = ServerConstants.Condition.UNKNOWN;
		int c = 0;
		String query = "SELECT study_condition FROM users WHERE user_id="
				+ userID;
		try {
			ResultSet rs = stmt.executeQuery(query);
			if (!rs.next()) {
				throw new SQLException("SQLException: UserID:" + userID
						+ " not set?");
			}
			ServerConstants.Condition[] values = ServerConstants.Condition
					.values();
			c = Integer.parseInt(rs.getString("study_condition"));
			cond = values[c];
		} catch (SQLException e) {
			System.out.println(e);
			System.out
					.println("The exception was thrown while attempting this query: "
							+ query);
			System.out
					.println("getUserCondition: Unable to find the UserCondition for the User "
							+ userID);
		} catch (NumberFormatException nfe) {
			System.out.println(nfe);
			System.out.println("number format exception getting UserCondition:"
					+ nfe);
		}

		return cond;
	}

	@Override
	public int getUsersStudyDay() {
System.out.println("getUsersStudyDay, id="+userID);		
		String query = "SELECT study_day FROM users WHERE user_id=" + userID;
		try {
			ResultSet rs = stmt.executeQuery(query);
			if (!rs.next())
				throw new SQLException("SQLException: UserID:" + userID
						+ " not set?");
			studyDay = rs.getInt("study_day");
			//TWB - in some cases, this is returning a bogus study day for the initialization (-1) case.
			if(studyDay>3650) studyDay=-1;
System.out.println("DB studyday="+studyDay);
		} catch (SQLException e) {
			System.out.println(e);
			System.out
					.println("The exception was thrown while attempting this query: "
							+ query);
			System.out.println("Unable to find the Study Day for the User "
					+ userID);
		}
		return studyDay;
	}

	// Gets the User Status which is an Enum
	@Override
	public ServerConstants.UsersStatus getStatus() {
		ServerConstants.UsersStatus us = ServerConstants.UsersStatus.UNKNOWN;
		String query = "SELECT status FROM users WHERE user_id=" + userID;
		try {
			ResultSet rs = stmt.executeQuery(query);
			if (!rs.next()) {
				throw new SQLException("SQLException: UserID:" + userID
						+ " not set?");
			}
			// Set the instance of Enum to a String Value
			us = ServerConstants.UsersStatus.valueOf((rs.getString("status"))
					.toUpperCase());
		} catch (SQLException e) {
			System.out.println(e);
			System.out
					.println("The exception was thrown while attepting this query: "
							+ query);
			System.out.println("Unable to find the Status for the User "
					+ userID);
		}
		return (us);
	}

	// Gets the User Status which is an Enum. We really shouldn't be converting
	// it to an int, so please use the above method instead
	// public int getUsersStatus() {
	// int usersStatus = -1;
	// Create an instance of the Enum
	// ServerConstants.UsersStatus us;
	// try{
	// ResultSet rs = stmt.executeQuery("SELECT status FROM users WHERE
	// user_id=" + userID);
	// if(!rs.next())
	// throw new SQLException("SQLException: UserID not set?");
	// Set the instance of Enum to a String Value
	// us =
	// ServerConstants.UsersStatus.valueOf((rs.getString("status")).toUpperCase());
	// Get the corresponding int
	// usersStatus = us.ordinal();
	// }catch(SQLException e){
	// System.out.println("Unable to find the Status for the User " + userID);
	// }
	// return usersStatus;
	// }

	public String getUsersLoginID() {
		String loginID = "";
		String query = "SELECT login_id FROM users WHERE user_id=" + userID;
		try {
			ResultSet rs = stmt.executeQuery(query);
			if (!rs.next())
				throw new SQLException("SQLException: UserID:" + userID
						+ " not set?");
			loginID = rs.getString("login_id");
		} catch (SQLException e) {
			System.out.println(e);
			System.out
					.println("The exception was thrown while attepting this query: "
							+ query);
			System.out.println("Unable to find the Login ID for the User "
					+ userID);
		}
		return loginID;
	}

	@Override
	public int getUsersLoginPIN() {
		int loginPIN = 0;
		String query = "SELECT login_pin FROM users WHERE user_id=" + userID;
		try {
			ResultSet rs = stmt.executeQuery(query);
			if (!rs.next())
				throw new SQLException("SQLException: UserID:" + userID
						+ " not set?");
			loginPIN = rs.getInt("login_pin");
		} catch (SQLException e) {
			System.out.println(e);
			System.out
					.println("The exception was thrown while attepting this query: "
							+ query);
			System.out.println("Unable to find the Login PIN for the User "
					+ userID);
		}
		return loginPIN;
	}

	public String getUsersCharacter() {

		String ch = new String();
		String query = "SELECT `character` FROM users WHERE user_id=" + userID;
		try {
			ResultSet rs = stmt.executeQuery(query);
			if (!rs.next())
				throw new SQLException("SQLException: UserID:" + userID
						+ " not set?");
			ch = rs.getString("character").toUpperCase();

		} catch (SQLException e) {
			System.out.println(e);
			System.out
					.println("The exception was thrown while attepting this query: "
							+ query);
			System.out.println("Unable to find the Character for the User "
					+ userID);
		}
		return ch;
	}


	// Makes START_DATE today and STUDY_DAY -1
	public void usersStartStudy() {
		System.out.println("Setting the users study start date");
		String query = "";
		try {
			query = ("UPDATE `users` SET `Start_Date`=DATE(NOW()), `Study_Day`=0 WHERE `User_ID`=" + userID);
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println(e);
			System.out
					.println("The error was encountered while running the query: "
							+ query);
			System.out
					.println("Unable to update the Start Date and Study Day for the User "
							+ userID);
		}

		try {
			query = ("INSERT into `properties` (`User_ID`,`property`,`value`) VALUES (" + userID + ",\"START_DATE\",DATE(NOW()))");
	//		query = ("INSERT into `properties` SET `property`=START_DATE, `value`=DATE(NOW) WHERE `User_ID`=" + userID);
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println(e);
			System.out
					.println("The error was encountered while running the query: "
							+ query);
			System.out
					.println("Unable to update the Start Date for the User in the properties table "
							+ userID);
		}
	}
    
    @Override
	public void setUsersStudyDay(int day) {
    String query = ("UPDATE `users` SET `Study_Day`=" + day + " WHERE `User_ID`=" + userID);
    try{
        stmt.executeUpdate(query);
    }
    catch(SQLException e)
    {
    	System.out.println(e);
    	System.out.println("The exception was encountered while running the query: " +query);
		System.out.println("Unable to update the Study Day for the User " + userID);
    }
    studyDay=day;
    } 
     
    public void setUsersStatus(ServerConstants.UsersStatus us) {
    String query="";
    try{
        query = ("UPDATE `users` SET `Status`='" + us.name().toUpperCase() + "' WHERE `User_ID`=" + userID);
        stmt.executeUpdate(query);
	}catch(SQLException ex){
		System.out.println(ex);
		System.out.println("The exception was encountered while running the query: " + query);
		System.out.println("Unable to update the Status for the User " + userID);
    }   	
    }

	// Returns today - START_DATE
	@Override
	public int usersComputeStudyDay() {
		String query = "SELECT (SELECT DATEDIFF(DATE(NOW()), DATE(`Start_Date`))) FROM users WHERE user_id="
				+ userID;
		int study = -1;
		try {
			ResultSet rs = stmt.executeQuery(query);
			if (!rs.next())
				throw new SQLException("SQLException: UserID:" + userID
						+ " not set?");
			study = rs.getInt(1);
		} catch (SQLException e) {
			System.out.println(e);
			System.out
					.println("The error was encountered while running the query: "
							+ query);
			System.out.println("Unable to compute the Study day for the User "
					+ userID);
		}
		return study;
	}

	// Returns the input date - START_DATE
	// Returns -1 on error, or 0 for all dates prior to and equal to start date.
	@Override
	public int usersComputeStudyDay(Calendar c)  throws Exception{
/* stub - was
		int studyDay = -1;
		Date date = new Date(c.getTimeInMillis());
		return studyDay;
*/
	 	TDate check =new TDate(c);
	 	int diff = -1;
 	String query = "SELECT `Start_Date` FROM users WHERE user_id=" + userID;
	try {
		ResultSet rs = stmt.executeQuery(query);
		if (!rs.next()) {
			throw new SQLException("SQLException: UserID:" + userID + " not set?"); 
		}
		String start_date = rs.getString(1);
		//System.out.println("users start_date is "+start_date);
		TDate tdatestart = new TDate(start_date);
		//System.out.println("   TDate version="+tdatestart);
		diff = TDate.daysBetween(tdatestart,check);
		//System.out.println("usersComputeStudyDay: diff("+check+","+tdatestart+") = "+diff);
	} catch (SQLException e) {
		System.out.println(e);
		System.out.println("The error was encountered while running the query: " + query);
		System.out.println("Unable to compute the Study day for the User " + userID);
		throw e;
		//twb - really should propagate this exception!
	}
	return diff;
	}
	
	// DEBUG ONLY!: This method is only to be used to adjust the start date for internal testing
	public void adjustStartDate(int offset) {
		String query = "SELECT `Start_Date` FROM users WHERE user_id=" + userID;
		try {
			ResultSet rs = stmt.executeQuery(query);
			if (!rs.next()) {
				throw new SQLException("SQLException: UserID:" + userID + " not set?"); 
			} else {
				java.sql.Date start_date = rs.getDate(1);
				System.out.println("Adjusting start date for userid: " + userID + " old date: " + start_date);
				query = "UPDATE users set start_date=(select '" + start_date + "' - INTERVAL " + offset + " day) where user_id=" + userID;
				stmt.executeUpdate(query);
			} 
		} catch (SQLException e) {
			System.out.println("Error adjusting user startdate:");
			e.toString();
			e.printStackTrace();
		}
	
	}
 
    // Sets START_TIME to now, returns ID
    @Override
	public int addSession(ServerConstants.Media media) { 
    
    String query="";
    try{
    
        query = ("INSERT INTO sessions (User_ID,Media,Start_Time) VALUES (" + userID + ",'" + media.name().toUpperCase() + "', NOW() )");
        stmt.executeUpdate(query);
        ResultSet rs = stmt.executeQuery("Select MAX(session_ID) FROM sessions WHERE user_id=" + userID + " AND END_TIME=0");
    	if(!rs.next())
    		throw new SQLException("SQLException: Could not add entry to session table. UserID:" + userID + " not set?");
        sessionID = rs.getInt(1);
	}catch(SQLException ex){
		System.out.println(ex);
		System.out.println("The exception was encountered while running the query: " + query);
		System.out.println("Unable to create new session for the User " + userID);
    } 
	return sessionID;
    }
    
    // DB 8/08: there won't be a session unless the user successfully logged in, so 
	//                    check that there is a sessionID before closing it
    public void closeSession(DialogueListener.TerminationReason endReason) {
    String query="";
    if (sessionID > 0) {
    	try{
    		query = ("UPDATE `sessions` SET `END_TIME`= NOW(), `END_REASON`='" + endReason + "' WHERE `Session_ID`=" + sessionID);
    		stmt.executeUpdate(query);
    	} catch(SQLException e) {
    		System.out.println(e);
    		System.out.println("The exception was encountered while running the query: " + query);
    		System.out.println("Unable to end session for the User " + userID);
    	}  
    }
    }    
		
    @Override
	public void loadProperties(Properties p) throws Exception {
    	
    String property;
    String value;
    String query="";
    p.clear();
    try {
    	query="SELECT Property,Value FROM properties WHERE User_ID=" + userID;
    	ResultSet RS = stmt.executeQuery(query); 
    	while(RS.next()) {
    		property = RS.getString(1);
    		value = RS.getString(2);
    		p.setProperty(property, value);
    	}
    	System.out.println("Properties object initialized from DB properties table ");
    }	catch (SQLException e) {
    	System.out.println(e);
    	System.out.println("The exception was encountered while running the query: " + query);
    	System.out.println("DBSTore Error loading properties " + e.toString());
    }
    }
    
    @Override
	public void saveProperties(Properties p) throws Exception {
	System.out.println("Saving properties from p to DB. Properties list: " );
    
	//Thread.dumpStack();
	
    if(p!=null) {
   		//stmt.executeUpdate("DELETE FROM properties WHERE User_ID=" + userID);
   		
 	PreparedStatement pstmt = DBCon.prepareStatement("REPLACE INTO properties (`User_ID`,`Property`,`Value`) VALUES (?,?,?)");
      Enumeration<Object> keys = p.keys();
  	while (keys.hasMoreElements()) {
  				pstmt.setInt(1,userID);
  				String property = (String)keys.nextElement();
  				pstmt.setString(2, property);
	            pstmt.setString(3, (String)p.get(property));		
	        try {     
	        	pstmt.executeUpdate();
	        } catch (SQLException e) {
	           System.out.println("SQL exception " + e);
	           System.out.println("on query " + pstmt.toString());
	           System.out.println(e.getStackTrace());
	        }
	    }
    }
    }
        // when setting the value of a particular property, go on and save it in case server crashes
    public void saveProperty(String property, String value) throws Exception {
    	System.out.println("Saving property " + property + " to DB." );
       
        try {    	
     	stmt.executeUpdate("REPLACE INTO properties (`User_ID`,`Property`,`Value`) VALUES (" + userID + ",'" + property + "', '" + value + "')");
    	        } catch (SQLException e) {
    	           System.out.println("SQL exception " + e);
    	           System.out.println("on query " + e.getMessage());
    	           System.out.println(e.getStackTrace());
    	        }
        }
    
// DB 8/08 changed to preparedstatement to eliminate SQL syntax errors when the eventData contains single quote
    @Override
	public void addLog(LogEventType eventType,String eventData) throws Exception {
    	if(testing)
    		return;
        try{
        	PreparedStatement pstmt = DBCon.prepareStatement("INSERT INTO log (`User_ID`,`Session_ID`,`Event_Type`,`Event_Data`) VALUES (?,?,?,?)");
	      	pstmt.setInt(1,userID);
	      	pstmt.setInt(2,sessionID);
	      	pstmt.setInt(3,eventType.ordinal());
	      	pstmt.setString(4,eventData);
	      	pstmt.executeUpdate();
	    }catch(SQLException e){
	    	System.out.println(e.toString());
			System.out.println("Unable to create new log for the session " + sessionID);
			throw new Exception("SQL error writing to the LOG table");
	    } 
	}
	 
    public void addUserResponse(int surveyID,int question,int response) {
	    String query="";
	    try{
	        query = ("INSERT INTO survey_responses (Survey_Id,Question_Num,Response_Num) VALUES (" + surveyID + ",'" + question + "','" + response + "')");
	        stmt.executeUpdate(query);
	    }catch(SQLException e){
	    	System.out.println(e);
	    	System.out.println("The exception was encountered while running the query: " + query);
			System.out.println("Unable to add a response for the survey " + surveyID);
	    }     	
    }
    
    //LMP: adding methods to log bugs (for test-harness)
    public void addBugReport(String script, String state, String bugDescription, String lastAction, String flashVersion)
    {
    	String query = "";
    	
    	try{
    		query = "insert into test_bugs (script, state, bug_description, last_action, flash_version, session_id) " +
    				" values ('"+script+"', '"+state+"', '"+bugDescription+"', '"+lastAction+"', '"+flashVersion+"', "+sessionID+")";
    		
    		System.out.println("insert query: "+query);
    		stmt.executeUpdate(query);	
    	}
    	catch(SQLException e){
    		System.out.println("The exception was encountered in addBugReport");
	    	e.printStackTrace();
	    }
    }
    
    // Returns a true value if the UserID exists or false otherwise
    @Override
	public boolean isValidID(int ID) {
    	System.out.println("Validating userid");
    try{
        ResultSet rs = stmt.executeQuery("SELECT user_id FROM users");
        while(rs.next())
        	if(rs.getInt("user_id") == ID) {
        		System.out.println("Userid is valid");
        		return true;
        	}
    }catch(SQLException e){
		System.out.println("validating the user ID " + ID + " got exception" + e.toString());
		 return false;
		 }
    return false;
    }
        
    // Checks if there are any logins today else returns a false
    @Override
	public boolean alreadyLoggedInToday() {
    String query="";
    try{
    	query="SELECT DATEDIFF(DATE(NOW()),DATE(MAX(start_time))) FROM sessions WHERE User_ID=" + userID;
    	ResultSet rs = stmt.executeQuery(query);
    	if(!rs.next())
    		throw new SQLException("SQLException: UserID not set?");
    	String numofdayssincelastlogin = rs.getString(1);
    	if(numofdayssincelastlogin == null)
    		return false;
    	if(Integer.parseInt(numofdayssincelastlogin) == 0)
    		return true;
    	System.out.println(numofdayssincelastlogin);
    }catch(SQLException e){
    	System.out.println(e);
    	System.out.println("The exception was encountered while running the query: " + query);
    	System.out.println("Unable to check the last login for the user " + userID);
    }
    return false;
    }
    
  	 
    /**
	 * update the steps table for the given user and studyday 
	 * @deprecated 
	 *   this method is duplicated by recordsteps, use that one instead
	 */
    @Override
	public  int saveSteps(int studyday, int steps){
    	String query = new String();
    	int returnval = -1;
    	try{
    	    query  = new String("REPLACE into PA_STEPS (USER_ID, STUDY_DAY, STEP_COUNT) VALUES ("+userID+ "," + studyday + "," + steps + ")");
	    	stmt.executeUpdate(query);
	    	returnval = 0;
	   	} catch(SQLException e){
	    	System.out.println("SQL error on saveSteps  " + e);
	    	System.out.println("Query was " + query);
	    }   
	   	return returnval;
   	}
     

	// Add an entry to the ALERTS table
	public void setAlert(String message, int type) {
		String query = new String();
		int newID = 0;
		try {
			query = ("SELECT * FROM alerts");
			ResultSet rs = stmt.executeQuery(query);
	        while(rs.next()){
	        	   //find the largest alert ID so far
	        	if(rs.getInt("id") >= newID) {
	        		newID = rs.getInt("id")+1;
	        	}
			}
			query = ("INSERT INTO alerts (ID, USER_ID ,DATE, TYPE, TEXT, DISMISSED) VALUES ("
					+ newID + ", " + userID + ", DATE(NOW()), " + type + ", \"" + message + "\", \"" + "0" + "\")");
			int returnval = stmt.executeUpdate(query);
			System.out.println("return val from updating alerts table="
					+ returnval);

	    } catch (SQLException e) {
	    		System.out.println(e);
	    		System.out.println("The exception was encountered while running the query: " + query);
	     		System.out.println("SQLException updating alerts table: UserID not set?" + userID + e);
	    }
    }

    //caches steps for user in memory for quick retrieval.
    private int[] stepsCache;
    private int[] goalsCache;
    
    public int[] getStepsCache(){
    	if (stepsCache == null) { 	
    		cacheStepsAndGoals();
    	}
    	return stepsCache;
    }

    public int[] getGoalsCache(){
    	if (goalsCache == null) { 	
    		cacheStepsAndGoals();
    	}
    	return goalsCache;
    }
    
    public void updateCache(int day, int steps) {
    	if (stepsCache != null) {
    		if ((day < stepsCache.length) &&
    			  (day > -1)) {
    		stepsCache[day] = steps;
    	}
    	}
    }
    
    public void updateGoalsCache(int day, int steps) {
    	if (goalsCache != null) {
    		if ((day < goalsCache.length) &&
      			  (day > -1)) {	
    			goalsCache[day] = steps;
    		}
    	}
    }
    
 	 // update the steps table for the given user and studyday
   public  void recordSteps(int studyday, int steps){
	   if (studyday < 1){
		   return;
	   }
	   	String query = new String();
	   	try{
	   		query = new String("SELECT * FROM PA_STEPS WHERE USER_ID="+userID+" AND STUDY_DAY="+studyday);
			ResultSet rs = stmt.executeQuery(query);
	        while(rs.next()){
	        	   //find the largest alert ID so far
	        	if(rs.getInt("STEP_COUNT") >= steps) {
	        		return;
	        	}
			}
	   	    query  = new String("REPLACE into PA_STEPS (USER_ID, STUDY_DAY, STEP_COUNT) VALUES ("+userID+ "," + studyday + "," + steps + ")");
	   	    stmt.executeUpdate(query);
   	   	} catch(SQLException e){
   	    	System.out.println("SQL error on saveSteps  " + e);
   	    	System.out.println("Query was " + query);
   	    }   
   	   	
   	   	if(stepsCache!=null) {
   	   		updateCache(studyday,steps);
	   	}
  	}

   public  void recordSteps(int steps){
	   	String query = new String();
	   	int studyday = getUsersStudyDay();
	   	try{
	   		query = new String("SELECT * FROM PA_STEPS WHERE USER_ID="+userID+" AND STUDY_DAY="+studyday);
			ResultSet rs = stmt.executeQuery(query);
	        while(rs.next()){
	        	   //find the largest alert ID so far
	        	if(rs.getInt("STEP_COUNT") >= steps) {
	        		return;
	        	}
			}
	   	    query  = new String("REPLACE into PA_STEPS (USER_ID, STUDY_DAY, STEP_COUNT) VALUES ("+userID+ "," + studyday + "," + steps + ")");
	   	    stmt.executeUpdate(query);
   	   	} catch(SQLException e){
   	    	System.out.println("SQL error on saveSteps  " + e);
   	    	System.out.println("Query was " + query);
   	    }   
   	   	
   	   	if(stepsCache!=null) {
   	   	updateCache(studyday,steps);
	   	}
  	}
   
   public void addDataFlag(int studyday, int steps, int dataflag) {
	   	String query = new String();
	   	try{
	   	    query  = "UPDATE PA_STEPS set GOOD_DATA=" + dataflag + " where user_id=" + userID + " and study_day="+ studyday;
	   	    stmt.executeUpdate(query);
   	   	} catch(SQLException e){
   	    	System.out.println("SQL error on saveSteps  " + e);
   	    	System.out.println("Query was " + query);
   	    }   
   	   	
   }
   
   public void clearCache() {
	   System.out.println("Clearing the steps and goals cache");
	stepsCache = null;
	goalsCache = null;
   }
   
 public void cacheStepsAndGoals() {

    	int studyDay=getUsersStudyDay();
    	stepsCache=new int[studyDay+1];
    	goalsCache=new int[studyDay+1];
		for(int i=0;i<stepsCache.length;i++){
			stepsCache[i]=-1;
			goalsCache[i]=-1;
		}
		try {
			int i=0;
			int lastGoal=0;
			int next = 0;
		    String query="SELECT STUDY_DAY,STEP_COUNT FROM PA_STEPS WHERE User_ID=" + userID;
		    ResultSet RS = stmt.executeQuery(query); 
		    while(RS.next()) {
		    	next = RS.getInt(1);
		    	if (next>-1 && next<stepsCache.length){
		    		stepsCache[RS.getInt(1)]= RS.getInt(2);
		    	}
		    }
		    query="SELECT STUDY_DAY,GOAL FROM PA_STEP_GOALS WHERE User_ID=" + userID;
		    RS = stmt.executeQuery(query);
		    while(RS.next()) {
		    	i = RS.getInt(1);
		    	if (i>-1 && i<goalsCache.length){
		    		goalsCache[i]= RS.getInt(2);
		    		if (goalsCache[i]>0){
		    			lastGoal=goalsCache[i];
		    			System.out.println("Last Goal is: "+lastGoal);
		    		}
		    	}
		    }
		    if (lastGoal>0){
		    	goalsCache[goalsCache.length-1]=lastGoal;
		    }
		} catch (SQLException e) {
		    System.out.println("cache ex: "+e);
		}
		System.out.println("\nCached steps...");
		for(int j=0;j<stepsCache.length;j++) {
			System.out.println("Day "+j+" Steps = "+stepsCache[j]);
			System.out.println("Day "+j+" Goals = "+goalsCache[j]);
		}
    }

 
    public int getSteps(int dayToGet) {
    	if(stepsCache==null) cacheStepsAndGoals();
    	int highestIndex = stepsCache.length - 1;
    	if (dayToGet > highestIndex || dayToGet < 0) {
    		return -1;
    	} else {
    		return stepsCache[dayToGet];
    	}
    }

    public int getGoal(int studyDay) {
    	if(goalsCache==null) cacheStepsAndGoals();
    	if (studyDay > (goalsCache.length - 1)) {
    		return -1;
    	} else {
    		return goalsCache[studyDay];
    	}
    }

    @Override
	public void recordGoal(int stepsPerDay) {
		int studyDay=getUsersStudyDay();
		try {
		    stmt.executeUpdate("REPLACE into PA_STEP_GOALS (USER_ID,STUDY_DAY,GOAL) VALUES ("+userID+","+studyDay+","+stepsPerDay+")");
		} catch(SQLException e){
		    System.out.println("recordGoal ex: "+e);
		}
   	   	
   	   	if(goalsCache!=null) {
   	   	updateGoalsCache(studyDay,stepsPerDay);
	   	}
    }
     
	/**
	 * @return the open
	 */
	private boolean isOpen() {
		return open;
	}

	/**
	 * @param open the open to set
	 */
	private void setOpen(boolean open) {
		this.open = open;
	}

	@Override
	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
		
	}

} // end of class DBStore
