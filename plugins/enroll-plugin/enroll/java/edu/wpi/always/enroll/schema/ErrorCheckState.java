package edu.wpi.always.enroll.schema;

import java.util.List;
import org.joda.time.MonthDay;
import edu.wpi.always.client.KeyboardAdjacencyPair;
import edu.wpi.always.user.UserUtils;
import edu.wpi.always.user.people.*;
import edu.wpi.always.user.people.Person.Gender;
import edu.wpi.always.user.people.Person.Relationship;
import edu.wpi.always.user.places.*;
import edu.wpi.always.user.places.ZipCodes.StateEntry;
import edu.wpi.always.user.places.ZipCodes.ZipCodeEntry;
import edu.wpi.disco.rt.menu.*;

public abstract class ErrorCheckState extends EnrollAdjacencyPairs {

   protected static String nameForLastState;
   protected static Boolean firstTimeHere = true;
   protected static String mainPromptForCheckCorrection = 
         "Okay, here's what I have. Are there any mistakes?";
   protected static String nameToUse;
   
   protected static void resetErrorCheckingMainPrompt(){
      mainPromptForCheckCorrection = 
            "Okay, here's what I have. Are there any mistakes?";
   }
   
   public static class CheckCorrectionAdjacencyPair extends
         AdjacencyPairBase<EnrollStateContext> {

      private Person person;
      
      public CheckCorrectionAdjacencyPair (final EnrollStateContext context,
            final Person person) {
         super(mainPromptForCheckCorrection, context,true);
         this.person = person;

         choice("Edit Name", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new EditNameAdjacencyPair(getContext());
            }
         });
         choice("Edit Birthday", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new TellEditBirthdayAdjacencyPair(getContext());
            }
         });
         choice("Edit Zipcode", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new DoKnowZipCodeAdjacencyPair(getContext());
            }
         });
         choice("Edit Relation", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new EditRelationAdjacencyPair(getContext());
            }
         });
         choice("Edit Age", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new EditAgeAdjacencyPair(getContext());
            }
         });
         choice("Edit Gender", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new EditGenderAdjacencyPair(getContext());
            }
         });
         choice("Edit Spouse", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new EditSpouseAdjacencyPair(getContext());
            }
         });
         choice("Edit Skype", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new EditSkypeAdjacencyPair(getContext());
            }
         });
         choice("All Correct", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               getContext().hideEnrollUI();
               return new DoneCurrentPersonAdjacencyPair(getContext());
            }
         });
      }

      @Override
      public void enter () {
         getContext().getEnrollUI().showCurrentEntry(person);
         if(firstTimeHere){
            nameForLastState = 
                  EditPersonState.editingSelf ? "you better": name;
            nameToUse = EditPersonState.editingSelf ? "your" : name + "'s";
         }
         firstTimeHere = false;
         mainPromptForCheckCorrection = 
               "Is there anything else to correct?";
      }
   }

   public static class EditNameAdjacencyPair extends
         KeyboardAdjacencyPair<EnrollStateContext> {

      public EditNameAdjacencyPair (final EnrollStateContext context) {
         super("Please enter the Person's name.", "Enter name:", context,
               context.getKeyboard());
      }

      @Override
      public AdjacencyPair success (String text) {
         getContext().hideKeyboard();
         person.setName(text);
         return new CheckCorrectionAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel () {
         getContext().hideKeyboard();
         return new CheckCorrectionAdjacencyPair(getContext(), person);
      }
   }

   public static class TellEditBirthdayAdjacencyPair extends
         AdjacencyPairBase<EnrollStateContext> {

      public TellEditBirthdayAdjacencyPair (final EnrollStateContext context) {
         super("Do you want me to know " + nameToUse + " birthday?", context);
         choice("Yes", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new EditBirthdayMonthAdjacencyPair(getContext(), person);
            }
         });
         choice("No", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               person.setBirthday(null);
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
         choice("Never Mind", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
        
      }
   }

   public static class EditBirthdayMonthAdjacencyPair extends
         AdjacencyPairBase<EnrollStateContext> {

      public EditBirthdayMonthAdjacencyPair (final EnrollStateContext context,
            final Person person) {
         super("What is " + nameToUse + " birthday's month ?", context, true);
         for (int i = 0; i < 12; i++) {
            final int MonthNum = i;
            choice(Person.Month[i], new DialogStateTransition() {
               @Override
               public AdjacencyPair run () {
                  Month = MonthNum + 1;
                  return new EditBirthdayDayAdjacencyPair(getContext(), person);
               }
            });
         }
         choice("Never Mind", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
      }
   }

   public static class EditBirthdayDayAdjacencyPair extends
         KeyboardAdjacencyPair<EnrollStateContext> {

      public EditBirthdayDayAdjacencyPair (final EnrollStateContext context,
            final Person person) {
         super("What is " + nameToUse + " day of birthday ?", "Enter "
            + nameToUse + " Birthday:", context, context.getKeyboard(), true);
      }

      @Override
      public AdjacencyPair success (String text) {
         int day = Integer.parseInt(text);
         if ( UserUtils.isValidDayOfMonth(Month, day) ) {
            getContext().hideKeyboard();
            Day = day;
            personBirthday = new MonthDay(Month, Day);
            person.setBirthday(personBirthday);
            return new CheckCorrectionAdjacencyPair(getContext(), person);
         }
         return new EditBirthdayDayInvalidAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel () {
         getContext().hideKeyboard();
         return new CheckCorrectionAdjacencyPair(getContext(), person);
      }
   }

   public static class EditBirthdayDayInvalidAdjacencyPair extends
         KeyboardAdjacencyPair<EnrollStateContext> {

      public EditBirthdayDayInvalidAdjacencyPair (
            final EnrollStateContext context, final Person person) {
         super("The day you entered is invalid. Please enter the day again",
               "Enter valid " + nameToUse + " birthday:", context, context
                     .getKeyboard(), true);
      }

      @Override
      public AdjacencyPair success (String text) {
         int day = Integer.parseInt(text);
         if ( UserUtils.isValidDayOfMonth(Month, day) ) {
            getContext().hideKeyboard();
            Day = day;
            personBirthday = new MonthDay(Month, Day);
            person.setBirthday(personBirthday);
            return new CheckCorrectionAdjacencyPair(getContext(), person);
         }
         return new EditBirthdayDayInvalidAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel () {
         getContext().hideKeyboard();
         person.setBirthday(null);
         return new CheckCorrectionAdjacencyPair(getContext(), person);
      }
   }

   public static class EditGenderAdjacencyPair extends
         AdjacencyPairBase<EnrollStateContext> {

      public EditGenderAdjacencyPair (final EnrollStateContext context) {
         super("What is " + nameToUse + " gender?", context);
         choice("Male", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               person.setGender(Gender.Male);
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
         choice("Female", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               person.setGender(Gender.Female);
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
         choice("Never Mind", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
      }
   }

   public static class EditAgeAdjacencyPair extends
         KeyboardAdjacencyPair<EnrollStateContext> {

      public EditAgeAdjacencyPair (final EnrollStateContext context) {
         super("What is " + nameToUse + " age?", "Enter " + nameToUse + " age:",
               context, context.getKeyboard(), true);
      }

      @Override
      public AdjacencyPair success (String text) {
         if ( UserUtils.isInteger(text) ) {
            getContext().hideKeyboard();
            person.setAge(Integer.parseInt(text));
            return new CheckCorrectionAdjacencyPair(getContext(), person);
         }
         return new EditAgeInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel () {
         person.setAge(0);
         getContext().hideKeyboard();
         return new CheckCorrectionAdjacencyPair(getContext(), person);
      }
   }

   public static class EditAgeInvalidAdjacencyPair extends
         KeyboardAdjacencyPair<EnrollStateContext> {

      public EditAgeInvalidAdjacencyPair (final EnrollStateContext context) {
         super("The age you entered does not seem right. Please enter it again",
               nameToUse + " valid age:", context, context
                     .getKeyboard(), true);
      }

      @Override
      public AdjacencyPair success (String text) {
         if ( UserUtils.isInteger(text) ) {
            getContext().hideKeyboard();
            person.setAge(Integer.parseInt(text));
            return new CheckCorrectionAdjacencyPair(getContext(), person);
         }
         return new EditAgeInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel () {
         person.setAge(0);
         getContext().hideKeyboard();
         return new CheckCorrectionAdjacencyPair(getContext(), person);
      }
   }

   public static class DoKnowZipCodeAdjacencyPair extends
         AdjacencyPairBase<EnrollStateContext> {

      public DoKnowZipCodeAdjacencyPair (final EnrollStateContext context) {
         super("Do you know " + nameToUse + " ZipCode?", context);
         choice("Yes", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new EditZipCodeAdjacencyPair(getContext());
            }
         });
         choice("No", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new EditStateAdjacencyPair(getContext());
            }
         });
        
      }
   }

   public static class EditZipCodeAdjacencyPair extends
         KeyboardAdjacencyPair<EnrollStateContext> {

      public EditZipCodeAdjacencyPair (final EnrollStateContext context) {
         super("What is " + nameToUse + " zipcode?", "Enter " + nameToUse
            + " zipcode:", context, context.getKeyboard(), true);
      }

      @Override
      public AdjacencyPair success (String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         ZipCodeEntry zip = zipcodes.getPlaceData(text);
         if ( zip != null ) {
            person.setLocation(getContext().getPlaceManager().getPlace(
                  zip.getZip()));
            getContext().hideKeyboard();
            return new CheckCorrectionAdjacencyPair(getContext(), person);
         }
         return new ZipCodeAgainInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel () {
         person.setLocation(null);
         getContext().hideKeyboard();
         return new CheckCorrectionAdjacencyPair(getContext(), person);
      }
   }

   public static class ZipCodeAgainInvalidAdjacencyPair extends
         KeyboardAdjacencyPair<EnrollStateContext> {

      public ZipCodeAgainInvalidAdjacencyPair (final EnrollStateContext context) {
         super("This zipcode does not seem right to me. Can you please enter it again?",
               "Enter " + nameToUse + " zipcode again:", context, context
                     .getKeyboard(), true);
      }

      @Override
      public AdjacencyPair success (String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         ZipCodeEntry zip = zipcodes.getPlaceData(text);
         if ( zip != null ) {
            person.setLocation(getContext().getPlaceManager().getPlace(
                  zip.getZip()));
            getContext().hideKeyboard();
            return new CheckCorrectionAdjacencyPair(getContext(), person);
         }
         return new ZipCodeAgainInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel () {
         person.setLocation(null);
         getContext().hideKeyboard();
         return new CheckCorrectionAdjacencyPair(getContext(), person);
      }
   }

   public static class EditStateAdjacencyPair extends
         KeyboardAdjacencyPair<EnrollStateContext> {

      public EditStateAdjacencyPair (final EnrollStateContext context) {
         super("What is " + nameToUse + "?", "Enter " + nameToUse
            + " state:", context, context.getKeyboard());
      }

      @Override
      public AdjacencyPair success (String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         StateEntry state = zipcodes.getState(text);
         if ( state != null ) {
            personState = state.getStateAbbrev().get(0);
            return new EditCityAdjacencyPair(getContext());
         }
         return new EditStateInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel () {
         return new EditStateInvalidAdjacencyPair(getContext());
      }
   }

   public static class EditStateInvalidAdjacencyPair extends
         KeyboardAdjacencyPair<EnrollStateContext> {

      public EditStateInvalidAdjacencyPair (final EnrollStateContext context) {
         super("Sorry, but the state seems incorrect to me, would you pelase try again?",
               "Please enter valid state name:", context, context.getKeyboard());
      }

      @Override
      public AdjacencyPair success (String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         StateEntry state = zipcodes.getState(text);
         if ( state != null ) {
            personState = state.getStateAbbrev().get(0);
            return new EditCityAdjacencyPair(getContext());
         }
         return new EditStateInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel () {
         return new EditStateInvalidAdjacencyPair(getContext());
      }
   }

   public static class EditCityAdjacencyPair extends
         KeyboardAdjacencyPair<EnrollStateContext> {

      public EditCityAdjacencyPair (final EnrollStateContext context) {
         super("What is " + nameToUse + " city ?", "Enter " + nameToUse
            + " city:", context, context.getKeyboard());
      }

      @Override
      public AdjacencyPair success (String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         List<ZipCodeEntry> cities = zipcodes.getCityData(text);
         for (ZipCodeEntry city : cities) {
            if ( city.getState().equals(personState) ) {
               getContext().hideKeyboard();
               person.setLocation(getContext().getPlaceManager().getPlace(
                     city.getZip()));
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         }
         return new EditCityInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel () {
         getContext().hideKeyboard();
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         StateEntry state = zipcodes.getState(personState);
         person.setLocation(getContext().getPlaceManager().getPlace(
               state.getCapitalZip()));
         return new CheckCorrectionAdjacencyPair(getContext(), person);
      }
   }

   public static class EditCityInvalidAdjacencyPair extends
         KeyboardAdjacencyPair<EnrollStateContext> {

      public EditCityInvalidAdjacencyPair (final EnrollStateContext context) {
         super("This seems invalid. Could you please try again?",
               "Please re-enter " + nameToUse + " city:",
               context, context.getKeyboard());
      }

      @Override
      public AdjacencyPair success (String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         List<ZipCodeEntry> cities = zipcodes.getCityData(text);
         for (ZipCodeEntry city : cities) {
            if ( city.getState().equals(personState) ) {
               getContext().hideKeyboard();
               person.setLocation(getContext().getPlaceManager().getPlace(
                     city.getZip()));
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         }
         getContext().hideKeyboard();
         StateEntry state = zipcodes.getState(personState);
         person.setLocation(getContext().getPlaceManager().getPlace(
               state.getCapitalZip()));
         return new CheckCorrectionAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel () {
         getContext().hideKeyboard();
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         StateEntry state = zipcodes.getState(personState);
         person.setLocation(getContext().getPlaceManager().getPlace(
               state.getCapitalZip()));
         return new CheckCorrectionAdjacencyPair(getContext(), person);
      }
   }

   public static class EditRelationAdjacencyPair extends
         AdjacencyPairBase<EnrollStateContext> {

      public EditRelationAdjacencyPair (final EnrollStateContext context) {
         super("What is " + nameToUse + " relation to you?", context, true);
         choice("Friend", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               person.setRelationship(Relationship.Friend);
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
         choice("Sister", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               person.setRelationship(Relationship.Sister);
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
         choice("Brother", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               person.setRelationship(Relationship.Brother);
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
         choice("Mother", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               person.setRelationship(Relationship.Mother);
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
         choice("Father", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               person.setRelationship(Relationship.Father);
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
         choice("Daughter", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               person.setRelationship(Relationship.Daughter);
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
         choice("Son", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               person.setRelationship(Relationship.Son);
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
         choice("Granddaughter", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               person.setRelationship(Relationship.Granddaughter);
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
         choice("Grandson", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               person.setRelationship(Relationship.Grandson);
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
         choice("Aunt", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               person.setRelationship(Relationship.Aunt);
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
         choice("Uncle", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               person.setRelationship(Relationship.Uncle);
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
         choice("Cousin", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               person.setRelationship(Relationship.Cousin);
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
         choice("Never Mind", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
      }
   }

   public static class EditSpouseAdjacencyPair extends
         AdjacencyPairBase<EnrollStateContext> {

      public EditSpouseAdjacencyPair (final EnrollStateContext context) {
         super("Ok, what is " + nameToUse + " marital status?", context);
         choice("Married", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new ReenterSpouseAdjacencyPair(getContext());
            }
         });
         choice("Not married", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
         choice("Never Mind", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new CheckCorrectionAdjacencyPair(getContext(), person);
            }
         });
      }
   }

   public static class ReenterSpouseAdjacencyPair extends
         KeyboardAdjacencyPair<EnrollStateContext> {

      public ReenterSpouseAdjacencyPair (final EnrollStateContext context) {
         super("What is " + nameToUse + " spouse's name?", "Enter " + nameToUse
            + " spouse's name:", context, context.getKeyboard());
      }

      @Override
      public AdjacencyPair success (String text) {
         getContext().hideKeyboard();
         person.addRelated(getContext().getPeopleManager().getPerson(text),
               Relationship.Spouse);
         return new CheckCorrectionAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel () {
         getContext().hideKeyboard();
         return new CheckCorrectionAdjacencyPair(getContext(), person);
      }
   }

   public static class EditSkypeAdjacencyPair extends
         KeyboardAdjacencyPair<EnrollStateContext> {

      public EditSkypeAdjacencyPair (final EnrollStateContext context) {
         super("What is " + nameToUse + " skype name?", "Enter " + nameToUse
            + " skype name:", context, context.getKeyboard());
      }

      @Override
      public AdjacencyPair success (String text) {
         getContext().hideKeyboard();
         person.setSkypeNumber(text);
         return new CheckCorrectionAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel () {
         person.setSkypeNumber(null);
         getContext().hideKeyboard();
         return new CheckCorrectionAdjacencyPair(getContext(), person);
      }
   }

   public static class NextPersonAdjacencyPair extends
         AdjacencyPairBase<EnrollStateContext> {

      public NextPersonAdjacencyPair (final EnrollStateContext context) {
         super("Do you want to tell me about someone else?", context);
         choice("No", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new DoneCurrentPersonAdjacencyPair(getContext());
            }
         });
         choice("Yes", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new PersonNameAdjacencyPair(getContext());
            }
         });
      }
   }

   public static class DoneCurrentPersonAdjacencyPair extends
         AdjacencyPairBase<EnrollStateContext> {

      public DoneCurrentPersonAdjacencyPair (final EnrollStateContext context) {
         super("Ok, now I know about " + nameForLastState
            + ". Would you like to introduce someone else to me?", context);
         choice("Yes, I do", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new PersonNameAdjacencyPair(getContext());
            }
         });
         choice("No, that's it for now", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new InitialEnroll.DialogEndEvent(getContext());
            }
         });
     }
   }
}
