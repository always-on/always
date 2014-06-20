package edu.wpi.always.enroll.schema;

import edu.wpi.always.client.KeyboardAdjacencyPair;
import edu.wpi.always.enroll.EnrollPlugin;
import edu.wpi.always.enroll.schema.ErrorCheckState.CheckCorrectionAdjacencyPair;
import edu.wpi.always.user.UserUtils;
import edu.wpi.always.user.people.*;
import edu.wpi.always.user.people.Person.Gender;
import edu.wpi.always.user.people.Person.Relationship;
import edu.wpi.always.user.places.*;
import edu.wpi.always.user.places.ZipCodes.StateEntry;
import edu.wpi.always.user.places.ZipCodes.ZipCodeEntry;
import edu.wpi.disco.rt.menu.*;
import org.joda.time.MonthDay;
import java.util.List;

public abstract class EnrollAdjacencyPairs{

   protected static String name, skype;
   protected static int age;
   protected static Relationship relationship;
   protected static MonthDay personBirthday;
   protected static int Month, Day;
   protected static Gender gender;
   protected static Place location;
   protected static Person person, spouse;
   protected static String personState;
   
   private static void addPerson (EnrollStateContext context) {
      person = context.getPeopleManager().addPerson(name, relationship, gender, 
               age, skype, location, spouse, personBirthday);
      context.getUserModel().setProperty(EnrollPlugin.PERFORMED, true);
   }

   public static class PersonNameAdjacencyPair extends 
   KeyboardAdjacencyPair<EnrollStateContext> {

      public PersonNameAdjacencyPair(final EnrollStateContext context) {
         super("Tell me the person's name", 
               "Enter name:", context, context.getKeyboard());
         age = Month = Day = 0;
         name = skype = personState = null;
         relationship = null;
         personBirthday = null;
         gender = null; 
         location = null;
         person = spouse = null;
      }

      @Override
      public AdjacencyPair success(String text) {
         if(!text.isEmpty()){
            name = text;
            return new PersonAgeAdjacencyPair(getContext());
         }
         return new PersonNameInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new InitialEnroll(getContext());
      }

   }
   
   public static class PersonNameInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public PersonNameInvalidAdjacencyPair(final EnrollStateContext context) {
         super("You must enter a name, even a nickname will do", 
               "Enter name:", context, context.getKeyboard());
      }

      @Override
      public AdjacencyPair success(String text) {
         if(!text.isEmpty()){
            name = text;
            return new PersonAgeAdjacencyPair(getContext());
         }
         return new PersonNameInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new InitialEnroll(getContext());
      }  
   }

   public static class PersonAgeAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public PersonAgeAdjacencyPair(final EnrollStateContext context) {
         super("What is " + name + "'s age", "Enter "+ name+ "'s age:", 
               context, context.getKeyboard(), true);
         choice("Cancel providing information on "+ name, new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               getContext().hideKeyboard();
               return new InitialEnroll(getContext());
            }
         });
      }

      @Override
      public AdjacencyPair success(String text) {
         if(UserUtils.isInteger(text)){
            getContext().hideKeyboard();
            age = Integer.parseInt(text);
            return new TellPersonBirthdayAdjacencyPair(getContext());
         }
         return new PersonAgeInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new TellPersonBirthdayAdjacencyPair(getContext());
      }	
   }

   public static class PersonAgeInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public PersonAgeInvalidAdjacencyPair(final EnrollStateContext context) {
         super("The age you entered is invalid. Please enter it again", 
               "Enter valid "+ name + "'s age:", context, context.getKeyboard(), true);
         choice("Cancel entering information about " + name, new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               getContext().hideKeyboard();
               return new InitialEnroll(getContext());
            }
         });
      }

      @Override
      public AdjacencyPair success(String text) {
         if(UserUtils.isInteger(text)){
            getContext().hideKeyboard();
            age = Integer.parseInt(text);
            return new TellPersonBirthdayAdjacencyPair(getContext());
         }
         return new PersonAgeInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new TellPersonBirthdayAdjacencyPair(getContext());
      }	
   }

   public static class TellPersonBirthdayAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public TellPersonBirthdayAdjacencyPair(final EnrollStateContext context) {
         super("Do you know " + name + "'s birthday ?", context);
         choice("Yes", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new PersonBirthdayMonthAdjacencyPair(getContext());
            }
         });
         choice("No", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new PersonGenderAdjacencyPair(getContext());
            }
         });
         choice("Skip this information", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new PersonGenderAdjacencyPair(getContext());
            }
         });
      }
   }

   public static class PersonBirthdayMonthAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public PersonBirthdayMonthAdjacencyPair(final EnrollStateContext context) {
         super("What is " + name + "'s birthday month ?", context, true);
         for(int i = 0; i < 12; i++) {
            final int MonthNum = i;
            choice(Person.Month[i], new DialogStateTransition() {
               @Override
               public AdjacencyPair run() {
                  Month = MonthNum + 1;
                  return new PersonBirthdayDayAdjacencyPair(getContext());
               }
            });
         }
         choice("Skip this information", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new PersonGenderAdjacencyPair(getContext());
            }
         });
         choice("Cancel entering information on "+name, new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               getContext().hideKeyboard();
               return new InitialEnroll(getContext());
            }
         });
      }
   }

   public static class PersonBirthdayDayAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public PersonBirthdayDayAdjacencyPair(final EnrollStateContext context) {
         super("What is the date of " + name + "'s Birthday ? ", 
               "Enter "+ name+ "'s Birthday:", 
               context, context.getKeyboard(), true);
         
         choice("Cancel entering information on "+name, new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               getContext().hideKeyboard();
               return new InitialEnroll(getContext());
            }
         });
      }

      @Override
      public AdjacencyPair success(String text) {
         int day = Integer.parseInt(text);
         if(UserUtils.isValidDayOfMonth(Month, day)){
            getContext().hideKeyboard();
            Day = day;
            personBirthday = new MonthDay(Month, Day);
            return new PersonGenderAdjacencyPair(getContext());
         }
         return new PersonBirthdayDayInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new PersonGenderAdjacencyPair(getContext());
      }	
   }

   public static class PersonBirthdayDayInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public PersonBirthdayDayInvalidAdjacencyPair(final EnrollStateContext context) {
         super("The date you entered is invalid. Please enter the date again", 
               "Enter valid "+ name+ "'s birthday:", context, context.getKeyboard(), true);
         choice("Skip "+name, new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               getContext().hideKeyboard();
               return new InitialEnroll(getContext());
            }
         });
      }

      @Override
      public AdjacencyPair success(String text) {
         int day = Integer.parseInt(text);
         if(UserUtils.isValidDayOfMonth(Month, day)){
            getContext().hideKeyboard();
            Day = day;
            personBirthday = new MonthDay(Month, Day);
            return new PersonGenderAdjacencyPair(getContext());
         }
         return new PersonBirthdayDayInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new PersonGenderAdjacencyPair(getContext());
      }	
   }

   public static class PersonGenderAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public PersonGenderAdjacencyPair(final EnrollStateContext context) {
         super("What is " + name + "'s gender ?", context);
         choice("Male", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               gender = Gender.Male;
               return new KnowZipCodeAdjacencyPair(getContext());
            }
         });
         choice("Female", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               gender = Gender.Female;
               return new KnowZipCodeAdjacencyPair(getContext());
            }
         });
         choice("Skip this information", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new KnowZipCodeAdjacencyPair(getContext());
            }
         });
         choice("Cancel entering information on "+name, new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               getContext().hideKeyboard();
               return new InitialEnroll(getContext());
            }
         });
      }
   }

   public static class KnowZipCodeAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public KnowZipCodeAdjacencyPair(final EnrollStateContext context) {
         super("Do you know " + name + "'s ZipCode ?", context);
         choice("Yes", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new PersonZipCodeAdjacencyPair(getContext());
            }
         });
         choice("No", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new PersonStateAdjacencyPair(getContext());
            }
         });
         choice("Cancel entering information on "+name, new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new InitialEnroll(getContext());
            }
         });
      }
   }

   public static class PersonZipCodeAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public PersonZipCodeAdjacencyPair(final EnrollStateContext context) {
         super("Please tell me " + name + "'s zipcode ?", "Enter " +name+ "'s zipcode:",
               context, context.getKeyboard(), true);
         choice("Skip "+name, new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               getContext().hideKeyboard();
               return new InitialEnroll(getContext());
            }
         });
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         ZipCodeEntry zip = zipcodes.getPlaceData(text);
         if(zip != null){
            location = getContext().getPlaceManager().getPlace(zip.getZip());
            getContext().hideKeyboard();
            return new PersonRelationshipAdjacencyPair(getContext());
         }
         return new ZipCodeInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new PersonRelationshipAdjacencyPair(getContext());
      }
   }

   public static class ZipCodeInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public ZipCodeInvalidAdjacencyPair(final EnrollStateContext context) {
         super("I think that zipcode seems invalid. Can you please try again?", 
               "Enter " +name +"'s zipcode again:", context, context.getKeyboard(), true);
         choice("Skip "+name, new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               getContext().hideKeyboard();
               return new InitialEnroll(getContext());
            }
         });
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         ZipCodeEntry zip = zipcodes.getPlaceData(text);
         if(zip != null){
            location = getContext().getPlaceManager().getPlace(zip.getZip());
            getContext().hideKeyboard();
            return new PersonRelationshipAdjacencyPair(getContext());
         }
         return new ZipCodeInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new PersonRelationshipAdjacencyPair(getContext());
      }
   }

   public static class PersonStateAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public PersonStateAdjacencyPair(final EnrollStateContext context) {
         super("Which state does " + name + " live in ?", "Enter " +name+ "'s state:",
               context, context.getKeyboard());
         choice("Skip "+name, new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               getContext().hideKeyboard();
               return new InitialEnroll(getContext());
            }
         });
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         StateEntry state = zipcodes.getState(text);
         if(state != null){
            personState = state.getStateAbbrev().get(0);
            return new PersonCityAdjacencyPair(getContext());
         }
         return new StateInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new PersonRelationshipAdjacencyPair(getContext());
      }
   }

   public static class StateInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public StateInvalidAdjacencyPair(final EnrollStateContext context) {
         super("Sorry, but that state name does not sound right, "
            + "Could you please try again?", "Please enter valid state name:",
               context, context.getKeyboard());
         choice("Skip "+name, new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               getContext().hideKeyboard();
               return new InitialEnroll(getContext());
            }
         });
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         StateEntry state = zipcodes.getState(text);
         if(state != null){
            personState = state.getStateAbbrev().get(0);
            return new PersonCityAdjacencyPair(getContext());
         }
         return new StateInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new PersonRelationshipAdjacencyPair(getContext());
      }
   }

   public static class PersonCityAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public PersonCityAdjacencyPair(final EnrollStateContext context) {
         super("Which city does " + name + " live in?", 
               "Enter " + name + "'s city:",
               context, context.getKeyboard());
         choice("Skip "+name, new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               getContext().hideKeyboard();
               return new InitialEnroll(getContext());
            }
         });
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         List<ZipCodeEntry> cities = zipcodes.getCityData(text);
         for(ZipCodeEntry city : cities){
            if(city.getState().equals(personState)){
               getContext().hideKeyboard();
               location = getContext().getPlaceManager().getPlace(city.getZip());
               return new PersonRelationshipAdjacencyPair(getContext());
            }
         }
         return new CityInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         StateEntry state = zipcodes.getState(personState);
         location = getContext().getPlaceManager().getPlace(state.getCapitalZip());
         return new PersonRelationshipAdjacencyPair(getContext());
      }
   }

   public static class CityInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public CityInvalidAdjacencyPair(final EnrollStateContext context) {
         super("That city name seems invalid.  Please enter it again here", 
               
               "Invalid city name. Plase re-enter " + name + "'s city:",
               context, context.getKeyboard());
         choice("Skip "+name, new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               getContext().hideKeyboard();
               return new InitialEnroll(getContext());
            }
         });
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         List<ZipCodeEntry> cities = zipcodes.getCityData(text);
         for(ZipCodeEntry city : cities){
            if(city.getState().equals(personState)){
               getContext().hideKeyboard();
               location = getContext().getPlaceManager().getPlace(city.getZip());
               return new PersonRelationshipAdjacencyPair(getContext());
            }
         }
         getContext().hideKeyboard();
         StateEntry state = zipcodes.getState(personState);
         location = getContext().getPlaceManager().getPlace(state.getCapitalZip());
         return new PersonRelationshipAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         StateEntry state = zipcodes.getState(personState);
         location = getContext().getPlaceManager().getPlace(state.getCapitalZip());
         return new PersonRelationshipAdjacencyPair(getContext());
      }
   }


   public static class PersonRelationshipAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public PersonRelationshipAdjacencyPair(final EnrollStateContext context) {
         super("Who is " + name +  " to you? "
            + "For example, is " + name + " your cousin or your friend?", context, true);
         choice("Friend", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               relationship = Relationship.Friend;
               return new PersonSpouseAdjacencyPair(getContext());
            }
         });
         choice("Sister", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               relationship = Relationship.Sister;
               return new PersonSpouseAdjacencyPair(getContext());
            }
         });
         choice("Brother", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               relationship = Relationship.Brother;
               return new PersonSpouseAdjacencyPair(getContext());
            }
         });
         choice("Mother", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               relationship = Relationship.Mother;
               return new PersonSpouseAdjacencyPair(getContext());
            }
         });
         choice("Father", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               relationship = Relationship.Father;
               return new PersonSpouseAdjacencyPair(getContext());
            }
         });
         choice("Daughter", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               relationship = Relationship.Daughter;
               return new PersonSpouseAdjacencyPair(getContext());
            }
         });
         choice("Son", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               relationship = Relationship.Son;
               return new PersonSpouseAdjacencyPair(getContext());
            }
         });
         choice("Granddaughter", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               relationship = Relationship.Granddaughter;
               return new PersonSpouseAdjacencyPair(getContext());
            }
         });
         choice("Grandson", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               relationship = Relationship.Grandson;
               return new PersonSpouseAdjacencyPair(getContext());
            }
         });
         choice("Aunt", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               relationship = Relationship.Aunt;
               return new PersonSpouseAdjacencyPair(getContext());
            }
         });
         choice("Uncle", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               relationship = Relationship.Uncle;
               return new PersonSpouseAdjacencyPair(getContext());
            }
         });
         choice("Cousin", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               relationship = Relationship.Cousin;
               return new PersonSpouseAdjacencyPair(getContext());
            }
         });
         choice("Skip this information", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new PersonSpouseAdjacencyPair(getContext());
            }
         });
         choice("Cancel entering information on "+name, new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               getContext().hideKeyboard();
               return new InitialEnroll(getContext());
            }
         });
      }
   }

   public static class PersonSpouseAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public PersonSpouseAdjacencyPair(final EnrollStateContext context){
         super("Is " + name + " married?", context);
         choice("Yes", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new EnterSpouseAdjacencyPair(getContext());
            }
         });
         choice("No", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new PersonContactAdjacencyPair(getContext());
            }
         });
         choice("Skip this information", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new PersonContactAdjacencyPair(getContext());
            }
         });
         choice("Cancel entering information on "+name, new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               getContext().hideKeyboard();
               return new InitialEnroll(getContext());
            }
         });
      }
   }

   public static class EnterSpouseAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public EnterSpouseAdjacencyPair(final EnrollStateContext context){
         super("What is " + name + "'s spouse's name ?", "Enter " +name +"'s spouse name:", 
               context, context.getKeyboard());
         choice("Cancel entering information on "+name, new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               getContext().hideKeyboard();
               return new InitialEnroll(getContext());
            }
         });
      }

      @Override
      public AdjacencyPair success(String text) {
         getContext().hideKeyboard();
         spouse = getContext().getPeopleManager().getPerson(text);
         return new PersonContactAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new PersonContactAdjacencyPair(getContext());
      }
   }

   public static class PersonContactAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public PersonContactAdjacencyPair(final EnrollStateContext context) {
         super("Do you plan to talk to this person by video calls ?", context);
         choice("Yes", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new SkypeNameAdjacencyPair(getContext());
            }
         });
         choice("No", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               addPerson(getContext());
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
         choice("Cancel entering information on "+name, new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new InitialEnroll(getContext());
            }
         });
      }
   }

   public static class SkypeNameAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public SkypeNameAdjacencyPair(final EnrollStateContext context) {
         super("What is " + name + "'s video call name ?", "Enter " + name + "'s video call name:", 
               context, context.getKeyboard());

	 choice("I do not know", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               addPerson(getContext());
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            } 
            } );
  
	 
         choice("Cancel entering information on "+name, new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               getContext().hideKeyboard();
               return new InitialEnroll(getContext());
            }
         } );
      }

      @Override
      public AdjacencyPair success(String text) {
         getContext().hideKeyboard();
         skype = text;
         addPerson(getContext());
         return new CheckCorrectionAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         addPerson(getContext());
         return new CheckCorrectionAdjacencyPair(getContext(), person);
      }
   }
}
