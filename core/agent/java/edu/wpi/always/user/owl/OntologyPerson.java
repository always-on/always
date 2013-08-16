package edu.wpi.always.user.owl;

import java.util.*;
import org.joda.time.*;
import org.semanticweb.owlapi.model.*;
import edu.wpi.always.user.UserModelBase;
import edu.wpi.always.user.calendar.*;
import edu.wpi.always.user.people.Person;
import edu.wpi.always.user.places.Place;

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
      UserModelBase.saveIf();
   }

   @Override
   public Person[] getRelated (Relationship relationship) {
      Set<OWLNamedIndividual> values = owlPerson
            .getObjectPropertyValues(relationship.name());
      if ( values.isEmpty() )
         return null;
      else {
         Person[] related = new Person[values.size()];
         int i = 0;
         for (OWLNamedIndividual value : values)
            related[i++] = model.getPeopleManager()
                  .getPerson(
                        new OntologyIndividual(helper.getOntologyDataObject(),
                              value));
         return related;
      }
   }
   
   @Override
   public Person getSpouse () {
      Person[] spouses = getRelated(Relationship.Spouse);
      return spouses == null ? null : spouses[0]; // ignore polygamy
   }

   @Override
   public void addRelated (Person otherPerson, Relationship relationship) {
      owlPerson.addObjectProperty(relationship.name(),
            helper.getNamedIndividual(otherPerson.getName()));
      UserModelBase.saveIf();
   }

   @Override
   public void setPhoneNumber (String number) {
      owlPerson.setDataProperty(PHONE_NUMBER_PROPERTY, (number != null)
         ? helper.getLiteral(number) : null);
      UserModelBase.saveIf();
   }

   @Override
   public String getPhoneNumber () {
      return owlPerson.getDataPropertyValue(PHONE_NUMBER_PROPERTY).asString();
   }

   @Override
   public void setSkypeNumber (String number) {
      owlPerson.setDataProperty(SKYPE_NUMBER_PROPERTY, (number != null)
         ? helper.getLiteral(number) : null);
      UserModelBase.saveIf();
   }

   @Override
   public String getSkypeNumber () {
      return owlPerson.getDataPropertyValue(SKYPE_NUMBER_PROPERTY).asString();
   }

   @Override
   public void setAge (int age) {
      owlPerson.setDataProperty(AGE_PROPERTY, helper.getLiteral(age));
      UserModelBase.saveIf();
   }

   @Override
   public int getAge () {
      return owlPerson.getDataPropertyValue(AGE_PROPERTY).asInteger();
   }

   @Override
   public void setRelationship (Relationship relationship) {
      addRelated(model.getPeopleManager().getUser(), relationship);
   }

   @Override
   public Relationship getRelationship () {
      OntologyIndividual user = model.getPeopleManager().getUser().owlPerson;
      for (Relationship r : Relationship.values())
         // TODO Prefer most specific to first one found
         if ( owlPerson.getObjectPropertyValue(r.name()) == user )
            return r;
      return null;
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
      UserModelBase.saveIf();
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
      UserModelBase.saveIf();
   }

   @Override
   public Place getLocation () {
      return model.getPlaceManager().getPlace(
            owlPerson.getObjectPropertyValue(LIVES_IN_PROPERTY));
   }

   @Override
   public void setLocation (Place place) {
      owlPerson.setObjectProperty(LIVES_IN_PROPERTY, 
            (place != null) ? 
               model.getPlaceManager().getPlace(place.getZip()).getIndividual() 
               : null);
      UserModelBase.saveIf();
   }

   @Override
   public String toString () {
      return getName();
   }

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

   public static final String
         ABOUT_STATUS_PROPERTY = "PersonAboutStatus",
         ABOUT_COMMENT_PROPERTY = "PersonAboutComment",
         ABOUT_MENTIONED_PROPERTY = "PersonAboutMentioned";
   
   @Override
   public void setAboutStatus (AboutStatus status) {
      owlPerson.setDataProperty(ABOUT_STATUS_PROPERTY, 
            (status != null) ? helper.getLiteral(status.name()) : null);
      UserModelBase.saveIf();
   }

   @Override
   public AboutStatus getAboutStatus () {
      String status = owlPerson.getDataPropertyValue(ABOUT_STATUS_PROPERTY).asString();
      return status == null ? null : AboutStatus.valueOf(status);
   }
 
   @Override
   public void setAboutComment (String comment) {
      owlPerson.setDataProperty(ABOUT_COMMENT_PROPERTY, (comment != null)
         ? helper.getLiteral(comment) : null);
      UserModelBase.saveIf();
   }

   @Override
   public String getAboutComment () {
      return owlPerson.getDataPropertyValue(ABOUT_COMMENT_PROPERTY).asString();
   }

   @Override
   public void setAboutMentioned (boolean mentioned) {
      owlPerson.setDataProperty(ABOUT_MENTIONED_PROPERTY, helper.getLiteral(mentioned));
      UserModelBase.saveIf();
   }

   @Override
   public boolean isAboutMentioned () {
      return owlPerson.getDataPropertyValue(ABOUT_MENTIONED_PROPERTY).asBoolean();
   }
}
