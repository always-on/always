package edu.wpi.always;

import java.util.List;
import edu.wpi.disco.rt.*;

/**
 * A plugin is an authoring unit that implements one or more activities. 
 * 
 * @see Activity
 */
public interface Plugin {
   
    /**
    * Returns the activities that this plugin currently makes available.  This method
    * is called by {@link edu.wpi.always.rm.IRelationshipManager}.
    * 
    * @param baseline The closeness at the start of this session (see {@link edu.wpi.always.rm.Closeness})
    */
   List<Activity> getActivities (int baseline);
    
   /**
    * Returns registries containing the schemas and other components that
    * implement the given activity. This method is called by
    * {@link edu.wpi.always.cm.ICollaborationManager}.
    */
   List<Registry> getRegistries (Activity activity);
  
}

