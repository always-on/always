package edu.wpi.always.rm.plugin;

import edu.wpi.always.rm.*;
import edu.wpi.always.rm.RelationshipManager.relationshipStage;
import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.*;

public class DynamicPluginAccess extends ActivityPlugin {

   ArrayList<Activity> activities;

   public DynamicPluginAccess () {
      File demoModelFile = new File("Models/StudyTasks.xml");
      DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory
            .newInstance();
      docBuildFactory.setNamespaceAware(true);
      DocumentBuilder docBuilder;
      activities = new ArrayList<Activity>();
      // query the plugins, get the specs.
      // assuming the same set of plugins
   }

   public void injectPlan (ArrayList<Activity> activities) {
      this.activities = activities;
   }

   @Override
   public void update (RelationshipManager RM) {
      double cutoff;
      if ( RM.currentStage == relationshipStage.STRANGERS ) {
         cutoff = 2;
      } else if ( RM.currentStage == relationshipStage.ACQUAINTANCES ) {
         cutoff = 6;
      } else {
         cutoff = 10;
      }
      for (Activity act : activities) {
         if ( ((act.buildup <= cutoff) && (act.name != "Introduction"))
            || ((RM.currentStage == relationshipStage.STRANGERS) && (act.name == "Introduction")) ) {
            RM.addActivity(act);
         }
      }
   }
}
