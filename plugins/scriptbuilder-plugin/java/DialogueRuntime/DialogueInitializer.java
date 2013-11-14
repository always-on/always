package DialogueRuntime;

import java.util.Map;

public abstract class DialogueInitializer {
    public abstract void initialize(@SuppressWarnings("rawtypes") Map scripts);
    
    protected DialogueScript create(String className) {
    	try {
    		@SuppressWarnings("rawtypes")
			Class scriptClass = Class.forName(className);
    		return (DialogueScript) scriptClass.newInstance();
    	} catch (Throwable t) {
    		//System.out.println("Error loading script: " + className + t.toString());
    		return new ErrorScript(className);
    	}
    }
}
    
 