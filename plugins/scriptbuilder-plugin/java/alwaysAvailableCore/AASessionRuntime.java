package alwaysAvailableCore;


import java.sql.ResultSet;
import java.io.*;
import java.sql.SQLException;

import DialogueRuntime.*;

/** This class implements server-side services for HB project, such as makePlot() 
 * and  saveSteps(), etc.
 */

public class AASessionRuntime extends  SessionRuntime{
	public AASessionRuntime(PersistentStore s, PropertiesInitializer i) {
			super(s,i);
	}
}