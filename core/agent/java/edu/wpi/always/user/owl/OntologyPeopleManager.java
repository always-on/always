package edu.wpi.always.user.owl;

import edu.wpi.always.user.people.*;
import edu.wpi.always.user.people.Person.Gender;
import edu.wpi.always.user.people.Person.Relationship;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import java.util.Set;

public class OntologyPeopleManager implements PeopleManager {

   private OntologyUserModel model;
   private final OntologyHelper helper;
   private final Ontology ontology;

   public OntologyPeopleManager (Ontology ontology) {
      this.ontology = ontology;
      helper = new OntologyHelper(ontology);
   }

   // MUST be called for getUser to work (called in OntologyUserModel)
   void setUserModel (OntologyUserModel model) {
      this.model = model;
   }

   @Override
   public OntologyPerson getUser () {
      OntologyIndividual owlPerson = helper.getNamedIndividual(model
            .getUserName());
      if ( !owlPerson.hasSuperclass(OntologyPerson.USER_CLASS) ) {
         owlPerson.addSuperclass(OntologyPerson.USER_CLASS);
         OntologyPerson person = addPerson(model.getUserName(), null, null);
         return person;
      }
      return getPerson(owlPerson);
   }

   @Override
   public OntologyPerson addPerson (String name, Relationship relationship,
         Gender gender) {
      OntologyIndividual owlPerson = helper.getNamedIndividual(name);
      owlPerson.addSuperclass(OntologyPerson.PERSON_CLASS);
      OntologyPerson person = new OntologyPerson(
            helper.getOntologyDataObject(), model, owlPerson);
      person.setName(name);
      if ( relationship != null )
         getUser().addRelationship(person, relationship);
      if ( gender != null )
         person.setGender(gender);
      return person;
   }

   public OntologyPerson getPerson (OntologyIndividual owlPerson) {
      if ( owlPerson == null )
         return null;
      if ( !owlPerson.hasSuperclass(OntologyPerson.PERSON_CLASS) )
         return null;
      return new OntologyPerson(helper.getOntologyDataObject(), model,
            owlPerson);
   }

   @Override
   public OntologyPerson getPerson (String name) {
      // TODO get individual by name property
      OntologyPerson person = getPerson(helper.getNamedIndividual(name));
      if ( person == null )
         return addPerson(name, null, null);
      return person;
   }

   @Override
   public OntologyPerson[] getPeople () {
      Set<OWLNamedIndividual> owlPeople = helper
            .getAllOfClass(OntologyPerson.PERSON_CLASS);
      OntologyPerson[] people = new OntologyPerson[owlPeople.size()];
      int i = 0;
      for (OWLNamedIndividual owlPerson : owlPeople) {
         people[i++] = getPerson(new OntologyIndividual(ontology, owlPerson));
      }
      return people;
   }
}
