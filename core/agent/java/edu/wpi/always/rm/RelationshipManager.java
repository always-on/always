package edu.wpi.always.rm;

import edu.wpi.always.rm.plugin.*;
import edu.wpi.always.user.owl.OntologyRM;
import edu.wpi.cetask.Task;
import edu.wpi.disco.*;
import edu.wpi.disco.rt.DiscoSynchronizedWrapper;
import edu.wpi.disco.rt.action.DiscoAction;
import edu.wpi.disco.rt.util.DiscoDocument;
import org.semanticweb.owlapi.model.*;
import org.w3c.dom.*;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class RelationshipManager extends Thread implements IRelationshipManager {

    public static void main (String[] args) {
      new FakeCollaborationManager(new RelationshipManager()).start();
   }

   // Has handles for each plug-in,
   // stores relevant data (important locations, etc.)
   static double maxDuration = 3;
   // used by afterInteraction to cross-reference tasks with activities
   ArrayList<Activity> latestActivities;
   Context context;
   Properties userModel;
   OntologyRM ontology;

   public relationshipStage currentStage;
   double baseCloseness;
   double currentCloseness;
   Date closenessTime;
   double stockedSocial;
   ArrayList<ActivityPlugin> plugins;
   // Hashtable<String, Float> activities; // change to something else, using
   // "Activity" class...
   ConcurrentLinkedQueue<ActivityReport> reportQueue;
   final private ConcurrentLinkedQueue<Activity> activityQueue = new ConcurrentLinkedQueue<Activity>();
   Hashtable<String, Occurrence> activityOccurrences;
   Boolean doPlan;
   int decompCounter;
   int choiceCounter;

   public enum relationshipStage {
      STRANGERS, ACQUAINTANCES, FRIENDS
   }

   public RelationshipManager () {
      ontology = new OntologyRM("User"); // TODO: user name input
      currentStage = relationshipStage.STRANGERS;
      baseCloseness = 0;
      plugins = new ArrayList<ActivityPlugin>();
      plugins.add(new StudyPlugin());
      for (ActivityPlugin plugin : plugins) {
         plugin.initial(this);
      }
      context = new Context();
      context.closeness = 0;
      activityOccurrences = new Hashtable<String, Occurrence>();
   }

   /*
    * public void run(){ startup(); try{ while(true){
    * if(!reportQueue.isEmpty()){ processReports(); } update(); if(doPlan){
    * plan(); doPlan = false; } Thread.sleep(1000); // loop needed? or just
    * update and provide new tasks when asked? } } catch(Throwable e) {} }
    */
   /*
    * public void startup(){ doPlan = false; userModel = new Properties();
    * for(ActivityPlugin plugin : plugins){ System.out.println("Initializing " +
    * plugin); plugin.initial(this); } }
    */
   /*
    * private void addActivity(String task, double salience){ // Process task to
    * calculate initial salience values }
    */
   public void report (ActivityReport r) {
      reportQueue.add(r);
   }

   @SuppressWarnings("unused")
   private void update () {
      for (ActivityPlugin plugin : plugins) {
         plugin.update(this);
      }
      /*
       * //needed to update from within RM? Enumeration<String> tasks =
       * activities.keys(); while( tasks.hasMoreElements()){ Object task =
       * tasks.nextElement(); // update salience value }
       */
   }

   /*
    * private void processReports(){ for(ActivityReport r : reportQueue){
    * System.out.println(r.toString()); } }
    */
   /*
    * private TaskModel assembleModel(ArrayList<TaskModel> models){ return null;
    * }
    */
   // unused ?
   /*
    * private void requestTasks(){ //PlanningMessage message = new
    * PlanningMessage(); // go through potential activities, pick the best,
    * select requisites, // produce list of tasks and restrictions, then fire
    * off the message //collab.planTasks(message); }
    */
   // Plugins use this to provide activities to the RM
   public void addActivity (Activity activity) {
      activityQueue.add(activity);
   }

   @SuppressWarnings("unused")
   private ArrayList<Activity> cloneArrayList (ArrayList<Activity> list) {
      ArrayList<Activity> clonedList = new ArrayList<Activity>();
      for (Activity activity : list) {
         clonedList.add(activity.clone());
      }
      return clonedList;
   }

   @SuppressWarnings("unused")
   private void makePlans (PlanNode currentNode) {
      makePlans(currentNode, new ArrayList<PlanNode>());
   }

   // Main planner method.
   private void makePlans (PlanNode currentNode, ArrayList<PlanNode> leaves,
         double utilMargin, double limit) {
      if ( !(currentCloseness >= baseCloseness) ) {
         currentCloseness = baseCloseness;
      }
      // Assemble plan tree
      assemblePlan(currentNode, leaves, limit);
      // Prune plan tree
      double maxUtil = currentNode.getMaxUtility();
      double acceptableUtil = maxUtil * utilMargin;
      // double acceptableUtil = 0;
      for (PlanNode leaf : leaves) {
         if ( leaf.getUtility() < acceptableUtil ) {
            recursivePrune(leaf);
         }
      }
      relevanceSort(currentNode);
   }

   // Sorts activities in the plan tree according to relevance algorithm.
   private void relevanceSort (PlanNode currentNode) {
      ArrayList<PlanNode> children = currentNode.getChildren();
      ArrayList<PlanNode> relevant = new ArrayList<PlanNode>();
      ArrayList<PlanNode> irrelevant = new ArrayList<PlanNode>();
      if ( (children.size() >= 2) && (currentNode.getActivity() != null) ) {
         ArrayList<OWLNamedIndividual> relevantTopics = relevantTopics(currentNode
               .getActivity().associatedTopics);
         for (PlanNode child : children) {
            boolean added = false;
            relevanceLoop: for (OWLNamedIndividual childTopic : child
                  .getActivity().associatedTopics) {
               for (OWLNamedIndividual relevantTopic : relevantTopics) {
                  /*
                   * System.out.println("Comparison of " +
                   * childTopic.toString()); System.out.println(" and " +
                   * relevantTopic.toString() + ": ");
                   * System.out.println(childTopic.compareTo(relevantTopic));
                   */
                  if ( childTopic.equals(relevantTopic) ) {
                     relevant.add(child);
                     // System.out.println(currentNode.getActivity().name +
                     // " is relevant to " + child.getActivity().name);
                     added = true;
                     break relevanceLoop;
                  }
               }
            }
            if ( !added ) {
               irrelevant.add(child);
               // System.out.println(currentNode.getActivity().name +
               // " is NOT relevant to " + child.getActivity().name);
            }
         }
         ArrayList<PlanNode> toAdd = relevant;
         toAdd.addAll(irrelevant);
         currentNode.setChildren(toAdd);
      }
      for (PlanNode child : children) {
         relevanceSort(child);
      }
   }

   private void makePlans (PlanNode currentNode, ArrayList<PlanNode> leaves) {
      double limit = 0;
      if ( currentStage == relationshipStage.STRANGERS ) {
         limit = 2;
      } else if ( currentStage == relationshipStage.ACQUAINTANCES ) {
         limit = 6;
      } else if ( currentStage == relationshipStage.FRIENDS ) {
         limit = 100;
      } else {
         System.out.println("Relationship Stage not any appropriate value: "
            + currentStage.toString());
      }
      makePlans(currentNode, leaves, 0.9, limit);
   }

   // Checks to determine whether the given activity occurs prior to a given
   // node in a plan.
   public boolean inPast (Activity activity, PlanNode node) {
      PlanNode currentNode = node;
      while (currentNode.hasParent()) {
         if ( currentNode.getActivity().name == activity.name ) {
            return true;
         }
         currentNode = currentNode.getParent();
      }
      return false;
   }

   // Populates the exhaustive plan tree.
   private void assemblePlan (PlanNode currentNode, ArrayList<PlanNode> leaves,
         double limit) {
      boolean addedChild = false;
      for (Activity activity : activityQueue) {
         if ( (currentNode.getDuration() + activity.duration <= maxDuration)
            && isAccessable(activity, currentNode)
            // && !inPast(activity, currentNode)
            // && (limit == 2 || activity.name != "Introduction")
            && (activity.buildup <= limit) ) {
            assemblePlan(new PlanNode(activity, currentNode, this), leaves,
                  limit);
            addedChild = true;
         }
      }
      if ( !addedChild ) {
         leaves.add(currentNode);
      }
   }

   // Prunes suboptimal paths from plan tree.
   private void recursivePrune (PlanNode node) {
      if ( node.hasParent() ) {
         PlanNode parentNode = node.getParent();
         parentNode.removeChild(node);
         if ( !parentNode.hasChildren() ) {
            recursivePrune(parentNode);
         }
      }
   }

   private ArrayList<Activity> getLatestActivities (PlanNode node) {
      ArrayList<Activity> latest = new ArrayList<Activity>();
      if ( node.getActivity() != null ) {
         latest.add(node.getActivity());
      }
      for (PlanNode child : node.getChildren()) {
         latest.addAll(getLatestActivities(child));
      }
      return latest;
   }

   // Determines which topics are connected to any of the given array of topics
   // by any axiom in the ontology
   // init ontology, create fail cases
   public ArrayList<OWLNamedIndividual> relevantTopics (
         ArrayList<OWLNamedIndividual> topics) {
      OWLOntology ont = ontology.getOWLOntology();
      ArrayList<OWLNamedIndividual> relevant = new ArrayList<OWLNamedIndividual>();
      for (OWLNamedIndividual topic : topics) {
         relevant.add(topic);
         for (OWLAxiom axiom : topic.getReferencingAxioms(ont)) {
            for (OWLNamedIndividual relatedTopic : axiom
                  .getIndividualsInSignature()) {
               relevant.add(relatedTopic);
            }
         }
      }
      return relevant;
   }

   // A testing method for the relevance algorithms
   public void relevanceTest () {
      activityQueue.clear();
      for (ActivityPlugin plugin : plugins) {
         plugin.update(this);
      }
      /*
       * OWLOntology ont = ontology.getOWLOntology(); Set<OWLNamedIndividual>
       * sign = ont.getIndividualsInSignature();
       * System.out.println("  OWL Signature Individuals: ");
       * for(OWLNamedIndividual topic : sign){
       * System.out.println(topic.toString()); }
       * System.out.println("  Individual data:"); for(OWLNamedIndividual topic
       * : sign){ System.out.println("topic: " + topic.toString() + ": ");
       * for(OWLAxiom axiom : topic.getReferencingAxioms(ont)){
       * System.out.println("axiom: " + axiom.toString());
       * for(OWLNamedIndividual topic2 : axiom.getIndividualsInSignature()){
       * System.out.println(topic2.toString()); } } }
       */
      PlanNode rootNode = new PlanNode(this);
      makePlans(rootNode, new ArrayList<PlanNode>());
      // test "relevant nodes" algorithm instead.
      System.out.println("Sorted:");
      printPlanTree(rootNode);
      /*
       * System.out.println("Individuals in ontology:"); Set<OWLNamedIndividual>
       * individuals = ontology.getOWLOntology().getIndividualsInSignature();
       * for(OWLNamedIndividual individual : individuals){
       * System.out.println(individual.toString()); }
       * System.out.println("Active node:"); OWLIndividual primary =
       * (OWLIndividual)individuals.toArray()[0];
       * System.out.println(primary.toString()); ArrayList<OWLIndividual> active
       * = new ArrayList<OWLIndividual>(); active.add(primary);
       * System.out.println("All relevant nodes:"); for(OWLIndividual individual
       * : relevantTopics(active)){ System.out.println(individual.toString()); }
       */
   }

   // Primary method for tests performed during study, etc.
   public PlanningMessage plan () {
      activityQueue.clear();
      for (ActivityPlugin plugin : plugins) {
         plugin.update(this);
      }
      Date now = new Date();
      long elapsedMins = (now.getTime() - closenessTime.getTime()) / 60000;
      if ( elapsedMins >= 1 ) {
         currentCloseness = Math.max(
               ((currentCloseness - baseCloseness) / (Math
                     .pow(elapsedMins, 0.5))) + baseCloseness, baseCloseness);
      }
      PlanNode rootNode = new PlanNode(this);
      makePlans(rootNode, new ArrayList<PlanNode>());
      System.out.println("Current Plan:");
      printPlanTree(rootNode);
      latestActivities = getLatestActivities(rootNode);
      ArrayList<Document> preload = new ArrayList<Document>();
      getPreloads(rootNode, preload);
      /*
       * PossiblePlan planResult = makePlans(new ArrayList<Activity>(), 0.0,
       * 0.0, null, maxDuration, 0.0); ArrayList<Activity> activities =
       * planResult.plan;
       */
      /*
       * //pick goal double max = 0; Activity topActivity = null; for(Activity
       * activity : activityQueue){ if (activity.priority > max){ max =
       * activity.priority; topActivity = activity; } } if (topActivity ==
       * null){ //welp } if (isAccessable(topActivity)){ collab.planTasks(new
       * PlanningMessage(topActivity.taskModel)); } else{ // path req'd or retry
       * }
       */
      try {
         DocumentBuilderFactory docFactory = DocumentBuilderFactory
               .newInstance();
         docFactory.setNamespaceAware(true);
         DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
         Document plan = docBuilder.newDocument();
         Element taskModel = plan.createElementNS("http://ce.org/cea-2018",
               "taskModel");
         plan.appendChild(taskModel);
         Attr about = plan.createAttribute("about");
         about.setValue("urn:relationships.wpi.edu:examples:Today");
         taskModel.setAttributeNode(about);
         Attr xmlns = plan.createAttribute("xmlns");
         xmlns.setValue("http://ce.org/cea-2018");
         taskModel.setAttributeNode(xmlns);
         addNamespaces(plan, taskModel, rootNode);
         Element task = plan.createElementNS("http://ce.org/cea-2018", "task");
         taskModel.appendChild(task);
         Attr id1 = plan.createAttribute("id");
         id1.setValue("Today");
         task.setAttributeNode(id1);
         decompCounter = 0;
         choiceCounter = 0;
         writePlan(plan, task, taskModel, rootNode);
         for (Activity activity : activityQueue) {
            Element loadTask = plan.createElementNS("http://ce.org/cea-2018",
                  "task");
            taskModel.appendChild(loadTask);
            Attr loadId = plan.createAttribute("id");
            loadId.setValue(activity.name);
            loadTask.setAttributeNode(loadId);
         }
         // Disco.load(String from, Document document, Properties properties) {
         // Disco.load("", plan, new Properties());
         TransformerFactory transformerFactory = TransformerFactory
               .newInstance();
         Transformer transformer = transformerFactory.newTransformer();
         DOMSource source = new DOMSource(plan);
         transformer.setOutputProperty(OutputKeys.INDENT, "yes");
         // DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
         // Date date = new Date();
         // String nowTag = dateFormat.format(date);
         // StreamResult result = new StreamResult(new File("Models/Today_" +
         // nowTag + ".xml"));
         // String date = new
         // SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
         // StreamResult result = new StreamResult(new File("Models/Today_" +
         // date + ".xml"));
         // StreamResult result = new StreamResult(new
         // File("C:/Users/Will/today.xml"));
         StreamResult result;
         if ( currentStage == relationshipStage.ACQUAINTANCES ) {
            result = new StreamResult(new File("Models/today.xml"));
         } else {
            result = new StreamResult(new File("Models/today2.xml"));
         }
         // StreamResult result = new StreamResult(System.out);
         transformer.transform(source, result);
         return new PlanningMessage(plan, preload);
      } catch (Throwable e) {
         System.out.println(e.getMessage());
      }
      return null;
   }

   // For getting the task models for tasks in the daily plan.
   private void getPreloads (PlanNode node, ArrayList<Document> preloads) {
      Activity act = node.getActivity();
      if ( act != null ) {
         preloads.add(act.taskModelDoc);
      }
      if ( node.hasChildren() ) {
         for (PlanNode child : node.getChildren()) {
            getPreloads(child, preloads);
         }
      }
   }

   private void addNamespaces (Document plan, Element taskModel, PlanNode node) {
      Activity thisActivity = node.getActivity();
      // TODO: return this functionality
      /*
       * if (thisActivity != null){ Attr namespace =
       * plan.createAttribute("xmlns:"+thisActivity.prefix);
       * namespace.setValue(thisActivity.namespace);
       * taskModel.setAttributeNode(namespace); }
       */
      for (PlanNode child : node.getChildren()) {
         addNamespaces(plan, taskModel, child);
      }
   }

   private void writePlan (Document plan, Element supertask, Element taskModel,
         PlanNode node) {
      for (PlanNode child : node.getChildren()) {
         Element subtasks = plan.createElementNS("http://ce.org/cea-2018",
               "subtasks");
         supertask.appendChild(subtasks);
         Attr id = plan.createAttribute("id");
         id.setValue("decomp" + decompCounter);
         decompCounter++;
         subtasks.setAttributeNode(id);
         Activity firstActivity = child.getActivity();
         Element firstStep = plan.createElementNS("http://ce.org/cea-2018",
               "step");
         subtasks.appendChild(firstStep);
         Attr firstName = plan.createAttribute("name");
         firstName.setValue(firstActivity.name);
         firstStep.setAttributeNode(firstName);
         Attr firstTask = plan.createAttribute("task");
         // firstTask.setValue(firstActivity.prefix + firstActivity.name);
         firstTask.setValue(firstActivity.name);
         firstStep.setAttributeNode(firstTask);
         if ( child.hasChildren() ) {
            Element secondStep = plan.createElementNS("http://ce.org/cea-2018",
                  "step");
            subtasks.appendChild(secondStep);
            Attr secondName = plan.createAttribute("name");
            secondName.setValue("choice" + choiceCounter);
            secondStep.setAttributeNode(secondName);
            Attr secondTask = plan.createAttribute("task");
            secondTask.setValue("choice" + choiceCounter);
            secondStep.setAttributeNode(secondTask);
            Element task = plan.createElementNS("http://ce.org/cea-2018",
                  "task");
            taskModel.appendChild(task);
            Attr id1 = plan.createAttribute("id");
            id1.setValue("choice" + choiceCounter);
            task.setAttributeNode(id1);
            choiceCounter++;
            writePlan(plan, task, taskModel, child);
         }
      }
   }

   // For evaluating activities without supplying a hypothetical lead-in
   // Maybe obsoleted by changes to "closeness"
   @SuppressWarnings("unused")
   private boolean isAccessable (Activity activity) {
      return (currentCloseness >= activity.buildup);
   }

   // For evaluating activities in possible plans after a hypothetical series of
   // activities (the partialPlan)
   @SuppressWarnings("unused")
   private boolean isAccessable (Activity activity,
         ArrayList<Activity> partialPlan) {
      double ingratiation = 0;
      for (Activity contextActivity : partialPlan) {
         ingratiation += contextActivity.social;
      }
      return ((ingratiation + context.closeness) >= activity.buildup);
   }

   private boolean isAccessable (Activity activity, PlanNode node) {
      double closenessRate = 1; // TODO: select appropriate rate based on collab
                                // man
      return ((closenessRate * node.totalDuration()) + node.getCloseness() >= activity.buildup);
   }

   class Context {

      double closeness;
   }
   
   @Override
   public DiscoDocument getSession () {
      return new DiscoDocument(plan().taskModel, null, null);
   }

   // public void afterInteraction(DiscoSynchronizedWrapper discoWrapper){}
   // Method used in study
   public void scenarioPlans () {
      System.out.println("\n\nStrangers:\n");
      System.out.println("\nSocial Planner:\n");
      PlanNode rootNode = new PlanNode(this);
      makePlans(rootNode, new ArrayList<PlanNode>(), 1.0, 2);
      ArrayList<Activity> planList = linearize(rootNode);
      printPlan(planList);
      System.out.println("\nSocial Planner Alt:\n");
      planList = linearizeAlt(rootNode);
      printPlan(planList);
      System.out.println("\nAntisocial Planner:\n");
      rootNode = new PlanNode(this);
      makeAntisocialPlans(rootNode, new ArrayList<PlanNode>(), 1.0, 2);
      planList = linearize(rootNode);
      printPlan(planList);
      System.out.println("\nAntisocial Planner Alt:\n");
      planList = linearizeAlt(rootNode);
      printPlan(planList);
      System.out.println("\n\nAcquaintances:\n");
      System.out.println("\nSocial Planner:\n");
      rootNode = new PlanNode(this);
      makePlans(rootNode, new ArrayList<PlanNode>(), 1.0, 6);
      planList = linearize(rootNode);
      printPlan(planList);
      System.out.println("\nSocial Planner Alt:\n");
      planList = linearizeAlt(rootNode);
      printPlan(planList);
      System.out.println("\nAntisocial Planner:\n");
      rootNode = new PlanNode(this);
      makeAntisocialPlans(rootNode, new ArrayList<PlanNode>(), 1.0, 6);
      planList = linearize(rootNode);
      printPlan(planList);
      System.out.println("\nAntisocial Planner Alt:\n");
      planList = linearizeAlt(rootNode);
      printPlan(planList);
      System.out.println("\n\nCompanions:\n");
      System.out.println("\nSocial Planner:\n");
      rootNode = new PlanNode(this);
      makePlans(rootNode, new ArrayList<PlanNode>(), 1.0, 100);
      planList = linearize(rootNode);
      printPlan(planList);
      System.out.println("\nSocial Planner Alt:\n");
      planList = linearizeAlt(rootNode);
      printPlan(planList);
      System.out.println("\nAntisocial Planner:\n");
      rootNode = new PlanNode(this);
      makeAntisocialPlans(rootNode, new ArrayList<PlanNode>(), 1.0, 100);
      planList = linearize(rootNode);
      printPlan(planList);
      System.out.println("\nAntisocial Planner Alt:\n");
      planList = linearizeAlt(rootNode);
      printPlan(planList);
   }

   // Makes an array from the 'topmost' path in a plan tree.
   private ArrayList<Activity> linearize (PlanNode rootNode) {
      ArrayList<Activity> planList = new ArrayList<Activity>();
      PlanNode currentNode = rootNode;
      while (currentNode.hasChildren()) {
         currentNode = currentNode.getChildren().get(0);
         planList.add(currentNode.getActivity());
      }
      return planList;
   }

   // Returns an array of the activities in a path through a plan tree,
   // specified by a leaf.
   private ArrayList<Activity> planFromLeaf (PlanNode leaf) {
      ArrayList<Activity> result = null;
      if ( leaf.hasParent() ) {
         result = planFromLeaf(leaf.getParent());
      } else {
         result = new ArrayList<Activity>();
      }
      if ( leaf.getActivity() != null ) {
         result.add(leaf.getActivity());
      }
      return result;
   }

   private ArrayList<Activity> linearizeAlt (PlanNode rootNode) {
      ArrayList<PlanNode> leaves = new ArrayList<PlanNode>();
      ArrayList<Activity> planOne = linearize(rootNode);
      findLeaves(rootNode, leaves);
      double maxDiff = 0;
      ArrayList<Activity> candidate = null;
      for (PlanNode leaf : leaves) {
         ArrayList<Activity> thisPlan = planFromLeaf(leaf);
         double diff = 100;
         for (Activity altActivity : thisPlan) {
            for (Activity activity : planOne) {
               if ( altActivity.name == activity.name ) {
                  diff -= 1;
               }
            }
         }
         if ( diff >= maxDiff ) {
            maxDiff = diff;
            candidate = thisPlan;
         }
      }
      return candidate;
      /*
       * PlanNode currentNode = rootNode; while(currentNode.hasChildren()){
       * currentNode = currentNode.getChildren().get(0);
       * planList.add(currentNode.getActivity()); }
       */
   }

   private void findLeaves (PlanNode node, ArrayList<PlanNode> leaves) {
      if ( node.hasChildren() ) {
         for (PlanNode child : node.getChildren()) {
            findLeaves(child, leaves);
         }
      } else {
         leaves.add(node);
      }
   }

   // A utility method from teh study, for printing descriptions of plans.
   private void printPlan (ArrayList<Activity> planList) {
      for (int i = 0; i < planList.size(); i++) {
         if ( i == 0 ) {
            System.out.print("First");
         } else if ( i == (planList.size() - 1) ) {
            System.out.print("Finally");
         } else if ( i % 3 == 0 ) {
            System.out.print("Then");
         } else if ( i % 3 == 1 ) {
            System.out.print("Next");
         } else if ( i % 3 == 2 ) {
            System.out.print("After that");
         }
         System.out.print(", ");
         System.out.print(planList.get(i).description);
         System.out.print(" for ");
         System.out.print((int) planList.get(i).duration * 20);
         System.out.println(" minutes.");
      }
   }

   @SuppressWarnings("unused")
   private void printStack (Stack<Segment> stack) {
      System.out.println("Printing Stack");
      for (Segment segment : stack) {
         printSegment(segment);
      }
   }

   private void printSegment (Segment segment) {
      System.out.println("Printing Segment");
      for (Iterator<Object> children = segment.children(); children.hasNext();) {
         Object child = children.next();
         if ( child instanceof Task ) {
            System.out.println("Task: " + ((Task) child).toString());
         } else if ( child instanceof Segment ) {
            printSegment((Segment) child);
         } else {
            System.out.println("Unidentified child type");
         }
      }
   }

   private ArrayList<Task> linearizeStack (Stack<Segment> stack) {
      ArrayList<Task> tasks = new ArrayList<Task>();
      for (Segment segment : stack) {
         getSegmentTasks(segment, tasks);
      }
      return tasks;
   }

   private void getSegmentTasks (Segment segment, ArrayList<Task> tasks) {
      for (Iterator<Object> children = segment.children(); children.hasNext();) {
         Object child = children.next();
         if ( child instanceof Task ) {
            tasks.add((Task) child);
         } else if ( child instanceof Segment ) {
            getSegmentTasks((Segment) child, tasks);
         } else {
            System.out.println("Unidentified child type");
         }
      }
   }

   // This method is called after an interaction (surprising), analyzes the
   // disco state to determine information about the interaction.
   @Override
   public void afterInteraction (DiscoSynchronizedWrapper discoWrap,
         int closeness, int time) {
      // currentCloseness = closeness;
      closenessTime = new Date();
      discoWrap.execute(new DiscoAction() {

         @Override
         public void execute (Disco disco) {
            ArrayList<Task> history = linearizeStack(disco.getStack());
            // Stack<Segment> stack = disco.getStack();
            // disco.history(System.out);
            // ArrayList<TaskClass> history = new ArrayList<TaskClass>();
            /*
             * for(int i = 0; i < stack.size(); i++){ Segment seg =
             * stack.get(i); Iterator<Object> children = seg.children();
             * while(children.hasNext()){ Object child = children.next();
             * if((child instanceof Task) && true){ history.add(((Task)
             * child).getType()); } } }
             */
            /*
             * for(Task task : history){ System.out.println(task.toString()); }
             */
            /*
             * for(TaskClass task : history){ String name = task.toString();
             * for(Activity act : latestActivities){ if (act.name == name){
             * currentCloseness += act.social; break; } } }
             */
            /*
             * Date now = new Date(); //System.out.println("History:"); for(Task
             * task : history){ String name = task.toString();
             * //System.out.println(name); for(Activity act : activityQueue){ if
             * (act.name == name){ activityOccurrences.put(name, new
             * Occurrence(now, act)); stockedSocial += act.social; //modulate
             * via usual methods? break; } } }
             */
            for (Task task : history) {
               String name = task.toString();
               Activity activity = null;
               for (Activity potentialActivity : activityQueue) {
                  if ( name == potentialActivity.name ) {
                     activity = potentialActivity;
                     break;
                  }
               }
               if ( activity != null ) {
                  currentCloseness += activity.social; // TODO: modify based on
                                                       // damping! timing of
                                                       // activities important
                                                       // here.
               }
            }
            // informative printing:
            /*
             * for(int i = 0; i < stack.size(); i++){ Iterator<Object> children
             * = stack.get(i).children(); System.out.println("Stack element " +
             * i + " - " + stack.get(i).toString()+ ":");
             * System.out.println("  Children:"); while(children.hasNext()){
             * Object child = children.next(); if(child instanceof Task){
             * System.out.println("    " + disco.toHistoryString(((Task)
             * child))); } else if(child instanceof Segment){
             * System.out.println("    segment - " + ((Segment)
             * child).toString()); }else{ System.out.println("    ???"); } }
             * System.out.println("  Parent:"); Segment parent =
             * stack.get(i).getParent(); if(parent != null){
             * System.out.println("  " + stack.get(i).getParent().toString()); }
             * else { System.out.println("  null"); } }
             */
            if ( stockedSocial >= 50 && stockedSocial < 100 ) {
               currentStage = relationshipStage.ACQUAINTANCES;
            } else if ( stockedSocial >= 100 ) {
               System.out.println("Friends!");
               currentStage = relationshipStage.FRIENDS;
               baseCloseness += 2;
            }
            System.out.println("Replanning:");
            System.out.println("Current Closeness:" + currentCloseness);
            plan();
         }
      });
      plan();
   }

   // Method from the study. These plans use a pathological utility model.
   private void makeAntisocialPlans (PlanNode currentNode,
         ArrayList<PlanNode> leaves, double utilMargin, double limit) {
      // Assemble plan tree
      assembleAntisocialPlan(currentNode, leaves, limit);
      // Prune plan tree
      /*
       * ArrayList<PlanNode> checkNext = new ArrayList<PlanNode>(); for(PlanNode
       * leaf : leaves){ checkNext.add(leaf); } while(!checkNext.isEmpty()){
       * ArrayList<PlanNode> toCheck = new ArrayList<PlanNode>(); for(PlanNode
       * leaf : checkNext){ toCheck.add(leaf); } checkNext.clear(); for(PlanNode
       * leaf : toCheck){ Activity activity = leaf.getActivity();
       * if(activity.virtue - activity.social + Math.max(0, activity.buildup -
       * leaf.getExistingBuildup()) <= 0){ leaves.remove(leaf);
       * if(leaf.hasParent()){ PlanNode parent = leaf.getParent();
       * parent.removeChild(leaf); if(!parent.hasChildren()){
       * checkNext.add(parent); leaves.add(parent); } } } } }
       */
      double maxUtil = currentNode.getMaxAntisocialUtility();
      double acceptableUtil = maxUtil * utilMargin;
      // double acceptableUtil = 0;
      System.out.println("MAX = " + maxUtil);
      // System.out.println("max = " + maxUtil + ", accept = " +
      // acceptableUtil);
      for (PlanNode leaf : leaves) {
         // System.out.println("util = " + leaf.getAntisocialUtility());
         if ( leaf.getAntisocialUtility() < acceptableUtil ) {
            // System.out.println("unacceptable, pruning");
            recursivePrune(leaf);
         }
      }
   }

   @SuppressWarnings("unused")
   private void makeAntisocialPlans (PlanNode currentNode,
         ArrayList<PlanNode> leaves) {
      makeAntisocialPlans(currentNode, leaves, 0.8, 100);
   }

   private void assembleAntisocialPlan (PlanNode currentNode,
         ArrayList<PlanNode> leaves, double limit) {
      boolean addedChild = false;
      for (Activity activity : activityQueue) {
         if ( (currentNode.getDuration() + activity.duration <= maxDuration)
            && !inPast(activity, currentNode) && (activity.buildup <= limit)
            && (limit == 2 || activity.name != "Introduction") ) {
            assembleAntisocialPlan(new PlanNode(activity, currentNode, this),
                  leaves, limit);
            addedChild = true;
         }
      }
      if ( !addedChild ) {
         leaves.add(currentNode);
      }
   }

   // Utility method for printing entire plan trees w/ nesting/indents
   private void printTree (PlanNode node, int layer) {
      if ( node.hasChildren() ) {
         for (PlanNode child : node.getChildren()) {
            for (int i = 0; i < layer; i++) {
               System.out.print("  ");
            }
            // System.out.println(child.getActivity().name);
            System.out.println(child.getActivity().name + " max = "
               + child.getMaxAntisocialUtility() + " util = "
               + child.getAntisocialUtility());
            printTree(child, layer + 1);
         }
      }
   }

   private void printTree (PlanNode node) {
      printTree(node, 0);
   }

   public void test () {
      System.out.println("Begin Test");
      // GET ANTISOCIAL PLAN TREE
      PlanNode rootNode = new PlanNode(this);
      makeAntisocialPlans(rootNode, new ArrayList<PlanNode>(), 0.9, 100);
      System.out.println("Plan Complete:" + (rootNode.hasChildren()));
      printTree(rootNode);
      // GET NORMAL PLANS
      PlanNode strangersNode = new PlanNode(this);
      PlanNode acquaintancesNode = new PlanNode(this);
      PlanNode companionsNode = new PlanNode(this);
      makePlans(strangersNode, new ArrayList<PlanNode>(), 1, 2);
      makePlans(acquaintancesNode, new ArrayList<PlanNode>(), 1, 6);
      makePlans(companionsNode, new ArrayList<PlanNode>(), 1, 100);
      ArrayList<Activity> strangers = linearize(strangersNode);
      ArrayList<Activity> strangersAlt = linearizeAlt(strangersNode);
      ArrayList<Activity> acquaintances = linearize(acquaintancesNode);
      ArrayList<Activity> acquaintancesAlt = linearizeAlt(acquaintancesNode);
      ArrayList<Activity> companions = linearize(companionsNode);
      ArrayList<Activity> companionsAlt = linearizeAlt(companionsNode);
      // GET LEAVES
      ArrayList<PlanNode> leaves = getLeaves(rootNode);
      printPlan(strangers);
      printPlan(strangersAlt);
      int count = 0;
      // GET RELEVANT PLANS
      for (PlanNode leaf : leaves) {
         ArrayList<Activity> planList = getPlanList(leaf);
         int strRel = getRelevance(planList, strangers);
         int strAltRel = getRelevance(planList, strangersAlt);
         int acqRel = getRelevance(planList, acquaintances);
         int acqAltRel = getRelevance(planList, acquaintancesAlt);
         int comRel = getRelevance(planList, companions);
         int comAltRel = getRelevance(planList, companionsAlt);
         System.out.println("Plan " + count + ": " + strRel + " " + strAltRel
            + " " + acqRel + " " + acqAltRel + " " + comRel + " " + comAltRel);
         printPlan(planList);
         System.out.println("");
         count++;
      }
      // ArrayList<Activity>planList = linearize(rootNode);
      // printPlan(planList);
   }

   private ArrayList<PlanNode> getLeaves (PlanNode node) {
      ArrayList<PlanNode> leaves = new ArrayList<PlanNode>();
      if ( node.hasChildren() ) {
         for (PlanNode child : node.getChildren()) {
            leaves.addAll(getLeaves(child));
         }
      } else {
         leaves.add(node);
      }
      return leaves;
   }

   private ArrayList<Activity> getPlanList (PlanNode node) {
      ArrayList<Activity> planList = null;
      if ( node.hasParent() ) {
         planList = getPlanList(node.getParent());
      } else {
         planList = new ArrayList<Activity>();
      }
      if ( node.getActivity() != null ) {
         planList.add(node.getActivity());
      }
      return planList;
   }

   // A notion of relevance used only in the study, for comparing PLANS.
   private int getRelevance (ArrayList<Activity> listOne,
         ArrayList<Activity> listTwo) {
      int relevance = 0;
      for (Activity activityOne : listOne) {
         loop: for (Activity activityTwo : listTwo) {
            if ( activityOne.name == activityTwo.name ) {
               relevance++;
               break loop;
            }
         }
      }
      return relevance;
   }

   private void printPlanTree (PlanNode node) {
      printPlanTree(node, 0);
   }

   private void printPlanTree (PlanNode node, int indent) {
      if ( node.getActivity() != null ) {
         printIndents(indent);
         System.out.println(node.getActivity().name);
      }
      for (PlanNode child : node.getChildren()) {
         printPlanTree(child, indent + 1);
      }
   }

   private void printIndents (int indent) {
      for (int i = 0; i < indent; i++) {
         System.out.print("  ");
      }
   }

   public OntologyRM getOntology () {
      return ontology;
   }
}
