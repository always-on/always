package DialogueRuntime;

import DialogueRuntime.DialogueListener.TerminationReason;
import DialogueRuntime.ServerConstants.Condition;
import DialogueRuntime.ServerConstants.Media;
import DialogueRuntime.ServerConstants.UsersStatus;

import java.sql.*;
import java.util.*;

public class OntologyDBStore extends PersistentStore {

	@Override
	public void open() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addLog(LogEventType eventType, String eventData)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close(TerminationReason reason) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isValidID(int userID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setUserID(int userID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSessionID(int sessionID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getUsersLoginPIN() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public UsersStatus getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int usersComputeStudyDay() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int usersComputeStudyDay(Calendar c) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setUsersStudyDay(int studyDay) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean alreadyLoggedInToday() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getUsersStudyDay() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int saveSteps(int day, int steps) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getUserName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Condition getUserCondition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void recordGoal(int goal) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean tryLoadProperties(Properties p) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	//STUFF THAT NEEDS TO BE FIXED
	public int addSession(ServerConstants.Media media) { 
	    return 0;
	    /*
	    String query="";
	    try{
	    
	        query = ("INSERT INTO sessions (User_ID,Start_Time) VALUES (" + userID + ", NOW() )");
	        stmt.executeUpdate(query);
	        ResultSet rs = stmt.executeQuery("Select MAX(session_ID) FROM sessions WHERE user_id=" + userID + " AND END_TIME is null");
	    	if(!rs.next())
	    		throw new SQLException("SQLException: Could not add entry to session table. UserID:" + userID + " not set?");
	        sessionID = rs.getInt(1);
		}catch(SQLException ex){
			System.out.println(ex);
			System.out.println("The exception was encountered while running the query: " + query);
			System.out.println("Unable to create new session for the User " + userID);
	    } 
		return sessionID;*/
	}
	
	private void updateMiniSessionID(int id) {
//		this.mini_session_id = id;		
	}
	
	private int getMiniSessionID() {
		return 0;
//		return this.mini_session_id;		
	}
	
	public void createMiniSession(String interaction_num) {
		/*
		String query="";
	    try{
	    
	        query = ("INSERT INTO mini_sessions (session_id, start_time, interaction_num) VALUES (" + sessionID + ", NOW(), '"+interaction_num+"')");
	        stmt.executeUpdate(query);
	        ResultSet RS = stmt.getGeneratedKeys();
	        if(RS.next()){
	        	int miniID = RS.getInt(1);
	        	updateMiniSessionID(miniID);
	        }
	        		
	        
		}catch(SQLException ex){
			System.out.println(ex);
			System.out.println("The exception was encountered while running the query: " + query);
			System.out.println("Unable to create new minisession for the User " + userID);
	    }*/
	}
	
	public void endMiniSession() {
		/*
		String query="";
	    try{
	    
	        query = ("UPDATE mini_sessions SET end_time=NOW(), end_reason='1' where mini_session_id='"+this.mini_session_id+"'");
	        System.out.println("query = "+query);
	        stmt.executeUpdate(query);
	        
		}catch(SQLException ex){
			System.out.println(ex);
			System.out.println("The exception was encountered while running the query: " + query);
			System.out.println("Unable to end the minisession for the minisessionid " + this.mini_session_id);
	    }*/
	}
	
	public void endMiniSessionTimeout() {
		/*
		String query="";
	    try{
	    
	        query = ("UPDATE mini_sessions SET end_time=NOW(), end_reason='2' where mini_session_id='"+this.mini_session_id+"'");
	        System.out.println("query = "+query);
	        stmt.executeUpdate(query);
	        
		}catch(SQLException ex){
			System.out.println(ex);
			System.out.println("The exception was encountered while running the query: " + query);
			System.out.println("Unable to end the minisession for the minisessionid " + this.mini_session_id);
	    }*/
	}
	
	public boolean prevInteractionToday(){
		return false;
		/*
		boolean interactedToday = false;
		
		String query="";
	    try{
	    
	        query = ("SELECT DATEDIFF( (SELECT start_time from mini_sessions where mini_session_id='"+(this.mini_session_id)+"'), (SELECT start_time from mini_sessions where mini_session_id='"+(this.mini_session_id-1)+"'))");
	        System.out.println("query = "+query);
	        ResultSet RS = stmt.executeQuery(query);
	        
	        if(RS.next()){
	        	int daysdiff = RS.getInt(1);
	        	if(daysdiff == 0)
	        	{
	        		interactedToday = true;
	        	}
	        }
	        
	        
		}catch(SQLException ex){
			System.out.println(ex);
			System.out.println("The exception was encountered while running the query: " + query);
			System.out.println("Unable to find out if previous interaction today " + this.mini_session_id);
	    }
	    return interactedToday;*/
	}
	
	public int lastInteractionInDays() {
		return 0;
		/*
		int daysAgo = 0;
		String query="";
	    try{
	    
	        query = ("SELECT DATEDIFF( (SELECT start_time from mini_sessions where mini_session_id='"+(this.mini_session_id)+"'), (SELECT start_time from mini_sessions where mini_session_id='"+(this.mini_session_id-1)+"'))");
	        System.out.println("query = "+query);
	        ResultSet RS = stmt.executeQuery(query);
	        
	        if(RS.next()){
	        	daysAgo = RS.getInt(1);
	        }
	        
	        
		}catch(SQLException ex){
			System.out.println(ex);
			System.out.println("The exception was encountered while running the query: " + query);
			System.out.println("Unable to get last interaction in days " + this.mini_session_id);
	    }
	    return daysAgo;*/
	}
	
	public int lastInteractionInHours() {
		return 0;
		/*
		int hoursAgo = 0;
		String query="";
	    try{
	    
	        query = ("SELECT TIMESTAMPDIFF(HOUR,(SELECT start_time from mini_sessions where mini_session_id='"+(this.mini_session_id-1)+"'), (SELECT start_time from mini_sessions where mini_session_id='"+(this.mini_session_id)+"'))");
	        System.out.println("query = "+query);
	        ResultSet RS = stmt.executeQuery(query);
	        
	        if(RS.next()){
	        	hoursAgo = RS.getInt(1);
	        }
	        
	        
		}catch(SQLException ex){
			System.out.println(ex);
			System.out.println("The exception was encountered while running the query: " + query);
			System.out.println("Unable to get last interaction in hours " + this.mini_session_id);
	    }
	    return hoursAgo;*/
	}
	
	public int lastInteractionInMinutes() {
		return 0;
		/*
		int minutesAgo = 0;
		String query="";
	    try{
	    
	        query = ("SELECT TIMESTAMPDIFF(MINUTE,(SELECT start_time from mini_sessions where mini_session_id='"+(this.mini_session_id-1)+"'), (SELECT start_time from mini_sessions where mini_session_id='"+(this.mini_session_id)+"'))");
	        System.out.println("query = "+query);
	        ResultSet RS = stmt.executeQuery(query);
	        
	        if(RS.next()){
	        	minutesAgo = RS.getInt(1);
	        }
	        
	        
		}catch(SQLException ex){
			System.out.println(ex);
			System.out.println("The exception was encountered while running the query: " + query);
			System.out.println("Unable to get last interaction in minutes " + this.mini_session_id);
	    }
	    return minutesAgo;*/
	}
	
	public int hoursSinceInteraction(String interactionNum) {
		return 0;
		/*
		int hoursAgo = 0;
		String query="";
	    try{
	    	
	    	//first got to get the mini-session_id for that interactionNum
	    	String firstQuery = "SELECT mini_session_id from mini_sessions where interaction_num = '"+interactionNum+"' and mini_session_id in (select m.mini_session_id from mini_sessions m, sessions s where m.session_id=s.session_id and s.user_id='"+userID+"') order by mini_session_id DESC LIMIT 1";
	    	ResultSet firstRS = stmt.executeQuery(firstQuery);
	    	int mini_session_id = 0;
	    	
	    	if(firstRS.next()){
	    		mini_session_id = firstRS.getInt(1);
	    		
	    		
	    		
	    		query = ("SELECT TIMESTAMPDIFF(HOUR,(SELECT start_time from mini_sessions where mini_session_id='"+mini_session_id+"'), (SELECT start_time from mini_sessions where mini_session_id='"+(this.mini_session_id)+"'))");
		        System.out.println("query = "+query);
		        ResultSet RS = stmt.executeQuery(query);
		        
		        if(RS.next()){
		        	hoursAgo = RS.getInt(1);
		        	
		        	System.out.println("Last interaction was the following hours ago: "+hoursAgo);
		        }
	    	} 
	        
		}catch(SQLException ex){
			System.out.println(ex);
			System.out.println("The exception was encountered while running the query: " + query);
			System.out.println("Unable to get hoursSinceStartOfInteraction " + this.mini_session_id);
	    }
	    return hoursAgo;*/
	}
	
	
	public int daysSinceInteraction(String interactionNum) {
		return 0;
		/*
		int daysAgo = 0;
		String query="";
	    try{
	    	
	    	//first got to get the mini-session_id for that interactionNum
	    	String firstQuery = "SELECT mini_session_id from mini_sessions where interaction_num = '"+interactionNum+"' and mini_session_id in (select m.mini_session_id from mini_sessions m, sessions s where m.session_id=s.session_id and s.user_id='"+userID+"')  order by mini_session_id DESC LIMIT 1";
	    	ResultSet firstRS = stmt.executeQuery(firstQuery);
	    	int mini_session_id = 0;
	    	
	    	if(firstRS.next()){
	    		mini_session_id = firstRS.getInt(1);
	    		
	    		query = ("SELECT TIMESTAMPDIFF(DAY,(SELECT start_time from mini_sessions where mini_session_id='"+mini_session_id+"'), (SELECT start_time from mini_sessions where mini_session_id='"+(this.mini_session_id)+"'))");
		        System.out.println("query = "+query);
		        ResultSet RS = stmt.executeQuery(query);
		        
		        if(RS.next()){
		        	daysAgo = RS.getInt(1);
		        }
	    		
	    	}
	        
		}catch(SQLException ex){
			System.out.println(ex);
			System.out.println("The exception was encountered while running the query: " + query);
			System.out.println("Unable to get daysSinceEndOfInteraction " + this.mini_session_id);
	    }
	    return daysAgo;*/
	}
	
		
	
	@Override
	public void loadProperties(Properties p) throws Exception {
    	/*
	    String property;
	    String value;
	    String query="";

	    p.clear();
	    try {
	    	
	    	query="SELECT property,value FROM properties WHERE user_id=" + userID;
	    	ResultSet RS = stmt.executeQuery(query); 
	    	while(RS.next()) {
	    		property = RS.getString(1);
	    		value = RS.getString(2);
	    		p.setProperty(property, value);
	    		System.out.println("**********    " + property + " value " + value);
		    	}
	    	
	    	System.out.println("Properties object initialized from DB properties table ");
	    	System.out.println("Value ::"+ p.size());
	    	
	    }	catch (SQLException e) {
	    	System.out.println(e);
	    	System.out.println("The exception was encountered while running the query: " + query);
	    	System.out.println("DBSTore Error loading properties " + e.toString());
	    }*/
	    }
	
    public void saveProperties(Properties p) throws Exception {
		/*
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
	        } catch (Exception e) {
	           System.out.println("SQL exception " + e);
	           System.out.println("on query " + pstmt.toString());
	           System.out.println(e.getStackTrace());
	        }
	    }
    }*/
    }
	
	
        // when setting the value of a particular property, go on and save it in case server crashes
    public void saveProperty(String property, String value) throws Exception {
		/*
    	System.out.println("Saving property " + property + " to DB." );
       
        try {    	
     	stmt.executeUpdate("REPLACE INTO properties (`User_ID`,`Property`,`Value`) VALUES (" + userID + ",'" + property + "', '" + value + "')");
    	        } catch (Exception e) {
    	           System.out.println("SQL exception " + e);
    	           System.out.println("on query " + e.getMessage());
    	           System.out.println(e.getStackTrace());
    	        }*/
        }
	

	
	public String[] getNextGame(String team) {
		return new String[4];
/*
		 	String query="";
		 	String[] nextGameInfo = new String[3];
		    try {
		    	
		    	query="select schedule.scheduled_time, schedule.home, t.name" +
		    			" from " +
		    			"(SELECT s.scheduled_time, s.opponent, s.home FROM baseball_schedule s " +
		    			" WHERE s.team='sox' and scheduled_time > CURRENT_TIMESTAMP	) as schedule, " +
		    			" baseball_teams t  " +
		    			" where t.baseball_teams_id=schedule.opponent";
		    	ResultSet RS = stmt.executeQuery(query); 
		    	if(RS.next()) {
		    		nextGameInfo[0] = RS.getString(1);
		    		nextGameInfo[1] = RS.getString(2);
		    		nextGameInfo[2] = RS.getString(3);
			    }

		    }	catch (SQLException e) {
		    	System.out.println(e);
		    	System.out.println("The exception was encountered while running the query: " + query);
		    	System.out.println("DBSTore Error loading properties " + e.toString());
		    }
		return nextGameInfo;*/
	}
	
	
	public String[] gameTodayInfo() {
		return new String[4];
/*
		String query = "";
		String [] gameToday = new String[4];
		
		try {
			
			query = "select s.baseball_schedule_id, t.name as opponent, s.home, DATE_FORMAT(s.scheduled_time, '%l') as start_time from baseball_schedule s, baseball_teams t " +
					"where DATEDIFF(CURRENT_DATE(), scheduled_time)=0 and s.opponent = t.baseball_teams_id";
			ResultSet RS = stmt.executeQuery(query); 
	    	if(RS.next()) {
	    		gameToday[0] = RS.getString(1);
	    		gameToday[1] = RS.getString(2);
	    		gameToday[2] = RS.getString(3);
	    		gameToday[3] = RS.getString(4);
		    }
			
		}
		catch (SQLException e) {
	    	System.out.println(e);
	    	System.out.println("The exception was encountered while running the query: " + query);
	    }
		
		return gameToday;*/
	}
	
	public String[] gameYesterdayInfo() {
		return new String[4];
		/*
		String query = "";
		String [] gameYesterday = new String[4];
		
		try {
			
			query = "select s.baseball_schedule_id, t.name as opponent, s.home, DATE_FORMAT(s.scheduled_time, '%l') as start_time from baseball_schedule s, baseball_teams t " +
					"where DATEDIFF(CURRENT_DATE(), scheduled_time)=1 and s.opponent = t.baseball_teams_id";
			ResultSet RS = stmt.executeQuery(query); 
	    	if(RS.next()) {
	    		gameYesterday[0] = RS.getString(1);
	    		gameYesterday[1] = RS.getString(2);
	    		gameYesterday[2] = RS.getString(3);
	    		gameYesterday[3] = RS.getString(4);
		    }
			
		}
		catch (SQLException e) {
	    	System.out.println(e);
	    	System.out.println("The exception was encountered while running the query: " + query);
	    }
		
		return gameYesterday;*/
	}
	
	
	public String[] gameTomorrowInfo() {
		return new String[4];
		/*String query = "";
		String [] gameTomorrow= new String[4];
		
		try {
			
			query = "select s.baseball_schedule_id, t.name as opponent, s.home, DATE_FORMAT(s.scheduled_time, '%l') as start_time from baseball_schedule s, baseball_teams t " +
					"where DATEDIFF(CURRENT_DATE(), scheduled_time)=-1 and s.opponent = t.baseball_teams_id";
			ResultSet RS = stmt.executeQuery(query); 
	    	if(RS.next()) {
	    		gameTomorrow[0] = RS.getString(1);
	    		gameTomorrow[1] = RS.getString(2);
	    		gameTomorrow[2] = RS.getString(3);
	    		gameTomorrow[3] = RS.getString(4);
		    }
			
		}
		catch (SQLException e) {
	    	System.out.println(e);
	    	System.out.println("The exception was encountered while running the query: " + query);
	    }
		
		return gameTomorrow;*/
	}
	
}
