package edu.wpi.always.user.people;

import edu.wpi.always.user.people.Person.*;

public interface PeopleManager {
	
	public Person getUser();

	public Person addPerson(String name, Relationship relationship, Gender gender);

	public Person getPerson(String name);

	public Person[] getPeople();
}
