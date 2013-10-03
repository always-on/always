package DialogueRuntime;

import java.util.Enumeration;
import java.util.Properties;

/* Project-specific code to initialize pre-session properties.  Any data that needs to be accessible in the scripts, and can 
 * be computed at initialization time, should be loaded into a property */
public abstract class PropertiesInitializer {
    public abstract  void initialize(PersistentStore s, Properties p) throws Exception;    
}

