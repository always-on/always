package edu.wpi.always.rm;

import edu.wpi.disco.rt.DiscoSynchronizedWrapper;
import edu.wpi.disco.rt.action.LoadModelDocument;
import edu.wpi.disco.rt.util.DiscoDocument;

/**
 * Dummy version of relationship manager for testing that always
 * returns Init.xml.
 */
public class DummyRelationshipManager implements IRelationshipManager {

   private DiscoDocument session;
   
   public DummyRelationshipManager (DiscoSynchronizedWrapper disco) {
      session = new DiscoDocument(disco.getDisco(), "Init.xml");
      // load init model now to register plugins (this copy of
      // of Disco will be gc'ed when first session starts)
      disco.execute(new LoadModelDocument(session));
   }
   
   @Override
   public DiscoDocument getSession () { return session; }

   @Override
   public void afterInteraction (DiscoSynchronizedWrapper disco,
         int closeness, int time) {}

}
