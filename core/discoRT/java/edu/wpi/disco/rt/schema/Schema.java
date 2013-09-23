package edu.wpi.disco.rt.schema;

import java.util.concurrent.ScheduledFuture;

/**
 *  A Schema's run() method will be called periodically by the executor. 
 *  Do not block the thread!
 */
public interface Schema extends Runnable {

   /**
    * Cause this schema to no longer be scheduled for execution.
    */
   void cancel ();
   
   /**
    * Return true iff this schema is no longer scheduled for execution.  
    */
   boolean isDone ();
    
   /**
    * Called by arbitrator each time this schema's proposal including focus is chosen.
    * 
    * @see Schema.Base#getFocusMillis()
    */
    void focus ();
    
    /**
    * Called by schema manager.
    */
   void setFuture (ScheduledFuture<?> future);
}