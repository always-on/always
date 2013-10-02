package DialogueRuntime;

import java.util.Enumeration;
import java.util.Properties;

/* Project-specific code to initialize pre-session properties.  Any data that needs to be accessible in the scripts, and can 
 * be computed at initialization time, should be loaded into a property */
public  class PropertiesInitializer {
    public  void initialize(PersistentStore s, Properties p) throws Exception { 
    for(Enumeration props=p.keys();props.hasMoreElements();) {
	    String prop=(String)props.nextElement();
	    if(prop.startsWith("CURRENT_"))
		    p.remove(prop);
		};	
		p.setProperty("STUDY_DAY",""+s.getUsersStudyDay());
    	p.setProperty("NAME",s.getUserName());
    	
    	
	}
    
}

