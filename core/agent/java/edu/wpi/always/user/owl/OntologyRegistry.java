package edu.wpi.always.user.owl;

import edu.wpi.disco.rt.Registry;

public interface OntologyRegistry extends Registry {

   void register (OntologyRuleHelper ontology);
}
