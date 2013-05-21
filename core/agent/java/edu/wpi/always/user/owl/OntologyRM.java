package edu.wpi.always.user.owl;

//TODO: add interface for plugins etc to get data
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import java.io.File;
import java.util.ArrayList;

public class OntologyRM {

   private static final String location = "file:C:/Users/mel/Desktop/AlwaysOntology.owl";
   private OWLOntologyManager manager;
   private OWLOntology ontology;
   private OWLDataFactory factory;
   private PrefixManager pm;
   private String user;

   public OWLOntology getOWLOntology () {
      return ontology;
   }

   public OntologyRM (String userName) {
      try {
         manager = OWLManager.createOWLOntologyManager();
         ontology = manager.loadOntologyFromOntologyDocument(IRI
               .create(location));
         factory = manager.getOWLDataFactory();
         pm = new DefaultPrefixManager(location);
         user = userName;
         OWLIndividual toAdd = factory
               .getOWLNamedIndividual(":" + userName, pm);
         OWLClassAssertionAxiom isUser = factory.getOWLClassAssertionAxiom(
               factory.getOWLClass(":User", pm), toAdd);
         manager.addAxiom(ontology, isUser);
      } catch (OWLOntologyCreationException e) {
         System.err.println("Failed to load ontology: " + e.getMessage());
      }
   }

   // Add a new person to the ontology, along with their location and
   // relationship to the user
   // TODO Accepts relationships:
   public void AddPerson (String name, String zip, String relationship) {
      // TODO check if relationship acceptable !
      OWLIndividual person = factory.getOWLNamedIndividual(":" + name, pm);
      OWLIndividual place = factory.getOWLNamedIndividual(":" + zip, pm);
      ArrayList<AddAxiom> axioms = new ArrayList<AddAxiom>();
      axioms.add(new AddAxiom(ontology, factory
            .getOWLObjectPropertyAssertionAxiom(
                  factory.getOWLObjectProperty(":LivesIn", pm), person, place)));
      axioms.add(new AddAxiom(ontology, factory
            .getOWLObjectPropertyAssertionAxiom(
                  factory.getOWLObjectProperty(":" + relationship, pm),
                  factory.getOWLNamedIndividual(":" + user, pm), person)));
      manager.applyChanges(axioms);
   }

   public void AddRelationship (String person1, String person2,
         String relationship) {
      // TODO check relationship
      manager.addAxiom(
            ontology,
            factory.getOWLObjectPropertyAssertionAxiom(
                  factory.getOWLObjectProperty(":" + relationship, pm),
                  factory.getOWLNamedIndividual(":" + person1, pm),
                  factory.getOWLNamedIndividual(":" + person2, pm)));
   }

   // Code to generate the OWL ontology file. Adds basic classes and properties.
   // Not to be run by Always agent!
   public static void main (String[] args) {
      try {
         OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
         PrefixManager pm = new DefaultPrefixManager(location);
         OWLOntology ontology = manager.createOntology(IRI.create(location));
         OWLDataFactory factory = manager.getOWLDataFactory();
         // Preset classes etc.
         ArrayList<AddAxiom> axioms = new ArrayList<AddAxiom>();
         OWLClass place = factory.getOWLClass(":Place", pm);
         axioms.add(new AddAxiom(ontology, factory
               .getOWLDeclarationAxiom(place)));
         OWLClass person = factory.getOWLClass(":Person", pm);
         OWLObjectProperty livesIn = factory.getOWLObjectProperty(":LivesIn",
               pm);
         axioms.add(new AddAxiom(ontology, factory
               .getOWLDeclarationAxiom(livesIn)));
         OWLClass relative = factory.getOWLClass(":Relative", pm);
         axioms.add(new AddAxiom(ontology, factory.getOWLSubClassOfAxiom(
               relative, person)));
         OWLClass child = factory.getOWLClass(":Child", pm);
         axioms.add(new AddAxiom(ontology, factory.getOWLSubClassOfAxiom(child,
               relative)));
         OWLClass grandchild = factory.getOWLClass(":Grandchild", pm);
         axioms.add(new AddAxiom(ontology, factory.getOWLSubClassOfAxiom(
               grandchild, relative)));
         OWLClass sibling = factory.getOWLClass(":Sibling", pm);
         axioms.add(new AddAxiom(ontology, factory.getOWLSubClassOfAxiom(
               sibling, relative)));
         OWLClass user = factory.getOWLClass(":User", pm);
         axioms.add(new AddAxiom(ontology, factory.getOWLSubClassOfAxiom(user,
               person)));
         OWLClass friend = factory.getOWLClass(":Friend", pm);
         axioms.add(new AddAxiom(ontology, factory.getOWLSubClassOfAxiom(
               friend, person)));
         manager.applyChanges(axioms);
         manager.saveOntology(ontology, IRI.create(new File(location).toURI()));
      } catch (OWLOntologyCreationException e) {
         System.err.println("Could not create ontology: " + e.getMessage());
      } catch (OWLOntologyStorageException e) {
         // Auto-generated catch block
         e.printStackTrace();
      }
   }
}
