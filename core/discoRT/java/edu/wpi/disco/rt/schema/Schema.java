package edu.wpi.disco.rt.schema;

import java.util.concurrent.ScheduledFuture;

/**
 *  A Schema's run() method will be called periodically by the executor. 
 *  Do not block the thread!
 */
public interface Schema extends Runnable {

   /**
    * Default interval in millis at which run() method is called.
    */
   long DEFAULT_INTERVAL = 500;
   
   /**
    * Cause this schema to no longer be scheduled for execution.
    */
   void cancel ();
   
   /**
    * Return true iff this schema is no longer scheduled for execution.  
    */
   boolean isDone ();
    
   /**
    * Called by executor each time this schema is given focus resource.
    * 
    * @see Schema.Base#getFocusMillis()
    */
    void focus ();
    
    /**
    * Called by schema manager.
    */
   void setFuture (ScheduledFuture<?> future);
}