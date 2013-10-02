//Superclass for all dialogue states.

package DialogueRuntime;

import java.util.*;
import java.io.*;

public abstract class DialogueState {

	protected String name;
	protected DialogueScript script;
	protected Map flags;

	public static final int ACTION_ONLY = 0;
	public static final int OUTPUT_ONLY = 1;
	public static final int MENU_INPUT = 2;
	public static final int TEXT_INPUT = 3;

	public static final String[] TYPE_NAMES = { "ACTION_ONLY", "OUTPUT_ONLY",
			"MENU_INPUT", "TEXT_INPUT" };

	public DialogueState(String name, DialogueScript script) {
		this.name = name;
		this.script = script;
	}

	// interface...
	public int getStateType() throws Exception {
		throw new Exception("DialogueState " + name
				+ ": getStateType undefined.");
	}

	public String getStateName() {
		return name;
	}

	public DialogueScript getScript() {
		return script;
	}
	
	public String getFlag(String key) {
		return (flags == null) ? null : (String) flags.get(key);
	}
	
	public boolean hasFlag(String key) {
		return (flags == null) ? false :  flags.containsKey(key);
	}

	// Action 0 is default, unless a MENU type..then indexed by menu selection.
	public void doAction(DialogueStateMachine DSM, int n) throws Exception {
		throw new Exception("DialogueState " + name + ": doAction undefined.");
	}

	public OutputText getOutput(DialogueStateMachine DSM) throws Exception {
		throw new Exception("DialogueState " + name + ": getOutput undefined.");
	}

	public OutputText getRepeatOutput(DialogueStateMachine DSM) throws Exception {
		return getOutput(DSM);
	}

	public String getTextPrompt(DialogueStateMachine DSM) throws Exception {
		throw new Exception("DialogueState " + name
				+ ": getTextPrompt undefined.");
	}
	
	public OutputText[] getMenuPrompts(DialogueStateMachine DSM) throws Exception {
		throw new Exception("DialogueState " + name
				+ ": getMenuPrompts undefined.");
	}
	
	public static String menuXml(OutputText[] prompts, DialogueStateMachine DSM) {
		StringBuffer b;
		try{
			if (DSM.hasFlag("NOLINGERING")){
				b = new StringBuffer("<menu>");
			}else {
				b = new StringBuffer("<menu>");
			}
		}catch (Exception e){
			b = new StringBuffer("<menu>");
		}
		
		for (int n=0; n<prompts.length; n++) {
			b.append("<item>");
			b.append(prompts[n].getOutput());
			b.append("</item>");
		}
		
		b.append("</menu>");
		return b.toString();
	}

	// ---Navigation Primitives
	public void GO(DialogueStateMachine DS, String state) throws Exception {
		DS.GO(state, getScript());
	}

	public void PUSH(DialogueStateMachine DS, String script, String state)
			throws Exception {
		DS.PUSH(script, state, getScript());
	}

	public void POP(DialogueStateMachine DS) throws Exception {
		DS.POP();
	}
    
    public String READ(String url) throws Exception {
    	String temp = url.substring(url.indexOf("FILE:///")+8, url.lastIndexOf("\"")).replace('|',':');
    	System.out.println(temp);
    	BufferedReader reader = new BufferedReader(new FileReader(temp));
    	String in;
    	boolean record = false;
    	String result = "";
    	while((in = reader.readLine())!=null){
    		if (in.toLowerCase().contains("font")){
    			while(in.startsWith("<")&&in.contains(">")){
    				in = in.substring(in.indexOf(">")+1);
    			}
    			while(in.endsWith(">")&&in.contains("<")){
    				in = in.substring(0, in.indexOf("<"));
    			}
    			result += in+". ";
    		}
    		if (in.contains("<P>")){
    			record = true;
    			result += in.replace("<P>", " ")+" ";
    		}else if (in.contains("</P>")){
    			record = false;
    			result += in.replace("</P>", " ")+" ";
    		}
    		else if (record == true){
    			result += in+" ";
    		}
			if (result.contains("<P>")){
				result = result.replace("<P>", " ");
			}
			if (result.contains("</P>")){
				result = result.replace("</P>", " ");
			}
			if (result.contains("<p>")){
				result = result.replace("<p>", " ");
			}
			if (result.contains("</p>")){
				result = result.replace("</p>", " ");
			}
			if (result.contains("<UL>")){
				result = result.replace("<UL>", ". ");
			}
			if (result.contains("</UL>")){
				result = result.replace("</UL>", ". ");
			}
			if (result.contains("<ul>")){
				result = result.replace("<ul>", ". ");
			}
			if (result.contains("</ul>")){
				result = result.replace("</ul>", ". ");
			}
			if (result.contains("<LI>")){
				result = result.replace("<LI>", ". ");
			}
			if (result.contains("</LI>")){
				result = result.replace("</LI>", ". ");
			}
			if (result.contains("<li>")){
				result = result.replace("<li>", ". ");
			}
			if (result.contains("</li>")){
				result = result.replace("</li>", ". ");
			}
			if (result.contains("<OL>")){
				result = result.replace("<OL>", ". ");
			}
			if (result.contains("<ol>")){
				result = result.replace("<ol>", ". ");
			}
			if (result.contains("<B>")){
				result = result.replace("<B>", ". ");
			}
			if (result.contains("</B>")){
				result = result.replace("</B>", ". ");
			}
			if (result.contains("<b>")){
				result = result.replace("<b>", ". ");
			}
			if (result.contains("</b>")){
				result = result.replace("</b>", ". ");
			}
			if (result.contains("<u>")){
				result = result.replace("<u>", ". ");
			}
			if (result.contains("</u>")){
				result = result.replace("</u>", ". ");
			}
			if (result.contains("<U>")){
				result = result.replace("<U>", ". ");
			}
			if (result.contains("</U>")){
				result = result.replace("</U>", ". ");
			}
			if (result.contains("(")){
				result = result.replace("(", ", ");
			}
			if (result.contains(")")){
				result = result.replace(")", ". ");
			}
			if (result.contains("<body>")){
				result = result.replace("<body>", " ");
			}
			if (result.contains("</body>")){
				result = result.replace("</body>", " ");
			}
			if (result.contains("<html>")){
				result = result.replace("<html>", " ");
			}
			if (result.contains("</html>")){
				result = result.replace("</html>", " ");
			}
			if (result.contains("<BODY>")){
				result = result.replace("<BODY>", " ");
			}
			if (result.contains("</BODY>")){
				result = result.replace("</BODY>", " ");
			}
			if (result.contains("<HTML>")){
				result = result.replace("<HTML>", " ");
			}
			if (result.contains("</HTML>")){
				result = result.replace("</HTML>", " ");
			}
    	}
    	return result;
    }

	public boolean EXISTS_STATE(DialogueStateMachine DS, String state) {
		return (getScript().getState(state) != null);
	}
	
	public boolean EXISTS_SCRIPT(DialogueStateMachine DS, String script) {
		return DS.EXISTS_SCRIPT(script, getScript());
	}
	
	public void PUSHOPT(DialogueStateMachine DS, String script, String state)
			throws Exception {
		if (EXISTS_SCRIPT(DS, script))
			PUSH(DS, script, state);
		else
			GO(DS, state);
	}
	
	// ---Parameter Primitives
	// NOTE: all params and values UPCASED when stored
	public static final int UNKNOWN_INT = -1;
	public static final String UNKNOWN_STRING = "";

	public String GET(DialogueStateMachine DS, String property) throws Exception {
		return DS.GET(property.trim().toUpperCase());
	}
	
	public String GET_ORIGINAL(DialogueStateMachine DS, String property) throws Exception {
		return DS.GET(property.trim());
	}

	public void SET(DialogueStateMachine DS, String property, String value) throws Exception {
		DS.SET(property.trim().toUpperCase(), value.trim().toUpperCase());
	}
	
	public void SetMixedCase(DialogueStateMachine DS, String property, String value) throws Exception {
		DS.SET(property.trim().toUpperCase(), value.trim());
	}

	public String GetMixedCase(DialogueStateMachine DS, String property) throws Exception {
		return DS.GET(property.trim());
	}
	
	public int GETINT(DialogueStateMachine DS, String property)
			throws Exception {
		String value = DS.GET(property.trim().toUpperCase());
		if (UNDEFINED(value))
			return UNKNOWN_INT;
		return Integer.parseInt(value);
	}


    public static boolean UNDEFINED(String value) {
    	
	return (value == null || 
				    value.trim().length() == 0 ||
				    value == "") ;
    }
    
    public static boolean UNDEFINED(int value) {
	return value == UNKNOWN_INT;
    }
     
    public boolean GET_SESSION_EQ(DialogueStateMachine DS, String param, int session, String value)
	throws Exception {
    	return DS.GET_SESSION_EQ(param, session, value);
    }
    
    public void SET_SESSION(DialogueStateMachine DS, String param, String value)
	throws Exception {
	DS.SET_SESSION(param,value);
    }
    

    public int EXISTS_SESSION(DialogueStateMachine DS, String param, String value)
	throws Exception {
	return DS.EXISTS_SESSION(param,  value);
    }
    

    public boolean GET_EQ(DialogueStateMachine DS, String param, String value)
	throws Exception {
	return GET(DS, param).equalsIgnoreCase(value.trim());
    }
    
    // ---User input primitives
    public String GETTEXT(DialogueStateMachine DS) throws Exception {
	return DS.GET(DialogueStateMachine.MENU_TEXT_PARAM);
    }

	public String GET_VALUE_ONLY(String param, DialogueStateMachine DSM) throws Exception {
		return DSM.GET_VALUE_ONLY(param);
	}
	

    //twb - 8/2/08 - not sure why this is suddenly missing???
  public static int PICK(int numItems) { //Given N, this returns 0..N-1 with equal distribution. 
    double P=Math.random(); 
    for(int i=0;i<numItems;i++) 
      if(P<(double)(i+1)/(double)numItems) 
	return i; 
    return numItems-1; 
  } 
  
    public String PICKPROP(DialogueStateMachine DSM, String userID, String prefix) throws Exception {
    	String result = ((DBStore)(DSM.runtime.store)).pickProperties(userID, prefix);
    	if (result == null){
    		return null;
    	}
    	return result.substring(prefix.length()+1);
    }
    
    public String GETPROPPREFIX(DialogueStateMachine DSM, String userID, String prefix, String suffix) throws Exception {
    	String result = ((DBStore)(DSM.runtime.store)).pickProperties(userID, prefix, suffix);
    	if (result == null){
    		return null;
    	}
    	return result.substring(0, result.lastIndexOf("_"));
    }
    
    public String PICKPROPVALUE(DialogueStateMachine DSM, String userID, String prefix, String suffix) throws Exception {
    	String result = ((DBStore)(DSM.runtime.store)).pickPropertiesValue(userID, prefix, suffix);
    	if (result == null){
    		return null;
    	}
    	return result;
    }
    
    public String PICKPROPVALUESINGLE(DialogueStateMachine DSM, String userID, String prefix, String suffix) throws Exception {
    	String result = ((DBStore)(DSM.runtime.store)).pickPropertyValue(userID, prefix, suffix);
    	if (result == null){
    		return null;
    	}
    	return result;
    }
   
}
