package edu.wpi.always.user.owl;

import org.joda.time.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import uk.ac.manchester.cs.owl.owlapi.OWLDatatypeImpl;
import java.io.*;
import java.util.*;

public class OntologyHelper {
   
   // synchronization added to support multiple threads
   
   private OWLDataFactory factory;
   private OWLOntologyManager manager;
   private OWLOntology ontology;
   private PrefixManager pm;
   private Ontology ontologyData;

   public OntologyHelper (Ontology ontologyData) {
      this.ontologyData = ontologyData;
      init();
   }
   
   public void reset () { 
      synchronized (OntologyUserModel.LOCK) {
         ontologyData.reset(); 
         init();
      }
   }

   private void init () {
      factory = ontologyData.getFactory();
      manager = ontologyData.getManager();
      ontology = ontologyData.getOntology();
      pm = ontologyData.getPm();
   }

   public Ontology getOntologyDataObject () {
      return ontologyData;
   }

   public OWLReasoner getReasoner () {
      return getOntologyDataObject().getReasoner();
   }

   public OWLDataFactory getFactory () {
      return factory;
   }

   public OWLOntologyManager getManager () {
      return manager;
   }

   public OWLOntology getOntology () {
      return ontology;
   }

   public PrefixManager getPm () {
      return pm;
   }

   protected String toIRIName (String name) {
      return "#" + name.replaceAll(" ", "%20");
   }

   protected String fromIRIName (String name) {
      if ( name == null )
         return null;
      if ( name.startsWith("#") )
         name = name.substring(1);
      return name.replaceAll("%20", " ");
   }

   public OWLClass getClass (String name) {
      synchronized (OntologyUserModel.LOCK) {
         return factory.getOWLClass(toIRIName(name), pm);
      }
   }

   public OWLClassAssertionAxiom getClassAssertionAxiom (
         OWLClassExpression clazz, OWLIndividual individual) {
      synchronized (OntologyUserModel.LOCK) {
         return factory.getOWLClassAssertionAxiom(clazz, individual);
      }
   }

   public String getName (OWLNamedIndividual individual) {
      if ( individual == null ) return null;
      return fromIRIName(individual.getIRI().getFragment());
   }

   public OntologyIndividual getNamedIndividual (String name) {
      synchronized (OntologyUserModel.LOCK) {
         return new OntologyIndividual(getOntologyDataObject(),
               factory.getOWLNamedIndividual(toIRIName(name), pm));
      }
   }

   public OWLObjectProperty getObjectProperty (String name) {
      synchronized (OntologyUserModel.LOCK) {
         return factory.getOWLObjectProperty(toIRIName(name), pm);
      }
   }

   public OWLDataProperty getDataProperty (String name) {
      synchronized (OntologyUserModel.LOCK) {
         return factory.getOWLDataProperty(toIRIName(name), pm);
      }
   }

   public OntologyClass declareClass (String name) {
      synchronized (OntologyUserModel.LOCK) {
         OWLClass clazz = getClass(name);
         addAxiom(getFactory().getOWLDeclarationAxiom(clazz));
         return new OntologyClass(getOntologyDataObject(), clazz);
      }
   }

   public Set<OWLNamedIndividual> getAllOfClass (String className) {
      synchronized (OntologyUserModel.LOCK) {
         return getReasoner().getInstances(getClass(className), false)
               .getFlattened();
      }
   }

   public OntologyValue getLiteral (String lexicalValue, OWL2Datatype datatype) {
      return new OntologyValue(getFactory().getOWLLiteral(lexicalValue,
            datatype));
   }

   public OntologyValue getLiteral (String lexicalValue, OWLDatatype datatype) {
      return new OntologyValue(getFactory().getOWLLiteral(lexicalValue,
            datatype));
   }

   public OntologyValue getLiteral (String value) {
      return new OntologyValue(getFactory().getOWLLiteral(value));
   }

   public OntologyValue getLiteral (double value) {
      return new OntologyValue(getFactory().getOWLLiteral(value));
   }

   public OntologyValue getLiteral (int value) {
      return new OntologyValue(getFactory().getOWLLiteral(value));
   }

   public OntologyValue getLiteral (long value) {
      return getLiteral(Long.toString(value), OWL2Datatype.XSD_LONG);
   }
   
   public OntologyValue getLiteral (boolean value) {
      return new OntologyValue(getFactory().getOWLLiteral(value));
   }
   
   private final OWLDatatype XSD_GMonthDay_TYPE = new OWLDatatypeImpl(
         getFactory(), IRI.create("xsd:gMonthDay"));

   public OntologyValue getLiteral (MonthDay date) {
      return new OntologyValue(getFactory().getOWLLiteral(
            OntologyValue.XML_GMonthDay_FORMAT.print(date), XSD_GMonthDay_TYPE));
   }

   public OntologyValue getLiteral (ReadableInstant instant) {
      return new OntologyValue(getFactory().getOWLLiteral(
            OntologyValue.XML_DATE_TIME_FORMAT.print(instant),
            OWL2Datatype.XSD_DATE_TIME));
   }

   private final OWLDatatype XSD_DATE_TYPE = new OWLDatatypeImpl(getFactory(),
         IRI.create("xsd:date"));

   public OntologyValue getLiteral (LocalDate date) {
      return new OntologyValue(getFactory().getOWLLiteral(
            OntologyValue.XML_DATE_FORMAT.print(date), XSD_DATE_TYPE));
   }

   private final OWLDatatype XSD_TIME_TYPE = new OWLDatatypeImpl(getFactory(),
         IRI.create("xsd:time"));

   public OntologyValue getLiteral (LocalTime time) {
      return new OntologyValue(getFactory().getOWLLiteral(
            OntologyValue.XML_TIME_FORMAT.print(time), XSD_TIME_TYPE));
   }

   private final OWLDatatype XSD_DURATION_TYPE = new OWLDatatypeImpl(
         getFactory(), IRI.create("xsd:duration"));

   public OntologyValue getLiteral (ReadablePeriod duration) {
      return new OntologyValue(getFactory().getOWLLiteral(
            OntologyValue.XML_DURATION_FORMAT.print(duration),
            XSD_DURATION_TYPE));
   }

   public void addAxiom (OWLAxiom axiom) {
      synchronized (OntologyUserModel.LOCK) {
         manager.addAxiom(ontology, axiom);
      }
   }

   public void addAxiomForClass (String className, OWLIndividual individual) {
      synchronized (OntologyUserModel.LOCK) {
         manager.addAxiom(ontology,
               getClassAssertionAxiom(getClass(className), individual));
      }
   }

   public void removeAxiom (OWLAxiom axiom) {
      synchronized (OntologyUserModel.LOCK) {
         manager.removeAxiom(ontology, axiom);
      }
   }

   public AddAxiom addAxiom (List<OWLOntologyChange> changeList, OWLAxiom axiom) {
      AddAxiom add = new AddAxiom(getOntology(), axiom);
      changeList.add(add);
      return add;
   }

   public RemoveAxiom removeAxiom (List<OWLOntologyChange> changeList, OWLAxiom axiom) {
      RemoveAxiom remove = new RemoveAxiom(getOntology(), axiom);
      changeList.add(remove);
      return remove;
   }

   public void applyChanges (List<OWLOntologyChange> axioms) {
      synchronized (OntologyUserModel.LOCK) {
         manager.applyChanges(axioms);
      }
   }

   public void addAxioms (InputStream inputStream) {
      try { addAxioms(manager.loadOntologyFromOntologyDocument(inputStream)); } 
      catch (OWLOntologyCreationException e) {
         e.printStackTrace();
      }
   }

   public void addAxioms (File file) {
      // loadOntologyFromOntologyDocument(File) does not close stream!
      try (InputStream stream = new FileInputStream(file)) { 
         addAxioms(manager.loadOntologyFromOntologyDocument(stream)); 
      } catch (OWLOntologyCreationException | IOException e) {
         e.printStackTrace();
      }
   }

   private void addAxioms (OWLOntology tmpOntology) {
      synchronized (OntologyUserModel.LOCK) {
         Set<OWLAxiom> axioms = tmpOntology.getAxioms();
         List<OWLOntologyChange> axiomChanges = new ArrayList<OWLOntologyChange>();
         for (OWLAxiom axiom : axioms)
            addAxiom(axiomChanges, axiom);
         applyChanges(axiomChanges);
         manager.removeOntology(tmpOntology);
      }
   }
   
   /**
    * If B is initialProperty of A and C is throughProperty of B then C is
    * resultProperty of A
    */
   public OWLSubPropertyChainOfAxiom getOWLSubPropertyChainOfAxiom (
         OWLObjectProperty initialProperty,
         OWLObjectProperty transitiveProperty, OWLObjectProperty resultProperty) {
      return getOWLSubPropertyChainOfAxiom(
            Arrays.asList(initialProperty, transitiveProperty), resultProperty);
   }

   public OWLSubPropertyChainOfAxiom getOWLSubPropertyChainOfAxiom (
         List<? extends OWLObjectPropertyExpression> chain,
         OWLObjectProperty resultProperty) {
      synchronized (OntologyUserModel.LOCK) {
         return getFactory().getOWLSubPropertyChainOfAxiom(chain, resultProperty);
      }
   }
}
