package DialogueRuntime;

import java.util.HashMap;
import java.util.Map;

public abstract class DialogueScript {

	protected String name;
	protected String[] qualifiers;
	protected DialogueState initState;
	@SuppressWarnings("rawtypes")
	protected Map states = new HashMap();
	
	protected DialogueScript(String name, String[] qualifiers) {
		this.name = name;
		this.qualifiers = qualifiers;
	}

	protected void add(DialogueState state) {
		if (states.isEmpty())
			initState = state;
		states.put(state.getStateName(), state);
	}
	
	public String getName() {
		return name;
	}
	
	public String getFullName() {
		return qualifiers.length==0 ? name : qualifiers[0]+"."+name;
	}
	
	public DialogueState getState(String name) {
		return (DialogueState) states.get(name);
	}
	
	public DialogueState getInitState() {
		return initState;
	}
	
	public int qualifyCount() {
		return qualifiers.length + 1;
	}
	
	public String qualify(String scriptName, int depth) {
		if (depth >= qualifiers.length)
			return scriptName;
		return qualifiers[depth] + '.' + scriptName;
	}
}
