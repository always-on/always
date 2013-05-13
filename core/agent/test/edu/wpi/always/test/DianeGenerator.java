package edu.wpi.always.test;

import org.joda.time.*;
import java.util.Collections;
import edu.wpi.always.Always;
import edu.wpi.always.user.*;
import edu.wpi.always.user.calendar.*;
import edu.wpi.always.user.people.*;
import edu.wpi.always.user.people.Person.Gender;
import edu.wpi.always.user.people.Person.Relationship;
import edu.wpi.always.user.places.PlaceManager;

public class DianeGenerator {

   public static void main (String[] args) {
      Always always = new Always(true);
      always.start();
      UserModel model = always.getContainer().getComponent(UserModel.class);
      generate(model);
      UserUtils.print(model, System.out);
      model.save();
   }
      
   public static void generate (UserModel userModel) {
      PeopleManager peopleMgr = userModel.getPeopleManager();
      PlaceManager placeMgr = userModel.getPlaceManager();
      Calendar calendar = userModel.getCalendar();
      peopleMgr.getUser().setLocation(placeMgr.getPlace("02118"));
      peopleMgr.getUser().setGender(Gender.Female);
      Person daughter = peopleMgr.addPerson("Ellen Lewis",
            Relationship.Daughter, null);
      daughter.setPhoneNumber("650-339-0221");
      daughter.setLocation(placeMgr.getPlace("92041"));
      Person daughterHusband = peopleMgr.addPerson("Mike", null, null);
      daughterHusband.addRelated(daughter, Relationship.Wife);
      Person linda = peopleMgr.addPerson("Linda", null, Gender.Female);
      linda.addRelated(daughterHusband, Relationship.Father);
      Person ed = peopleMgr.addPerson("Ed", null, Gender.Male);
      ed.addRelated(daughterHusband, Relationship.Father);
      Person sister = peopleMgr.addPerson("Linda Jefferson",
            Relationship.Sister, null);
      sister.setLocation(placeMgr.getPlace("38120"));
      sister.setPhoneNumber("615-334-7889");
      Person friend1 = peopleMgr.addPerson("Harriet Jones",
            Relationship.Friend, Gender.Female);
      friend1.setLocation(placeMgr.getPlace("02118"));
      friend1.setPhoneNumber("617-324-0997");
      Person friend2 = peopleMgr.addPerson("Marion Smith",
            Relationship.Friend, Gender.Female);
      friend2.setLocation(placeMgr.getPlace("02124"));
      friend2.setPhoneNumber("617-238-3779");
      Person friend3 = peopleMgr.addPerson("Philip Morley",
            Relationship.Friend, Gender.Male);
      friend3.setLocation(placeMgr.getPlace("33604"));
      friend3.setPhoneNumber("727-671-4536");      
      calendar.create(new CalendarEntryImpl(null,
               CalendarEntryTypeManager.Types.MedicalAppointment,
               null, null,
               new DateTime(2014,1, 1, 10, 0), Hours.hours(1)));
   }
}

