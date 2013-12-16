package edu.wpi.always.enroll.schema;

import edu.wpi.always.client.KeyboardAdjacencyPair;
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

public class EditPersonState extends EnrollAdjacencyPairs{

   public static class EditPersonAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> { 
     
      Person person;
     
      public EditPersonAdjacencyPair(final EnrollStateContext context, final Person person){
         super("Here is the previous information about this person", context, true);
         this.person = person;
         choice("Edit name", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new ChangeNameAdjacencyPair(getContext(), person);
            }
         });
         choice("Edit Birthday", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new TellChangeBirthdayAdjacencyPair(getContext(), person);
            }
         });
         choice("Edit Zipcode", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new IfKnowZipCodeAdjacencyPair(getContext(), person);
            }
         });
         choice("Edit Relation", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new ChangeRelationAdjacencyPair(getContext(), person);
            }
         });
         choice("Edit Age", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new ChangeAgeAdjacencyPair(getContext(), person);
            }
         });
         choice("Edit Gender", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new ChangeGenderAdjacencyPair(getContext(), person);
            }
         });
         choice("Edit Spouse.", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new ChangeSpouseAdjacencyPair(getContext(), person);
            }
         });
         choice("Edit Skype", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new ChangeSkypeAdjacencyPair(getContext(), person);
            }
         });
         choice("Edit Phone", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new ChangePhoneAdjacencyPair(getContext(), person);
            }
         });
         choice("Done Edit", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               getContext().hideEnrollUI();
               return new InitialEnroll(getContext());
            }
         });
      }
      @Override
      public void enter() {
         getContext().getEnrollUI().showCurrentEntry(person);
      }
   }

   public static class ChangeNameAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      private Person person;

      public ChangeNameAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("Please enter the new name", "New name:", context, context.getKeyboard());
         this.person = person;
      }

      @Override
      public AdjacencyPair success(String text) {
         getContext().hideKeyboard();
         person.setName(text);
         return new EditPersonAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new EditPersonAdjacencyPair(getContext(), person);
      }
   }

   public static class TellChangeBirthdayAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public TellChangeBirthdayAdjacencyPair(final EnrollStateContext context, 
            final Person person) {
         super("Do you want to provide " + person.getName() + "'s birthday?", context);
         choice("Yes", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new ChangeBirthdayMonthAdjacencyPair(getContext(), person);
            }
         });
         choice("No", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               person.setBirthday(null);
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
         choice("Never Mind", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               person.setBirthday(null);
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
      }
   }

   public static class ChangeBirthdayMonthAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public ChangeBirthdayMonthAdjacencyPair(final EnrollStateContext context,
            final Person person) {
         super("What is " + person.getName() + "s birthday month ?", context, true);
         for(int i = 0; i < 12; i++) {
            final int MonthNum = i;
            choice(Person.Month[i], new DialogStateTransition() {
               @Override
               public AdjacencyPair run() {
                  Month = MonthNum + 1;
                  return new ChangeBirthdayDayAdjacencyPair(getContext(), person);
               }
            });
         }
         choice("Never Mind", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               person.setBirthday(null);
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
      }
   }

   public static class ChangeBirthdayDayAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public ChangeBirthdayDayAdjacencyPair(final EnrollStateContext context,
            final Person person) {
         super("What is the date of " + person.getName() + "'s Birthday ?", 
               "Enter "+ person.getName() + "'s Birthday:", 
               context, context.getKeyboard(), true);
      }

      @Override
      public AdjacencyPair success(String text) {
         int day = Integer.parseInt(text);
         if(UserUtils.isValidDayOfMonth(Month, day)){
            getContext().hideKeyboard();
            Day = day;
            personBirthday = new MonthDay(Month, Day);
            person.setBirthday(personBirthday);
            return new EditPersonAdjacencyPair(getContext(), person);
         }
         return new ChangePersonBirthdayDayInvalidAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         person.setBirthday(null);
         return new EditPersonAdjacencyPair(getContext(), person);
      }	
   }

   public static class ChangePersonBirthdayDayInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      private Person person;

      public ChangePersonBirthdayDayInvalidAdjacencyPair(final EnrollStateContext context,
            final Person person) {
         super("The date you entered is invalid. Please enter it again", 
               "Enter valid "+ person.getName() + "'s birthday:", context, context.getKeyboard(), true);
         this.person = person;
      }

      @Override
      public AdjacencyPair success(String text) {
         int day = Integer.parseInt(text);
         if(UserUtils.isValidDayOfMonth(Month, day)){
            getContext().hideKeyboard();
            Day = day;
            personBirthday = new MonthDay(Month, Day);
            person.setBirthday(personBirthday);
            return new EditPersonAdjacencyPair(getContext(), person);
         }
         return new ChangePersonBirthdayDayInvalidAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         person.setBirthday(null);
         return new EditPersonAdjacencyPair(getContext(), person);
      }	
   }

   public static class ChangeGenderAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public ChangeGenderAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("What is" + person.getName() +  "s gender?", context);
         choice("Male", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               person.setGender(Gender.Male);
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
         choice("Female", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               person.setGender(Gender.Female);
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
         choice("Never Mind", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               person.setGender(null);
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
      }
   }

   public static class ChangeAgeAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      private Person person;

      public ChangeAgeAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("What is" + person.getName() +  "s age?", "Enter " + person.getName() + "'s age:", 
               context, context.getKeyboard(),true);
         this.person = person;
      }

      @Override
      public AdjacencyPair success(String text) {
         if(UserUtils.isInteger(text)){
            getContext().hideKeyboard();
            person.setAge(Integer.parseInt(text));
            return new EditPersonAdjacencyPair(getContext(), person);
         }
         return new ChangeAgeInvalidAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel() {
         person.setAge(0);
         getContext().hideKeyboard();
         return new EditPersonAdjacencyPair(getContext(), person);
      }	
   }

   public static class ChangeAgeInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      private Person person;

      public ChangeAgeInvalidAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("The age you entered is invalid.  Please enter it again", 
               "Enter valid " + person.getName() + "'s age:", 
               context, context.getKeyboard(),true);
         this.person = person;
      }

      @Override
      public AdjacencyPair success(String text) {
         if(UserUtils.isInteger(text)){
            getContext().hideKeyboard();
            person.setAge(Integer.parseInt(text));
            return new EditPersonAdjacencyPair(getContext(), person);
         }
         return new ChangeAgeInvalidAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel() {
         person.setAge(0);
         getContext().hideKeyboard();
         return new EditPersonAdjacencyPair(getContext(), person);
      }	
   }

   public static class IfKnowZipCodeAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public IfKnowZipCodeAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("Do you know " + person.getName() + "'s ZipCode?", context);
         choice("Yes", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new ChangeZipCodeAdjacencyPair(getContext(), person);
            }
         });
         choice("No", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new ChangeStateAdjacencyPair(getContext(), person);
            }
         });
      }
   }


   public static class ChangeZipCodeAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      private Person person;

      public ChangeZipCodeAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("What is the zipcode for this address?", "Enter " + person.getName() + "'s zipcode:",
               context, context.getKeyboard(), true);
         this.person = person;
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         ZipCodeEntry zip = zipcodes.getPlaceData(text);
         if(zip != null){
            person.setLocation(getContext().getPlaceManager().getPlace(zip.getZip()));
            getContext().hideKeyboard();
            return new EditPersonAdjacencyPair(getContext(), person);
         }
         return new ChangeZipCodeAgainInvalidAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel() {
         person.setLocation(null);
         getContext().hideKeyboard();
         return new EditPersonAdjacencyPair(getContext(), person);
      }
   }

   public static class ChangeZipCodeAgainInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      private Person person;

      public ChangeZipCodeAgainInvalidAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("The zipcode you entered is invalid. Please enter a valid zipcode.", 
               "Enter " + person.getName() + "'s zipcode again:", context, context.getKeyboard(), true);
         this.person = person;
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         ZipCodeEntry zip = zipcodes.getPlaceData(text);
         if(zip != null){
            person.setLocation(getContext().getPlaceManager().getPlace(zip.getZip()));
            getContext().hideKeyboard();
            return new EditPersonAdjacencyPair(getContext(), person);
         }
         return new ChangeZipCodeAgainInvalidAdjacencyPair(getContext(), person);
      }  	     

      @Override
      public AdjacencyPair cancel() {
         person.setLocation(null);
         getContext().hideKeyboard();
         return new EditPersonAdjacencyPair(getContext(), person);
      }
   }

   public static class ChangeStateAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      private Person person;

      public ChangeStateAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("Which state does" + person.getName() +  "reside in ? ", "Enter " + person.getName() + "'s state:",
               context, context.getKeyboard());
         this.person = person;
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         StateEntry state = zipcodes.getState(text);
         if(state != null){
            personState = state.getStateAbbrev().get(0);
            return new ChangeCityAdjacencyPair(getContext(), person);
         }
         return new ChangeStateInvalidAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel() {
         return new ChangeStateInvalidAdjacencyPair(getContext(), person);
      }
   }

   public static class ChangeStateInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      private Person person;

      public ChangeStateInvalidAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("Sorry, but you must enter a valid state name here", 
               "Please Enter valid state name:",
               context, context.getKeyboard());
         this.person = person;
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         StateEntry state = zipcodes.getState(text);
         if(state != null){
            personState = state.getStateAbbrev().get(0);
            return new ChangeCityAdjacencyPair(getContext(), person);
         }
         return new ChangeStateInvalidAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel() {
         return new ChangeStateInvalidAdjacencyPair(getContext(), person);
      }
   }

   public static class ChangeCityAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      private Person person;

      public ChangeCityAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("Which city does" + person.getName() +  " live in ?", 
               "Enter " + person.getName() + "'s city:",
               context, context.getKeyboard());
         this.person = person;
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         List<ZipCodeEntry> cities = zipcodes.getCityData(text);
         for(ZipCodeEntry city : cities){
            if(city.getState().equals(personState)){
               getContext().hideKeyboard();
               person.setLocation(getContext().getPlaceManager().getPlace(city.getZip()));
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         }
         return new ChangeCityInvalidAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         StateEntry state = zipcodes.getState(personState);
         person.setLocation(getContext().getPlaceManager().getPlace(state.getCapitalZip()));
         return new EditPersonAdjacencyPair(getContext(), person);
      }
   }

   public static class ChangeCityInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      private Person person;

      public ChangeCityInvalidAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("That city name is not valid. Please enter it again", 
               "Invalid city name. Please re-enter " + person.getName() + "'s city:",
               context, context.getKeyboard());
         this.person = person;
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         List<ZipCodeEntry> cities = zipcodes.getCityData(text);
         for(ZipCodeEntry city : cities){
            if(city.getState().equals(personState)){
               getContext().hideKeyboard();
               person.setLocation(getContext().getPlaceManager().getPlace(city.getZip()));
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         }
         getContext().hideKeyboard();
         StateEntry state = zipcodes.getState(personState);
         person.setLocation(getContext().getPlaceManager().getPlace(state.getCapitalZip()));
         return new EditPersonAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         StateEntry state = zipcodes.getState(personState);
         person.setLocation(getContext().getPlaceManager().getPlace(state.getCapitalZip()));
         return new EditPersonAdjacencyPair(getContext(), person);
      }
   }

   public static class ChangeRelationAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public ChangeRelationAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("What is your relationship with" + person.getName() , context, true);
         choice("Friend", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               person.setRelationship(Relationship.Friend);
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
         choice("Sister", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               person.setRelationship(Relationship.Sister);
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
         choice("Brother", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               person.setRelationship(Relationship.Brother);
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
         choice("Mother", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               person.setRelationship(Relationship.Mother);
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
         choice("Father", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               person.setRelationship(Relationship.Father);
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
         choice("Daughter", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               person.setRelationship(Relationship.Daughter);
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
         choice("Son", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               person.setRelationship(Relationship.Son);
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
         choice("Granddaughter", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               person.setRelationship(Relationship.Granddaughter);
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
         choice("Grandson", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               person.setRelationship(Relationship.Grandson);
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
         choice("Aunt", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               person.setRelationship(Relationship.Aunt);
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
         choice("Uncle", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               person.setRelationship(Relationship.Uncle);
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
         choice("Cousin", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               person.setRelationship(Relationship.Cousin);
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
         choice("Never Mind", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               person.setRelationship(null);
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
      }
   }

   public static class ChangeSpouseAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public ChangeSpouseAdjacencyPair(final EnrollStateContext context, final Person person){
         super("ok, Is" + person.getName() + "married?", context);
         choice("Yes", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new ChangeReenterSpouseAdjacencyPair(getContext(), person);
            }
         });
         choice("No", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
         choice("Never Mind", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
      }
   }

   public static class ChangeReenterSpouseAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      private Person person;

      public ChangeReenterSpouseAdjacencyPair(final EnrollStateContext context, final Person person){
         super("What is" + person.getName() + "s spouse's name?", "Enter " + person.getName() +"'s spouse name:", 
               context, context.getKeyboard());
         this.person = person;
      }

      @Override
      public AdjacencyPair success(String text) {
         getContext().hideKeyboard();
         person.addRelated(getContext().getPeopleManager().getPerson(text), Relationship.Spouse);
         return new EditPersonAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new EditPersonAdjacencyPair(getContext(), person);
      }
   }

   public static class ChangePhoneAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      private Person person;

      public ChangePhoneAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("What is" + person.getName() +  "s phone number?", "Enter " + person.getName() + "'s phone number: (XXX-XXX-XXXX)", 
               context, context.getKeyboard(),true);
         this.person = person;
      }

      @Override
      public AdjacencyPair success(String text) {
         if(UserUtils.isPhoneNumberValid(text)){
            getContext().hideKeyboard();
            person.setPhoneNumber(text);
            return new EditPersonAdjacencyPair(getContext(), person);
         }
         return new ChangePhoneInvalidAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel() {
         person.setPhoneNumber(null);
         getContext().hideKeyboard();
         return new EditPersonAdjacencyPair(getContext(), person);
      }
   }

   public static class ChangePhoneInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      private Person person;

      public ChangePhoneInvalidAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("That's not a valid phone number. Please enter the number again", 
               "Enter valid " + person.getName() + "'s phone number:(XXX-XXX-XXXX)", 
               context, context.getKeyboard(),true);
         this.person = person;
      }

      @Override
      public AdjacencyPair success(String text) {
         if(UserUtils.isPhoneNumberValid(text)){
            getContext().hideKeyboard();
            person.setPhoneNumber(text);
            return new EditPersonAdjacencyPair(getContext(), person);
         }
         return new ChangePhoneInvalidAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel() {
         person.setPhoneNumber(null);
         getContext().hideKeyboard();
         return new EditPersonAdjacencyPair(getContext(), person);
      }
   }

   public static class ChangeSkypeAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      private Person person;

      public ChangeSkypeAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("What is" + person.getName() + "s skype account?", "Enter " + person.getName() +"'s skype name:", 
               context, context.getKeyboard());
         this.person = person;
      }

      @Override
      public AdjacencyPair success(String text) {
         getContext().hideKeyboard();
         person.setSkypeNumber(text);
         return new EditPersonAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel() {
         person.setSkypeNumber(null);
         getContext().hideKeyboard();
         return new EditPersonAdjacencyPair(getContext(), person);
      }
   }
}
