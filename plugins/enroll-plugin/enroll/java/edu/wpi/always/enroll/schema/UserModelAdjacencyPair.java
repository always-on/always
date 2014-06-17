package edu.wpi.always.enroll.schema;

import edu.wpi.always.client.KeyboardAdjacencyPair;
import edu.wpi.always.user.UserUtils;
import edu.wpi.always.user.people.*;
import edu.wpi.always.user.people.Person.Gender;
import edu.wpi.always.user.places.*;
import edu.wpi.always.user.places.ZipCodes.StateEntry;
import edu.wpi.always.user.places.ZipCodes.ZipCodeEntry;
import edu.wpi.disco.rt.menu.*;
import org.joda.time.MonthDay;
import java.util.List;

public class UserModelAdjacencyPair extends KeyboardAdjacencyPair<EnrollStateContext>{

   private static String UserName, SkypeNumber;
   private static Person Spouse;
   private static int UserAge, Month, Day;
   private static MonthDay userBirthday;
   private static Gender gender;
   private static Place location;
   private static String UserState;

   public UserModelAdjacencyPair(final EnrollStateContext context) {
      super("What is the name you want me to use to talk with you ?", 
            "Enter your name:", context, context.getKeyboard());
   }

   @Override
   public AdjacencyPair success(String text) {
      if ( text.isEmpty() ) return new UserModelAdjacencyPair(getContext());
      UserName = text;
      getContext().getUserModel().setUserName(text);
      return new UserAgeAdjacencyPair(getContext());
   }

   @Override
   public AdjacencyPair cancel() {
      return new UserModelAdjacencyPair(getContext());
   }

   public static class UserAgeAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserAgeAdjacencyPair(final EnrollStateContext context) {
         super("Please tell me your age? ", "Please enter your age:", 
               context, context.getKeyboard(), true);
      }

      @Override
      public AdjacencyPair success(String text) {
         if(UserUtils.isInteger(text)){
            getContext().hideKeyboard();
            UserAge = Integer.parseInt(text);
            getContext().getPeopleManager().getUser().setAge(UserAge);
            return new TellUserBirthdayAdjacencyPair(getContext());
         }
         getContext().hideKeyboard();
         return new UserAgeInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new TellUserBirthdayAdjacencyPair(getContext());
      }
   }

   public static class UserAgeInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserAgeInvalidAdjacencyPair(final EnrollStateContext context) {
         super("The age you entered doesn't seem right. Can you please try again", 
               "Enter valid age:", context, context.getKeyboard(), true);
      }

      @Override
      public AdjacencyPair success(String text) {
         if(UserUtils.isInteger(text)){
            getContext().hideKeyboard();
            UserAge = Integer.parseInt(text);
            getContext().getPeopleManager().getUser().setAge(UserAge);
            return new TellUserBirthdayAdjacencyPair(getContext());
         }
         getContext().hideKeyboard();
         return new UserAgeInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new TellUserBirthdayAdjacencyPair(getContext());
      }	
   }

   public static class TellUserBirthdayAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public TellUserBirthdayAdjacencyPair(final EnrollStateContext context) {
         super("Do you want to tell me your birthday ?", context);
         choice("Yes", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new UserBirthdayMonthAdjacencyPair(getContext());
            }
         });
         choice("No", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new UserGenderAdjacencyPair(getContext());
            }
         });
         choice("Skip this information", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new UserGenderAdjacencyPair(getContext());
            }
         });
      }
   }

   public static class UserBirthdayMonthAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public UserBirthdayMonthAdjacencyPair(final EnrollStateContext context) {
         super("What is your birthday month ?", context, true);
         for(int i = 0; i < 12; i++) {
            final int MonthNum = i;
            choice(Person.Month[i], new DialogStateTransition() {
               @Override
               public AdjacencyPair run() {
                  Month = MonthNum + 1;
                  return new UserBirthdayDayAdjacencyPair(getContext());
               }
            });
         }
         choice("Skip this information", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new UserGenderAdjacencyPair(getContext());
            }
         });
      }
   }

   public static class UserBirthdayDayAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserBirthdayDayAdjacencyPair(final EnrollStateContext context) {
         super("What is the day of your Birthday ?", 
               "Enter your Birthday:", 
               context, context.getKeyboard(), true);}

      @Override
      public AdjacencyPair success(String text) {
         int day = Integer.parseInt(text);
         if(UserUtils.isValidDayOfMonth(Month, day)){
            getContext().hideKeyboard();
            Day = day;
            userBirthday = new MonthDay(Month, Day);
            getContext().getUserModel().getPeopleManager().
            getPerson(UserName).setBirthday(userBirthday);
            return new UserGenderAdjacencyPair(getContext());
         }
         return new UserBirthdayDayInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new UserGenderAdjacencyPair(getContext());
      }	
   }

   public static class UserBirthdayDayInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserBirthdayDayInvalidAdjacencyPair(final EnrollStateContext context) {
         super("The date you entered does not sound right. please enter it again", 
               "Enter valid birthday:", context, context.getKeyboard(), true);}

      @Override
      public AdjacencyPair success(String text) {
         int day = Integer.parseInt(text);
         if(UserUtils.isValidDayOfMonth(Month, day)){
            getContext().hideKeyboard();
            Day = day;
            userBirthday = new MonthDay(Month, Day);
            getContext().getUserModel().getPeopleManager().
            getPerson(UserName).setBirthday(userBirthday);
            return new UserGenderAdjacencyPair(getContext());
         }
         return new UserBirthdayDayInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new UserGenderAdjacencyPair(getContext());
      }	
   }

   public static class UserGenderAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public UserGenderAdjacencyPair(final EnrollStateContext context) {
         super("What is your gender?", context);
         choice("Male", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               gender = Gender.Male;
               getContext().getPeopleManager().getUser().setGender(gender);
               return new KnowUserZipCodeAdjacencyPair(getContext());
            }
         });
         choice("Female", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               gender = Gender.Female;
               getContext().getPeopleManager().getUser().setGender(gender);
               return new KnowUserZipCodeAdjacencyPair(getContext());
            }
         });
         choice("Skip this information", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new KnowUserZipCodeAdjacencyPair(getContext());
            }
         });
      }
   }

   public static class KnowUserZipCodeAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public KnowUserZipCodeAdjacencyPair(final EnrollStateContext context) {
         super("Do you know your ZipCode?", context);
         choice("Yes", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new UserZipCodeAdjacencyPair(getContext());
            }
         });
         choice("No", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new UserStateAdjacencyPair(getContext());
            }
         });
      }
   }

   public static class UserZipCodeAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserZipCodeAdjacencyPair(final EnrollStateContext context) {
         super("What is your zipcode?", "Enter your zipcode:",
               context, context.getKeyboard(), true);
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         ZipCodeEntry zip = zipcodes.getPlaceData(text);
         if(zip != null){
            location = getContext().getPlaceManager().getPlace(zip.getZip());
            getContext().getPeopleManager().getUser().setLocation(location);
            getContext().hideKeyboard();
            return new UserSpouseAdjacencyPair(getContext());
         }
         return new UserZipCodeInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new UserSpouseAdjacencyPair(getContext());
      }
   }

   public static class UserZipCodeInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserZipCodeInvalidAdjacencyPair(final EnrollStateContext context) {
         super("The zipcode you entered seems not right to me. Could you please enter it again?", 
               "Enter your zipcode again:", context, context.getKeyboard(), true);
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         ZipCodeEntry zip = zipcodes.getPlaceData(text);
         if(zip != null){
            location = getContext().getPlaceManager().getPlace(zip.getZip());
            getContext().getPeopleManager().getUser().setLocation(location);
            getContext().hideKeyboard();
            return new UserSpouseAdjacencyPair(getContext());
         }
         return new UserZipCodeInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new UserSpouseAdjacencyPair(getContext());
      }
   }

   public static class UserStateAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserStateAdjacencyPair(final EnrollStateContext context) {
         super("Which state do you live in?", "Enter your state:",
               context, context.getKeyboard());
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         StateEntry state = zipcodes.getState(text);
         if(state != null){
            UserState = state.getStateAbbrev().get(0);
            return new UserCityAdjacencyPair(getContext());
         }
         return new UserStateInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         return new UserStateInvalidAdjacencyPair(getContext());
      }
   }

   public static class UserStateInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserStateInvalidAdjacencyPair(final EnrollStateContext context) {
         super("Sorry, but that state does not sound right,", 
               "Please enter valid state name:",
               context, context.getKeyboard());
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         StateEntry state = zipcodes.getState(text);
         if(state != null){
            UserState = state.getStateAbbrev().get(0);
            return new UserCityAdjacencyPair(getContext());
         }
         return new UserStateInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         return new UserStateInvalidAdjacencyPair(getContext());
      }
   }

   public static class UserCityAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserCityAdjacencyPair(final EnrollStateContext context) {
         super("Which city do you live in ?", 
               "Enter your city:",
               context, context.getKeyboard());
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         List<ZipCodeEntry> cities = zipcodes.getCityData(text);
         for(ZipCodeEntry city : cities){
            if(city.getState().equals(UserState)){
               getContext().hideKeyboard();
               location = getContext().getPlaceManager().getPlace(city.getZip());
               getContext().getPeopleManager().getUser().setLocation(location);
               return new UserSpouseAdjacencyPair(getContext());
            }
         }
         return new UserCityInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         StateEntry state = zipcodes.getState(UserState);
         location = getContext().getPlaceManager().getPlace(state.getCapitalZip());
         getContext().getPeopleManager().getUser().setLocation(location);
         return new UserSpouseAdjacencyPair(getContext());
      }
   }

   public static class UserCityInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserCityInvalidAdjacencyPair(final EnrollStateContext context) {
         super("That city name does not sound right, could you please try again?", 
               "Please re-enter your city name:",
               context, context.getKeyboard());
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         List<ZipCodeEntry> cities = zipcodes.getCityData(text);
         for(ZipCodeEntry city : cities){
            if(city.getState().equals(UserState)){
               getContext().hideKeyboard();
               location = getContext().getPlaceManager().getPlace(city.getZip());
               getContext().getPeopleManager().getUser().setLocation(location);
               return new UserSpouseAdjacencyPair(getContext());
            }
         }
         getContext().hideKeyboard();
         StateEntry state = zipcodes.getState(UserState);
         location = getContext().getPlaceManager().getPlace(state.getCapitalZip());
         getContext().getPeopleManager().getUser().setLocation(location);
         return new UserSpouseAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         StateEntry state = zipcodes.getState(UserState);
         location = getContext().getPlaceManager().getPlace(state.getCapitalZip());
         getContext().getPeopleManager().getUser().setLocation(location);
         return new UserSpouseAdjacencyPair(getContext());
      }
   }

   public static class UserSpouseAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public UserSpouseAdjacencyPair(final EnrollStateContext context){
         super("ok, Are you married?", context);
         choice("Yes", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new EnterUserSpouseAdjacencyPair(getContext());
            }
         });
         choice("No", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new UserSkypeNumberAdjacencyPair(getContext());
            }
         });
         choice("Skip this information", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new UserSkypeNumberAdjacencyPair(getContext());
            }
         });
      }
   }

   public static class EnterUserSpouseAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public EnterUserSpouseAdjacencyPair(final EnrollStateContext context){
         super("What is your spouse's name?", "Enter your spouse name:", 
               context, context.getKeyboard());
      }

      @Override
      public AdjacencyPair success(String text) {
         Spouse = getContext().getPeopleManager().getPerson(text);
         getContext().getPeopleManager().getUser().addRelated(Spouse, Person.Relationship.Spouse);
         return new UserSkypeNumberAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         return new UserSkypeNumberAdjacencyPair(getContext());
      }
   }

   public static class UserSkypeNumberAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserSkypeNumberAdjacencyPair(final EnrollStateContext context) {
         super("What is your skype Account?", "Enter your skype name:", 
               context, context.getKeyboard());
      }

      @Override
      public AdjacencyPair success(String text) {
         getContext().hideKeyboard();
         SkypeNumber = text;
         getContext().getPeopleManager().getUser().setSkypeNumber(SkypeNumber);
         return new InitialEnroll(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new InitialEnroll(getContext());
      }
   }
}

