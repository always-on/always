package edu.wpi.always.user.owl;

import org.semanticweb.owlapi.model.*;

public abstract class OntologyCardinality {
	public static final OntologyCardinality ONE = exactly(1);
	public static final OntologyCardinality ONE_OR_MORE = moreOrEqual(1);
	public static final OntologyCardinality AT_MOST_ONE = lessOrEqual(1);
	public static final OntologyCardinality ANY = null;

	public static OntologyCardinality exactly(final int num){
		return new OntologyCardinality() {
			
			@Override
			public OWLDataCardinalityRestriction getDataCardinality(OWLDataFactory factory, OWLDataPropertyExpression property, OWLDataRange dataRange) {
				return factory.getOWLDataExactCardinality(num, property, dataRange);
			}
			
			@Override
			public OWLObjectCardinalityRestriction getObjectCardinality(OWLDataFactory factory, OWLObjectPropertyExpression property, OWLClassExpression range) {
				return factory.getOWLObjectExactCardinality(num, property, range);
			}
		};
	}
	public static OntologyCardinality moreOrEqual(final int num){
		return new OntologyCardinality() {
			
			@Override
			public OWLDataCardinalityRestriction getDataCardinality(OWLDataFactory factory, OWLDataPropertyExpression property, OWLDataRange dataRange) {
				return factory.getOWLDataMinCardinality(num, property, dataRange);
			}
			
			@Override
			public OWLObjectCardinalityRestriction getObjectCardinality(OWLDataFactory factory, OWLObjectPropertyExpression property, OWLClassExpression range) {
				return factory.getOWLObjectMinCardinality(num, property, range);
			}
		};
	}
	public static OntologyCardinality lessOrEqual(final int num){
		return new OntologyCardinality() {
			
			@Override
			public OWLDataCardinalityRestriction getDataCardinality(OWLDataFactory factory, OWLDataPropertyExpression property, OWLDataRange dataRange) {
				return factory.getOWLDataMaxCardinality(num, property, dataRange);
			}
			
			@Override
			public OWLObjectCardinalityRestriction getObjectCardinality(OWLDataFactory factory, OWLObjectPropertyExpression property, OWLClassExpression range) {
				return factory.getOWLObjectMaxCardinality(num, property, range);
			}
		};
	}

	public abstract OWLDataCardinalityRestriction getDataCardinality(OWLDataFactory factory, OWLDataPropertyExpression property, OWLDataRange dataRange);
	public abstract OWLObjectCardinalityRestriction getObjectCardinality(OWLDataFactory factory, OWLObjectPropertyExpression property, OWLClassExpression range);
}
