package edu.wpi.sgf.scenario;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.gson.Gson;

/**
 * ScenarioManager class manages importing of social 
 * attributes, different approached fo doing that task
 * at instanciation or in runtime, and also picks the 
 * current scenario. It determines scenario failure and 
 * updates the scenario if necessary. 
 * @author Morteza Behrooz
 * @version 2.1
 */
public class ScenarioManager {

	protected Scenario currentScneario;
	private static boolean importedByDirectCall;

	//if scenario fails, ++, changes after
	//getting to scenarioFailingBear
	int scenarioFailCycles = 0;

	private static List<String> importedSocialAttributes = 
			new ArrayList<String>();
	private static Map<List<String>, 
	Class<? extends Scenario>> allScenarios;

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

		ScenarioManager.importedSocialAttributes.clear();
		ScenarioManager.importedSocialAttributes
		.addAll(importedSocialAttributes);

		ScenarioManager.allScenarios.clear();
		ScenarioManager.allScenarios = 
				new HashMap<List<String>, 
				Class<? extends Scenario>>();

		chooseOrUpdateScenario();

	}

	/**
	 * This constructor does not get input attributes,
	 * so the ScenarioManager will have to look for them
	 * in the json file (look into documents). At later rounds 
	 * (after scenario failure/completion) it is possible 
	 * to update social attributes by direct call of 
	 * {@link #importTheseSocialAttributes(List)} or updating 
	 * the json file.
	 */
	public ScenarioManager(){

		ScenarioManager.importedSocialAttributes.clear();

		/*if not passed as argument, at least for
		the first round, social attributes are 
		read from JSON file. If not desired, attributes 
		should be passed to the constructor, and for a
		change in them, can be called at any given runtime.*/
		importSocialAttributesFromFile();
		
		ScenarioManager.allScenarios.clear();
		ScenarioManager.allScenarios = 
				new HashMap<List<String>, 
				Class<? extends Scenario>>();

		chooseOrUpdateScenario();

	}

	public void importTheseSocialAttributes(
			List<String> importedAttributeList){

		ScenarioManager.importedSocialAttributes
		.addAll(importedAttributeList);
		ScenarioManager.importedByDirectCall = true;

	}

	public void importSocialAttributesFromFile(){

		Gson gson = new Gson();
		try {
			BufferedReader bufferedReader = new BufferedReader(
					new FileReader("socialAttributes.json"));
			GsonBridge gsonBridge = gson.fromJson(
					bufferedReader, GsonBridge.class);
			ScenarioManager.importedSocialAttributes
			.addAll((gsonBridge.list));
		} catch (FileNotFoundException e) {
			System.out.println("Social attributes file not found; " +
					"\n(put the Json file next to the API/inside project root, " +
					"refere to documents for the formatting)");
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
		TreeMap<Integer, Class<? extends Scenario>> coveringMap 
		= new TreeMap<Integer, Class<? extends Scenario>>();
		int howMuchEachScenarioCoversRequestedAtts;
		for(List<String> eachScenarioAttributeSet : allScenarios.keySet()){
			howMuchEachScenarioCoversRequestedAtts = 0;
			for(String eachRequestedAtt : importedSocialAttributes)
				if(eachScenarioAttributeSet.contains(eachRequestedAtt))
					howMuchEachScenarioCoversRequestedAtts ++;
			coveringMap.put(howMuchEachScenarioCoversRequestedAtts
					, allScenarios.get(eachScenarioAttributeSet));
		}
		//gives a 'view' of the last (hence entries with highest keys) elements
		SortedMap<Integer, Class<? extends Scenario>> maxCoveringMapView 
		= coveringMap.tailMap(coveringMap.lastKey());
		//copied in a new map
		TreeMap<Integer, Class<? extends Scenario>> maxCoveringMap 
		= new TreeMap<Integer, Class<? extends Scenario>>();
		maxCoveringMap.putAll(maxCoveringMapView);
		//<<

		//be exact>>
		TreeMap<Integer, Class<? extends Scenario>> extraMap 
		= new TreeMap<Integer, Class<? extends Scenario>>();
		int howManyExtraAttributesEachMaxCoveringHasBesidesOverlap;
		for(Class<? extends Scenario> eachScenario : maxCoveringMap.values()){
			howManyExtraAttributesEachMaxCoveringHasBesidesOverlap = 0;
			for(Entry<List<String>, Class<? extends Scenario>> eachOriginalEntry 
					: allScenarios.entrySet()){
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
		SortedMap<Integer, Class<? extends Scenario>> candidatesMapView 
		= extraMap.headMap(coveringMap.firstKey());
		//copied in a new map
		TreeMap<Integer, Class<? extends Scenario>> candidatesMap 
		= new TreeMap<Integer, Class<? extends Scenario>>();
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
	 * can get called anytime to update the static field
	 * of socialAttributes. This method also retrieves social 
	 * attributes by getting from JSON file if they have not been 
	 * imported by a public direct call of {@link #importSocialAttributes()}
	 * already.
	 */
	public void chooseOrUpdateScenario(){

		if(!ScenarioManager.importedByDirectCall)
			importSocialAttributesFromFile();
		ScenarioManager.importedByDirectCall = false;

		currentScneario = 
				Scenario.class.cast(
						matchingAlgorithm());

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
				> currentScneario.scenarioFailingBear)
			chooseOrUpdateScenario();
	}

	public Scenario getCurrentScenario(){
		return currentScneario;
	}

	private class GsonBridge{

		private List<String> list = new ArrayList<String>() ;
		@Override
		public String toString() {
			return "list=" + list + "]";
		}

	}


}
