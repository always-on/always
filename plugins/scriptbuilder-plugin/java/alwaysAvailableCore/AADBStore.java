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

	private int[] VFCountCache;
	private int[] VFGoalsCache;

	public AADBStore(UserModel userModel) throws Exception {
		super();
		this.store = userModel;
	}

	public void saveAll() {
		store.save();
	}

	public void clearDB() {

	}

	@Override
	public int addSession(ServerConstants.Media media) {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		store.setProperty("start_time",
				df.format(Calendar.getInstance().getTime()));
		// String query = "";
		// try {
		//
		// query = ("INSERT INTO sessions (User_ID,Start_Time) VALUES ("
		// + userID + ", NOW() )");
		// stmt.executeUpdate(query);
		// ResultSet rs = stmt
		// .executeQuery("Select MAX(session_ID) FROM sessions WHERE user_id="
		// + userID + " AND END_TIME is null");
		// if (!rs.next())
		// throw new SQLException(
		// "SQLException: Could not add entry to session table. UserID:"
		// + userID + " not set?");
		// sessionID = rs.getInt(1);
		// } catch (SQLException ex) {
		// System.out.println(ex);
		// System.out
		// .println("The exception was encountered while running the query: "
		// + query);
		// System.out.println("Unable to create new session for the User "
		// + userID);
		// }
		// return sessionID;
		return 0;
	}

	public void updateStartTime() {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		store.setProperty("start_time",
				df.format(Calendar.getInstance().getTime()));
	}

	public void updateLastStartTime(String date) {
		store.setProperty("last_start_time", date);
	}

	public void setStudyDate(String studyType, String studyDate) {
		store.setProperty(studyType, studyDate);
	}

	public int getStudyDate(String studyType) {
		int day = 0;
		if (store.getProperty(studyType) != null) {
			day = Integer.valueOf(store.getProperty(studyType));
		}
		return day;
	}

	public void updateStudyDate() {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		store.setProperty("study_date",
				df.format(Calendar.getInstance().getTime()));
	}

	public void updateLastStudyDate(String date) {
		store.setProperty("last_study_date", date);
	}

	/**
	 * mini session part has been ignored for now private void
	 * updateMiniSessionID(int id) { this.mini_session_id = id; }
	 * 
	 * private int getMiniSessionID() { return this.mini_session_id; }
	 **/

	public void createMiniSession(String interaction_num) {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		store.setProperty("start_time",
				df.format(Calendar.getInstance().getTime()));
		store.setProperty("INTERACTION_NUM", interaction_num);
		// String query = "";
		// try {
		//
		// query =
		// ("INSERT INTO mini_sessions (session_id, start_time, interaction_num) VALUES ("
		// + sessionID + ", NOW(), '" + interaction_num + "')");
		// stmt.executeUpdate(query);
		// ResultSet RS = stmt.getGeneratedKeys();
		// if (RS.next()) {
		// int miniID = RS.getInt(1);
		// updateMiniSessionID(miniID);
		// }
		//
		// } catch (SQLException ex) {
		// System.out.println(ex);
		// System.out
		// .println("The exception was encountered while running the query: "
		// + query);
		// System.out.println("Unable to create new minisession for the User "
		// + userID);
		// }
	}

	public void endMiniSession() {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		store.setProperty("end_time",
				df.format(Calendar.getInstance().getTime()));
		// String query = "";
		// try {
		//
		// query =
		// ("UPDATE mini_sessions SET end_time=NOW(), end_reason='1' where mini_session_id='"
		// + this.mini_session_id + "'");
		// System.out.println("query = " + query);
		// stmt.executeUpdate(query);
		//
		// } catch (SQLException ex) {
		// System.out.println(ex);
		// System.out
		// .println("The exception was encountered while running the query: "
		// + query);
		// System.out
		// .println("Unable to end the minisession for the minisessionid "
		// + this.mini_session_id);
		// }
	}

	public void endMiniSessionTimeout() {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		store.setProperty("end_time",
				df.format(Calendar.getInstance().getTime()));
		// String query = "";
		// try {
		//
		// query =
		// ("UPDATE mini_sessions SET end_time=NOW(), end_reason='2' where mini_session_id='"
		// + this.mini_session_id + "'");
		// System.out.println("query = " + query);
		// stmt.executeUpdate(query);
		//
		// } catch (SQLException ex) {
		// System.out.println(ex);
		// System.out
		// .println("The exception was encountered while running the query: "
		// + query);
		// System.out
		// .println("Unable to end the minisession for the minisessionid "
		// + this.mini_session_id);
		// }
	}

	public boolean prevInteractionToday() {
		boolean interactedToday = false;
		int daysAgo = 0;

		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		if (store.getProperty("last_start_time") == null) {
			interactedToday = false;
		} else {
			try {
				Date last_interaction = df.parse(store
						.getProperty("last_start_time"));
				long dif = (Calendar.getInstance().getTime().getTime() - last_interaction
						.getTime()) / (1000 * 60 * 60 * 24);
				daysAgo = (int) daysAgo;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (daysAgo == 0) {
			interactedToday = true;
		}
		// String query = "";
		// try {
		//
		// query =
		// ("SELECT DATEDIFF( (SELECT start_time from mini_sessions where mini_session_id='"
		// + (this.mini_session_id)
		// + "'), (SELECT start_time from mini_sessions where mini_session_id='"
		// + (this.mini_session_id - 1) + "'))");
		// System.out.println("query = " + query);
		// ResultSet RS = stmt.executeQuery(query);
		//
		// if (RS.next()) {
		// int daysdiff = RS.getInt(1);
		// if (daysdiff == 0) {
		// interactedToday = true;
		// }

		// }
		//
		// } catch (SQLException ex) {
		// System.out.println(ex);
		// System.out
		// .println("The exception was encountered while running the query: "
		// + query);
		// System.out
		// .println("Unable to find out if previous interaction today "
		// + this.mini_session_id);
		// }
		// return interactedToday;
		return interactedToday;
	}

	public void updateLastNutritionInteractionDate()  {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		String lastNutritionInteractionDate = store.getProperty("last_n_date");
		if (lastNutritionInteractionDate == null) {
			store.setProperty("count_day", "1");
			store.setProperty("STUDY_DAY", "2");
		} else {
			Date last_date;
			try {
				last_date = df.parse(store.getProperty("last_n_date"));
				long dif = (Calendar.getInstance().getTime().getTime() - last_date.getTime()) / (1000 * 60 * 60 * 24);
				int p_count = Integer.valueOf(store.getProperty("count_day"));
				int c_count = p_count+ (int)dif;
				int study_count = c_count+1;
				store.setProperty("count_day",String.valueOf(c_count));
				store.setProperty("STUDY_DAY",String.valueOf(study_count));
			} catch (ParseException e) {
				e.printStackTrace();
			}			
		}
		lastNutritionInteractionDate = df.format(Calendar.getInstance().getTime());
		store.setProperty("last_n_date", lastNutritionInteractionDate);
	}

	public void updateContentDay() {
		String content_day = store.getProperty("content_day");
		if(content_day == null){
			store.setProperty("content_day", "1");	
		} else {
			int current_content_day = Integer.valueOf(store.getProperty("content_day"));
			current_content_day ++;
			store.setProperty("content_day", String.valueOf(current_content_day));	
		}
	}
	
	public int getContentDay() {
		int content_day = 0;
		if(store.getProperty("content_day")!=null){
			content_day=Integer.valueOf(store.getProperty("content_day"));
		}
		return content_day;
	}
	
	public int lastInteractionInDays() {
		int daysAgo = 0;
		store.getProperty("start_time");
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		if (store.getProperty("last_start_time") == null) {
			daysAgo = 0;
		} else {
			try {
				Date last_interaction = df.parse(store
						.getProperty("last_start_time"));
				long dif = (Calendar.getInstance().getTime().getTime() - last_interaction
						.getTime()) / (1000 * 60 * 60 * 24);
				daysAgo = (int) dif;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		// int daysAgo = 0;
		// String query = "";
		// try {
		//
		// query =
		// ("SELECT DATEDIFF( (SELECT start_time from mini_sessions where mini_session_id='"
		// + (this.mini_session_id)
		// + "'), (SELECT start_time from mini_sessions where mini_session_id='"
		// + (this.mini_session_id - 1) + "'))");
		// System.out.println("query = " + query);
		// ResultSet RS = stmt.executeQuery(query);
		//
		// if (RS.next()) {
		// daysAgo = RS.getInt(1);
		// }
		//
		// } catch (SQLException ex) {
		// System.out.println(ex);
		// System.out
		// .println("The exception was encountered while running the query: "
		// + query);
		// System.out.println("Unable to get last interaction in days "
		// + this.mini_session_id);
		// }
		// return daysAgo;
		return daysAgo;
	}

	public int lastInteractionInHours() {
		int hoursAgo = 0;
		store.getProperty("start_time");
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		if (store.getProperty("last_start_time") == null) {
			hoursAgo = 0;
		} else {
			try {
				Date last_interaction = df.parse(store
						.getProperty("last_start_time"));
				long dif = (Calendar.getInstance().getTime().getTime() - last_interaction
						.getTime()) / (60 * 60 * 1000);
				hoursAgo = (int) dif;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		// int hoursAgo = 0;
		// String query = "";
		// try {
		//
		// query =
		// ("SELECT TIMESTAMPDIFF(HOUR,(SELECT start_time from mini_sessions where mini_session_id='"
		// + (this.mini_session_id - 1)
		// + "'), (SELECT start_time from mini_sessions where mini_session_id='"
		// + (this.mini_session_id) + "'))");
		// System.out.println("query = " + query);
		// ResultSet RS = stmt.executeQuery(query);
		//
		// if (RS.next()) {
		// hoursAgo = RS.getInt(1);
		// }
		//
		// } catch (SQLException ex) {
		// System.out.println(ex);
		// System.out
		// .println("The exception was encountered while running the query: "
		// + query);
		// System.out.println("Unable to get last interaction in hours "
		// + this.mini_session_id);
		// }
		// return hoursAgo;
		return hoursAgo;
	}

	public int lastInteractionInMinutes() {
		int minutesAgo = 0;
		store.getProperty("start_time");
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		if (store.getProperty("last_start_time") == null) {
			minutesAgo = 0;
		} else {
			try {
				Date last_interaction = df.parse(store
						.getProperty("last_start_time"));
				long dif = (Calendar.getInstance().getTime().getTime() - last_interaction
						.getTime()) / (60 * 1000);
				minutesAgo = (int) dif;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		// int minutesAgo = 0;
		// String query = "";
		// try {
		//
		// query =
		// ("SELECT TIMESTAMPDIFF(MINUTE,(SELECT start_time from mini_sessions where mini_session_id='"
		// + (this.mini_session_id - 1)
		// + "'), (SELECT start_time from mini_sessions where mini_session_id='"
		// + (this.mini_session_id) + "'))");
		// System.out.println("query = " + query);
		// ResultSet RS = stmt.executeQuery(query);
		//
		// if (RS.next()) {
		// minutesAgo = RS.getInt(1);
		// }
		//
		// } catch (SQLException ex) {
		// System.out.println(ex);
		// System.out
		// .println("The exception was encountered while running the query: "
		// + query);
		// System.out.println("Unable to get last interaction in minutes "
		// + this.mini_session_id);
		// }
		// return minutesAgo;
		return minutesAgo;
	}

	public int hoursSinceInteraction() {
		int hoursAgo = 0;
		store.getProperty("start_time");
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		if (store.getProperty("last_start_time") == null) {
			hoursAgo = 1;
		} else {
			try {
				Date last_interaction = df.parse(store
						.getProperty("last_start_time"));
				long dif = (Calendar.getInstance().getTime().getTime() - last_interaction
						.getTime()) / (60 * 60 * 1000);
				hoursAgo = (int) dif;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		// String query = "";
		// try {
		//
		// // first got to get the mini-session_id for that interactionNum
		// String firstQuery =
		// "SELECT mini_session_id from mini_sessions where interaction_num = '"
		// + interactionNum
		// +
		// "' and mini_session_id in (select m.mini_session_id from mini_sessions m, sessions s where m.session_id=s.session_id and s.user_id='"
		// + userID + "') order by mini_session_id DESC LIMIT 1";
		// ResultSet firstRS = stmt.executeQuery(firstQuery);
		// int mini_session_id = 0;
		//
		// if (firstRS.next()) {
		// mini_session_id = firstRS.getInt(1);
		//
		// query =
		// ("SELECT TIMESTAMPDIFF(HOUR,(SELECT start_time from mini_sessions where mini_session_id='"
		// + mini_session_id
		// + "'), (SELECT start_time from mini_sessions where mini_session_id='"
		// + (this.mini_session_id) + "'))");
		// System.out.println("query = " + query);
		// ResultSet RS = stmt.executeQuery(query);
		//
		// if (RS.next()) {
		// hoursAgo = RS.getInt(1);
		//
		// System.out
		// .println("Last interaction was the following hours ago: "
		// + hoursAgo);
		// }
		// }
		//
		// } catch (SQLException ex) {
		// System.out.println(ex);
		// System.out
		// .println("The exception was encountered while running the query: "
		// + query);
		// System.out.println("Unable to get hoursSinceStartOfInteraction "
		// + this.mini_session_id);
		// }
		// return hoursAgo;
		return hoursAgo;
	}
	
	public void travel(){
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		Calendar last_n_cal = Calendar.getInstance();
		String last_n_date_str = store.getProperty("last_n_date");
		try {
			Date last_n_date = df.parse(last_n_date_str);
			last_n_cal.setTime(last_n_date);
			last_n_cal.add(Calendar.DATE, -1);
			String updatedDate = df.format(last_n_cal.getTime());
			store.setProperty("last_n_date",updatedDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
	
	public int checkDuplicateInteraction(){
		int result=0;
		int daysAgo = 0;
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		if(store.getProperty("last_n_date")==null){
			result=0;
		}
		else{			
			try {
				String last_n_date = store.getProperty("last_n_date");
				Date lastNutritionInteractionDate = df.parse(last_n_date);
				long dif = (Calendar.getInstance().getTime().getTime() - lastNutritionInteractionDate
						.getTime()) / (1000 * 60 * 60 * 24);
				daysAgo = (int) dif;
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if(daysAgo>=1){
				result=2;
			}
			else{
				result = 1;
			}
		}
		return result;
	}

	public int daysSinceInteraction() {
		int daysAgo = 0;
		store.getProperty("start_time");
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		if (store.getProperty("last_start_time") == null) {
			daysAgo = 0;
		} else {
			try {
				Date last_interaction = df.parse(store
						.getProperty("last_start_time"));
				long dif = (Calendar.getInstance().getTime().getTime() - last_interaction
						.getTime()) / (1000 * 60 * 60 * 24);
				daysAgo = (int) dif;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		// String query = "";
		// try {
		//
		// // first got to get the mini-session_id for that interactionNum
		// String firstQuery =
		// "SELECT mini_session_id from mini_sessions where interaction_num = '"
		// + interactionNum
		// +
		// "' and mini_session_id in (select m.mini_session_id from mini_sessions m, sessions s where m.session_id=s.session_id and s.user_id='"
		// + userID + "')  order by mini_session_id DESC LIMIT 1";
		// ResultSet firstRS = stmt.executeQuery(firstQuery);
		// int mini_session_id = 0;
		//
		// if (firstRS.next()) {
		// mini_session_id = firstRS.getInt(1);
		//
		// query =
		// ("SELECT TIMESTAMPDIFF(DAY,(SELECT start_time from mini_sessions where mini_session_id='"
		// + mini_session_id
		// + "'), (SELECT start_time from mini_sessions where mini_session_id='"
		// + (this.mini_session_id) + "'))");
		// System.out.println("query = " + query);
		// ResultSet RS = stmt.executeQuery(query);
		//
		// if (RS.next()) {
		// daysAgo = RS.getInt(1);
		// }
		//
		// }
		//
		// } catch (SQLException ex) {
		// System.out.println(ex);
		// System.out
		// .println("The exception was encountered while running the query: "
		// + query);
		// System.out.println("Unable to get daysSinceEndOfInteraction "
		// + this.mini_session_id);
		// }
		// return daysAgo;
		return daysAgo;
	}

	@Override
	public void loadProperties(Properties p) throws Exception {
		//
		// String property;
		// String value;
		// String query = "";
		//
		// p.clear();
		// try {
		//
		// query = "SELECT property,value FROM properties WHERE user_id="
		// + userID;
		// ResultSet RS = stmt.executeQuery(query);
		// while (RS.next()) {
		// property = RS.getString(1);
		// value = RS.getString(2);
		// p.setProperty(property, value);
		// System.out.println("**********    " + property + " value "
		// + value);
		// }
		//
		// System.out
		// .println("Properties object initialized from DB properties table ");
		// System.out.println("Value ::" + p.size());
		//
		// } catch (SQLException e) {
		// System.out.println(e);
		// System.out
		// .println("The exception was encountered while running the query: "
		// + query);
		// System.out.println("DBSTore Error loading properties "
		// + e.toString());
		// }
	}

	@Override
	public void saveProperties(Properties p) throws Exception {
		// System.out.println("Saving properties from p to DB. Properties list: ");
		//
		// // Thread.dumpStack();
		//
		// if (p != null) {
		// // stmt.executeUpdate("DELETE FROM properties WHERE User_ID=" +
		// // userID);
		//
		// PreparedStatement pstmt = DBCon
		// .prepareStatement("REPLACE INTO properties (`User_ID`,`Property`,`Value`) VALUES (?,?,?)");
		// Enumeration<Object> keys = p.keys();
		// while (keys.hasMoreElements()) {
		// pstmt.setInt(1, userID);
		// String property = (String) keys.nextElement();
		// pstmt.setString(2, property);
		// pstmt.setString(3, (String) p.get(property));
		// try {
		// pstmt.executeUpdate();
		// } catch (Exception e) {
		// System.out.println("SQL exception " + e);
		// System.out.println("on query " + pstmt.toString());
		// System.out.println(e.getStackTrace());
		// }
		// }
		// }
	}

	@Override
	// when setting the value of a particular property, go on and save it in
	// case server crashes
	public void saveProperty(String property, String value) throws Exception {
		// System.out.println("Saving property " + property + " to DB.");
		//
		// try {
		// stmt.executeUpdate("REPLACE INTO properties (`User_ID`,`Property`,`Value`) VALUES ("
		// + userID + ",'" + property + "', '" + value + "')");
		// } catch (Exception e) {
		// System.out.println("SQL exception " + e);
		// System.out.println("on query " + e.getMessage());
		// System.out.println(e.getStackTrace());
		// }
	}

	public String[] getNextGame(String team) {
		String query = "";
		String[] nextGameInfo = new String[3];
		// try {
		//
		// query = "select schedule.scheduled_time, schedule.home, t.name"
		// + " from "
		// +
		// "(SELECT s.scheduled_time, s.opponent, s.home FROM baseball_schedule s "
		// +
		// " WHERE s.team='sox' and scheduled_time > CURRENT_TIMESTAMP	) as schedule, "
		// + " baseball_teams t  "
		// + " where t.baseball_teams_id=schedule.opponent";
		// ResultSet RS = stmt.executeQuery(query);
		// if (RS.next()) {
		// nextGameInfo[0] = RS.getString(1);
		// nextGameInfo[1] = RS.getString(2);
		// nextGameInfo[2] = RS.getString(3);
		// }
		//
		// } catch (SQLException e) {
		// System.out.println(e);
		// System.out
		// .println("The exception was encountered while running the query: "
		// + query);
		// System.out.println("DBSTore Error loading properties "
		// + e.toString());
		// }
		return nextGameInfo;
	}

	public String[] gameTodayInfo() {

		String query = "";
		String[] gameToday = new String[4];
		//
		// try {
		//
		// query =
		// "select s.baseball_schedule_id, t.name as opponent, s.home, DATE_FORMAT(s.scheduled_time, '%l') as start_time from baseball_schedule s, baseball_teams t "
		// +
		// "where DATEDIFF(CURRENT_DATE(), scheduled_time)=0 and s.opponent = t.baseball_teams_id";
		// ResultSet RS = stmt.executeQuery(query);
		// if (RS.next()) {
		// gameToday[0] = RS.getString(1);
		// gameToday[1] = RS.getString(2);
		// gameToday[2] = RS.getString(3);
		// gameToday[3] = RS.getString(4);
		// }
		//
		// } catch (SQLException e) {
		// System.out.println(e);
		// System.out
		// .println("The exception was encountered while running the query: "
		// + query);
		// }

		return gameToday;
	}

	public String[] gameYesterdayInfo() {

		String query = "";
		String[] gameYesterday = new String[4];
		//
		// try {
		//
		// query =
		// "select s.baseball_schedule_id, t.name as opponent, s.home, DATE_FORMAT(s.scheduled_time, '%l') as start_time from baseball_schedule s, baseball_teams t "
		// +
		// "where DATEDIFF(CURRENT_DATE(), scheduled_time)=1 and s.opponent = t.baseball_teams_id";
		// ResultSet RS = stmt.executeQuery(query);
		// if (RS.next()) {
		// gameYesterday[0] = RS.getString(1);
		// gameYesterday[1] = RS.getString(2);
		// gameYesterday[2] = RS.getString(3);
		// gameYesterday[3] = RS.getString(4);
		// }
		//
		// } catch (SQLException e) {
		// System.out.println(e);
		// System.out
		// .println("The exception was encountered while running the query: "
		// + query);
		// }

		return gameYesterday;
	}

	public String[] gameTomorrowInfo() {

		String query = "";
		String[] gameTomorrow = new String[4];
		//
		// try {
		//
		// query =
		// "select s.baseball_schedule_id, t.name as opponent, s.home, DATE_FORMAT(s.scheduled_time, '%l') as start_time from baseball_schedule s, baseball_teams t "
		// +
		// "where DATEDIFF(CURRENT_DATE(), scheduled_time)=-1 and s.opponent = t.baseball_teams_id";
		// ResultSet RS = stmt.executeQuery(query);
		// if (RS.next()) {
		// gameTomorrow[0] = RS.getString(1);
		// gameTomorrow[1] = RS.getString(2);
		// gameTomorrow[2] = RS.getString(3);
		// gameTomorrow[3] = RS.getString(4);
		// }
		//
		// } catch (SQLException e) {
		// System.out.println(e);
		// System.out
		// .println("The exception was encountered while running the query: "
		// + query);
		// }

		return gameTomorrow;
	}
	
	public int getDaysToAsk() throws ParseException{
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		Date last_date = df.parse(store.getProperty("last_n_date"));
		long dif = (Calendar.getInstance().getTime().getTime() - last_date.getTime()) / (1000 * 60 * 60 * 24);
		int days = (int)dif;
		return days;
	}
	
	public int getCurrentCountDay(){
		int count_day = 0;
		count_day = Integer.valueOf(store.getProperty("count_day"));
		return count_day;
	}

	public void cacheVFCountAndGoals() {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		int count_days = 0;		
		int p_count = Integer.valueOf(store.getProperty("count_day"));
		Date last_date;
		try {
			last_date = df.parse(store.getProperty("last_n_date"));
			long dif = (Calendar.getInstance().getTime().getTime() - last_date.getTime()) / (1000 * 60 * 60 * 24);
			int days = (int)dif;
			count_days = p_count+days;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		VFCountCache = new int[count_days];
		VFGoalsCache = new int[count_days];
		for (int i = 0; i < VFCountCache.length; i++) {
			VFCountCache[i] = -1;
			VFGoalsCache[i] = -1;
		}
		for (int i = 0; i < VFCountCache.length; i++) {
			if (store.getProperty("C_"+String.valueOf(i + 1)) != null) {
				VFCountCache[i + 1] = Integer.valueOf(store.getProperty("C_"+String
						.valueOf(i + 1)));
			}
		}
		
		int current_goal=0;
		for (int i = 0; i < VFGoalsCache.length-1; i++) {
			if (store.getProperty("G_"+String.valueOf(i + 1)) != null) {
				VFGoalsCache[i + 1] = Integer.valueOf(store.getProperty("G_"+String
						.valueOf(i + 1)));
				current_goal = Integer.valueOf(store.getProperty("G_"+String
						.valueOf(i + 1)));
			}
			else{
				if(i>3){
				   VFGoalsCache[i + 1] = current_goal;
				}
			}
			
		}
		System.out.println("\nCached steps...");
		for (int j = 0; j < VFCountCache.length; j++) {
			System.out.println("Day " + j + " Steps = " + VFCountCache[j]);
			System.out.println("Day " + j + " Goals = " + VFGoalsCache[j]);
		}
	}

	// This can be reused.
	public int[] getVFCountCache() {
		if (VFCountCache == null) {
			cacheVFCountAndGoals();
		}
		return VFCountCache;
	}

	// This can be reused.
	public int[] getVFGoalsCache() {
		if (VFGoalsCache == null) {
			cacheVFCountAndGoals();
		}
		return VFGoalsCache;
	}

	public void updateVFCache(int day, int steps) {
		if (VFCountCache != null) {
			if ((day < VFCountCache.length) && (day > -1)) {
				VFCountCache[day] = steps;
			}
		}
	}

	public void updateVFGoalsCache(int day, int steps) {
		if (VFGoalsCache != null) {
			if ((day < VFGoalsCache.length) && (day > -1)) {
				VFGoalsCache[day] = steps;
			}
		}
	}
	
//	public void recordVF(int studyday, int mins) {
//		// store.setProperty("C_"+String.valueOf(studyday),
//		// String.valueOf(mins));
//		store.setProperty(String.valueOf(studyday), String.valueOf(mins));
//		if (VFCountCache != null) {
//			cacheVFCountAndGoals();
//		}
//	}
	
	public void recordVF(int count_day, int mins) {
		store.setProperty("C_"+String.valueOf(count_day),String.valueOf(mins));
		if (VFCountCache != null) {
			cacheVFCountAndGoals();
		}
	}

	public void recordVFGoal(int goalPerDay) {
		int studyDay = Integer.valueOf(store.getProperty("STUDY_DAY"));
		store.setProperty("G_" + String.valueOf(studyDay),
				String.valueOf(goalPerDay));
		if (VFGoalsCache != null) {
			updateVFGoalsCache(studyDay, goalPerDay);
		}
	}

	public int getVFSteps(int dayToGet) {
		if (VFCountCache == null)
			cacheStepsAndGoals();
		int highestIndex = VFCountCache.length - 1;
		if (dayToGet > highestIndex || dayToGet < 0) {
			return -1;
		} else {
			return VFCountCache[dayToGet];
		}
	}

	public int getMax(String property) {
		// String query="SELECT VALUE from properties where user_id=" + userID
		// +" and property='" + property + "'";
		// int max = -1;
		// int num = -1;
		// try{
		// ResultSet RS = stmt.executeQuery(query);
		// while(RS.next()) {
		// String str = RS.getString(1).replaceAll("[a-zA-Z]", "");
		// if (str.length()>0){
		// num = Integer.parseInt(str);
		// if (num > max){
		// max = num;
		// }
		// }
		// }
		// } catch(SQLException e){
		// System.out.println("Unable to find the Language for the User " +
		// userID);
		// System.out.println("Query was " + query);
		// }
		int max = -1;
		max = Integer.valueOf(store.getProperty(property));
		return max;
	}

}