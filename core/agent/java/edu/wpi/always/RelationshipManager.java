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
   
   private Closeness closeness = Closeness.Stranger;
   
   public Closeness getCloseness () { return closeness; }
   
   /**
    * To call from Disco console:
    * 
    * <code>eval $always.getRM().setCloseness(edu.wpi.always.Closeness.Acquaintance)</code>
    */
   public void setCloseness (Closeness closeness) {
      this.closeness = closeness;
   }
   
   public DiscoDocument getSession () { 
      switch (closeness) {
         case Stranger:
            return new DiscoDocument(interaction.getDisco(), "Stranger.xml"); 
         case Acquaintance:
            return new DiscoDocument(interaction.getDisco(), "Acquaintance.xml"); 
         case Companion:
            return new DiscoDocument(interaction.getDisco(), "Acquaintance.xml");
         default: throw new IllegalStateException("Unknown closeness value: "+closeness);
      }
   }

   public void afterInteraction (Interaction interaction, int closeness, int time) { }
}
