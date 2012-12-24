package edu.wpi.always.user.calendar;

import edu.wpi.always.user.calendar.RepeatingCalendarEntry.Frequency;
import edu.wpi.always.user.people.Person;
import java.util.Set;

public class CalendarEntryTypeManager {

   public static CalendarEntryType forName (String name) {
      for (Types type : Types.values()) {
         if ( type.name().equals(name) )
            return type;
      }
      return new DefaultType(name);
   }

   public static enum Types implements CalendarEntryType {
      Birthday("Birthday") {

         @Override
         public String getTitle (CalendarEntry entry) {
            Set<Person> people = entry.getPeople();
            if ( people.size() == 1 ) {
               String personsName = people.iterator().next().getName();
               return personsName + "'s Birthday";
            }
            return "Birthday";
         }

         @Override
         public String getPersonQuestion () {
            return "Whose birthday is it?";
         }

         @Override
         public void prefill (RepeatingCalendarEntry newEntry) {
            newEntry.setRepeat(Frequency.YEARLY);
         }
      },
      MedicalAppointment("Medical Appointment") {

         @Override
         public String getTitle (CalendarEntry entry) {
            Set<Person> people = entry.getPeople();
            if ( people.size() == 1 ) {
               String personsName = people.iterator().next().getName();
               return "Medical Appointment with " + personsName;
            }
            return "Medical Appointment";
         }

         @Override
         public String getPersonQuestion () {
            return "Who is the Medical Appointment with?";
         }
      },
      Meal("Meal") {

         @Override
         public String getTitle (CalendarEntry entry) {
            Set<Person> people = entry.getPeople();
            if ( people.size() == 1 ) {
               String personsName = people.iterator().next().getName();
               return "Meal with " + personsName;
            }
            return "Meal";
         }

         @Override
         public String getPersonQuestion () {
            return "Who is the Meal with?";
         }
      },
      Coffee("Coffee") {

         @Override
         public String getTitle (CalendarEntry entry) {
            Set<Person> people = entry.getPeople();
            if ( people.size() == 1 ) {
               String personsName = people.iterator().next().getName();
               return "Coffee with " + personsName;
            }
            return "Coffee";
         }

         @Override
         public String getPersonQuestion () {
            return "Who are you haveing coffee with?";
         }
      },
      ReligiousService("Religious Service"), CommunityMeeting(
            "Community Meeting"), PhoneCall("Phone Call") {

         @Override
         public String getTitle (CalendarEntry entry) {
            Set<Person> people = entry.getPeople();
            if ( people.size() == 1 ) {
               String personsName = people.iterator().next().getName();
               return "Phone Call with " + personsName;
            }
            return "Phone Call";
         }

         @Override
         public String getPersonQuestion () {
            return "Who are you talking to on the phone?";
         }
      },
      Reminder("Reminder"), Party("Party"), VisitFrom("Visit") {

         @Override
         public String getTitle (CalendarEntry entry) {
            Set<Person> people = entry.getPeople();
            if ( people.size() == 1 ) {
               String personsName = people.iterator().next().getName();
               return "Visit from " + personsName;
            }
            return "Visit";
         }

         @Override
         public String getPersonQuestion () {
            return "Who is visiting you?";
         }
      },
      TravelTo("Travel");

      private final String name;

      private Types (String name) {
         this.name = name;
      }

      @Override
      public String getTitle (CalendarEntry entry) {
         return name;
      }

      @Override
      public String getDisplayName () {
         return name;
      }

      @Override
      public String getId () {
         return name();
      }

      @Override
      public String getPersonQuestion () {
         return null;
      }

      @Override
      public void prefill (RepeatingCalendarEntry newEntry) {
      }

      @Override
      public void prefill (CalendarEntry newEntry) {
      }
   }

   public static class DefaultType implements CalendarEntryType {

      private String name;

      public DefaultType (String name) {
         this.name = name;
      }

      @Override
      public String getTitle (CalendarEntry entry) {
         return name;
      }

      @Override
      public String getId () {
         return name;
      }

      @Override
      public String getDisplayName () {
         return name;
      }

      @Override
      public String getPersonQuestion () {
         return null;
      }

      @Override
      public void prefill (RepeatingCalendarEntry newEntry) {
      }

      @Override
      public void prefill (CalendarEntry newEntry) {
      }
   }
}
