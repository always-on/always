package edu.wpi.always.user.owl;

import edu.wpi.always.user.calendar.*;
import edu.wpi.always.user.people.Person;
import edu.wpi.always.user.places.Place;
import edu.wpi.always.user.places.ZipCodes.ZipCodeEntry;

import org.joda.time.*;
import org.semanticweb.owlapi.model.*;
import java.util.*;

public class OntologyPerson implements Person {

   public static final String PERSON_CLASS = "Person";
   public static final String USER_CLASS = "User";
   public static final String FEMALE_PERSON_CLASS = "FemalePerson";
   public static final String MALE_PERSON_CLASS = "MalePerson";
   public static final String NAME_PROPERTY = "PersonName";
   public static final String BIRTHDAY_PROPERTY = "PersonBirthday";
   public static final String PHONE_NUMBER_PROPERTY = "PersonPhoneNumber";
   public static final String SKYPE_NUMBER_PROPERTY = "PersonSkypeNumber";
   public static final String AGE_PROPERTY = "PersonAge";
   public static final String SPOUSE_PROPERTY = "PersonSpouse";
   public static final String RELATION_PROPERTY = "PersonRelation";
   public static final String LIVES_IN_PROPERTY = "PersonLivesIn";
   private final OntologyHelper helper;
   private final OntologyIndividual owlPerson;
   private final OntologyUserModel model;

   OntologyPerson (Ontology ontology, OntologyUserModel model,
         OntologyIndividual owlPerson) {
      this.model = model;
      this.helper = new OntologyHelper(ontology);
      this.owlPerson = owlPerson;
   }

   @Override
   public String getName () {
      String name = owlPerson.getDataPropertyValue(NAME_PROPERTY).asString();
      if ( name == null )
         return helper.getName(owlPerson.getOWLIndividual());
      return name;
   }

   public OntologyIndividual getIndividual () {
      return owlPerson;
   }

   @Override
   public void setName (String name) {
      owlPerson.setDataProperty(NAME_PROPERTY, helper.getLiteral(name));
   }

   @Override
   public Person[] getRelated (Relationship relationship) {
      Set<OWLNamedIndividual> values = owlPerson.getObjectPropertyValues(relationship.name());
      if ( values.isEmpty() ) return null;
      else {
         Person[] related = new Person[values.size()];
         int i = 0;
         for (OWLNamedIndividual value : values) 
            related[i++] = model.getPeopleManager().getPerson(
                  new OntologyIndividual(helper.getOntologyDataObject(), value));
         return related;
      }
   }

   @Override
   public void addRelated (Person otherPerson, Relationship relationship) {
      owlPerson.addObjectProperty(relationship.name(),
            helper.getNamedIndividual(otherPerson.getName()));// TODO remove
      // dependancy on
      // helper
   }

   @Override
   public void setPhoneNumber (String number) {
      owlPerson.setDataProperty(PHONE_NUMBER_PROPERTY,
            (number != null) ? helper.getLiteral(number) : null);
   }

   @Override
   public String getPhoneNumber () {
      return owlPerson.getDataPropertyValue(PHONE_NUMBER_PROPERTY).asString();
   }

   @Override
   public void setSpouse (String spouse) {
      owlPerson.setDataProperty(SPOUSE_PROPERTY, 
            (spouse != null) ? helper.getLiteral(spouse) : null);
   }

   @Override
   public String getSpouse () {
      return owlPerson.getDataPropertyValue(SPOUSE_PROPERTY).asString();
   }

   @Override
   public void setSkypeNumber (String number) {
      owlPerson.setDataProperty(SKYPE_NUMBER_PROPERTY, 
            (number != null) ? helper.getLiteral(number) : null);
   }

   @Override 
   public String getSkypeNumber () {
      return owlPerson.getDataPropertyValue(SKYPE_NUMBER_PROPERTY).asString();
   }

   @Override
   public void setAge (String age){
      owlPerson.setDataProperty(AGE_PROPERTY, 
            (age != null) ? helper.getLiteral(age) : null);
   }

   @Override
   public String getAge () {
      return owlPerson.getDataPropertyValue(AGE_PROPERTY).asString();
   }

   @Override
   public void setRelationship(Relationship relationship){
      owlPerson.setDataProperty(RELATION_PROPERTY, 
            (relationship != null)? helper.getLiteral(relationship.name()) : null);
   }

   @Override
   public String getRelationship () {
      return owlPerson.getDataPropertyValue(RELATION_PROPERTY).asString();
   }

   @Override
   public void setGender (Gender gender) {
      List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
      if ( gender == Gender.Male )
         owlPerson.addSuperclass(MALE_PERSON_CLASS);
      else
         owlPerson.removeSuperclass(MALE_PERSON_CLASS);
      if ( gender == Gender.Female )
         owlPerson.addSuperclass(FEMALE_PERSON_CLASS);
      else
         owlPerson.removeSuperclass(FEMALE_PERSON_CLASS);
      helper.applyChanges(changes);
   }

   @Override
   public Gender getGender () {
      if ( owlPerson.hasSuperclass(FEMALE_PERSON_CLASS) )
         return Gender.Female;
      if ( owlPerson.hasSuperclass(MALE_PERSON_CLASS) )
         return Gender.Male;
      return null;
   }

   @Override
   public MonthDay getBirthday () {
      return owlPerson.getDataPropertyValue(BIRTHDAY_PROPERTY).asMonthDay();
   }

   @Override
   public void setBirthday (MonthDay day) {
      owlPerson.setDataProperty(BIRTHDAY_PROPERTY, (day != null) ? helper.getLiteral(day) : null);
      /**
		CalendarEntry birthdayEntry = null;
		for (CalendarEntry entry : model.getCalendar().retrieve(null)) {
			if ( entry.getType().equals(CalendarEntryTypeManager.Types.Birthday) ) {
				if ( entry.getPeople().size() == 1
						&& entry.getPeople().iterator().next().equals(this) )
					birthdayEntry = entry;
			}
		}
		// TODO handle repeating event
		if ( birthdayEntry == null ) {
			birthdayEntry = new CalendarEntryImpl(null,
					CalendarEntryTypeManager.Types.Birthday,
					Collections.singleton((Person) this), null,
					day.toDateTime(new DateMidnight()), Hours.hours(24));
			model.getCalendar().create(birthdayEntry);
		} else {
			birthdayEntry.setDuration(Hours.hours(24));
			birthdayEntry.setStart(day.toDateTime(new DateMidnight()));
			model.getCalendar().update(birthdayEntry, true);
		}
       */
   }

   @Override
   public Place getLocation () {
      return model.getPlaceManager().getPlace(
            owlPerson.getObjectPropertyValue(LIVES_IN_PROPERTY));
   }

   @Override
   public void setLocation (Place place) {
      owlPerson.setObjectProperty(LIVES_IN_PROPERTY, (place != null) ? 
            model.getPlaceManager().getPlace(place.getZip()).getIndividual() : null);
   }

   @Override
   public String toString () { return getName(); }

   @Override
   public int hashCode () {
      return getName().hashCode();
   }

   @Override
   public boolean equals (Object obj) {
      if ( this == obj )
         return true;
      if ( obj == null )
         return false;
      if ( !(obj instanceof Person) )
         return false;
      Person other = (Person) obj;
      return other.getName().equals(getName());
   }

}
