package edu.wpi.always.cm;

import java.util.*;

public class MetadataToFuzzyVariableTranslator {

	Map<String, String> map = new HashMap<String, String>();

	public MetadataToFuzzyVariableTranslator () {
		map.put("specificity", "spec");
		map.put("dueIn", "due");
		map.put("timeRemaining", "time");
	}

	String translate (String metadataVar) {
		if (map.containsKey(metadataVar))
			return map.get(metadataVar);

		return metadataVar;
	}

}
