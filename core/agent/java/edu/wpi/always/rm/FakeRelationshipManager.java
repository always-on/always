package edu.wpi.always.rm;

import edu.wpi.always.cm.ICollaborationManager;
import edu.wpi.disco.Interaction;
import edu.wpi.disco.rt.util.DiscoDocument;

/**
 * Fake version of relationship manager 
 */
public class FakeRelationshipManager implements IRelationshipManager {

   private final Interaction interaction;
   
   public FakeRelationshipManager (ICollaborationManager cm) {
      interaction = cm.getContainer().getComponent(Interaction.class);
   }
   
   private Closeness closeness = Closeness.Stranger;
   
   /**
    * To call from Disco console, e.g.,
    * 
    * <code>eval ALWAYS.getRM().setCloseness(edu.wpi.always.rm.Closeness.Acquaintance)</code>
    */
   public void setCloseness (Closeness closeness) {
      this.closeness = closeness;
   }
   
   @Override
   public DiscoDocument getSession () { 
      return new DiscoDocument(interaction.getDisco(),
            closeness == Closeness.Stranger ? "Stranger.xml" :
               closeness == Closeness.Acquaintance ? "Acquaintance.xml" :
                  closeness == Closeness.Companion ? "Companion.xml" : 
                     null);
   }

   @Override
   public void afterInteraction (Interaction interaction, int closeness,
         int time) { }
}
