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

   public static String prompt;
   protected static String nameToUse;
   public static boolean editingSelf = false;
   protected static Boolean firstTimeHere = true;
   
   public static class EditPersonAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> { 
     
      Person person;
     
      public EditPersonAdjacencyPair(final EnrollStateContext context, final Person person){
         super(prompt, context, true);
         this.person = person;
         
         if(firstTimeHere){
            nameToUse = editingSelf ? "your" : person.getName() + "'s";
         }
         firstTimeHere = false;
         
         choice("Edit Name", new DialogStateTransition() {
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
         if(!editingSelf)
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
         choice("Edit Spouse", new DialogStateTransition() {
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
         choice("Done editing", new DialogStateTransition() {
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
         super("Ok, please tell me the new name", "New name:", context, context.getKeyboard());
         this.person = person;
      }

      @Override
      public AdjacencyPair success(String text) {
         getContext().hideKeyboard();
         if(!text.isEmpty()){
            person.setName(text);
            if(!editingSelf){
               nameToUse = text + "'s";
               EditPersonState.prompt = 
                     "Ok, here is what I know about " + person.getName();
            }
            return new EditPersonAdjacencyPair(getContext(), person);
         }
         return new PersonNameEditInvalidAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new EditPersonAdjacencyPair(getContext(), person);
      }
   }
   
   public static class PersonNameEditInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {
      
      private Person person;
      
      public PersonNameEditInvalidAdjacencyPair(
            final EnrollStateContext context, Person person) {
         super("You must enter some name, even a nickname will do", 
               "Enter name:", context, context.getKeyboard());
         this.person = person;
      }
      @Override
      public AdjacencyPair success(String text) {
         if(!text.isEmpty()){
            person.setName(text);
            if(!editingSelf){
               nameToUse = text + "'s";
               EditPersonState.prompt = 
                     "Ok, here is what I know about " + person.getName();
            }
            return new EditPersonAdjacencyPair(getContext(), person);
         }
         return new PersonNameEditInvalidAdjacencyPair(getContext(), person);
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
         super("Do you want to provide " + nameToUse + " birthday?", context);
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
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
      }
   }

   public static class ChangeBirthdayMonthAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public ChangeBirthdayMonthAdjacencyPair(final EnrollStateContext context,
            final Person person) {
         super("What is " + nameToUse + " birthday month ?", context, true);
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
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
      }
   }

   public static class ChangeBirthdayDayAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      Person person;
      
      public ChangeBirthdayDayAdjacencyPair(final EnrollStateContext context,
            final Person person) {
         super("What is the date of " + nameToUse + " Birthday ?", 
               "Enter "+ nameToUse + " Birthday:", context, context.getKeyboard(), true);
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
         return new EditPersonAdjacencyPair(getContext(), person);
      }	
   }

   public static class ChangePersonBirthdayDayInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      private Person person;

      public ChangePersonBirthdayDayInvalidAdjacencyPair(final EnrollStateContext context,
            final Person person) {
         super("The date you entered seems invalid. Could you please enter it again?", 
               "Enter valid "+ nameToUse + " birthday:", context, context.getKeyboard(), true);
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
         return new EditPersonAdjacencyPair(getContext(), person);
      }	
   }

   public static class ChangeGenderAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public ChangeGenderAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("What is " + nameToUse +  " gender?", context);
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
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
      }
   }

   public static class ChangeAgeAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      private Person person;

      public ChangeAgeAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("What is " + nameToUse +  " age?",
               "Enter " + nameToUse + " age:", 
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
         getContext().hideKeyboard();
         return new EditPersonAdjacencyPair(getContext(), person);
      }	
   }

   public static class ChangeAgeInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      private Person person;

      public ChangeAgeInvalidAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("The age you entered is invalid.  Please enter it again", 
               "Enter valid " + nameToUse + " age:", 
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
         getContext().hideKeyboard();
         return new EditPersonAdjacencyPair(getContext(), person);
      }	
   }

   public static class IfKnowZipCodeAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public IfKnowZipCodeAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("Do you know " + nameToUse + " ZipCode?", context);
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

      public ChangeZipCodeAdjacencyPair(
            final EnrollStateContext context, final Person person) {
         super("What is " + nameToUse + " the zipcode?", 
               "Enter " + nameToUse + " zipcode:",
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
         getContext().hideKeyboard();
         return new EditPersonAdjacencyPair(getContext(), person);
      }
   }

   public static class ChangeZipCodeAgainInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      private Person person;

      public ChangeZipCodeAgainInvalidAdjacencyPair(
            final EnrollStateContext context, final Person person) {
         super("The zipcode you entered is invalid. Please enter a valid zipcode.", 
               "Enter " + nameToUse + " zipcode again:",
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
         getContext().hideKeyboard();
         return new EditPersonAdjacencyPair(getContext(), person);
      }
   }

   public static class ChangeStateAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      private Person person;

      public ChangeStateAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("What is " + nameToUse +  " state ? ",
               "Enter " + nameToUse + " state:",
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
         getContext().hideKeyboard();
         return new EditPersonAdjacencyPair(getContext(), person);
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
         getContext().hideKeyboard();
         return new EditPersonAdjacencyPair(getContext(), person);
      }
   }

   public static class ChangeCityAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      private Person person;

      public ChangeCityAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("Which city does " + nameToUse +  " live in ?", 
               "Enter " + nameToUse + " city:",
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
               "Invalid city name. Please re-enter " + nameToUse + " city:",
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
               return new EditPersonAdjacencyPair(getContext(), person);
            }
         });
      }
   }

   public static class ChangeSpouseAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public ChangeSpouseAdjacencyPair(
            final EnrollStateContext context, final Person person){
         super("Ok, what is " + nameToUse + " marital status?", context);
         choice("Married", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new ChangeReenterSpouseAdjacencyPair(getContext(), person);
            }
         });
         choice("Not married", new DialogStateTransition() {
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

      public ChangeReenterSpouseAdjacencyPair(
            final EnrollStateContext context, final Person person){
         super("What is " + nameToUse + " spouse's name?", 
               "Enter " + nameToUse +" spouse name:", 
               context, context.getKeyboard());
         this.person = person;
      }

      @Override
      public AdjacencyPair success(String text) {
         getContext().hideKeyboard();
         person.addRelated(getContext().getPeopleManager().getPerson(text), 
               Relationship.Spouse);
         return new EditPersonAdjacencyPair(getContext(), person);
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new EditPersonAdjacencyPair(getContext(), person);
      }
   }

   public static class ChangeSkypeAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      private Person person;

      public ChangeSkypeAdjacencyPair(final EnrollStateContext context, final Person person) {
         super("What is " + nameToUse + " skype name?", 
               "Enter " + nameToUse +" skype name:",  context, context.getKeyboard());
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
         getContext().hideKeyboard();
         return new EditPersonAdjacencyPair(getContext(), person);
      }
   }
}
