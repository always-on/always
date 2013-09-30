package alwaysAvailableCore;

import org.joda.time.Period;

import edu.wpi.always.Plugin;
import edu.wpi.always.user.UserModel;
import DialogueRuntime.*;

import java.io.InputStream;
import java.sql.*;

import java.text.*;
import java.util.*;
import java.util.Date;

public class AADBStore extends DBStore {

//	private int mini_session_id;
 	private int[] VFCountCache;
    private int[] VFGoalsCache;
	
	public AADBStore(UserModel userModel) throws Exception {
		super();
		this.store = userModel;
	}
	
	public void saveAll(){
		store.save();
	}

	@Override
	public int addSession(ServerConstants.Media media) {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");        
		store.setProperty("start_time",df.format(Calendar.getInstance().getTime()));
//		String query = "";
//		try {
//
//			query = ("INSERT INTO sessions (User_ID,Start_Time) VALUES ("
//					+ userID + ", NOW() )");
//			stmt.executeUpdate(query);
//			ResultSet rs = stmt
//					.executeQuery("Select MAX(session_ID) FROM sessions WHERE user_id="
//							+ userID + " AND END_TIME is null");
//			if (!rs.next())
//				throw new SQLException(
//						"SQLException: Could not add entry to session table. UserID:"
//								+ userID + " not set?");
//			sessionID = rs.getInt(1);
//		} catch (SQLException ex) {
//			System.out.println(ex);
//			System.out
//					.println("The exception was encountered while running the query: "
//							+ query);
//			System.out.println("Unable to create new session for the User "
//					+ userID);
//		}
//		return sessionID;
		return 0;
	}
	
	public void updateStartTime(){
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");        
		store.setProperty("start_time",df.format(Calendar.getInstance().getTime()));
	}

	public void updateLastStartTime(String date){
		store.setProperty("last_start_time",date);
	}
	
	public void updateStudyDate(){
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");        
		store.setProperty("study_date",df.format(Calendar.getInstance().getTime()));
	}
	
	public void updateLastStudyDate(String date){
		store.setProperty("last_study_date",date);
	}
/** mini session part has been ignored for now	
	private void updateMiniSessionID(int id) {
		this.mini_session_id = id;
	}

	private int getMiniSessionID() {
		return this.mini_session_id;
	} **/

	public void createMiniSession(String interaction_num) {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");        
		store.setProperty("start_time",df.format(Calendar.getInstance().getTime()));
		store.setProperty("INTERACTION_NUM", interaction_num);
//		String query = "";
//		try {
//
//			query = ("INSERT INTO mini_sessions (session_id, start_time, interaction_num) VALUES ("
//					+ sessionID + ", NOW(), '" + interaction_num + "')");
//			stmt.executeUpdate(query);
//			ResultSet RS = stmt.getGeneratedKeys();
//			if (RS.next()) {
//				int miniID = RS.getInt(1);
//				updateMiniSessionID(miniID);
//			}
//
//		} catch (SQLException ex) {
//			System.out.println(ex);
//			System.out
//					.println("The exception was encountered while running the query: "
//							+ query);
//			System.out.println("Unable to create new minisession for the User "
//					+ userID);
//		}
	}

	public void endMiniSession() {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");      
		store.setProperty("end_time",df.format(Calendar.getInstance().getTime()));
//		String query = "";
//		try {
//
//			query = ("UPDATE mini_sessions SET end_time=NOW(), end_reason='1' where mini_session_id='"
//					+ this.mini_session_id + "'");
//			System.out.println("query = " + query);
//			stmt.executeUpdate(query);
//
//		} catch (SQLException ex) {
//			System.out.println(ex);
//			System.out
//					.println("The exception was encountered while running the query: "
//							+ query);
//			System.out
//					.println("Unable to end the minisession for the minisessionid "
//							+ this.mini_session_id);
//		}
	}

	public void endMiniSessionTimeout() {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");      
		store.setProperty("end_time",df.format(Calendar.getInstance().getTime()));
//		String query = "";
//		try {
//
//			query = ("UPDATE mini_sessions SET end_time=NOW(), end_reason='2' where mini_session_id='"
//					+ this.mini_session_id + "'");
//			System.out.println("query = " + query);
//			stmt.executeUpdate(query);
//
//		} catch (SQLException ex) {
//			System.out.println(ex);
//			System.out
//					.println("The exception was encountered while running the query: "
//							+ query);
//			System.out
//					.println("Unable to end the minisession for the minisessionid "
//							+ this.mini_session_id);
//		}
	}

	public boolean prevInteractionToday() {
		boolean interactedToday = false;
		int daysAgo = 0;

		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
		if(store.getProperty("last_start_time")==null)
		{
			interactedToday = false;
		}
		else{
			try {			
				Date last_interaction = df.parse(store.getProperty("last_start_time"));
				long dif=(Calendar.getInstance().getTime().getTime()-last_interaction.getTime())/(1000*60*60*24);
				daysAgo = (int) daysAgo;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if(daysAgo==0)
		{
			interactedToday=true;
		}
//		String query = "";
//		try {
//
//			query = ("SELECT DATEDIFF( (SELECT start_time from mini_sessions where mini_session_id='"
//					+ (this.mini_session_id)
//					+ "'), (SELECT start_time from mini_sessions where mini_session_id='"
//					+ (this.mini_session_id - 1) + "'))");
//			System.out.println("query = " + query);
//			ResultSet RS = stmt.executeQuery(query);
//
//			if (RS.next()) {
//				int daysdiff = RS.getInt(1);
//				if (daysdiff == 0) {
//					interactedToday = true;
//				}
		
//			}
//
//		} catch (SQLException ex) {
//			System.out.println(ex);
//			System.out
//					.println("The exception was encountered while running the query: "
//							+ query);
//			System.out
//					.println("Unable to find out if previous interaction today "
//							+ this.mini_session_id);
//		}
//		return interactedToday;
		return interactedToday;
	}

	public int lastInteractionInDays() {
		int daysAgo = 0;
		store.getProperty("start_time");
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
		if(store.getProperty("last_start_time")==null)
		{
			daysAgo=0;
		}
		else{
			try {
				Date last_interaction = df.parse(store.getProperty("last_start_time"));
				long dif=(Calendar.getInstance().getTime().getTime()-last_interaction.getTime())/(1000*60*60*24);
				daysAgo = (int) daysAgo;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
//		int daysAgo = 0;
//		String query = "";
//		try {
//
//			query = ("SELECT DATEDIFF( (SELECT start_time from mini_sessions where mini_session_id='"
//					+ (this.mini_session_id)
//					+ "'), (SELECT start_time from mini_sessions where mini_session_id='"
//					+ (this.mini_session_id - 1) + "'))");
//			System.out.println("query = " + query);
//			ResultSet RS = stmt.executeQuery(query);
//
//			if (RS.next()) {
//				daysAgo = RS.getInt(1);
//			}
//
//		} catch (SQLException ex) {
//			System.out.println(ex);
//			System.out
//					.println("The exception was encountered while running the query: "
//							+ query);
//			System.out.println("Unable to get last interaction in days "
//					+ this.mini_session_id);
//		}
//		return daysAgo;
		return daysAgo;
	}

	public int lastInteractionInHours() {
		int hoursAgo = 0;
		store.getProperty("start_time");
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
		if(store.getProperty("last_start_time")==null)
		{
			hoursAgo=0;
		}
		else{
			try {
				Date last_interaction = df.parse(store.getProperty("last_start_time"));
				long dif=(Calendar.getInstance().getTime().getTime()-last_interaction.getTime())/(60*60*1000);
				hoursAgo = (int) hoursAgo;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
//		int hoursAgo = 0;
//		String query = "";
//		try {
//
//			query = ("SELECT TIMESTAMPDIFF(HOUR,(SELECT start_time from mini_sessions where mini_session_id='"
//					+ (this.mini_session_id - 1)
//					+ "'), (SELECT start_time from mini_sessions where mini_session_id='"
//					+ (this.mini_session_id) + "'))");
//			System.out.println("query = " + query);
//			ResultSet RS = stmt.executeQuery(query);
//
//			if (RS.next()) {
//				hoursAgo = RS.getInt(1);
//			}
//
//		} catch (SQLException ex) {
//			System.out.println(ex);
//			System.out
//					.println("The exception was encountered while running the query: "
//							+ query);
//			System.out.println("Unable to get last interaction in hours "
//					+ this.mini_session_id);
//		}
//		return hoursAgo;
		return hoursAgo;
	}

	public int lastInteractionInMinutes() {
		int minutesAgo = 0;
		store.getProperty("start_time");
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
		if(store.getProperty("last_start_time")==null)
		{
			minutesAgo=0;
		}
		else{
			try {
				Date last_interaction = df.parse(store.getProperty("last_start_time"));
				long dif=(Calendar.getInstance().getTime().getTime()-last_interaction.getTime())/(60*1000);
				minutesAgo = (int) minutesAgo;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
//		int minutesAgo = 0;
//		String query = "";
//		try {
//
//			query = ("SELECT TIMESTAMPDIFF(MINUTE,(SELECT start_time from mini_sessions where mini_session_id='"
//					+ (this.mini_session_id - 1)
//					+ "'), (SELECT start_time from mini_sessions where mini_session_id='"
//					+ (this.mini_session_id) + "'))");
//			System.out.println("query = " + query);
//			ResultSet RS = stmt.executeQuery(query);
//
//			if (RS.next()) {
//				minutesAgo = RS.getInt(1);
//			}
//
//		} catch (SQLException ex) {
//			System.out.println(ex);
//			System.out
//					.println("The exception was encountered while running the query: "
//							+ query);
//			System.out.println("Unable to get last interaction in minutes "
//					+ this.mini_session_id);
//		}
//		return minutesAgo;
		return minutesAgo;
	}

	public int hoursSinceInteraction() {
		int hoursAgo = 0;
		store.getProperty("start_time");
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
		if(store.getProperty("last_start_time")==null)
		{
			hoursAgo=1;
		}
		else{
			try {
				Date last_interaction = df.parse(store.getProperty("last_start_time"));
				long dif=(Calendar.getInstance().getTime().getTime()-last_interaction.getTime())/(60*60*1000);
				hoursAgo = (int) hoursAgo;
			} catch (ParseException e) {
				e.printStackTrace();
			}	
		}
			
//		String query = "";
//		try {
//
//			// first got to get the mini-session_id for that interactionNum
//			String firstQuery = "SELECT mini_session_id from mini_sessions where interaction_num = '"
//					+ interactionNum
//					+ "' and mini_session_id in (select m.mini_session_id from mini_sessions m, sessions s where m.session_id=s.session_id and s.user_id='"
//					+ userID + "') order by mini_session_id DESC LIMIT 1";
//			ResultSet firstRS = stmt.executeQuery(firstQuery);
//			int mini_session_id = 0;
//
//			if (firstRS.next()) {
//				mini_session_id = firstRS.getInt(1);
//
//				query = ("SELECT TIMESTAMPDIFF(HOUR,(SELECT start_time from mini_sessions where mini_session_id='"
//						+ mini_session_id
//						+ "'), (SELECT start_time from mini_sessions where mini_session_id='"
//						+ (this.mini_session_id) + "'))");
//				System.out.println("query = " + query);
//				ResultSet RS = stmt.executeQuery(query);
//
//				if (RS.next()) {
//					hoursAgo = RS.getInt(1);
//
//					System.out
//							.println("Last interaction was the following hours ago: "
//									+ hoursAgo);
//				}
//			}
//
//		} catch (SQLException ex) {
//			System.out.println(ex);
//			System.out
//					.println("The exception was encountered while running the query: "
//							+ query);
//			System.out.println("Unable to get hoursSinceStartOfInteraction "
//					+ this.mini_session_id);
//		}
//		return hoursAgo;
		return hoursAgo;
	}

	public int daysSinceInteraction() {
		int daysAgo = 0;
		store.getProperty("start_time");
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		if(store.getProperty("last_start_time")==null)
		{
			daysAgo=0;
		}
		else{
			try {
				Date last_interaction = df.parse(store.getProperty("last_start_time"));
				long dif=(Calendar.getInstance().getTime().getTime()-last_interaction.getTime())/(1000*60*60*24);
				daysAgo = (int) daysAgo;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
//		String query = "";
//		try {
//
//			// first got to get the mini-session_id for that interactionNum
//			String firstQuery = "SELECT mini_session_id from mini_sessions where interaction_num = '"
//					+ interactionNum
//					+ "' and mini_session_id in (select m.mini_session_id from mini_sessions m, sessions s where m.session_id=s.session_id and s.user_id='"
//					+ userID + "')  order by mini_session_id DESC LIMIT 1";
//			ResultSet firstRS = stmt.executeQuery(firstQuery);
//			int mini_session_id = 0;
//
//			if (firstRS.next()) {
//				mini_session_id = firstRS.getInt(1);
//
//				query = ("SELECT TIMESTAMPDIFF(DAY,(SELECT start_time from mini_sessions where mini_session_id='"
//						+ mini_session_id
//						+ "'), (SELECT start_time from mini_sessions where mini_session_id='"
//						+ (this.mini_session_id) + "'))");
//				System.out.println("query = " + query);
//				ResultSet RS = stmt.executeQuery(query);
//
//				if (RS.next()) {
//					daysAgo = RS.getInt(1);
//				}
//
//			}
//
//		} catch (SQLException ex) {
//			System.out.println(ex);
//			System.out
//					.println("The exception was encountered while running the query: "
//							+ query);
//			System.out.println("Unable to get daysSinceEndOfInteraction "
//					+ this.mini_session_id);
//		}
//		return daysAgo;
		return daysAgo;
	}

	@Override
	public void loadProperties(Properties p) throws Exception {
//
//		String property;
//		String value;
//		String query = "";
//
//		p.clear();
//		try {
//
//			query = "SELECT property,value FROM properties WHERE user_id="
//					+ userID;
//			ResultSet RS = stmt.executeQuery(query);
//			while (RS.next()) {
//				property = RS.getString(1);
//				value = RS.getString(2);
//				p.setProperty(property, value);
//				System.out.println("**********    " + property + " value "
//						+ value);
//			}
//
//			System.out
//					.println("Properties object initialized from DB properties table ");
//			System.out.println("Value ::" + p.size());
//
//		} catch (SQLException e) {
//			System.out.println(e);
//			System.out
//					.println("The exception was encountered while running the query: "
//							+ query);
//			System.out.println("DBSTore Error loading properties "
//					+ e.toString());
//		}
	}

	@Override
	public void saveProperties(Properties p) throws Exception {
//		System.out.println("Saving properties from p to DB. Properties list: ");
//
//		// Thread.dumpStack();
//
//		if (p != null) {
//			// stmt.executeUpdate("DELETE FROM properties WHERE User_ID=" +
//			// userID);
//
//			PreparedStatement pstmt = DBCon
//					.prepareStatement("REPLACE INTO properties (`User_ID`,`Property`,`Value`) VALUES (?,?,?)");
//			Enumeration<Object> keys = p.keys();
//			while (keys.hasMoreElements()) {
//				pstmt.setInt(1, userID);
//				String property = (String) keys.nextElement();
//				pstmt.setString(2, property);
//				pstmt.setString(3, (String) p.get(property));
//				try {
//					pstmt.executeUpdate();
//				} catch (Exception e) {
//					System.out.println("SQL exception " + e);
//					System.out.println("on query " + pstmt.toString());
//					System.out.println(e.getStackTrace());
//				}
//			}
//		}
	}

	@Override
	// when setting the value of a particular property, go on and save it in
	// case server crashes
	public void saveProperty(String property, String value) throws Exception {
//		System.out.println("Saving property " + property + " to DB.");
//
//		try {
//			stmt.executeUpdate("REPLACE INTO properties (`User_ID`,`Property`,`Value`) VALUES ("
//					+ userID + ",'" + property + "', '" + value + "')");
//		} catch (Exception e) {
//			System.out.println("SQL exception " + e);
//			System.out.println("on query " + e.getMessage());
//			System.out.println(e.getStackTrace());
//		}
	}

	public String[] getNextGame(String team) {
		String query = "";
		String[] nextGameInfo = new String[3];
//		try {
//
//			query = "select schedule.scheduled_time, schedule.home, t.name"
//					+ " from "
//					+ "(SELECT s.scheduled_time, s.opponent, s.home FROM baseball_schedule s "
//					+ " WHERE s.team='sox' and scheduled_time > CURRENT_TIMESTAMP	) as schedule, "
//					+ " baseball_teams t  "
//					+ " where t.baseball_teams_id=schedule.opponent";
//			ResultSet RS = stmt.executeQuery(query);
//			if (RS.next()) {
//				nextGameInfo[0] = RS.getString(1);
//				nextGameInfo[1] = RS.getString(2);
//				nextGameInfo[2] = RS.getString(3);
//			}
//
//		} catch (SQLException e) {
//			System.out.println(e);
//			System.out
//					.println("The exception was encountered while running the query: "
//							+ query);
//			System.out.println("DBSTore Error loading properties "
//					+ e.toString());
//		}
		return nextGameInfo;
	}

	public String[] gameTodayInfo() {

		String query = "";
		String[] gameToday = new String[4];
//
//		try {
//
//			query = "select s.baseball_schedule_id, t.name as opponent, s.home, DATE_FORMAT(s.scheduled_time, '%l') as start_time from baseball_schedule s, baseball_teams t "
//					+ "where DATEDIFF(CURRENT_DATE(), scheduled_time)=0 and s.opponent = t.baseball_teams_id";
//			ResultSet RS = stmt.executeQuery(query);
//			if (RS.next()) {
//				gameToday[0] = RS.getString(1);
//				gameToday[1] = RS.getString(2);
//				gameToday[2] = RS.getString(3);
//				gameToday[3] = RS.getString(4);
//			}
//
//		} catch (SQLException e) {
//			System.out.println(e);
//			System.out
//					.println("The exception was encountered while running the query: "
//							+ query);
//		}

		return gameToday;
	}

	public String[] gameYesterdayInfo() {

		String query = "";
		String[] gameYesterday = new String[4];
//
//		try {
//
//			query = "select s.baseball_schedule_id, t.name as opponent, s.home, DATE_FORMAT(s.scheduled_time, '%l') as start_time from baseball_schedule s, baseball_teams t "
//					+ "where DATEDIFF(CURRENT_DATE(), scheduled_time)=1 and s.opponent = t.baseball_teams_id";
//			ResultSet RS = stmt.executeQuery(query);
//			if (RS.next()) {
//				gameYesterday[0] = RS.getString(1);
//				gameYesterday[1] = RS.getString(2);
//				gameYesterday[2] = RS.getString(3);
//				gameYesterday[3] = RS.getString(4);
//			}
//
//		} catch (SQLException e) {
//			System.out.println(e);
//			System.out
//					.println("The exception was encountered while running the query: "
//							+ query);
//		}

		return gameYesterday;
	}

	public String[] gameTomorrowInfo() {

		String query = "";
		String[] gameTomorrow = new String[4];
//
//		try {
//
//			query = "select s.baseball_schedule_id, t.name as opponent, s.home, DATE_FORMAT(s.scheduled_time, '%l') as start_time from baseball_schedule s, baseball_teams t "
//					+ "where DATEDIFF(CURRENT_DATE(), scheduled_time)=-1 and s.opponent = t.baseball_teams_id";
//			ResultSet RS = stmt.executeQuery(query);
//			if (RS.next()) {
//				gameTomorrow[0] = RS.getString(1);
//				gameTomorrow[1] = RS.getString(2);
//				gameTomorrow[2] = RS.getString(3);
//				gameTomorrow[3] = RS.getString(4);
//			}
//
//		} catch (SQLException e) {
//			System.out.println(e);
//			System.out
//					.println("The exception was encountered while running the query: "
//							+ query);
//		}

		return gameTomorrow;
	}
	
	 public void cacheVFCountAndGoals() {
	    	int studyDay=getUsersStudyDay();
	    	VFCountCache=new int[studyDay+1];
	    	VFGoalsCache=new int[studyDay+1];
			for(int i=0;i<VFCountCache.length;i++){
				VFCountCache[i]=-1;
				VFGoalsCache[i]=-1;
			}
			try {
				int i=0;
				int lastGoal=0;
				int next = 0;
			    String query="SELECT STUDY_DAY,VF_COUNT FROM VF_STEPS WHERE User_ID=" + userID;
			    ResultSet RS = stmt.executeQuery(query); 
			    while(RS.next()) {
			    	next = RS.getInt(1);
			    	if (next>-1 && next<VFCountCache.length){
			    		VFCountCache[RS.getInt(1)]= RS.getInt(2);
			    	}
			    }
			    query="SELECT STUDY_DAY,GOAL FROM VF_STEP_GOALS WHERE User_ID=" + userID;
			    RS = stmt.executeQuery(query);
			    while(RS.next()) {
			    	i = RS.getInt(1);
			    	if (i>-1 && i<VFGoalsCache.length){
			    		VFGoalsCache[i]= RS.getInt(2);
			    		if (VFGoalsCache[i]>0){
			    			lastGoal=VFGoalsCache[i];
			    			System.out.println("Last Goal is: "+lastGoal);
			    		}
			    	}
			    }
			    if (lastGoal>0){
			    	VFGoalsCache[VFGoalsCache.length-1]=lastGoal;
			    }
			} catch (SQLException e) {
			    System.out.println("cache ex: "+e);
			}
			System.out.println("\nCached steps...");
			for(int j=0;j<VFCountCache.length;j++) {
				System.out.println("Day "+j+" Steps = "+VFCountCache[j]);
				System.out.println("Day "+j+" Goals = "+VFGoalsCache[j]);
			}
	    }
}