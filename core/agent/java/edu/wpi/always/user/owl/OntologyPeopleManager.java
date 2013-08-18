<<<<<<< HEAD
package edu.wpi.always.user.owl;

import java.util.Set;
import org.joda.time.MonthDay;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import edu.wpi.always.user.*;
import edu.wpi.always.user.people.*;
import edu.wpi.always.user.people.Person.Gender;
import edu.wpi.always.user.people.Person.Relationship;
import edu.wpi.always.user.places.Place;

public class OntologyPeopleManager implements PeopleManager {

   private OntologyUserModel model; // cyclic dependency
   private final OntologyHelper helper;
   private final Ontology ontology;

   public OntologyPeopleManager (Ontology ontology) {
      this.ontology = ontology;
      helper = new OntologyHelper(ontology);
   }

   // MUST be called for getUser to work (called in OntologyUserModel constructor)
   void setUserModel (OntologyUserModel model) {
      this.model = model;
   }

   @Override
   public OntologyPerson getUser () {
      OntologyIndividual owlPerson = helper.getNamedIndividual(model
            .getUserName());
      if ( !owlPerson.hasSuperclass(OntologyPerson.USER_CLASS) ) {
         owlPerson.addSuperclass(OntologyPerson.USER_CLASS);
         OntologyPerson person = addPerson(model.getUserName());
         return person;
      }
      return getPerson(owlPerson);
   }

   @Override
   public OntologyPerson addPerson (String name) {
      return addPerson(name, null, null, 0, null, null, null, null, null);
   }

   @Override
   public OntologyPerson addPerson(String name, Relationship relationship, Gender gender, 
         int age, String phone, String skype, Place place, Person spouse, MonthDay birthday){
      OntologyIndividual owlPerson = helper.getNamedIndividual(name);
      owlPerson.addSuperclass(OntologyPerson.PERSON_CLASS);
      OntologyPerson person = new OntologyPerson(
            helper.getOntologyDataObject(), model, owlPerson);
      person.setName(name);
      if ( relationship != null )
         person.setRelationship(relationship);
      if ( gender != null )
         person.setGender(gender);
      if ( age != 0)
         person.setAge(age);
      if ( phone != null)
         person.setPhoneNumber(phone);
      if ( skype != null) 
         person.setSkypeNumber(skype);
      if ( place != null)
         person.setLocation(place);
      if ( spouse != null )
         person.addRelated(spouse, Relationship.Spouse);
      if (birthday != null)
         person.setBirthday(birthday);
      return person;
   }

   public OntologyPerson getPerson (OntologyIndividual owlPerson) {
      if ( owlPerson == null )
         return null;
      if ( !owlPerson.hasSuperclass(OntologyPerson.PERSON_CLASS) )
         return null;
      return new OntologyPerson(helper.getOntologyDataObject(), model, owlPerson);
   }

   @Override
   public OntologyPerson getPerson (String name) {
      OntologyPerson person = getPerson(helper.getNamedIndividual(name));
      if ( person == null ) return addPerson(name);
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

   @Override
   public UserModel getUserModel () {
      return model;
   }
}
=======
package edu.wpi.always.user.owl;

import java.util.Set;
import org.joda.time.MonthDay;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import edu.wpi.always.user.*;
import edu.wpi.always.user.people.*;
import edu.wpi.always.user.people.Person.Gender;
import edu.wpi.always.user.people.Person.Relationship;
import edu.wpi.always.user.places.Place;

public class OntologyPeopleManager implements PeopleManager {

   private OntologyUserModel model; // cyclic dependency
   private final OntologyHelper helper;
   private final Ontology ontology;

   public OntologyPeopleManager (Ontology ontology) {
      this.ontology = ontology;
      helper = new OntologyHelper(ontology);
   }

   // MUST be called for getUser to work (called in OntologyUserModel constructor)
   void setUserModel (OntologyUserModel model) {
      this.model = model;
   }

   @Override
   public OntologyPerson getUser () {
      OntologyIndividual owlPerson = helper.getNamedIndividual(model
            .getUserName());
      if ( !owlPerson.hasSuperclass(OntologyPerson.USER_CLASS) ) {
         owlPerson.addSuperclass(OntologyPerson.USER_CLASS);
         OntologyPerson person = addPerson(model.getUserName());
         return person;
      }
      return getPerson(owlPerson);
   }

   @Override
   public OntologyPerson addPerson (String name) {
      return addPerson(name, null, null, 0, null, null, null, null, null);
   }

   @Override
   public OntologyPerson addPerson(String name, Relationship relationship, Gender gender, 
         int age, String phone, String skype, Place place, Person spouse, MonthDay birthday){
      OntologyIndividual owlPerson = helper.getNamedIndividual(name);
      owlPerson.addSuperclass(OntologyPerson.PERSON_CLASS);
      OntologyPerson person = new OntologyPerson(
            helper.getOntologyDataObject(), model, owlPerson);
      person.setName(name);
      if ( relationship != null )
         person.setRelationship(relationship);
      if ( gender != null )
         person.setGender(gender);
      if ( age != 0)
         person.setAge(age);
      if ( phone != null)
         person.setPhoneNumber(phone);
      if ( skype != null) 
         person.setSkypeNumber(skype);
      if ( place != null)
         person.setLocation(place);
      if ( spouse != null )
         person.addRelated(spouse, Relationship.Spouse);
      if (birthday != null)
         person.setBirthday(birthday);
      return person;
   }

   public OntologyPerson getPerson (OntologyIndividual owlPerson) {
      if ( owlPerson == null )
         return null;
      if ( !owlPerson.hasSuperclass(OntologyPerson.PERSON_CLASS) )
         return null;
      return new OntologyPerson(helper.getOntologyDataObject(), model, owlPerson);
   }

   @Override
   public OntologyPerson getPerson (String name) {
      OntologyPerson person = getPerson(helper.getNamedIndividual(name));
      if ( person == null ) return addPerson(name);
      return person;
   }

   @Override
   public OntologyPerson[] getPeople (boolean includeUser) {
      Set<OWLNamedIndividual> owlPeople = helper
            .getAllOfClass(OntologyPerson.PERSON_CLASS);
      int size = owlPeople.size();
      if ( !includeUser ) size--;
      OntologyPerson[] people = new OntologyPerson[size];
      int i = 0;
      OntologyIndividual user = helper.getNamedIndividual(model.getUserName());
      for (OWLNamedIndividual owlPerson : owlPeople) {
         OntologyIndividual individual = new OntologyIndividual(ontology, owlPerson); 
         if ( !includeUser || individual != user ) 
            people[i++] = getPerson(individual); 
      }
      return people;
   }

   @Override
   public UserModel getUserModel () {
      return model;
   }
}
>>>>>>> upstream/develop
