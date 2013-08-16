package edu.wpi.always;

import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;
import edu.wpi.disco.Interaction;
import edu.wpi.disco.rt.util.DiscoDocument;

/**
 * This is a reduced implementation of the relationship management algorithm described
 * in docs/CoonSidnerRich2012 in which three session plans are precomputed. 
 */
public class RelationshipManager {

   private final Interaction interaction;
   private final UserModel model;
   
   public RelationshipManager (CollaborationManager cm) {
      interaction = cm.getContainer().getComponent(Interaction.class);
      model = cm.getContainer().getComponent(UserModel.class);
   }
 
   public DiscoDocument getSession () { 
      switch (model.getCloseness()) {
         case Stranger:
            return new DiscoDocument(interaction.getDisco(), "Stranger.xml"); 
         case Acquaintance:
            return new DiscoDocument(interaction.getDisco(), "Acquaintance.xml"); 
         case Companion:
            return new DiscoDocument(interaction.getDisco(), "Acquaintance.xml");
         default: throw new IllegalStateException("Unknown closeness value");
      }
   }

   public void afterInteraction (Interaction interaction, int closeness, int time) { }
}
