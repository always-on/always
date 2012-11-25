package edu.wpi.always.user.owl;

import edu.wpi.always.Registry;



public interface OntologyRegistry extends Registry {
	void register (OntologyRuleHelper ontology);
}
