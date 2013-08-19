package edu.wpi.always.user.owl;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

public class OntologyClass {

   public OWLClass type;
   private final OntologyHelper helper;

   public OntologyClass (Ontology ontology, OWLClass type) {
      helper = new OntologyHelper(ontology);
      this.type = type;
   }

   private OWLDatatype getDataType (OWL2Datatype datatype) {
      return helper.getFactory().getOWLDatatype(datatype.getIRI());
   }

   public OWLDataProperty declareDataProperty (OWLDataProperty property,
         OWL2Datatype datatype, OntologyCardinality cardinality) {
      return declareDataProperty(property, getDataType(datatype), cardinality);
   }

   public OWLDataProperty declareDataProperty (OWLDataProperty property,
         OWLDataRange datarange, OntologyCardinality cardinality) {
      helper.addAxiom(helper.getFactory().getOWLDeclarationAxiom(property));
      helper.addAxiom(helper.getFactory().getOWLDataPropertyDomainAxiom(
            property, type));
      if ( datarange != null )
         helper.addAxiom(helper.getFactory().getOWLDataPropertyRangeAxiom(
               property, datarange));
      if ( cardinality != null )
         helper.addAxiom(helper.getFactory().getOWLSubClassOfAxiom(
               type,
               cardinality.getDataCardinality(helper.getFactory(), property,
                     datarange)));
      return property;
   }

   public OWLObjectProperty declareObjectProperty (OWLObjectProperty property,
         OWLClassExpression domain, OWLClassExpression range,
         OntologyCardinality cardinality) {
      List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
      helper.addAxiom(changes,
            helper.getFactory().getOWLDeclarationAxiom(property));
      if ( domain != null )
         helper.addAxiom(changes, helper.getFactory()
               .getOWLObjectPropertyDomainAxiom(property, domain));
      if ( range != null )
         helper.addAxiom(changes, helper.getFactory()
               .getOWLObjectPropertyRangeAxiom(property, range));
      if ( cardinality != null )
         helper.addAxiom(
               changes,
               helper.getFactory().getOWLSubClassOfAxiom(
                     domain,
                     cardinality.getObjectCardinality(helper.getFactory(),
                           property, range)));
      helper.applyChanges(changes);
      return property;
   }
}
