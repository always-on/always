package DialogueRuntime;

import java.util.*;
 
public class DialogueStateMachine {
    public static final String MENU_TEXT_PARAM = "_MENU_TEXT_";
    protected static  boolean REPORT_STATE_NAME = false; // if true, add the state name to the PERFORM tag as a new tag
    protected static  boolean REPORT_SCRIPT_NAME = false;
    
    public static Map<String, DialogueScript> scripts = new HashMap<String, DialogueScript>();
    
    public static void loadStates() throws Exception {
    	loadStates("");
    }
    
    public static void loadStates(String name) throws Exception {
    	if (name == null)
    		name = "";
    	System.out.println(" *************Name " + name);
	    @SuppressWarnings("rawtypes")
		Class c = Class.forName("Dialogue." + name + "Initializer");
	    DialogueInitializer init = (DialogueInitializer) c.newInstance();
	    init.initialize(scripts);
    }
     
    public void setStateNameDisplay(boolean which) {
    	REPORT_STATE_NAME = which;
    	REPORT_SCRIPT_NAME = which;
    }
   
    public static boolean scriptExists(String scriptName) {
	return scripts.containsKey(scriptName);
    }

    protected DialogueSession session; //was server
    protected SessionRuntime runtime;
    //Properties userProperties;
    public Stack<DialogueState> stack = new Stack<DialogueState>(); // of Dialogue.DialogueState = ATN
    protected DialogueState previousState;
	
    public DialogueStateMachine() {}

    public DialogueSession getSession(){
    	return session;
    }
    
    public String getStateFlag(String key) throws Exception {
       if (stack.empty())
    	    throw new Exception("DSM: trying to getStateFlag on empty stack.");
       return ((DialogueState) stack.peek()).getFlag(key);
     }
  

    public DialogueStateMachine(DialogueSession session, String startScript,
				String startState, SessionRuntime r) throws Exception {
		this.session = session;
		runtime = r;
		DialogueScript script = scripts.get(startScript);
		if (script == null)
		    throw new Exception("DSM: Unknown start script " + startScript);
		DialogueState state = null;
		if(startState != null)
		    state = script.getState(startState);
		if(state == null)
		    state = script.getInitState();
		if (state == null)
		    throw new Exception("DSM: Empty start script " + startScript);
		stack.push(state);
    }
    
    public void restart(String startScript, String startState) throws Exception {

	DialogueScript script = scripts.get(startScript);
	if (script == null)
	    throw new Exception("DSM: Unknown start script " + startScript);
	DialogueState state = null;
	if(startState != null)
	    state = script.getState(startState);
	if(state == null)
	    state = script.getInitState();
	if (state == null)
	    throw new Exception("DSM: Empty start script " + startScript);
	stack.push(state);
}
    public static final int CONTINUE = 0;
    public static final int HALT = 1;

    public int doAction(int n) throws Exception { // -> HALT | CONTINUE
		if (stack.empty())
		    throw new Exception("DSM: trying to doAction on empty stack.");
		previousState = stack.peek();
		stack.peek().doAction(this, n);
		if (stack.empty())
		    return HALT;
		else
		    return CONTINUE;
    }

    // returns ACTION_ONLY | OUTPUT_ONLY | MENU_INPUT | TEXT_INPUT
    public int getStateType() throws Exception {
		if (stack.empty())
		    throw new Exception("DSM: trying to doAction on empty stack.");
		return (stack.peek()).getStateType();
    }

    public String getScriptName() {
		if (stack.empty())
		    return null;
		return (stack.peek()).getScript().getName();
    }

    public String getScriptFullName() {
    	if (stack.empty())
    		return null;
    	return (stack.peek()).getScript().getFullName();
    }
    
    public DialogueScript getScript() {
		if (stack.empty())
		    return null;
		return (stack.peek()).getScript();
    }
	
    public String getStateName() {
		if (stack.empty())
		    return null;
		return (stack.peek()).getStateName();
    }

    // return <PERFORM>... to send to client.
    public OutputText getOutput() throws Exception {
		if (stack.empty())
		    throw new Exception("DSM: trying to getOutput on empty stack.");
	
		DialogueState state = (DialogueState) stack.peek();
		OutputText output = state.getOutput(this);
		
		if (REPORT_STATE_NAME) {
			String info = state.name + "-Day" +GET("STUDY_DAY");
			if(REPORT_SCRIPT_NAME) info = getScriptFullName() + "." + info;
			String stateNameCmd = "<DEBUG COMMAND=\"State\" INFO=\"" + info + "\" /> ";
			return new OutputText(output.getSource(), stateNameCmd + output.getOutput()); // add state name information to the message
		}
		else
			return  output; // just return the output, no state names
    }

    // return <PERFORM>... to send to client.
    public OutputText getRepeatOutput() throws Exception {
		if (stack.empty())
		    throw new Exception("DSM: trying to getRepeatOutput on empty stack.");
		return (stack.peek()).getRepeatOutput(this);
    }

    // returns text prompt (non-evaluated)
    public String getTextPrompt() throws Exception {
		if (stack.empty())
		    throw new Exception("DSM: trying to getTextPrompt on empty stack.");
		return (stack.peek()).getTextPrompt(this);
    }

    // returns text of the FLAG that matches string input (if it exists for this state)
    public String getFlag(String key) throws Exception {
		if (stack.empty())
		    throw new Exception("DSM: trying to getFlag on empty stack.");
		String result=(stack.peek()).getFlag(key);
		if(result==null)
			return "";
		else
			return result;
    }

    // returns text of the FLAG that matches string input (if it exists for this state)
    public boolean hasFlag(String key) throws Exception {
		if (stack.empty())
		    throw new Exception("DSM: trying to getFlag on empty stack.");
		return (stack.peek()).hasFlag(key);
    }
    // return menu <PERFORM> (non-evaluated)
    public OutputText[] getMenuPrompts() throws Exception {
		if (stack.empty())
		    throw new Exception("DSM: trying to getMenu on empty stack.");
		return (stack.peek()).getMenuPrompts(this);
    }
    
    public String[] getMenuPromptsAsString(OutputText[] oldoutputs) throws Exception {
  
		String[] strings=new String[oldoutputs.length];
		if ((stack.peek()).getStateType() == DialogueState.MENU_INPUT) {
			for(int i=0;i<oldoutputs.length;i++){
				strings[i]=oldoutputs[i].getOutput().toString();
			}
		} else {
		    throw new Exception ("DSM: called getMenuPromptsAsString but state type is: " + getStateType());
	    }	
		return strings;
	}
    
    public List<String> getMenuPromptsAsList(OutputText[] oldOutputs) throws Exception { 
		return (Arrays.asList(getMenuPromptsAsString(oldOutputs)));
    }

				// Returns TRUE if the state is the same as before the last doAction was
    // executed.
    public boolean isRepeat() {
		return stack.peek() == previousState;
    }

    public boolean recursionOnStack() {
    	// check if TOS is anywhere else on stack
		if (stack.empty())
		    return false;
		DialogueState top = (DialogueState) stack.peek();
		for (int i = 0; i < stack.size() - 1; i++)
		    if (stack.elementAt(i) == top)
			return true;
		return false;
    }
    
    //DKB1108: added for kiosk, so that it won't replay the state at the top of the stack when 
    // a timeout happens
    public void clearStack() {
		stack.clear();
    }
    // DEBUG only
    public void dumpStack() {
		session.debug("STACK (bottom..top):");
		for (int i = 0; i < stack.size(); i++)
		    session.debug("   "+(stack.elementAt(i)).getStateName());
    }

    // ------- State utilities
    public void GO(String stateName, DialogueScript script) throws Exception {
		session.debug("   GO " + stateName);
		stack.pop();
		DialogueState state = script.getState(stateName);
		if (state == null)
		    throw new Exception("GO: Unknown  state " + stateName);
		stack.push(state);
    }

    public void PUSH(String scriptName, String stateName, DialogueScript script) throws Exception {
		session.debug("   PUSH " + scriptName + ", " + stateName);
		DialogueState state2 = script.getState(stateName);
		if (state2 == null)
		    throw new Exception("PUSH: Unknown  state " + stateName);
		
		DialogueState state1 = null;
		DialogueScript script2 = getScript(scriptName, script);
		if (script2 != null)
		    state1 = script2.getInitState();
		if (state1 == null)
		    throw new Exception("PUSH: Unknown  script " + scriptName);
		
		stack.pop();
		stack.push(state2);
		stack.push(state1);
    }

    public void POP() throws Exception {
		session.debug("   POP");
		stack.pop();
    }

    public boolean EXISTS_SCRIPT(String scriptName, DialogueScript script) {
    	return (getScript(scriptName, script) != null);
    }
    
    protected DialogueScript getScript(String scriptName, DialogueScript script) {
		for (int n = 0; n < script.qualifyCount(); n++) {
		    DialogueScript script2 = scripts.get(script.qualify(scriptName, n));
		    if (script2 != null)
			return script2;
		}
		return null;
    }
	
    public String GET(String property) throws Exception {
    	String value =  runtime.getProperty(property);
		if (value == null)
			return "";
		else
			return value;
    }
    
    public boolean GET_EQ(String property, String checkValue) throws Exception {
    	String value =  runtime.getProperty(property);
		if (value == null)
			return false;
		else
			return value.equalsIgnoreCase(checkValue);
    }
    
	public int GETINT(String property) throws Exception 
	{
		String value = GET(property);
		if (DialogueState.UNDEFINED(value))
			return DialogueState.UNKNOWN_INT;
		return Integer.parseInt(value);
	}

	public boolean UNDEFINED(String value) {
		return DialogueState.UNDEFINED(value);
	}
	
    public void SET(String property, String value) throws Exception {
    	runtime.setProperty(property, value);
    }
    
    public void SetMixedCase(String property, String value) throws Exception {
		SET(property.trim().toUpperCase(), value.trim());
	}
    
    public SessionRuntime getRuntime() { return runtime; }
    
    // service method so that saveSteps can be called from within scripts
    public void saveSteps(int steps, int studyDay) {
    	runtime.saveSteps(studyDay,steps);
    }
    
    public PersistentStore getStore() {return runtime.getStore(); }

   /** find the value of a property for a specific session. 
    * @param session can be either an absolute session num (a positive integer) 
    * 									or relative to the current session (a negative integer)
    */
    public String GET_SESSION(String param, int session)
    	throws Exception
    {
    	if  ( session == 0)   {
        throw new Exception("GET_SESSION: Illegal session index "+session + " for " + param.toString());
    }
    
    String list = GET(param.trim().toUpperCase());
    int searchSession = session >= 0 ? session : GETINT("THIS_SESSION_INT") + session;
    if(UNDEFINED(list))
        return "";
    for(StringTokenizer ST = new StringTokenizer(list, ",;"); ST.hasMoreTokens();)
    {
        String tokSession = ST.nextToken();
        if(!ST.hasMoreTokens())
            return "";
        String tokValue = ST.nextToken();
        if(searchSession == Integer.parseInt(tokSession))
            return tokValue;
    }

    return "";
}
    
    // DKB 0109: get only the final parameter value, stripping off the session number
    public String GET_VALUE_ONLY(String param)
    throws Exception
    {
    	String list = GET(param.trim().toUpperCase());
    	String tokValue = "";
    	if(UNDEFINED(list))
    		return "";
    	for(StringTokenizer ST = new StringTokenizer(list, ",;"); ST.hasMoreTokens();)
    	{
    		tokValue = ST.nextToken();
    	}

    	return tokValue;
    }
    
    public boolean GET_SESSION_EQ(String param, int session, String value)
    	throws Exception
	{
	    return GET_SESSION(param, session).equals(value.trim().toUpperCase());
	}

	public void SET_SESSION(String param, String value)
	    throws Exception
	{
	    param = param.trim().toUpperCase();
	    if(param.length() == 0)
	        return;
	    value = value.trim().toUpperCase();
	    if(value.length() == 0)
	        value = "NULL";
	    String list = GET(param);
	    if(UNDEFINED(list))
	    {
	        SET(param, GET("THIS_SESSION_INT")+(",") + value.toString());
	        return;
	    }
	    int lastFieldIndex = list.lastIndexOf(";");
	    if(lastFieldIndex < 0)
	        lastFieldIndex = 0;
	    else
	        lastFieldIndex++;
	    int commaIndex = list.indexOf(',', lastFieldIndex);
	    if(commaIndex < 0)
	        throw new Exception("SET_SESSION: Missing ',' in last " +param + " field of " +list.toString());
	    int tokSession = Integer.parseInt(list.substring(lastFieldIndex, commaIndex));
	    if(tokSession != GETINT("THIS_SESSION_INT"))
	    {
	        SET(param, String.valueOf(list) + ";" + GET("THIS_SESSION_INT") +"," + value.toString());
	        return;
	    } else {
	        SET(param, String.valueOf(list.substring(0, lastFieldIndex)) + GET("THIS_SESSION_INT") + "," + value.toString());
	        return;
	    }
	}

	public int EXISTS_SESSION(String param, String value)
	    throws Exception
	{
	    String list =GET(param.trim().toUpperCase());
	    value = value.trim().toUpperCase();
	    if(UNDEFINED(list))
	        return -1;
	    StringTokenizer ST = new StringTokenizer(list, ",;");
	    int session = -1;
	    if (ST.countTokens() < 2) {
	    	return -1;
	    } else {
	    while(ST.hasMoreTokens())  {
	        String tokSession = ST.nextToken();
	        String tokValue = ST.nextToken();
	        if(tokValue.equals(value))
	        {
	            int tokIntSession = Integer.parseInt(tokSession);
	            if(tokIntSession != GETINT("THIS_SESSION_INT"))
	                session = tokIntSession;
	        }
	    }
	    return session;
	}
	}
	
} // end of class DialogueStateMachine


