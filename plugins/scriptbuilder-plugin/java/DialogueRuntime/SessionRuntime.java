//To do - need a method to declare userID once known.

package DialogueRuntime;

import java.nio.charset.Charset;
import java.util.*;

//import Stanford.StanfordDBStore;
//import Stanford.TestLog;

/* Represents the runtime, server-side services for the dialogue 
   session. Subclasses will include methods for makePlot() and 
   saveSteps(), etc. as required. Note: may not have userID into well
   into the dialogue (e.g., in IVR). */

	public class SessionRuntime {
	    protected PersistentStore store;
	    private PropertiesInitializer propertiesInitializer=null;
	    protected Properties userProperties=null;
	    private int sessionID = -1;
	    
	    public SessionRuntime(PersistentStore s, PropertiesInitializer i) {
	    	if(!(s ==null)) {
		  		store=s;
		  	}
			
	    	if (!(i == null)) {
		  		propertiesInitializer=i;
		  	}
		    	
		    userProperties = new Properties();
	    }
	    
	    /* Performs all initialization, including loading persistent stores
	       and initializing properties. Called from session.start, where the userid 
	       may not be known yet */
	    public void initialize(boolean singleuser) throws Exception {
            if (!(store == null)) {
                    store.open();
            }
            System.out.println("Session Run Time ************** Before Loading");
            System.out.println("!!!!!Value of singleuser::"+ singleuser);
            System.out.println("!!!!!Store Value ::" + store);
            if (singleuser && store != null) {
                if (store.tryLoadProperties(userProperties)) {
                    if (!(propertiesInitializer == null)) {
                        propertiesInitializer.initialize(store, userProperties);
                    }
                 }
            }
            System.out.println("In sessionruntime.initialize, after propertiesinitializer");
            //userProperties.list(System.out);
	    }
	    
	    // Executed once the userid is known (after login).  Loads up all 
	    // stores and does properties initialization for the particular userid.
	    public void initializeForUser(int ID) throws Exception {
	    
	        store.setUserID(ID);
			int studyDay = getStore().usersComputeStudyDay();
			setUsersStudyDay(studyDay);	
	        if (store.tryLoadProperties(userProperties)) {
		        if (!(propertiesInitializer == null)) {
		        	propertiesInitializer.initialize(store, userProperties);
		        }
	        } else {
	        	throw new Exception ("User ID cannot be loaded");
	        }
	    }
	    
	    public int addSession(ServerConstants.Media media) {
	    	return store.addSession(media);
	    }
	    
	   public int getSessionID() {
		   return sessionID;
	   }
	   
	   public void setSessionID(int sessionID) {
		  this.sessionID = sessionID;
	   }
	    
	    
	    public String getProperty(String propName) {
	    	return store.store.getProperty(propName);
//	    return(userProperties.getProperty(propName));
	    }
	       
	    public void setProperty(String propName, String value) {
	    	store.store.setProperty(propName, value);
//	    userProperties.setProperty(propName,value);
	    }
	  
	    public void saveAndClearProperties() throws Exception {
			saveStores();
	    	userProperties.clear();
	    }
	    
	    public void checkPoint() throws Exception{
			if(store!=null) store.saveProperties(userProperties);
	}
	
	    /**
	     * Returns a reference to the global properties object. No other class
	     * should instantiate another version of properties.
	     * @return
	     * @throws Exception
	     */
	    public Properties getProperties() throws Exception {
	    return userProperties;
	    }
	    
	    public void setUsersStudyDay(int day) {
	    	userProperties.setProperty("STUDY_DAY",""+day);
	    	if (store instanceof DBStore) {
	    		store.setUsersStudyDay(day);
	    	}
	    }
	    
	    public int getUserID() {
	    	return Integer.parseInt(userProperties.getProperty("USERID"));
	    }
	    
	    public Enumeration<Object> enumProperties() {
	        return(userProperties.elements());
		}
	  
	    
	    /**
	     * Open and read any information needed from the files for this dialogueSession
	     * @throws Exception
	     */
	    protected void loadStores() throws Exception {
	    	if(store!=null) store.loadProperties(userProperties);
	    }
	    
	    /* Performs all end of session closeout actions, including saving 
	       stores. */
	    public void shutdown(DialogueListener.TerminationReason reason) throws Exception {
	    	try {
		    //System.out.println("SessionRuntime.shutdown... saving properties, props="+userProperties);
	        //twb - not here: userProperties.setProperty("LAST_SESSION_INT",userProperties.getProperty("THIS_SESSION_INT"));
		    //System.out.println("h9.");
	    		//To check if no store has been initiated
	    		if(store!=null) store.addLog(LogEventType.TERMINATION, reason.toString());
				saveStores();
				if(store!=null) 
				{
					store.close(reason);
					System.out.println("closing store");
				}
			}catch(Exception e) {
			    System.out.println("shutdown ex: "+e);
			    throw e;
			}
	    }     
	     
	    public void saveStores() throws Exception {
		//System.out.println("SessionRuntime.saveStores, store="+store);
	    	if(store!=null) store.saveProperties(userProperties);
	    }
	
	    public void log(LogEventType eventType,String arg) throws Exception {
	    	if(store!=null) store.addLog(eventType,arg);
	    }
	
	    public void debug(String msg) {
	    	if(store == null)
	    	{
	    		System.err.println(msg);
	    	}
	    	else
	    	{
	    	System.err.println("USER: " + store.userID + " " + msg);
	    	}
	    }
	    
	    public PersistentStore getStore(){
	    	return store;
	    }
	    
	    //@Override
	    public  void saveSteps(int studyday, int steps){
		  	userProperties.setProperty("PA_BEHAVIOR_"+studyday, ""+steps);
		  	if (store instanceof DBStore) {
		  		((DBStore) store).recordSteps(studyday, steps);
		  	}
	    }
	    
	    // alternate saveSteps method when you want to set the 'good_data' flag
	    public  void saveSteps(int studyday, int steps, int dataflag){
		  	userProperties.setProperty("PA_BEHAVIOR_"+studyday, ""+steps);
		  	if (store instanceof DBStore) {
		  		((DBStore) store).recordSteps(studyday, steps);
		  		((DBStore) store).addDataFlag(studyday,steps,dataflag);
		  	}
	    }

	    public int getSteps(int studyDay) {
	    	int returnval = -1;
	    	
	    	if (store instanceof DBStore) {
	    		returnval =  ((DBStore) store).getSteps(studyDay);
	    	} else if (store instanceof FileStore) {
	    		String steps = userProperties.getProperty("PA_BEHAVIOR_"+studyDay);
	    		try {
	 			   int theSteps = Integer.parseInt(steps);
	    			returnval = theSteps;
	    			} catch (NumberFormatException e) {
	    				returnval =  -1;
	    			}
	    		}
	    	return returnval;
	    	}
	    
	   public void setStatusCompleted() {
		   ServerConstants.UsersStatus us = ServerConstants.UsersStatus.COMPLETED;
		   if (store instanceof DBStore) {
			   ((DBStore) store).setUsersStatus(us); 
		   } else if (store instanceof FileStore) {
			   userProperties.setProperty("STATUS",us.toString());
		   }
	   }
	   
	   public String getUserStatus() {
		   String returnval = "";
		   if (store instanceof DBStore) {
			  returnval =  ((DBStore) store).getStatus().toString(); 
		   } else if (store instanceof FileStore) {	
			   returnval =  userProperties.getProperty("STATUS");
		   }
		   return(returnval);
	   }
	   
	   //* Data access stubs for the dialogs. Must be overridden for each project.
	   // override
	   public void CACHE_STEPS(DialogueStateMachine _DSM_) {
		    }
				  
		    /** Primitive to access steps information for the current user.
		     *  Probably implemented by caching all user steps for user at startup
		     *  into an array, then providing a local index. Returns UNDEFINED for
		     *  undefined days.
		     *  
		     *  For now, assumes steps are stored in PA_BEHAVIOR_<studyday> properties,
		     *  per upitt medtrack. This can be updated as needed - just assumes that
		     *  we can cheaply query many individual days of steps. However, for
		     *  for ElderWalk/tablet at least, will need to keep them here.
		     */
		    //Override
		    public int STEPS(int studyDay,DialogueStateMachine _DSM_) throws Exception {
		         return 0;
		    }

		    /** Another stub - placeholder that will be used to store daily step goals in the DB
			for plotting. Use GETINT("STUDY_DAY") for day index. May record '-1' for undefined or cleared goal. */
		    public void recordGoal(DialogueStateMachine _DSM_,int stepsPerDay) throws Exception {
			
		    }
		    
		    public void MAKE_PLOT(DialogueStateMachine _DSM_) {}    
		    
		    // for debugging
		    public void dumpProperties() {
		    	System.out.println("*** DEBUG Current properties values:");
		    	System.out.println("******** Count ::" + userProperties.size());
		    	Iterator<Object> i = userProperties.keySet().iterator();
		    	String key = new String();
		    	while (i.hasNext())  {
		    			    key = (String) i.next();
		    	           System.out.println(key + " = " + userProperties.get(key));
		    	}
		    }
	
		   
		     
}

	
 
