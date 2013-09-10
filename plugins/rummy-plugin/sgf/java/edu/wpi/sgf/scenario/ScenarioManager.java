package edu.wpi.sgf.scenario;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import com.google.gson.Gson;
import edu.wpi.cetask.Utils;

/**
 * ScenarioManager class manages importing of social 
 * attributes and different approached for doing that
 * at instantiation or in runtime, initialization of 
 * scenario classes, picking the current scenario and 
 * updating it if necessary. It determines scenario 
 * failure and updates the scenario if necessary. 
 * @author Morteza Behrooz
 * @version 2.3
 */
public class ScenarioManager {

   protected Scenario currentScneario;

   //multiple scenario
   protected List<Scenario> activeScenarios;

   private static boolean importedByDirectCall;
   private static final String AttributesFileName = 
         "socialAttributes.json";
   private static final String ScenarioNamesFile = 
         "Scenario.xml";
   private File AttributesFile;

   //if scenario fails, ++, changes after
   //getting to scenarioFailingBear
   int scenarioFailCycles = 0;

   private static List<String> importedSocialAttributes;


   /**
    * This constructor gets input attributes,
    * so the ScenarioManager will take them for the 
    * first round at least. At later rounds 
    * (after scenario failure/completion) it is possible 
    * to update social attributes by direct call of 
    * {@link #importTheseSocialAttributes(List)} or updating 
    * the json file.
    */
   public ScenarioManager(
         List<String> importedSocialAttributes){

      importedSocialAttributes = 
            new ArrayList<String>();
      activeScenarios = 
            new ArrayList<Scenario>();
      ScenarioManager.importedSocialAttributes.clear();
      ScenarioManager.importedSocialAttributes
      .addAll(importedSocialAttributes);

      loadScenarios();
      chooseOrUpdateScenario();

   }

   /**
    * This constructor does not get input attributes,
    * so the ScenarioManager will have to look for them
    * in the json file (look into documents). At later rounds 
    * (after scenario failure/completion) it is possible 
    * to update social attributes by direct call of 
    * {@link #importTheseSocialAttributes(List)} or updating 
    * the json file if this class instantiated again.
    */
   public ScenarioManager(){

      importedSocialAttributes = 
            new ArrayList<String>();
      ScenarioManager.
      importedSocialAttributes.clear();
      activeScenarios = 
            new ArrayList<Scenario>();

      loadScenarios();
      chooseOrUpdateScenario();

   }

   public void importTheseSocialAttributes(
         List<String> importedAttributeList){

      ScenarioManager.importedSocialAttributes.
      addAll(importedAttributeList);
      ScenarioManager.importedByDirectCall = true;

   }

   public void importSocialAttributesFromFile(){

      ScenarioManager.importedSocialAttributes.clear();
      Gson gson = new Gson();

      try{
         AttributesFile = new File(
               Utils.toURL("edu/wpi/sgf/resources/"+AttributesFileName).toURI());
         BufferedReader bufferedReader = new BufferedReader(
               new FileReader(AttributesFile));
         GsonBridge gsonBridge = gson.fromJson(
               bufferedReader, GsonBridge.class);
         ScenarioManager.importedSocialAttributes
         .addAll((gsonBridge.list));
      } catch (MalformedURLException|
            URISyntaxException|FileNotFoundException e) {
         System.out.println(
               "Resource loading error in loading Scenario attributes."
                     + "The .json file(s) should be in sgf/resources "
                     + "which should be in the sgf classpath (exported)");
         e.printStackTrace();
      }

   }

   /**
    * Matching algorithm tries to be as specific and as exact 
    * as possible in choosing one scenario each time it is called
    * among the scenarios inside {@value #allScenarios} based on 
    * the 'current' contents of {@link #importedSocialAttributes}. 
    */
   private Class<? extends Scenario> matchingAlgorithm() {

      //be specific>>
      //Java TreeMap keeps the map sorted based on keys.
      //its iterator traverses the set in an ascending order
      TreeMap<Integer, Class<? extends Scenario>> coveringMap =
            new TreeMap<Integer, Class<? extends Scenario>>();
      int howMuchEachScenarioCoversRequestedAtts;
      for(List<String> eachScenarioAttributeSet : Scenario.allScenarios.keySet()){
         howMuchEachScenarioCoversRequestedAtts = 0;
         for(String eachRequestedAtt : importedSocialAttributes)
            if(eachScenarioAttributeSet.contains(eachRequestedAtt))
               howMuchEachScenarioCoversRequestedAtts ++;
         coveringMap.put(howMuchEachScenarioCoversRequestedAtts
               , Scenario.allScenarios.get(eachScenarioAttributeSet));
      }
      //gives a 'view' of the last (hence entries with highest keys) elements
      SortedMap<Integer, Class<? extends Scenario>> maxCoveringMapView = 
            coveringMap.tailMap(coveringMap.lastKey());
      //copied in a new map
      TreeMap<Integer, Class<? extends Scenario>> maxCoveringMap = 
            new TreeMap<Integer, Class<? extends Scenario>>();
      maxCoveringMap.putAll(maxCoveringMapView);
      //<<

      //be exact>>
      TreeMap<Integer, Class<? extends Scenario>> extraMap = 
            new TreeMap<Integer, Class<? extends Scenario>>();
      int howManyExtraAttributesEachMaxCoveringHasBesidesOverlap;
      for(Class<? extends Scenario> eachScenario : maxCoveringMap.values()){
         howManyExtraAttributesEachMaxCoveringHasBesidesOverlap = 0;
         for(Entry<List<String>, Class<? extends Scenario>> eachOriginalEntry 
               : Scenario.allScenarios.entrySet()){
            if(eachScenario.equals(eachOriginalEntry.getValue()))
               howManyExtraAttributesEachMaxCoveringHasBesidesOverlap 
               = eachOriginalEntry.getKey().size() 
               - importedSocialAttributes.size();
            if(howManyExtraAttributesEachMaxCoveringHasBesidesOverlap < 0)
               howManyExtraAttributesEachMaxCoveringHasBesidesOverlap = 0;
         }
         extraMap.put(howManyExtraAttributesEachMaxCoveringHasBesidesOverlap
               , eachScenario);
      }
      //gives a 'view' of the first (hence entries with lowest keys) elements
      SortedMap<Integer, Class<? extends Scenario>> candidatesMapView = 
            extraMap.headMap(extraMap.firstKey(), true);
      //copied in a new map
      TreeMap<Integer, Class<? extends Scenario>> candidatesMap = 
            new TreeMap<Integer, Class<? extends Scenario>>();
      candidatesMap.putAll(candidatesMapView);
      //<<

      //creating candidate scenario list and shuffling it
      List<Class<? extends Scenario>> candidates =
            new ArrayList<Class<? extends Scenario>>();
      candidates.addAll(candidatesMap.values());
      Collections.shuffle(candidates);

      return
            candidates.get(new Random()
            .nextInt(candidates.size()))
            ;
   }

   /**
    * This method will get called by the ScenarioManager
    * constructor and is also called when a scenario 
    * fails a number of times more than the maximum 
    * its bearing limit. The {@link #importedSocialAttributes} 
    * could be different at that point so the scenario choosing 
    * algorithm must run again. {@link #importSocialAttributes()}
    * can get called any time to update the static field
    * of socialAttributes. This method also retrieves social 
    * attributes by getting from JSON file if they have not been 
    * imported by a public direct call of {@link #importSocialAttributes()}
    * already.
    */
   public void chooseOrUpdateScenario(){

      if(!ScenarioManager.importedByDirectCall)
         importSocialAttributesFromFile();
      ScenarioManager.importedByDirectCall = false;

      try {

         currentScneario = matchingAlgorithm().newInstance();
         activeScenarios.add(currentScneario);//temp ,later choose some in matching algorithm?

      } catch (InstantiationException | IllegalAccessException e) {
         System.out.println("Scenario instantiation error.");
         e.printStackTrace();
      }

   }

   /**
    * if failed, increment failure count; 
    * if more than maximum bearing, update
    * the scenario (same one may and might
    * be selected).
    */
   public void noMoveFound(){
      currentScneario.incrementFailures();
      if(currentScneario.getFailures() 
            > currentScneario.failingBear)
         chooseOrUpdateScenario();
   }

   public Scenario getCurrentScenario(){
      return currentScneario;
   }

   public List<Scenario> getCurrentActiveScenarios(){
      return activeScenarios;
   }

   private void loadScenarios(){

      List <String> scenarioNames = 
            new ArrayList<String>();
      scenarioNames.addAll(
            importScenarioNamesFromFile());
      for(String eachScenarioName : scenarioNames)
         try {
            Class.forName("edu.wpi.sgf.scenario." + eachScenarioName);
         } catch (ClassNotFoundException e) {
            System.out.println("Check if all the scenario names " +
                  "in the scenario.xml match scenario class names.");
            e.printStackTrace();
         }
      //      Reflections reflections = new Reflections("scenario");    
      //      Set<Class<? extends Scenario>> allScenarios = 
      //            reflections.getSubTypesOf(Scenario.class);
      //      for(Class<? extends Scenario> eachScenario : allScenarios){
      //         try {
      //            //to initialize
      //            Class.forName(eachScenario.getName());
      //         } catch (ClassNotFoundException e) {
      //            e.printStackTrace();
      //         }
      //      }
   }

   private List<String> importScenarioNamesFromFile(){

      List<String> scenarioNames = new ArrayList<String>();
      SAXBuilder builder = new SAXBuilder();

      File scenariosFile = null;
      try {
         scenariosFile = new File(
               Utils.toURL("edu/wpi/sgf/resources/"+ScenarioNamesFile).toURI());
      } catch (MalformedURLException | URISyntaxException e) {
         System.out.println(
               "Resource loading error in loading Scenario names."
                     + "The .json file(s) should be in sgf/resources "
                     + "which should be in the sgf classpath (exported)");
         e.printStackTrace();
      }
      try {
         Document xmldoc = (Document) builder
               .build(scenariosFile);
         Element rootNode = xmldoc.getRootElement();
         List<Element> retrievedScenarioNamesFromFile = 
               rootNode.getChildren("scenario");

         for(Element eachScenario 
               : retrievedScenarioNamesFromFile)
            scenarioNames.add(eachScenario.
                  getAttributeValue("name"));

      } catch (JDOMException | IOException e) {
         System.out.println(
               "Scenarios names file parse error.");
         e.printStackTrace();
      }

      return scenarioNames;

   }

   private class GsonBridge{

      private List<String> list = new ArrayList<String>() ;
      @Override
      public String toString() {
         return "list=" + list + "]";
      }

   }

   /**
    * This method 'ticks' all the scenarios currently active, 
    * meaning by their {@link Scenario#tick()}) method. 
    * @see Scenario
    * @since 2.3
    */
   public void tickAll(){

      for(Scenario easchScenario : activeScenarios)
         easchScenario.tick();

   }

   //testing main
   public static void main (String[] args) {
      new ScenarioManager();
      for(Class<? extends Scenario> each 
            : Scenario.allScenarios.values())
         System.out.println(each.getName());
   }

}
