package edu.wpi.always.rm.plugin;

import edu.wpi.always.rm.*;

// TODO: use this to update conversationplugin and activityplugin with methods (also see weather)
public class BaseballPlugin extends ConversationPlugin {

   public BaseballPlugin () {
   }

   @Override
   public void initial (RelationshipManager RM) {
      RM.addActivity(new Activity("TalkBaseball", 0.8, null, this));
   }

   @Override
   public void update (RelationshipManager RM) {
   }

   @Override
   public String toString () {
      return "Baseball ClientPlugin";
   }

   public void report () {
   }
}
