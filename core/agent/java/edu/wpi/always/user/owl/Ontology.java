package edu.wpi.always.user.owl;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;


public interface Ontology {
	String DOCUMENT_IRI_STRING = "http://www.wpi.org/ontologies/ontology/AlwaysOntology.owl";
	IRI DOCUMENT_IRI = IRI.create(DOCUMENT_IRI_STRING);


	public OWLOntologyManager getManager();


	public OWLOntology getOntology();


	public OWLDataFactory getFactory();


	public PrefixManager getPm();


	public OWLReasoner getReasoner();
}
