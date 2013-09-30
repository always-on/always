package DialogueRuntime;
import static java.lang.Integer.parseInt;
import DialogueRuntime.com.*;

import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.regex.*;
import java.util.Vector;

//import webframe.Command;

/* Represents a single interaction session (conversation) with an agent. Talks to
   the client via the Client object, accesses server-side resources (database, etc.) 
   via the DialogueRuntime object, and interprets scripts (loaded from the Dialogue package 
   in the server runtime environment) starting with the start state for the specified script. */

/* Note: DialogueSession 'owns' the properties table (can run stand-alone without
   persistent store across sessions). DialogueRuntime & PersistentStore merely load & save. */
public abstract class DialogueSession extends QueueModule implements ClientListener {
    public Client client; //made public so client is visible to FlashDialogueSessionInitializer 
    protected SessionRuntime runtime;
    protected String script;
    protected DialogueStateMachine DSM=null;
    public DialogueStateMachine getDSM() { return DSM; }
    private boolean killSwitch;
    private Date lastEventTime;

    /* Can be overridden in script for particular states. */
    protected int timeout1=15000;
    protected int timeout2=30000;
    public void setDefaultTimeout1(int ms) { timeout1=ms; }
    public void setDefaultTimeout2(int ms) { timeout2=ms; }

    public static final String NOTIMEOUT_SUFFIX="_NOTIMEOUT";
    public static final int OUTPUT_TIMEOUT=30000; //max we'll wait for a PERFORM_COMPLETE, in ms
    public static final int FINAL_USER_TIMEOUT=10000; //time after 'Are you there?' before bailing

    protected DialogueSession(Client c, SessionRuntime r, String scriptIn, String projname) {
    	super("DIALOGUE");
    	try{
    		System.out.println("*********** " +projname );
	    	DialogueStateMachine.loadStates(projname); //not clear this should be done for every session - static init?
	    	System.out.println("Finished loading script classes");
	    	client=c;
	    	runtime=r;
	    	script=scriptIn;
	    	client.setListener(this);
	    	client.registerRuntime(runtime);
    		DSM=new DialogueStateMachine(this,script,null,r);
		activeSessions.addElement(this);
    	}catch (Exception e){
    		handleException("DialogueSession constructor", e);
    		e.printStackTrace();
    	}
    	killSwitch = false;
    }
    
    public void setStateNameDisplay(boolean which) {
    	DSM.setStateNameDisplay(which);
      }
    
    private static Vector activeSessions=new Vector();
    public static Vector getActiveSessions() { return activeSessions; }

    public Client getClient() { return client; }
    public SessionRuntime getRuntime() { return DSM.getRuntime(); }

    /* Associates an optional DialogueListener with this session that is 
       sent events related to the session. */
    protected DialogueListener dialogueListener=null;
    public void setDialogueListener(DialogueListener l) { dialogueListener=l; }

    
    /* Associates an optional DialogueSessionInitializer with this session. */
    private DialogueSessionInitializer initializer=null;
    public void setDialogueSessionInitializer(DialogueSessionInitializer i) { 
	System.out.println("Setting dlgSessionInitializer...");
	initializer=i; 
    }
    
    public DialogueSessionInitializer getInitializer(){
    	return initializer;
    }

    private boolean initialized=false;

    public void start() throws Exception {
    	/*System.out.println("dlgSession.start()...  initializer="+initializer);
    	try{
        	runtime.initialize();
        	if(initializer!=null) 
        		initialized=initializer.handleInitializationEvent(null,this);
        	startup();
        	client.start();
    	}catch(Exception e){
    		handleException("DialogueSession.start", e);
    	}*/
    	boolean singleuser;
    	if (runtime.store instanceof DBStore){
    		singleuser = false;
    	}else{
    		singleuser = true;
    	}
    	System.out.println("===> Within Start");
    	runtime.initialize(singleuser);  
    	if(initializer!=null){
    		initializer.handleInitializationEvent(null, this);
    	}
        startup();
        client.start();
    }

    /* Aborts execution of the script by forever ignoring Client events 
       and killing all timers. */
    void abort() {
		killAllTimers();
		client.setListener(null);
    }

    /* Specifies an optional script to run if an exception occurs and
       DialogueListener.exception() returns RESTART_DESIGNATED. Note:
       if used, the stack is first flushed before this is started. */
    private String exceptionScript=null;
    public void setExceptionScript(String s) { exceptionScript=s; }

    /* Perform n'th action in current state. Returns true if dialogue should continue. */
    protected boolean doAction(int n) {
	try {
	    if (DSM.doAction(n) == DialogueStateMachine.HALT) {
		return false; //normal end of conversation
	    }
	}catch(Exception e){
		handleException("DialogueSession.start", e);
		return false;
	}
	return true;
    }

    public static final OutputText[][] OK_MENU_OPTS={
    	{new OutputText("OK.")},
    	{new OutputText("Right.")},
    	{new OutputText("Sure.")},
    	{new OutputText("Go on.")},
    	{new OutputText("OK.")},
    	{new OutputText("I understand.")}
    };

    public static final OutputText[] REPEAT_OPTS={
    	new OutputText("Excuse me?"),
    	new OutputText("Could you repeat that please?"),
    	new OutputText("What was that again?"),
    	new OutputText("Excuse me?"),
    	new OutputText("Could you repeat that please?"),
    	new OutputText("What was that again?")
        };

    /* Send MENU or TEXT prompt.  */
    protected String[] lastMenu;
    
    
    /*
     * 
     * LMP: I changed the return statement to work with webframework.
     * It used to return: "<INPUT PROMPT=\"" + DSM.getTextPrompt() + "\"/>"
     * 
     */
    /*
    protected String getInputSpec() { 
    	try{
			if (DSM.getStateType() == DialogueState.MENU_INPUT) {
				List<String> outputs= DSM.getMenuPromptsAsList(enhanceMenu(DSM.getMenuPrompts()));
			    return "<INPUT PROMPT=\"" + DSM.getTextPrompt() + "\"/>";
			} else if (DSM.getStateType() == DialogueState.TEXT_INPUT) {
				return "<INPUT PROMPT=\"" + DSM.getTextPrompt() + "\"/>";
			} else
			    throw new Exception("Calling getInputSpec() on non-input state of type "+DSM.getStateType());
    	}catch(Exception e){
    		handleException("DialogueSession.start", e);
    		return null;
    	}
    }
    */
    
    protected String getInputSpec() {
        try{
                        if (DSM.getStateType() == DialogueState.MENU_INPUT) {
                            OutputText[] outputs=DSM.getMenuPrompts();
                            if(outputs.length==1) { //TWB 3/29/08 - add more randomized opts if just 'OK'
                                        String prompt=outputs[0].toString().toUpperCase().trim();
                                        if(prompt.equals("OK") || prompt.equals("OK.")) {
                                            //Expand & vary options
                                            outputs=OK_MENU_OPTS[DialogueState.PICK(OK_MENU_OPTS.length)];
                                        }
                                };
                                String norepeat=DSM.getStateFlag("NO_REPEAT");
                                boolean addRepeatChoice = !(norepeat!=null && norepeat.toUpperCase().trim().equals("TRUE"));
                                if(addRepeatChoice) outputs=addRepeatChoice(outputs);
                            lastMenu=new String[outputs.length];
                            for(int i=0;i<outputs.length;i++) lastMenu[i]=outputs[i].getOutput();
                            return DialogueState.menuXml(outputs,DSM);
                        } else if (DSM.getStateType() == DialogueState.TEXT_INPUT) {
                            return "<INPUT PROMPT=\"" + DSM.getTextPrompt() + "\"/>";
                        } else
                            throw new Exception("Calling getInputSpec() on non-input state of type "+DSM.getStateType());
        }catch(Exception e){
                handleException("DialogueSession.start", e);
                return null;
        }
    }
    
    public OutputText[] enhanceMenu(OutputText[] originalMenu) throws Exception {
    	//OutputText[] outputs = new OutputText[originalMenu.length];   	
	    if(originalMenu.length==1) { //TWB 3/29/08 - add more randomized opts if just 'OK'
			String prompt=originalMenu[0].getOutput().toString().toUpperCase().trim(); // chaamari 11/26/2008 included getOutput() 
			
			if(prompt.equals("OK") || prompt.equals("OK.")) {
			    //Expand & vary options
				 debug("prompt "+prompt+" prompt.equals(\"OK\") " + prompt.equals("OK"));
			    originalMenu=OK_MENU_OPTS[DialogueState.PICK(OK_MENU_OPTS.length)];
			    debug("outputsafterpick.toString() "+originalMenu[0]+" length is " + originalMenu.length);
			}
		}
		
		String norepeat=DSM.getStateFlag("NO_REPEAT");
		boolean addRepeatChoice = !(norepeat!=null && norepeat.toUpperCase().trim().equals("TRUE"));
		if(addRepeatChoice) originalMenu=addRepeatChoice(originalMenu);
	    lastMenu = new String[originalMenu.length];
	    for(int i=0;i<originalMenu.length;i++) 
	    	lastMenu[i]=originalMenu[i].getOutput();
    	return originalMenu;
    	}
    
    public OutputText[] addRepeatChoice(OutputText[] outputs) {
		OutputText[] newOpts=new OutputText[outputs.length+1];
		for(int i=0;i<outputs.length;i++) newOpts[i]=outputs[i];
		newOpts[newOpts.length-1]=REPEAT_OPTS[DialogueState.PICK(REPEAT_OPTS.length)];
		return newOpts;
    }
    
    /* Inner class - represents an interpreter state machine state. */
    public class State {
        public void enter() throws Exception {}
        public void processEvent() throws Exception {}
        public void leave() throws Exception {}
        public String getName() { return "???"; }
        public String toString() { return "<DlgIntState: "+getName()+">"; };
    }

    public enum EventType { ET_UNKNOWN, ET_LOGIN, ET_USER_INPUT, ET_PERFORM_COMPLETE,  ET_TIMER, ET_USER_EXIT, ET_START, ET_WEBPAGE, ET_STEPS_ERROR,ET_STEPS,ET_IVRTIMEOUT, ET_RFID_ARRIVE, ET_RFID_LEFT, ET_EVALUATION };
    protected EventType eventType;
   

    protected State state=null;
    protected State prevState=null;
    protected int userChoice;
    protected String userChoiceString="";

    protected static final int UNDEFINED=-1;

    protected void changeState(State newState) {
    	debug("\nSESSION: "+displayDialogueState()+"\t "+newState.getName());
    	try {
    		killAllTimers();
	    if(state!=null) state.leave();
	    	prevState=state;
	    	state=newState;
	    	state.enter();
    	}catch(Exception e) {
    		handleException("changeState",e);
    	}
    }

    protected void handleException(String where,Exception e) {
    	//int result=
		if(dialogueListener!=null) 
		    dialogueListener.exception(e);
		// Shut everything down.
		try{
			e.printStackTrace();
			runtime.store.addLog(LogEventType.INTERNAL_ERROR, e.getMessage() + " at" + where);
		}catch(Exception ex){
			ex.printStackTrace();
		}
    }

    /* Kills all threads, etc. */
    // If killswitch is set to TRUE it also powers down the client machine
    public void shutdown(DialogueListener.TerminationReason reason) {

    	debug("\nSESSION: shutdown");
    	if (killSwitch) {
    		//TWB - must save properties out via runtime.shutdown() - even if shutting PC down???
    		try{
    			client.shutdown();
    		}catch(Exception e){
    			handleException("DialogueSession.shutdown", e);
    		}
    	} else {
    		client.close(); //Kill client stream
    		System.out.println("client.close()");
    		// RF: a super.shutdown() here will interrupt the thread and close our DB connection, so it's moved down
    		try { 
    			runtime.log(LogEventType.TERMINATION,""+reason); 
    			System.out.println("runtime.log(LogEventType.TERMINATION,reason)");
    		}catch(Exception e){
    			handleException("DialogueSession.shutdown", e);
    		};
    		try { 
    			runtime.shutdown(reason); 
    			System.out.println("runtime.shutdown(reason)");

    		}catch(Exception e){
    			handleException("DialogueSession.shutdown", e);
    		};  //save stores, disconnect db, etc.
    		super.shutdown(); //Kill event queue processing thread.
    		if(dialogueListener!=null) {
    			dialogueListener.terminationEvent(reason);
    			System.out.println("dialogueListener.terminationEvent");
    		}
    	}
    	activeSessions.removeElement(this);
    	System.out.println("activeSessions.removeElement");
    }

    protected State declaredStartState;

    protected String displayDialogueState() {
        if(DSM==null) return "---";
		try { 
		    return DSM.getStateName();
    	}catch(Exception e){
    		handleException("DialogueSession.displayDialogueState", e);
    		return "---";
    	}
    }

    protected String event;
    public String getEvent() {
		return event;
	}
	public void processEvent(String cevent) throws Exception{
    	this.setLastEventTime(new Date());
	    System.out.println("dlgsessn.procEvent: "+cevent);
		debug("SESSION.processEvent("+cevent+"), currentDState="+displayDialogueState()+", initialized="+initialized);
		if(dialogueListener!=null) dialogueListener.clientEvent("U: "+cevent);
		
		//If we're not initialized yet, then the initializer (if any) eats all events.
		try{
			if(!initialized && initializer!=null) {
		    	initialized=initializer.handleInitializationEvent(cevent,this);
		    	if(initialized)
		    		debug("SESSION: Initializer run, initialization COMPLETE.");
		    	else
		    		debug("SESSION: Initializer run, initialization not yet complete.");
		    	if(initialized) {
			    if(getActiveSessions().contains(this))
		    		changeState(declaredStartState);
		    	};
		    	return;
			}; //either initialized or no initializer;
    	}catch(Exception e){
    		handleException("DialogueSession.processEvent", e);
    	}
    	
		//Else, normal (dialogue) event processing.
	    event=cevent;
	    Matcher m = ServerConstants.OMRON_DAY_P.matcher(cevent);
	    Matcher err = ServerConstants.OMRON_ERROR_P.matcher(cevent);
	    
		if(cevent.startsWith("<USER_INPUT")) eventType=EventType.ET_USER_INPUT;
		else if(cevent.startsWith("<PERFORM_COMPLETE")) eventType=EventType.ET_PERFORM_COMPLETE;
		else if(cevent.startsWith("<TIME")) eventType=EventType.ET_TIMER;
		else if(cevent.startsWith("<USER_EXIT")) eventType=EventType.ET_USER_EXIT;
		else if(cevent.startsWith("<START")) eventType=EventType.ET_START;
		else if(cevent.startsWith("<WEB_PAGE")) eventType=EventType.ET_WEBPAGE;
		else if(cevent.startsWith("<IVR_NO_RESPONSE")) eventType=EventType.ET_IVRTIMEOUT;
		else if(cevent.startsWith("<RFID_ARRIVED")) eventType=EventType.ET_RFID_ARRIVE;
		else if(cevent.startsWith("<RFID_LEFT")) eventType=EventType.ET_RFID_LEFT;
		else if(cevent.startsWith("<EVALUATION")) eventType=EventType.ET_EVALUATION;
		else if(m.find()) eventType=EventType.ET_STEPS;
		else if(err.find()) eventType=EventType.ET_STEPS_ERROR;
		else eventType=EventType.ET_UNKNOWN;
	
		try{
			if(eventType==EventType.ET_USER_INPUT) { 
				System.out.println("%%%%%%%%%%%%%%%  Deciding the input");
			    String menu;
			    if((menu=parseAttribute(event,"MENU"))!=null) {
			    	userChoice=Integer.parseInt(menu);
			    	userChoiceString=lastMenu[userChoice];
					clientInputEvent(userChoiceString);
			    } else if((menu=parseAttribute(event,"TEXT"))!=null && menu.length()>0) {
			    	//userChoice=Integer.parseInt(menu);
			    	userChoiceString=""+menu; //=""+userChoice
					clientInputEvent(userChoiceString);
			    }/* else if(parseAttribute(event,"TYPE").equalsIgnoreCase("CHECKBOX")){
			    	System.out.println("Input Recieved :: "+ event);
			    	//clientInputEvent("0"); // decide
			    } */
			    else {
			    	userChoice=UNDEFINED;
					userChoiceString="";
			    };
			    DSM.SET(DialogueStateMachine.MENU_TEXT_PARAM,userChoiceString);
			    //twb - changing log data to index - allows us to traverse dialogue networks from DB dump.
			    runtime.log(LogEventType.USER_INPUT,""+userChoice+": "+userChoiceString);
			    debug("SESSION: User input = "+userChoiceString);
			};
    	}catch(Exception e){
    		handleException("DialogueSession.processEvent", e);
    	}
	
		//if eventType == EventType.ET_STEPS , go through message and load steps to persistent 
		if(eventType==EventType.ET_STEPS) {
			 m.reset();
			while (m.find()) {
				int month = parseInt(m.group(1));
				int day = parseInt(m.group(2));
				int year = parseInt(m.group(3));
				int steps = parseInt(m.group(4));
				debug("Got some steps for date "+ month + "/" + day + "/" + year+ " = " + steps);
				Calendar c = Calendar.getInstance();
				
				c.set(year, (month - 1 ), day);
				int studyday = runtime.store.usersComputeStudyDay(c);		
				//this returns zero for all dates before and equal to study day.
				if(studyday>0) {
				    runtime.saveSteps(studyday,steps);
				    debug("*found " + steps + " steps for study day " + studyday);
				} else
				    debug("*not saving steps");
			}				
		};
		
		if(eventType==EventType.ET_USER_EXIT) {
		    shutdown(DialogueListener.TerminationReason.USER_EXIT);
		    return;
		};
	
		try {
		    state.processEvent();
    	}catch(Exception e){
    		handleException("DialogueSession.processEvent", e);
    	}
    }

    // set the killSwitch flag to TRUE if you want the client machine to be powered down when the 
    // dialog session is terminated
	public void setKillType(boolean TotalShutdown) {
		killSwitch = TotalShutdown;
	}
	
	public void clientWrite(String xml) throws Exception {
System.out.println("******");		
		getClient().write(xml);
		if(dialogueListener!=null) dialogueListener.clientEvent("A: "+xml);
	}

	public void clientPerform(String xml,DialogueSession session) throws Exception {
		System.out.println("******");		
		getClient().perform(xml,session); 
		if(dialogueListener!=null) dialogueListener.clientEvent("A: "+xml);
	}
	
    /* ------------------ ClientListener implementation --- */
    public void clientInputEvent(String xml){
	    System.out.println("U: "+xml);
		addEvent(xml);
    }
    //public void clientOutputEvent(String xml){
	    //System.out.println("clientOutputEvent: "+xml);
		//addEvent(xml);
    //}

    /* ------------------- DEBUG stuff ---------------- */
    protected boolean DEBUG=false;
    public void setDEBUG(boolean to) { DEBUG=to; }
    public boolean getDEBUG() { return DEBUG; }
    public void debug(String msg) { if(DEBUG) runtime.debug(msg); }

    /* ------------------- UTILITIES ------------------ */
    
        //Generate a random int within the range.
    public static int randomRange(int low,int hi) {
        return (int)(low+(hi-low)*Math.random());    
    }
    
    //Returns true P*100% of the time.
    public boolean randBool(double P) {
        return P>Math.random();
    }
	/**
	 * @return the lastEventTime
	 */
	public Date getLastEventTime() {
		return lastEventTime;
	}
	/**
	 * @param lastEventTime the lastEventTime to set
	 */
	protected void setLastEventTime(Date lastEventTime) {
		this.lastEventTime = lastEventTime;
	}

}

