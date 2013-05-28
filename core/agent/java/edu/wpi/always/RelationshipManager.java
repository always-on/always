package edu.wpi.always;

import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.disco.Interaction;
import edu.wpi.disco.rt.util.DiscoDocument;

/**
 * This is a reduced implementation of the relationship management algorithm described
 * in docs/CoonSidnerRich2012 in which three session plans are precomputed. 
 */
public class RelationshipManager {

   private final Interaction interaction;
   
   public RelationshipManager (CollaborationManager cm) {
      interaction = cm.getContainer().getComponent(Interaction.class);
   }
   
   public enum Closeness { Stranger, Acquaintance, Companion }

   private Closeness closeness = Closeness.Stranger;
   
   /**
    * To call from Disco console, e.g.,
    * 
    * <code>eval ALWAYS.getRM().setCloseness(edu.wpi.always.rm.Closeness.Acquaintance)</code>
    */
   public void setCloseness (Closeness closeness) {
      this.closeness = closeness;
   }
   
   public DiscoDocument getSession () { 
      return new DiscoDocument(interaction.getDisco(),
            closeness == Closeness.Stranger ? "Stranger.xml" :
               closeness == Closeness.Acquaintance ? "Acquaintance.xml" :
                  closeness == Closeness.Companion ? "Companion.xml" : 
                     null);
   }

   public void afterInteraction (Interaction interaction, int closeness, int time) { }
}
