package edu.wpi.always.user.owl;

import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;

public class OntologyIndividual {
	private final OWLNamedIndividual individual;
	private final OntologyHelper helper;

	public OntologyIndividual(Ontology ontology, OWLNamedIndividual individual){
		helper = new OntologyHelper(ontology);
		this.individual = individual;
	}
	
	public OWLNamedIndividual getOWLIndividual(){
		return individual;
	}

	public void addSuperclass(String className){
		helper.addAxiom(helper.getFactory().getOWLClassAssertionAxiom(helper.getClass(className), individual));
	}

	public void removeSuperclass(String className){
		helper.removeAxiom(helper.getFactory().getOWLClassAssertionAxiom(helper.getClass(className), individual));
	}

	public void setDataProperty(String property, OntologyValue value) {
		setDataProperty(helper.getDataProperty(property), value);
	}


	public void setDataProperty(OWLDataProperty property, OntologyValue value) {
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		Set<OWLLiteral> oldValues = getDataPropertyValues(property);
		for (OWLLiteral oldValue : oldValues) {
			helper.removeAxiom(changes, helper.getFactory().getOWLDataPropertyAssertionAxiom(property, individual, oldValue));
		}
		if (value != null)
			addDataProperty(property, value);
		helper.applyChanges(changes);
	}

	public void addDataProperty(OWLDataProperty property, OntologyValue value) {
		if (value != null)
			helper.addAxiom(helper.getFactory().getOWLDataPropertyAssertionAxiom(property, individual, value.getOWLLiteral()));
	}

	public Set<OWLLiteral> getDataPropertyValues(OWLDataProperty property) {
		return helper.getReasoner().getDataPropertyValues(individual, property);
	}

	public OntologyValue getDataPropertyValue(String property) {
		return getDataPropertyValue(helper.getDataProperty(property));
	}
	
	public OntologyValue getDataPropertyValue(OWLDataProperty property) {
		Set<OWLLiteral> values = getDataPropertyValues(property);
		if (values.size() == 0)
			return new OntologyValue(null);
		if (values.size() != 1)
			throw new RuntimeException("The OWLIndividual " + individual + " has more than one value for the property " + property);
		return new OntologyValue(values.iterator().next());
	}


	public void setObjectProperty(String property, OntologyIndividual value) {
		setObjectProperty(helper.getObjectProperty(property), value);
	}

	public void setObjectProperty(OWLObjectProperty property, OntologyIndividual value) {
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		Set<OWLNamedIndividual> oldValues = getObjectPropertyValues(property);
		for (OWLNamedIndividual oldValue : oldValues) {
			helper.removeAxiom(changes, helper.getFactory().getOWLObjectPropertyAssertionAxiom(property, individual, oldValue));
		}
		if (value != null)
			addObjectProperty(property, value);
		helper.applyChanges(changes);
	}

	public void addObjectProperty(String property, OntologyIndividual value) {
		addObjectProperty(helper.getObjectProperty(property), value);
	}
	public void addObjectProperty(OWLObjectProperty property, OntologyIndividual value) {
		if (value != null)
			helper.addAxiom(helper.getFactory().getOWLObjectPropertyAssertionAxiom(property, individual, value.getOWLIndividual()));
	}

	public Set<OWLNamedIndividual> getObjectPropertyValues(String property) {
		return getObjectPropertyValues(helper.getObjectProperty(property));
	}

	public Set<OWLNamedIndividual> getObjectPropertyValues(OWLObjectProperty property) {
		return helper.getReasoner().getObjectPropertyValues(individual, property).getFlattened();
	}

	public OntologyIndividual getObjectPropertyValue(String property) {
		return getObjectPropertyValue(helper.getObjectProperty(property));
	}
	
	public OntologyIndividual getObjectPropertyValue(OWLObjectProperty property) {
		Set<OWLNamedIndividual> values = getObjectPropertyValues(property);
		if (values.size() == 0)
			return null;
		if (values.size() != 1)
			throw new RuntimeException("The OWLIndividual " + individual + " has more than one value for the property " + property);
		return new OntologyIndividual(helper.getOntologyDataObject(), values.iterator().next());
	}

	public boolean hasSuperclass(String className) {
		return helper.getReasoner().getTypes(individual, false).getFlattened().contains(helper.getClass(className));
	}

	public void delete() {
		OWLEntityRemover remover = new OWLEntityRemover(helper.getManager(), Collections.singleton(helper.getOntology()));
		individual.accept(remover);
		helper.applyChanges(remover.getChanges());
	}
}
