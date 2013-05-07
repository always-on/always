package edu.wpi.always.rm;

import edu.wpi.disco.rt.DiscoSynchronizedWrapper;
import edu.wpi.disco.rt.action.LoadModelDocument;
import edu.wpi.disco.rt.util.DiscoDocument;

/**
 * Dummy version of relationship manager for testing 
 */
public class DummyRelationshipManager implements IRelationshipManager {

   // TODO Prepare three handcoded session plans: Stranger.xml, Acquaintance.xml, 
   //      and Companion.xml

   @Override
   public DiscoDocument getSession () { return null; }

   @Override
   public void afterInteraction (DiscoSynchronizedWrapper disco,
         int closeness, int time) {}

}
