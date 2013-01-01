package edu.wpi.always.rm;

import edu.wpi.always.rm.plugin.ActivityPlugin;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.w3c.dom.Document;
import java.util.ArrayList;

public class Activity {

   public String name; // & etc?
   double social;
   double virtue;
   CurveType type;
   double curveVal; // slope, point at which value is attained, etc. TODO: put
                    // in curveType?
   public double buildup; // required buildup / "temporary closeness" to access
   double duration;
   public static double DURATIONUNIT = 60; // value of one "duration" in
                                                 // minutes
   ArrayList<OWLNamedIndividual> associatedTopics;
   // double priority;
   Document taskModelDoc;
   // TODO: & properties!
   ActivityPlugin plugin;
   public String prefix; // TODO: assign prefix
   public String namespace; // TODO: assign namespace
   // TODO: provide methods rather than public values
   public String description;

   // TaskModel - see task's Shell's "load" - but just use as xml without
   // loading task model???? hmmmm
   public Activity () {
   }

   public Activity (String actName, double socialVal, double virtueVal,
         double durationVal, double buildupVal, Document model,
         ActivityPlugin pluginVal, String prefixVal, String namespaceVal,
         String descriptionVal) {
      this(actName, socialVal, virtueVal, durationVal, buildupVal, model,
            pluginVal, prefixVal, namespaceVal, descriptionVal,
            new ArrayList<OWLNamedIndividual>());
   }

   public Activity (String actName, double socialVal, double virtueVal,
         double durationVal, double buildupVal, Document model,
         ActivityPlugin pluginVal, String prefixVal, String namespaceVal,
         String descriptionVal, ArrayList<OWLNamedIndividual> associatedVal) {
      name = actName;
      social = socialVal;
      virtue = virtueVal;
      duration = durationVal;
      buildup = buildupVal;
      taskModelDoc = model;
      plugin = pluginVal;
      prefix = prefixVal;
      namespace = namespaceVal;
      // TODO: change to prop
      description = descriptionVal;
      // prefix =""; //TODO: change this for namespace stuff...
      associatedTopics = associatedVal;
   }

   public Activity (String actName, double initPriority, Document actModel,
         ActivityPlugin myPlugin) {
      name = actName;
      // priority = initPriority; TODO: cleanup this
      taskModelDoc = actModel;
   }

   public void addAssociation (ArrayList<OWLNamedIndividual> associatedVal) {
      associatedTopics = associatedVal;
   }

   @Override
   public Activity clone () {
      Activity clonedActivity = new Activity(this.name, 1.0, this.taskModelDoc,
            this.plugin);
      // TODO: fix cloned attributes
      // TODO: fix access
      return clonedActivity;
   }
}
