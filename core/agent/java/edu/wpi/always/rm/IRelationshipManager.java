package edu.wpi.always.rm;

import edu.wpi.disco.rt.DiscoSynchronizedWrapper;
import edu.wpi.disco.rt.util.DiscoDocument;

// TODO think about replan requests
public interface IRelationshipManager {

   /**
    * @return model for next session
    */
   DiscoDocument getSession ();

   void afterInteraction (DiscoSynchronizedWrapper disco, int closeness, int time);
}
