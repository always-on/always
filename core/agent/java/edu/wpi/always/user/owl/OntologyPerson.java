package edu.wpi.always.user.owl;

import java.util.*;

import org.joda.time.*;
import org.semanticweb.owlapi.model.*;

import edu.wpi.always.user.calendar.*;
import edu.wpi.always.user.people.*;
import edu.wpi.always.user.places.*;

public class OntologyPerson implements Person {

	public static final String PERSON_CLASS = "Person";
	public static final String USER_CLASS = "User";
	public static final String FEMALE_PERSON_CLASS = "FemalePerson";
	public static final String MALE_PERSON_CLASS = "MalePerson";
	
	public static final String NAME_PROPERTY = "PersonName";
	public static final String BIRTHDAY_PROPERTY = "PersonBirthday";
	public static final String LIVES_IN_PROPERTY = "PersonLivesIn";
	public static final String PHONE_NUMBER_PROPERTY = "PersonPhoneNumber";

	private final OntologyHelper helper;
	private final OntologyIndividual owlPerson;
	private final OntologyUserModel model;

	OntologyPerson(Ontology ontology, OntologyUserModel model, OntologyIndividual owlPerson) {
		this.model = model;
		this.helper = new OntologyHelper(ontology);
		this.owlPerson = owlPerson;
	}

	@Override
	public String getName() {
		String name = owlPerson.getDataPropertyValue(NAME_PROPERTY).asString();
		if(name==null)
			return helper.getName(owlPerson.getOWLIndividual());
		return name;
	}
	
	public OntologyIndividual getIndividual(){
		return owlPerson;
	}

	public void setName(String name) {
		owlPerson.setDataProperty(NAME_PROPERTY, helper.getLiteral(name));
	}

	@Override
	public void addRelationship(Person otherPerson, Relationship relationship) {
		owlPerson.addObjectProperty(relationship.name(), helper.getNamedIndividual(otherPerson.getName()));//TODO remove dependancy on helper
	}

	@Override
	public void setPhoneNumber(String number) {
		owlPerson.setDataProperty(PHONE_NUMBER_PROPERTY, helper.getLiteral(number));
	}
	@Override
	public String getPhoneNumber() {
		return owlPerson.getDataPropertyValue(PHONE_NUMBER_PROPERTY).asString();
	}

	@Override
	public void setGender(Gender gender) {
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		if (gender == Gender.Male)
			owlPerson.addSuperclass(MALE_PERSON_CLASS);
		else
			owlPerson.removeSuperclass(MALE_PERSON_CLASS);
		if (gender == Gender.Female)
			owlPerson.addSuperclass(FEMALE_PERSON_CLASS);
		else
			owlPerson.removeSuperclass(FEMALE_PERSON_CLASS);
		helper.applyChanges(changes);
	}

	@Override
	public Gender getGender() {
		if(owlPerson.hasSuperclass(FEMALE_PERSON_CLASS))
			return Gender.Female;
		if(owlPerson.hasSuperclass(MALE_PERSON_CLASS))
			return Gender.Male;
		return null;
		
	}

	public MonthDay getBirthday() {
		return owlPerson.getDataPropertyValue(BIRTHDAY_PROPERTY).asMonthDay();
	}
	
	public Place getLocation(){
		return model.getPlaceManager().getPlace(owlPerson.getObjectPropertyValue(LIVES_IN_PROPERTY));
	}
	
	public void setLocation(Place place){
		owlPerson.setObjectProperty(LIVES_IN_PROPERTY, model.getPlaceManager().getPlace(place.getZip()).getIndividual());
	}

	@Override
	public void setBirthday(MonthDay day) {
		CalendarEntry birthdayEntry = null;
		for (CalendarEntry entry : model.getCalendar().retrieve(null)) {
			if (entry.getType().equals(CalendarEntryTypeManager.Types.Birthday)) {
				if (entry.getPeople().size() == 1 && entry.getPeople().iterator().next().equals(this))
					birthdayEntry = entry;
			}
		}
		// TODO handle repeating event
		if (birthdayEntry == null) {
			birthdayEntry = new CalendarEntryImpl(null, CalendarEntryTypeManager.Types.Birthday, Collections.singleton((Person) this),
					null, day.toDateTime(new DateMidnight()), Hours.hours(24));
			model.getCalendar().create(birthdayEntry);
		}
		else {
			birthdayEntry.setDuration(Hours.hours(24));
			birthdayEntry.setStart(day.toDateTime(new DateMidnight()));
			model.getCalendar().update(birthdayEntry, true);
		}
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Person))
			return false;
		Person other = (Person) obj;
		return other.getName().equals(getName());
	}
}
