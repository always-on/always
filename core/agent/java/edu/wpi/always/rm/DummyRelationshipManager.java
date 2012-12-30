package edu.wpi.always.rm;

import edu.wpi.disco.rt.DiscoSynchronizedWrapper;
import edu.wpi.disco.rt.util.DiscoDocument;

public class DummyRelationshipManager implements IRelationshipManager {

   private DiscoDocument session;
   
   @Override
   public DiscoDocument getSession () { return session; }

   @Override
   public void afterInteraction (DiscoSynchronizedWrapper disco,
         int closeness, int time) {}

}
