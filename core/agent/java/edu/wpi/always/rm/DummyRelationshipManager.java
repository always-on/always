package edu.wpi.always.rm;

import edu.wpi.disco.Interaction;
import edu.wpi.disco.rt.util.DiscoDocument;

/**
 * Dummy version of relationship manager for testing 
 */
public class DummyRelationshipManager implements IRelationshipManager {

   @Override
   public DiscoDocument getSession () { return null; }

   @Override
   public void afterInteraction (Interaction interaction, int closeness,
         int time) { }
}
