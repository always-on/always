package alwaysAvailableCore;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import DialogueRuntime.*;

/* This class is called from SessionRuntime to do any project-specific 
 * initialization of the properties object. Use the persistent stores to access 
 * persistent fields. Attributes from the 'properties' external file or DB have 
 * already been loaded into the Properties object by SessionRuntime
  */

public class AAPropertiesInitializer extends PropertiesInitializer{
	@Override
	public  void initialize(PersistentStore s, Properties p) throws Exception { 
	    for(Enumeration<Object> props=p.keys();props.hasMoreElements();) {
		    String prop=(String)props.nextElement();
		    if(prop.startsWith("CURRENT_"))
			    p.remove(prop);
			};	
			//p.setProperty("STUDY_DAY",""+s.getUsersStudyDay());
	    	//p.setProperty("NAME",s.getUserName());
	    	
	    	
		}
	
}