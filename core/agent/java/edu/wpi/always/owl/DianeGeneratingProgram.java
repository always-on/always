package edu.wpi.always.owl;

import edu.wpi.always.*;
import edu.wpi.always.cm.*;
import edu.wpi.always.rm.*;
import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.owl.*;
import edu.wpi.always.user.people.*;
import edu.wpi.always.user.people.Person.Gender;
import edu.wpi.always.user.people.Person.Relationship;
import edu.wpi.always.user.places.Place;
import edu.wpi.disco.rt.ComponentRegistry;
import org.joda.time.MonthDay;
import org.picocontainer.*;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import java.util.Set;

public class DianeGeneratingProgram {

   public static void main (String[] args) {
      Bootstrapper program = new Bootstrapper(false);
      program.addRegistry(new ComponentRegistry() {

         @Override
         public void register (MutablePicoContainer container) {
            container.as(Characteristics.CACHE).addComponent(
                  IRelationshipManager.class, DummyRelationshipManager.class);
            container.as(Characteristics.CACHE).addComponent(
                  ICollaborationManager.class, DummyCollaborationManager.class);
         }
      });
      program.addRegistry(new OntologyUserRegistry("Diane Ferguson"));
      program.start();
      OntologyPeopleManager peopleHelper = program.getContainer().getComponent(
            OntologyPeopleManager.class);
      OntologyPlaceManager placeHelper = program.getContainer().getComponent(
            OntologyPlaceManager.class);
      peopleHelper.getUser().setLocation(placeHelper.getPlace("02118"));
      peopleHelper.getUser().setGender(Gender.Female);
      Person daughter = peopleHelper.addPerson("Ellen Lewis",
            Relationship.Daughter, null);
      daughter.setPhoneNumber("650-339-0221");
      daughter.setLocation(placeHelper.getPlace("92041"));
      Person daughterHusband = peopleHelper.addPerson("Mike", null, null);
      daughterHusband.addRelationship(daughter, Relationship.Wife);
      Person linda = peopleHelper.addPerson("Linda", null, Gender.Female);
      linda.addRelationship(daughterHusband, Relationship.Father);
      Person ed = peopleHelper.addPerson("Ed", null, Gender.Male);
      ed.addRelationship(daughterHusband, Relationship.Father);
      Person sister = peopleHelper.addPerson("Linda Jefferson",
            Relationship.Sister, null);
      sister.setLocation(placeHelper.getPlace("38120"));
      sister.setPhoneNumber("615-334-7889");
      Person friend1 = peopleHelper.addPerson("Harriet Jones",
            Relationship.Friend, Gender.Female);
      friend1.setLocation(placeHelper.getPlace("02118"));
      friend1.setPhoneNumber("617-324-0997");
      Person friend2 = peopleHelper.addPerson("Marion Smith",
            Relationship.Friend, Gender.Female);
      friend2.setLocation(placeHelper.getPlace("02124"));
      friend2.setPhoneNumber("617-238-3779");
      Person friend3 = peopleHelper.addPerson("Philip Morley",
            Relationship.Friend, Gender.Male);
      friend3.setLocation(placeHelper.getPlace("33604"));
      friend3.setPhoneNumber("727-671-4536");
      System.out.println();
      System.out.println();
      System.out.println("Listing all people:");
      listPeople(program.getContainer().getComponent(Ontology.class),
            peopleHelper);
      UserModel model = program.getContainer().getComponent(UserModel.class);
      model.save();
      System.exit(0);
   }

   public static void listPeople (Ontology ontology,
         OntologyPeopleManager peopleHelper) {
      for (OntologyPerson person : peopleHelper.getPeople()) {
         System.out.print(person.getName());
         Gender gender = person.getGender();
         if ( gender != null )
            System.out.println(" (" + gender + ")");
         else
            System.out.println();
         for (Relationship relationship : Relationship.values()) {
            Set<OWLNamedIndividual> values = person.getIndividual()
                  .getObjectPropertyValues(relationship.name());
            if ( !values.isEmpty() ) {
               System.out.print("\t" + relationship.name() + " = ");
               for (OWLNamedIndividual value : values) {
                  OntologyPerson relatedPerson = peopleHelper
                        .getPerson(new OntologyIndividual(ontology, value));
                  System.out.print(relatedPerson.getName() + ", ");
               }
               System.out.println();
            }
         }
         MonthDay birthday = person.getBirthday();
         if ( birthday != null )
            System.out.println("\tBirthday = " + birthday.getMonthOfYear()
               + "/" + birthday.getDayOfMonth() + "/----");
         Place location = person.getLocation();
         if ( location != null )
            System.out.println("\tLocation = " + location.getCityName());
      }
   }
}
